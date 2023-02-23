package com.boyu;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientDemo {
    public static void main(String[] args) {
        try {
            System.out.println("----客户端----");
            //1.创建Socket通信管道请求有服务端的连接，端口号为6666
            Socket socket = new Socket("127.0.0.1",8085);
            //2.从Socket通信管道中得到一个字节输出流  负责发送数据
            OutputStream os = socket.getOutputStream();
            //3.把低级的字节流包装成打印流
            PrintStream ps = new PrintStream(os);

            Scanner sc = new Scanner(System.in);
                System.out.println("请输入:");
                String msg = sc.nextLine();
                ps.println(msg);
                ps.flush();

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String info = null;
            System.out.println("服务器发来消息说：" + br.readLine());

            System.out.println("请输入:");
            msg = sc.nextLine();
            ps.println(msg);
            ps.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
