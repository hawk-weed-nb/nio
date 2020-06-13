package com.nio.demo.easyIo.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class NioWorkerAndBoss {

    private static ServerSocketChannel channel;
    private static Selector boss;
    private static Selector worker;
    private static Selector worker1;

    static {
        try {
            channel = ServerSocketChannel.open();
            channel.bind(new InetSocketAddress(9090));
            channel.configureBlocking(false);
            boss = Selector.open();
            worker = Selector.open();
            worker1 = Selector.open();
            channel.register(boss, SelectionKey.OP_READ);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception{
        BossOrWorkerThread thread = new BossOrWorkerThread(boss, 2);


        thread.start();

        Thread.sleep(2000);

        BossOrWorkerThread work1 = new BossOrWorkerThread(worker);
        BossOrWorkerThread work = new BossOrWorkerThread(worker1);
        work.start();
        work1.start();
    }
}


class BossOrWorkerThread extends Thread {

    static AtomicInteger index = new AtomicInteger(0);
    static BlockingQueue[] blockingQueues;
    static int temp;
    private Selector selector;

    // boss 构造器
    BossOrWorkerThread(Selector selector, int n) {
        this.temp = n;
        this.selector = selector;
        this.blockingQueues = new BlockingQueue[n];
        for (int i = 0; i < n; i++) {
            this.blockingQueues[i] = new LinkedBlockingDeque();
        }
        System.out.println("bosss qi dongle  ;;;;;;;;;;");
    }

    //worker 构造器
    BossOrWorkerThread(Selector selector) {
        this.selector = selector;
        int i = index.getAndDecrement() % temp;
        System.out.println("woker qi dong le id:" + i);
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (selector.select(10) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey next = iterator.next();
                        if (next.isAcceptable()) {
                            ServerSocketChannel channel = (ServerSocketChannel)next.channel();
                            SocketChannel accept = channel.accept();
                            accept.configureBlocking(false);
                            blockingQueues[index.get()].add(accept);
                        } else if (next.isReadable()) {

                        }
                    }

                }
                //work  干活
                if (blockingQueues[index.get()].isEmpty()){

                    SocketChannel socketChannel = (SocketChannel)blockingQueues[index.get()].take();
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2048);
                    socketChannel.register(selector, SelectionKey.OP_READ);

                    int port = socketChannel.socket().getPort();
                    System.out.println(port);

                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
