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
            String messtr="7E 7E 02 08 02 29 02 01 12 34 34 00 FB 02 00 01 22 10 23 16 00 01 F1 F1 08 02 29 02 01 4B F0 F0 22 10 23 16 00 20 19 00 00 00 26 19 00 00 00 1A 19 00 00 00 F4 60 FF FF FF FF FF FF FF FF FF FF FF 00 F5 C0 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 00 FF 11 C0 10 01 03 01 10 01 03 02 10 01 03 03 10 01 03 04 10 01 03 05 10 01 03 06 FF 14 C0 00 00 50 02 00 00 00 08 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF 12 40 10 01 04 01 10 01 04 02 FF 15 40 00 00 00 00 00 00 00 00 FF 13 40 10 01 07 01 10 01 07 02 FF 16 50 FF 00 00 01 99 FF 00 00 00 73 FF 17 50 FF 00 00 01 84 FF 00 00 00 18 FF 18 50 FF 00 00 01 46 00 00 00 00 88 FF 19 50 01 10 47 80 00 01 10 47 80 00 FF 20 40 28 98 89 99 28 98 89 99 FF 21 40 00 62 07 07 00 61 53 92 FF F1 08 00 FF F2 08 00 38 12 13 35 03 84 CB";
            //加报
            //String messtr="7E7E0131235210011234330025020010230210023856F1F131235210014BF0F023021002382619000415201900001038121202036921";
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
