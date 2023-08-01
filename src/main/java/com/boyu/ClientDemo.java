package com.boyu;

import com.boyu.tool.HEXUtil;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClientDemo {
    public static void main(String[] args) {
        try {
            System.out.println("----客户端----");
            //1.创建Socket通信管道请求有服务端的连接，端口号为6666
            Socket socket = new Socket("127.0.0.1",8085);
            //2.从Socket通信管道中得到一个字节输出流  负责发送数据
            OutputStream os = socket.getOutputStream();
            //小时报
            String meshour="7E 7E 01 60 60 00 15 01 12 34 34 00 FB 02 00 20 22 07 14 10 00 46 F1 F1 60 60 00 15 01 4B F0 F0 22 07 14 10 00 20 19 00 01 34 26 19 00 16 58 1A 19 00 01 34 F4 60 00 15 00 0A 0F 13 0F 0F 0A 08 09 0C F5 C0 0E 5E 0F A0 0F A0 0F A2 0F A3 0F A4 0F A5 0F A7 0F A8 0F A9 0F AB 0F B4 FF 11 C0 10 01 03 01 10 01 03 02 10 01 03 03 10 01 03 04 10 01 03 05 10 01 03 06 FF 14 C0 00 00 01 00 00 00 02 00 00 00 03 00 00 00 00 50 00 00 00 08 00 00 00 60 FF 12 40 10 01 04 01 10 01 04 02 FF 15 40 00 00 00 04 00 00 00 09 FF 13 40 10 01 07 01 10 01 07 02 FF 16 50 00 00 00 00 12 FF 00 00 00 27 FF 17 50 FF 00 00 00 28 00 00 00 00 09 FF 18 50 00 00 00 00 45 FF 00 00 00 18 FF 19 50 01 13 02 42 33 01 13 03 25 41 FF 20 40 28 20 81 15 27 89 43 12 FF 21 40 00 02 76 00 00 00 02 85 FF F1 08 23 FF F2 08 00 38 12 12 13 03 FD 87";
            //加报
            //String messtr="7E 7E 01 60 60 00 15 01 12 34 33 00 25 02 00 1C 22 07 14 08 30 42 F1 F1 60 60 00 15 01 4B F0 F0 22 07 14 08 30 26 19 00 14 55 20 19 00 00 44 38 12 12 13 03 03 AE";
            //图像小时报
            //String messtr="7E 7E 01 60 60 00 15 01 12 34 E4 00 52 02 00 19 22 07 13 14 05 08 F1 F1 60 60 00 15 01 4B F3 3C 68 74 74 70 3A 2F 2F 31 32 30 2E 34 32 2E 34 36 2E 39 38 3A 32 30 30 30 32 2F 2F 2F 31 30 30 30 30 30 30 30 30 31 5F 30 5F 36 30 5F 31 36 35 37 37 32 31 31 30 30 5F 30 2E 6A 70 67 38 12 12 13 03 7E 92";
            //图像加报
            //String messtr="7E 7E 01 60 60 00 15 01 12 34 E3 00 57 02 00 1B 22 07 13 14 06 52 F1 F1 60 60 00 15 01 4B F3 41 68 74 74 70 3A 2F 2F 31 32 30 2E 34 32 2E 34 36 2E 39 38 3A 32 30 30 30 32 2F 2F 2F 31 30 30 30 30 30 30 30 30 31 5F 30 5F 36 30 5F 31 36 35 37 37 32 31 32 30 35 5F 38 37 38 32 37 39 2E 6A 70 67 38 12 12 13 03 3F A6";
            //测试报
            //String messtr="7E 7E 01 60 60 00 15 01 12 34 30 00 D3 02 00 14 22 07 13 13 56 50 F1 F1 60 60 00 15 01 4B F0 F0 22 07 13 13 56 26 19 00 14 11 1A 19 00 02 75 39 1A 00 09 00 FF 11 C0 10 01 03 01 10 01 03 02 10 01 03 03 10 01 03 04 10 01 03 05 10 01 03 06 FF 14 C0 00 00 03 00 00 00 04 00 00 00 05 00 00 00 01 20 00 00 02 30 00 00 03 20 FF 12 40 10 01 04 01 10 01 04 02 FF 15 40 00 00 00 40 00 00 00 90 FF 13 40 10 01 07 01 00 00 00 00 FF 16 50 00 00 00 02 30 00 00 00 00 00 FF 17 50 00 00 00 02 90 00 00 00 00 00 FF 18 50 00 00 00 04 50 00 00 00 00 00 FF 19 50 01 13 02 42 33 00 00 00 00 00 FF 20 40 28 20 81 15 00 00 00 00 FF 21 40 00 02 50 00 00 00 00 00 FF F1 08 23 FF F2 08 00 38 12 12 13 03 89 BF";
            //并发加报
            String mesrain="7E 7E 01 60 60 00 15 01 12 34 33 00 25 02 00 1C 22 07 14 09 10 42 F1 F1 60 60 00 15 01 4B F0 F0 22 07 14 09 10 26 19 00 15 45 20 19 00 00 21 38 12 12 13 03 BD 38";
            String meswater="7E 7E 01 60 60 00 15 01 12 34 33 00 20 02 00 15 22 07 14 10 00 58 F1 F1 60 60 00 15 01 4B F0 F0 22 07 14 10 00 39 1A 00 40 20 38 12 12 13 03 EB 94";
            String messafe="7E 7E 01 60 60 00 15 01 12 34 33 00 BC 02 00 1E 22 07 14 09 00 47 F1 F1 60 60 00 15 01 4B F0 F0 22 07 14 09 00 FF 11 C0 10 01 03 01 10 01 03 02 10 01 03 03 10 01 03 04 10 01 03 05 10 01 03 06 FF 14 C0 00 00 03 00 00 00 04 00 00 00 05 00 00 00 01 20 00 00 02 30 00 00 03 20 FF 12 40 10 01 04 01 10 01 04 02 FF 15 40 00 00 00 40 00 00 00 90 FF 13 40 10 01 07 01 00 00 00 00 FF 16 50 00 00 00 02 30 00 00 00 00 00 FF 17 50 00 00 00 02 90 00 00 00 00 00 FF 18 50 00 00 00 04 50 00 00 00 00 00 FF 19 50 01 13 02 42 33 00 00 00 00 00 FF 20 40 28 20 81 15 00 00 00 00 FF 21 40 00 02 50 00 00 00 00 00 38 12 12 13 03 A4 0B";
            String[] mesarray=new String[1];
            mesarray[0]=meshour;
            //mesarray[1]=mesrain;
            //mesarray[2]=meswater;
            for(int i=0;i<mesarray.length;i++){
                if(i>0){
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String messtr=mesarray[i];
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
