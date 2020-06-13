package com.nio.demo.easyIo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * java 提供的selector模型，selector多路复用
 * <p>
 * 此版本是一个线程 把所有活干完了  接收客户端，处理客户端数据，下个版本写一个 worker，boss
 */
public class JDKNioSelector {
    static Selector selector;
    static ServerSocketChannel serverSocketChannel;

    static {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(9090));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("初始化完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            while (true) {
                //调用linux系统selector机制，返回大于0表示有事件
                while (selector.select(0) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey next = iterator.next();
                        if (next.isReadable()) {
                            readHandle(next);
                        } else if (next.isAcceptable()) {
                            acceptHandel(next);
                        } else if (next.isWritable()) {

                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private static void acceptHandel(SelectionKey next) {

    }

    private static void readHandle(SelectionKey next) throws Exception {
        ServerSocketChannel channel = (ServerSocketChannel) next.channel();
        SocketChannel accept = channel.accept();
        if (accept != null) {//可以读取，肯定不会为null了
            accept.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2048);
            while (true) {
                int read = accept.read(byteBuffer);
                if (read == -1) { //此时有可能是close-wait事件 导致一直可读死循环
                    accept.close();
                    break;
                }
                if (read == 0) {
                    break;
                } else {
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()) {
                        byte[] bb = new byte[byteBuffer.limit()];
                        byteBuffer.get(bb);
                        String sss = new String(bb);
                        System.out.println("接收到数据为:" + sss);
                        accept.write(byteBuffer);
                    }
                    byteBuffer.clear();
                }
            }

//            accept.register(selector, SelectionKey.OP_READ, byteBuffer);
//            SocketAddress remoteAddress = accept.getRemoteAddress();
//            System.out.println("remote:"+remoteAddress);
        }

    }
}
