package com.boyu.service;

import com.boyu.SoketAppllication;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

// 负责处理每个线程通信的线程类
public class ServerThread implements Runnable {
    static Logger logger = Logger.getLogger(ServerThread.class.getName());
    // 定义当前线程所处理的Socket
    Socket s = null;
    // 该线程所处理的Socket所对应的输入流
    BufferedReader br = null;

    private String stcd;

    public ServerThread(Socket s){
        try {
            this.s = s;
            // 初始化该Socket对应的输入流
            br = new BufferedReader(new InputStreamReader(s.getInputStream() , "utf-8"));
        } catch (IOException e) {
            logger.error("获取监测站点数据失败！",e);
        }
    }
    public void run() {
        String content = "";
        while((content = readFromClient()) != null){  //不断循环得到对应APP端数据
            logger.info(content);
            System.out.println(s.getRemoteSocketAddress() + "说了 : " + content);
            try {
                OutputStream os = s.getOutputStream();
                os.write(("已收到监测站点发来的消息" + "\n").getBytes("utf-8"));
                logger.info("服务器确认消息发出！");
            } catch (IOException e) {
                logger.error("服务器确认消息发送失败！",e);
            }
        }
    }
    // 定义读取监测站点数据的方法
    private String readFromClient() {

        try {
            return br.readLine();
        } catch (IOException e) {// 如果捕捉到异常，表明该Socket对应的客户端已经关闭
            logger.error(stcd+"监测站点下线",e);
            // 删除该Socket。
            SoketAppllication.siteList.remove(stcd);
        }
        return null;
    }
}
