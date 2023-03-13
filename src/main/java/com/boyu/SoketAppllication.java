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
            System.out.println("----服务端----");
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
