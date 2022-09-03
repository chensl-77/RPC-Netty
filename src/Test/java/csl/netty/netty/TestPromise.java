package csl.netty.netty;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @Author: csl
 * @DateTime: 2022/7/15 12:07
 **/
@Slf4j
public class TestPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();

        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        Future<Integer> future = eventLoop.submit(() -> {
            log.info("执行计算...");
            Thread.sleep(1000);
            promise.setSuccess(80);
            return 80;
        });
        log.info("等待计算结果...");
        log.info("promise结果：{}",promise.get());
        log.info("future结果：{}",future.get());


    }
}
