package csl.netty.nio;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: csl
 * @DateTime: 2022/7/11 10:22
 * 多线程实现网络编程
 **/
@Slf4j
public class ThreadsServer {

    private static int a = 0;

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        Selector boss = Selector.open();
        serverSocketChannel.register(boss, SelectionKey.OP_ACCEPT, null);
        serverSocketChannel.bind(new InetSocketAddress(8888));
        //创建固定数量的work
        work work1 = new work("work1");
        ArrayList<work> works = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            works.add(new work("work" + i));
        }
        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = serverSocketChannel.accept();
                    sc.configureBlocking(false);
                    works.get(a % 4).register(sc);
                    a++;
                    log.debug("after register...{}", sc.getRemoteAddress());
                }
            }
        }

    }
}

@Data
@Slf4j
class work {
    private String name;
    private Selector selector;
    private Thread thread;

    //可使用队列来让两个线程之间传递数据，并不立即执行
    private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private volatile boolean isregister = false;

    public work(String name) {
        this.name = name;
    }

    public void register(SocketChannel sc) throws IOException {
        if (!isregister) {
            thread = new Thread(() -> {
                while (true) {
                    try {
                        selector.select();
                        Runnable task = queue.poll();
                        if (task != null) {
                            task.run();
                        }
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            if (key.isReadable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                channel.read(buffer);
                                buffer.flip();
                                ByteBufferUtil.debugAll(buffer);
                                log.info("workname:{}", name);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            selector = Selector.open();
            thread.start();
            isregister = true;
        }
        queue.add(() -> {
            try {
                sc.register(selector, SelectionKey.OP_READ, null);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        });
        selector.wakeup();
//        sc.register(selector, SelectionKey.OP_READ, null);//不稳定

    }

}
