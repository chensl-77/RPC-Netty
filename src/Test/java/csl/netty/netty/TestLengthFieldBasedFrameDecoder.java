package csl.netty.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Author: csl
 * @DateTime: 2022/7/19 14:50
 * LengthFieldBasedFrameDecoder处理粘包半包
 **/
public class TestLengthFieldBasedFrameDecoder {
    public static void main(String[] args) {
        //用于模拟channel处理Handler
        EmbeddedChannel channel = new EmbeddedChannel(
            new LengthFieldBasedFrameDecoder(1024,0,4,0,4),
            new LoggingHandler(LogLevel.DEBUG)
        );
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        send(buf, "Hello,world!");
        send(buf, "Hi!");
        channel.writeInbound(buf);
    }

    private static void send(ByteBuf buf, String s) {
        byte[] bytes = s.getBytes();
        int length = bytes.length;
        buf.writeInt(length);//内容长度
        buf.writeBytes(bytes);//实际内容
    }
}
