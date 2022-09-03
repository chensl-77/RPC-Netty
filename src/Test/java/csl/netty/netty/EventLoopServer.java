package csl.netty.netty;

import csl.netty.nio.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: csl
 * @DateTime: 2022/7/14 18:16
 **/
@Slf4j
public class EventLoopServer {
    public static void main(String[] args) {
        //细分2：创建DefaultEventLoopGroup来执行一些非IO耗时操作
        EventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                //细分1：boss 和 worker
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new StringDecoder());
                                socketChannel.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("{}",msg);
                                        ctx.fireChannelRead(msg);//让消息传递下去，不然下面的handler接收不到
                                    }
                                }).addLast(group,"handler2",new ChannelInboundHandlerAdapter(){//绑定EventLoopGroup
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("{}",msg);
                                    }
                                });
                            }
                        }
                )
                .bind(8080);

        //执行周期任务
        group.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info("ok");
            }
        },0,1, TimeUnit.SECONDS);
    }
}
