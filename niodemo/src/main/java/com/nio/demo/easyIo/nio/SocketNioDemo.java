package com.nio.demo.easyIo.nio;

import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * 此模型 缺点：需要不断轮询看看有没有结果，浪费性能，下个demo写多路复用模型  select，poll，epoll
 *
 * nio 模型 但是没有多路复用  ，netty是多路复用nio
 */
public class SocketNioDemo {

    public static void main(String[] args) throws Exception {
        LinkedList<SocketChannel> channels = new LinkedList<>();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9090));
        serverSocketChannel.configureBlocking(false);// false 设置非阻塞   true阻塞

        while (true) {
            Thread.sleep(2000);
            /**
             * 传统socket 会阻塞在此处；
             * nio不会阻塞在这里，返回的socketchannel 有可能为空，为空代表没有客户端连接
             */
            SocketChannel accept = serverSocketChannel.accept();
            if (accept == null) {//没有客户端链接
                System.out.println("now no client connect");
                continue;
            } else {
                int port = accept.socket().getPort();
                accept.configureBlocking(false);  //内核设计实现
                System.out.println("port:" + port);
                channels.add(accept);
            }

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2048);
            //遍历客户端 看看有没有数据到达
            for (SocketChannel client : channels) {
                int read = client.read(byteBuffer);//不会阻塞  0   -1   》0
                if (read > 0) {
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.limit()];
                    byteBuffer.get(bytes);

                    String res = new String(bytes);
                    System.out.println(res);
                    byteBuffer.clear();//重复利用buffer，netty中是一个管道对应一个buffer
                }
            }

        }

    }

    static class ReadThread extends Thread {
        private BufferedReader reader;

        public ReadThread(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            try {
                String s = reader.readLine();
                System.out.println(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
