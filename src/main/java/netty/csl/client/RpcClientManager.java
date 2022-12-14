package netty.csl.client;

import com.alibaba.nacos.api.exception.NacosException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import netty.csl.client.handler.HeartBeatClientHandler;
import netty.csl.client.handler.RpcResponseMessageHandler;
import netty.csl.loadBalancer.RoundRobinRule;
import netty.csl.message.RpcRequestMessage;
import netty.csl.protocol.MessageCodecSharable;
import netty.csl.protocol.ProcotolFrameDecoder;
import netty.csl.protocol.SequenceIdGenerator;
import netty.csl.register.NacosServerDiscovery;
import netty.csl.register.ServerDiscovery;
import netty.csl.server.service.HelloService;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Rpc客户端管理器
 * @Author: csl
 */
@Slf4j
public class RpcClientManager {
    //单例channel
    private static final Bootstrap bootstrap;
    static NioEventLoopGroup group;
    private final ServerDiscovery serviceDiscovery;
    //channel集合  可能请求多个服务
    public static Map<String, Channel> channels;
    private static final Object LOCK = new Object();

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        initChannel();
        channels = new ConcurrentHashMap<>();
    }

    public RpcClientManager() {
        this.group = new NioEventLoopGroup();
        this.serviceDiscovery = new NacosServerDiscovery(new RoundRobinRule());
    }


    /**
     * 获取channel  没有就建立链接
     * @param inetSocketAddress
     * @return
     */
    public static Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        //判断是否存在
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channels != null && channel.isActive()) {
                return channel;
            }
            channels.remove(key);
        }
        //建立连接
        Channel channel = null;
        try {
            channel = bootstrap.connect(inetSocketAddress).sync().channel();
            channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    log.debug("断开连接");
                }
            });
        } catch (InterruptedException e) {
            channel.close();
            log.debug("连接客户端出错" + e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    // 初始化 channel 方法
    private static Bootstrap initChannel() {
        //日志handler
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        //消息处理handler
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //处理相应handler
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        //心跳处理器
        HeartBeatClientHandler HEATBEAT_CLIENT = new HeartBeatClientHandler();
        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        //定长解码器
                        ch.pipeline().addLast(new ProcotolFrameDecoder());
                        ch.pipeline().addLast(MESSAGE_CODEC);
//                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(HEATBEAT_CLIENT);
                        ch.pipeline().addLast(RPC_HANDLER);
                    }
                });
        return bootstrap;
    }

    /**
     * 发送消息根据用户名 服务发现 找到地址
     * @param msg
     */
    public void sendRpcRequest(RpcRequestMessage msg) throws NacosException {
        InetSocketAddress service = serviceDiscovery.getService(msg.getInterfaceName());
        Channel channel = get(service);
        if (!channel.isActive() || !channel.isRegistered()) {
            group.shutdownGracefully();
            return;
        }
        channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.debug("客户端发送消息成功");
            }
        });
    }
}
