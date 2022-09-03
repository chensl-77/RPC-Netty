package csl.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static csl.netty.nio.ByteBufferUtil.debugAll;

/**
 * @Author: csl
 * @DateTime: 2022/7/14 10:30
 * 异步IO
 **/
@Slf4j
public class AIO {
    public static void main(String[] args) throws IOException {
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(Paths.get("date.txt"), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            //异步线程执行读操作（守护线程）
            asynchronousFileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                //成功读取
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.info("read complete...{}",result);
                    attachment.flip();
                    debugAll(attachment);
                }
                //失败
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.in.read();

    }
}
