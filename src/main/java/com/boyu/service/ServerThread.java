package com.boyu.service;

import com.boyu.SoketAppllication;
import com.boyu.tool.CRC16Util;
import com.boyu.tool.HEXUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Date;

// 负责处理每个线程通信的线程类
public class ServerThread implements Runnable {
    static Logger logger = Logger.getLogger(ServerThread.class.getName());
    // 定义当前线程所处理的Socket
    Socket s = null;
    // 该线程所处理的Socket所对应的输入流
    InputStream is = null;

    private String STCD;

    private AnalysisService service=new AnalysisService();

    public ServerThread(Socket s){
        try {
            this.s = s;
            // 初始化该Socket对应的输入流
            is=s.getInputStream();
        } catch (IOException e) {
            logger.error("获取监测站点数据失败！",e);
        }
    }
    public void run() {
        byte[] request;
        byte[] response;
        try {
            if(is.available()>0) {  //不断循环得到对应APP端数据
                request = new byte[is.available()];
                is.read(request);
            } else {
                logger.info("数据包报文长度为零！");
                return;
            }
        } catch (IOException e) {
            logger.error(STCD+"监测站点信号异常！",e);
            return;
        }
        byte[] checknum = new byte[request.length - 2];
        System.arraycopy(request, 0, checknum, 0, request.length - 2);
        byte[] code = new byte[2];
        System.arraycopy(request, request.length - 2, code, 0, 2);
        String checkhex=CRC16Util.getCRC16(checknum);
        String checkcode = HEXUtil.bytesToHexString(code,true);
        //校验通过，开始解析
        if (checkhex.equals(checkcode)) {
            byte codesign=request[10];
            switch (codesign){
                case 52:analysis34H(request);//小时报
                    break;
                case 51:                     //加报
                    break;
            }
        }else{
            logger.info("数据包错误！");
        }
            logger.info("");
            System.out.println(s.getRemoteSocketAddress() + "说了 : " + "");
            try {
                OutputStream os = s.getOutputStream();
                os.write(("已收到监测站点发来的消息" + "\n").getBytes("utf-8"));
                logger.info("服务器确认消息发出！");
            } catch (IOException e) {
                logger.error("服务器确认消息发送失败！",e);
            }
    }
    //小时报解析34H
    private void analysis34H(byte[] request){
        byte[] stcdbyte= new byte[5];
        System.arraycopy(request, 3, stcdbyte, 0, 5);
        //测站编码
        String stcd=HEXUtil.bytesToHexString(stcdbyte,false);
        STCD=stcd;
        //观测时间
        byte[] tmbyte=new byte[5];
        System.arraycopy(request, 32, tmbyte, 0, 5);
        String tm=HEXUtil.bytesToDatetime(tmbyte);
        //今日降雨量
        byte[] drainbyte=new byte[3];
        System.arraycopy(request, 39, drainbyte, 0, 3);
        String drainstr=HEXUtil.bytesToHexString(drainbyte,false);
        BigDecimal drain=new BigDecimal(Integer.parseInt(drainstr)/10);
        //累计降雨量
        byte[] trainbyte=new byte[3];
        System.arraycopy(request, 44, trainbyte, 0, 3);
        String trainstr=HEXUtil.bytesToHexString(trainbyte,false);
        BigDecimal train=new BigDecimal(Integer.parseInt(trainstr)/10);
        //当前小时降雨量
        byte[] hrainbyte=new byte[3];
        System.arraycopy(request, 49, hrainbyte, 0, 3);
        String hrainstr=HEXUtil.bytesToHexString(hrainbyte,false);
        BigDecimal hrain=new BigDecimal(Integer.parseInt(hrainstr)/10);
        //12个五分钟降雨量
        byte[] frainbyte=new byte[12];
        System.arraycopy(request, 54, frainbyte, 0, 12);
        BigDecimal[] frain=HEXUtil.bytesToFiverain(frainbyte);
        //12个五分钟水位
        byte[] frsvrbyte=new byte[24];
        System.arraycopy(request, 68, frsvrbyte, 0, 24);
        BigDecimal[] frsvr=HEXUtil.bytesToFiverrsvr(frainbyte);
        //6个渗压测点编号
        byte[] spprbyte=new byte[24];
        System.arraycopy(request, 95, spprbyte, 0, 24);
        String[] spprcd=HEXUtil.bytesToSpprCd(spprbyte);
        //6个渗压水位
        byte[] sywmbyte;
        BigDecimal[] sywm;
        if(spprcd.length==6){
            sywmbyte=new byte[24];
            System.arraycopy(request, 122, sywmbyte, 0, 24);
            sywm=HEXUtil.bytesToSafeval(sywmbyte);
        }
        //2个渗流测点编号
        byte[] slbyte=new byte[8];
        System.arraycopy(request, 149, slbyte, 0, 8);
        String[] slcd=HEXUtil.bytesToSpprCd(slbyte);
        //2个渗流量
        byte[] sllbyte;
        BigDecimal[] sll;
        if(slcd.length==2){
            sllbyte=new byte[8];
            System.arraycopy(request, 160, sllbyte, 0, 8);
            sll=HEXUtil.bytesToSafeval(sllbyte);
        }
        //2个位移监测点编号
        byte[] wycdbyte=new byte[8];
        System.arraycopy(request, 171, wycdbyte, 0, 8);
        String[] wycd=HEXUtil.bytesToSpprCd(wycdbyte);
        //2个水平X位移
        byte[] xhrbyte;
        BigDecimal[] xhr;
        if(wycd.length==2){
            xhrbyte=new byte[10];
            System.arraycopy(request, 182, xhrbyte, 0, 10);
            xhr=HEXUtil.bytesToHRval(xhrbyte);
        }
        //2个水平Y位移
        byte[] yhrbyte;
        BigDecimal[] yhr;
        if(wycd.length==2){
            yhrbyte=new byte[10];
            System.arraycopy(request, 195, yhrbyte, 0, 10);
            yhr=HEXUtil.bytesToHRval(yhrbyte);
        }
        //2个垂直位移
        byte[] vhrbyte;
        BigDecimal[] vhr;
        if(wycd.length==2){
            vhrbyte=new byte[10];
            System.arraycopy(request, 208, vhrbyte, 0, 10);
            vhr=HEXUtil.bytesToHRval(vhrbyte);
        }
        //2个经度
        byte[] eslgbyte;
        BigDecimal[] eslg;
        if(wycd.length==2){
            eslgbyte=new byte[10];
            System.arraycopy(request, 221, eslgbyte, 0, 10);
            eslg=HEXUtil.bytesToEslg(eslgbyte,5);
        }
        //2个纬度
        byte[] nrltbyte;
        BigDecimal[] nrlt;
        if(wycd.length==2){
            nrltbyte=new byte[8];
            System.arraycopy(request, 234, nrltbyte, 0, 8);
            nrlt=HEXUtil.bytesToEslg(nrltbyte,4);
        }
        //2个垂直高程
        byte[] inelbyte;
        BigDecimal[] inel;
        if(wycd.length==2){
            inelbyte=new byte[8];
            System.arraycopy(request,245,inelbyte,0,8);
            inel=HEXUtil.bytesToSafeval(inelbyte);
        }
        //主信道强度
        byte mcnelbyte=request[256];
        String mcnel=Integer.toHexString(mcnelbyte);
        //备用信道强度
        byte scnelbyte=request[260];
        String scnel=Integer.toHexString(scnelbyte);
        //电压
        byte[] volbyte=new byte[2];
        System.arraycopy(request,264,volbyte,0,2);
        String volstr=HEXUtil.bytesToHexString(volbyte,false);
        BigDecimal vol=new BigDecimal(Double.parseDouble(volstr)/100);
        //实时降雨量采集
        service.rainanalysis(stcd,tm,drain,train,hrain,frain);
        //实时水位采集

    }
    //加报解析33H
}
