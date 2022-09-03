package csl.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 * @Author: csl
 * @DateTime: 2022/7/19 15:19
 **/
@Slf4j
public class TestHttp {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                socketChannel.pipeline().addLast(new HttpServerCodec());
                                socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
                                        log.info("{}",httpRequest.uri());

                                        DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
                                        byte[] bytes = "<h1>Hello,world!</h1>".getBytes();
                                        response.headers().set(CONTENT_LENGTH,bytes.length);
                                        response.content().writeBytes(bytes);
                                        ctx.writeAndFlush(response);
                                    }
                                });
                            }
                        }
                )
                .bind(8087);
    }
}
