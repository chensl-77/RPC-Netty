package csl.netty.nio.Test;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author: csl
 * @DateTime: 2022/7/6 10:40
 **/
@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("date.txt").getChannel()) {
            while (true){
                ByteBuffer buffer = ByteBuffer.allocate(10);
                int readlength = channel.read(buffer);
                log.info("剩余未读字节{}",readlength);
                if (readlength == -1){
                    break;
                }

                buffer.flip();
                while (buffer.hasRemaining()){
                    byte b = buffer.get();
                    log.info("读到字节{}",(char)b);
                }
                buffer.clear();
            }
        } catch (IOException e) {
        }
    }
}
