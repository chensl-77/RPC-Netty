package csl.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @Author: csl
 * @DateTime: 2022/7/10 8:23
 **/
@Slf4j
public class Server {



    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8081));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey ssckey = serverSocketChannel.register(selector, 0, null);
        ssckey.interestOps(SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();//没有事件发生会阻塞在这里，有事件才会运行
            //当时间发生时必须处理(accept)或取消（cancel）,不然下次循环不会阻塞
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            if (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                log.info("key:{}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey sckey = accept.register(selector, 0, buffer);
                    sckey.interestOps(SelectionKey.OP_READ);
                    log.info("{}", accept);
                    log.info("sckey:{}", sckey);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if (read == -1) {
                            key.cancel();//防止客户端正常断开手动取消处理
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);//0123456789abcdef3333\n
                                key.attach(newBuffer);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        key.cancel();//防止客户端异常断开手动取消处理
                    }
                }
            }
        }
    }
    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        source.compact();
    }

//    public static void main(String[] args) throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(16);
//        //创建服务器
//        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//        //开启非阻塞
//        serverSocketChannel.configureBlocking(false);
//        serverSocketChannel.bind(new InetSocketAddress(8080));
//        List<SocketChannel> socketChannels = new ArrayList<>();
//        while (true) {
//            SocketChannel accept = serverSocketChannel.accept();
//            if (accept != null){
//                accept.configureBlocking(false);
//                socketChannels.add(accept);
//                //开启非阻塞
//                log.info("{}连接到服务器",accept);
//            }
//            for (SocketChannel socketChannel : socketChannels) {
//                int read = socketChannel.read(buffer);
//                if (read > 0){
//                    buffer.flip();
//                    while (buffer.hasRemaining()){
//                        byte b = buffer.get();
//                        log.info("读到字节{}",(char)b);
//                    }
//                    buffer.clear();
//                }
//            }
//        }
//
//    }
}
