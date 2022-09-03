package csl.netty.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @Author: csl
 * @DateTime: 2022/7/17 10:01
 **/
public class TestByteBuf {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        System.out.println(buffer);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 300; i++) {
            stringBuffer.append("a");
        }
        buffer.writeBytes(stringBuffer.toString().getBytes());
        System.out.println(buffer);
    }
}
