package com.boyu;

import com.boyu.service.ServerThread;
import com.boyu.tool.CRC16Util;
import com.boyu.tool.HEXUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoketAppllication {
    static Logger logger = Logger.getLogger(SoketAppllication.class.getName());
   //创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    public static void main(String[] args){
        try {
            String jssign="0345689abc";
            String[] signarray=jssign.substring(1).split("");
            System.out.println("字符数组长度为："+signarray.length);
            String checkstr="7E 7E 02 08 02 29 02 01 12 34 34 00 FB 02 00 01 22 10 23 16 00 01 F1 F1 08 02 29 02 01 4B F0 F0 22 10 23 16 00 20 19 00 00 00 26 19 00 00 00 1A 19 00 00 00 F4 60 FF FF FF FF FF FF FF FF FF FF FF 00 F5 C0 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 00 00 FF 11 C0 10 01 03 01 10 01 03 02 10 01 03 03 10 01 03 04 10 01 03 05 10 01 03 06 FF 14 C0 00 00 50 02 00 00 00 08 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF 12 40";
            String codestr="84CB";
            //System.out.println("字符串解析结果:"+CRC16Util.getCRC16(checkstr));
            byte[] checkbyte=HEXUtil.hexStringToByteArray(checkstr.replaceAll(" ",""));
            System.out.println("报文字节长度为："+(checkbyte.length));
            byte[] codebyte=HEXUtil.hexStringToByteArray(codestr);
            //System.out.println("报文字节长度为："+(checkbyte.length+codebyte.length));
            String checknum=CRC16Util.getCRC16(checkbyte);
            String code=HEXUtil.bytesToHexString(codebyte,true);
            System.out.println("校验码为:"+code+" 校验结果为："+checknum);
            //得到服务器端地址
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("local host:"+addr);
            //创建serverSocket套接字对象
            ServerSocket ss = new ServerSocket(8085);

            while(true) {
                // 此行代码会阻塞，将一直等待别人的连接
                Socket s = ss.accept();
                //输出日志
                System.out.println(s);  //输出socket对象
                //增加用户的操作在ServerThread类中实现
                // 每当客户端连接后启动一条ServerThread线程为该客户端服务
                cachedThreadPool.execute(new ServerThread(s));
                //new Thread(new ServerThread(s)).start();
            }
        } catch (IOException e) {
            logger.error("服务端启动失败!",e);
        }
    }
}
