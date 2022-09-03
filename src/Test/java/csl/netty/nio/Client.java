package csl.netty.nio;

import com.google.common.primitives.Chars;
import io.netty.channel.ServerChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @Author: csl
 * @DateTime: 2022/7/10 8:23
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress("localhost",8888));
        channel.write(Charset.defaultCharset().encode("csl"));
        System.out.println();
    }
}
