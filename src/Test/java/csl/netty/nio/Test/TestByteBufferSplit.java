package csl.netty.nio.Test;

import java.nio.ByteBuffer;

/**
 * @Author: csl
 * @DateTime: 2022/7/6 12:17
 * 处理网络传输中的粘包和半包
 **/
public class TestByteBufferSplit {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("cslno1\nchbno2\ncz".getBytes());
        split(buffer);
        buffer.put("no3\n".getBytes());
        split(buffer);
    }

    public static void split(ByteBuffer buffer){
        buffer.flip();//读模式
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n'){
                int len = i - buffer.position();
                ByteBuffer buffer1 = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    buffer1.put(buffer.get());
                }
            }
        }
        buffer.compact();//写模式
    }
}
