package com.nio.demo.easyIo.socket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 阻塞io
 */
public class SocketBlockDemo {

    public static void main(String[] args) throws Exception{

        ServerSocket serverSocket = new ServerSocket(9090);
        /**
         * 1、传统io模型在此刻会阻塞等待ready;
         * 2、netty 模型 在accept方法的时候不会阻塞，会告诉你有没有结果，不会阻塞
         */
        Socket accept = serverSocket.accept();//阻塞

        InputStream inputStream = accept.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        /**
         * nio模型在此刻也不会阻塞
         */
        String s = bufferedReader.readLine();//阻塞
        System.out.println(s);

    }
}
