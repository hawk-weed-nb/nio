package com.nio.demo.easyIo.socket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 阻塞io利用多线程  解决主线程读取数据那一步的阻塞
 */
public class SocketNonBlockDemo {

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(9090);
        while (true){
            /**
             * 1、传统io模型在此刻会阻塞等待ready;
             * 2、netty 模型 在accept方法的时候不会阻塞，会告诉你有没有结果，不会阻塞
             */
            Socket accept = serverSocket.accept();//阻塞

            InputStream inputStream = accept.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            ReadThread readThread = new ReadThread(bufferedReader);
            readThread.start();
        }
    }

    static class ReadThread extends Thread {
        private BufferedReader reader;

        public ReadThread(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            try{
                String s = reader.readLine();
                System.out.println(s);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
