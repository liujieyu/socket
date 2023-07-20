package com.boyu;

import com.boyu.tool.HEXUtil;

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
            //小时报
            String messtr="7E 7E 01 60 60 00 15 01 12 34 34 00 FB 02 00 20 22 07 13 15 00 46 F1 F1 60 60 00 15 01 4B F0 F0 22 07 13 15 00 20 19 00 01 30 26 19 00 14 95 1A 19 00 01 03 F4 60 13 08 07 05 0F 05 09 04 08 0C 07 04 F5 C0 0B 81 0B 81 0B 81 0B 82 0B 82 0B 82 0B 82 0B 84 0B 85 0B 87 0B 87 0B 87 FF 11 C0 10 01 03 01 10 01 03 02 10 01 03 03 10 01 03 04 10 01 03 05 10 01 03 06 FF 14 C0 00 00 03 00 00 00 04 00 00 00 05 00 00 00 01 20 00 00 02 30 00 00 03 20 FF 12 40 10 01 04 01 10 01 04 02 FF 15 40 00 00 00 40 00 00 00 90 FF 13 40 10 01 07 01 00 00 00 00 FF 16 50 00 00 00 02 30 00 00 00 00 00 FF 17 50 00 00 00 02 90 00 00 00 00 00 FF 18 50 00 00 00 04 50 00 00 00 00 00 FF 19 50 01 13 02 42 33 00 00 00 00 00 FF 20 40 28 20 81 15 00 00 00 00 FF 21 40 00 02 50 00 00 00 00 00 FF F1 08 23 FF F2 08 00 38 12 12 13 03 5B 95";
            //加报
            //String messtr="7E 7E 01 60 60 00 15 01 12 34 33 00 20 02 00 15 22 07 13 14 50 58 F1 F1 60 60 00 15 01 4B F0 F0 22 07 13 14 50 39 1A 00 29 51 38 12 12 13 03 90 D6";
            byte[] msgbyte= HEXUtil.hexStringToByteArray(messtr.replaceAll(" ",""));
            System.out.println(msgbyte.length);
            os.write(msgbyte);
            os.flush();
            InputStream is=socket.getInputStream();
            byte[] recive;
            if(is.available()>0){
                recive=new byte[is.available()];
                is.read(recive);
                String getmsg=HEXUtil.bytesToHexString(recive,true);
                System.out.println(getmsg);
            }
            //3.把低级的字节流包装成打印流
            PrintStream ps = new PrintStream(os);
            while (true) {
                Scanner sc = new Scanner(System.in);
                System.out.println("请输入:");
                String msg = sc.nextLine();
                ps.println(msg);
                ps.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
