package com.boyu.service;

import com.boyu.SoketAppllication;
import com.boyu.tool.CRC16Util;
import com.boyu.tool.HEXUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// 负责处理每个线程通信的线程类
public class ServerThread implements Runnable {
    static Logger logger = Logger.getLogger(ServerThread.class.getName());
    // 定义当前线程所处理的Socket
    private Socket s = null;
    // 该线程所处理的Socket所对应的输入流
    private InputStream is = null;
    // 该线程所处理的Socket所对应的输出流
    private OutputStream os = null;

    private String STCD;

    private AnalysisService service=new AnalysisService();
    private DamSafeService safeservice=new DamSafeService();

    public ServerThread(Socket s){
        try {
            this.s = s;
            // 初始化该Socket对应的输入流
            is=s.getInputStream();
            os=s.getOutputStream();
        } catch (IOException e) {
            logger.error("获取监测站点数据失败！",e);
        }
    }
    public void run() {
        try {
           while (true) {
               if (is.available() > 0) {  //不断循环得到对应APP端数据
                   byte[] request = new byte[is.available()];
                   is.read(request);
                   byte[] checknum = new byte[request.length - 2];
                   System.arraycopy(request, 0, checknum, 0, request.length - 2);
                   byte[] code = new byte[2];
                   System.arraycopy(request, request.length - 2, code, 0, 2);
                   String checkhex=CRC16Util.getCRC16(checknum);
                   String checkcode = HEXUtil.bytesToHexString(code,true);
                   System.out.println("验证结果"+checkhex+" && "+checkcode);
                   //校验通过，开始解析
                   if (!checkhex.equals(checkcode)) {
                       logger.info("数据包错误！");
                       return;
                   }
                   if(STCD==null) {
                       byte[] stcdbyte = new byte[5];
                       System.arraycopy(request, 3, stcdbyte, 0, 5);
                       //测站编码
                       String stcd = HEXUtil.bytesToHexString(stcdbyte, false);
                       STCD = stcd;
                   }
                   byte codesign=request[10];
                   resMessage(request, codesign);
                   switch (codesign){
                       case 52:analysis34H(request);  //小时报
                           break;
                       case 51:analysis33H(request);  //加报
                           break;
                       case -28:analysisJpgE4E3H(request,true);//图像小时报
                           break;
                       case -29:analysisJpgE4E3H(request,false);//图像加报
                           break;
                       case 48:analysis30H(request);  //测试报
                           break;
                   }
               }
               //每隔1秒执行一次
               Thread.sleep(1000*1);
           }
        } catch (IOException e) {
            //通讯异常
           // safeservice.statuscomloss(STCD,new Date());
            try {
                STCD=null;
                is.close();
                os.close();
                s.close();
            } catch (IOException ex) {
                logger.error(STCD+"监测站点通信关闭异常！",e);
            }
            logger.error(STCD+"监测站点信号异常！",e);
        } catch (InterruptedException e) {
            logger.error(STCD+"线程睡眠异常！",e);
        }
    }
    //发送下行报文
    private void resMessage(byte[] request, byte codesign) {
        byte[] response;
        String loginfo=STCD;
        try {
            response=new byte[1];
            byte[] headbyte=new byte[11];
            System.arraycopy(request, 0, headbyte, 0, 11);
            switch (codesign){
                case 52:response=returnMessage(headbyte);  //小时报
                    loginfo+=":小时报";
                    break;
                case 51:response=returnMessage(headbyte);  //加报
                    loginfo+=":加报";//加报
                    break;
                case -28:response=returnMessage(headbyte); //图像小时报
                    loginfo+=":图像小时报";
                    break;
                case -29:response=returnMessage(headbyte); //图像加报
                    loginfo+=":图像加报";
                    break;
                case 48:response=returnMessage(headbyte);  //测试报
                    loginfo+=":测试报";
                    break;
            }
            os.write(response);
            os.flush();
            logger.info(loginfo+"下行报文发出！");
        } catch (IOException e) {
            logger.error(loginfo+"下行报文发送失败！",e);
        }
    }
    //小时报(加报)返回下行报文
    private byte[] returnMessage(byte[] headbyte){
        StringBuilder sbsub = new StringBuilder();
        String xxhead=HEXUtil.bytesToHexString(headbyte,true);
        sbsub.append(xxhead);
        Date now=new Date();
        String nowstr=new SimpleDateFormat("yyyyMMddHHmmss").format(now).substring(2);
        int serialnum=service.getserialnum();
        String serialstr=HEXUtil.dec2Hex(serialnum).substring(4,8);
        sbsub.append("800802");
        sbsub.append(serialstr);
        sbsub.append(nowstr);
        sbsub.append("04");
        String xxsub=sbsub.toString();
        String yzcode=CRC16Util.getCRC16(xxsub);
        sbsub.append(yzcode);
        String responsestr=sbsub.toString();
        byte[] responsebyte=HEXUtil.hexStringToByteArray(responsestr);
        return responsebyte;
    }
    //测试报解析
    private void analysis30H(byte[] request){
        String stcd=STCD;
        //观测时间
        byte[] tmbyte=new byte[5];
        System.arraycopy(request, 32, tmbyte, 0, 5);
        String tm=HEXUtil.bytesToDatetime(tmbyte);
        //累计降雨量
        byte[] trainbyte=new byte[3];
        System.arraycopy(request, 39, trainbyte, 0, 3);
        String trainstr=HEXUtil.bytesToHexString(trainbyte,false);
        BigDecimal train=new BigDecimal(((double)Integer.parseInt(trainstr))/10).setScale(1,BigDecimal.ROUND_HALF_UP);
        //当前小时降雨量
        byte[] hrainbyte=new byte[3];
        System.arraycopy(request, 44, hrainbyte, 0, 3);
        String hrainstr=HEXUtil.bytesToHexString(hrainbyte,false);
        BigDecimal hrain=new BigDecimal(((double) Integer.parseInt(hrainstr))/10).setScale(1,BigDecimal.ROUND_HALF_UP);
        //瞬时水位
        byte[] rvsrbyte=new byte[3];
        System.arraycopy(request, 49, rvsrbyte, 0, 3);
        String rvsrstr=HEXUtil.bytesToHexString(rvsrbyte,false);
        BigDecimal rvsr=new BigDecimal(((double)Integer.parseInt(rvsrstr))/100).setScale(2,BigDecimal.ROUND_HALF_UP);
        //6个渗压测点编号
        byte[] spprbyte=new byte[24];
        System.arraycopy(request, 55, spprbyte, 0, 24);
        String[] spprcd=HEXUtil.bytesToSpprCd(spprbyte);
        //6个渗压水位
        byte[] sywmbyte;
        BigDecimal[] sywm=new BigDecimal[1];
        if(spprcd.length==6){
            sywmbyte=new byte[24];
            System.arraycopy(request, 82, sywmbyte, 0, 24);
            sywm=HEXUtil.bytesToSafeval(sywmbyte);
        }
        //2个渗流测点编号
        byte[] slbyte=new byte[8];
        System.arraycopy(request, 109, slbyte, 0, 8);
        String[] slcd=HEXUtil.bytesToSpprCd(slbyte);
        //2个渗流量
        byte[] sllbyte;
        BigDecimal[] sll=new BigDecimal[1];
        if(slcd.length==2){
            sllbyte=new byte[8];
            System.arraycopy(request, 120, sllbyte, 0, 8);
            sll=HEXUtil.bytesToSafeval(sllbyte);
        }
        //2个位移监测点编号
        byte[] wycdbyte=new byte[8];
        System.arraycopy(request, 131, wycdbyte, 0, 8);
        String[] wycd=HEXUtil.bytesToSpprCd(wycdbyte);
        //2个水平X位移
        byte[] xhrbyte;
        BigDecimal[] xhr=new BigDecimal[1];
        if(wycd.length==2){
            xhrbyte=new byte[10];
            System.arraycopy(request, 142, xhrbyte, 0, 10);
            xhr=HEXUtil.bytesToHRval(xhrbyte);
        }
        //2个水平Y位移
        byte[] yhrbyte;
        BigDecimal[] yhr=new BigDecimal[1];
        if(wycd.length==2){
            yhrbyte=new byte[10];
            System.arraycopy(request, 155, yhrbyte, 0, 10);
            yhr=HEXUtil.bytesToHRval(yhrbyte);
        }
        //2个垂直位移
        byte[] vhrbyte;
        BigDecimal[] vhr=new BigDecimal[1];
        if(wycd.length==2){
            vhrbyte=new byte[10];
            System.arraycopy(request, 168, vhrbyte, 0, 10);
            vhr=HEXUtil.bytesToHRval(vhrbyte);
        }
        //2个经度
        byte[] eslgbyte;
        BigDecimal[] eslg=new BigDecimal[1];
        if(wycd.length==2){
            eslgbyte=new byte[10];
            System.arraycopy(request, 181, eslgbyte, 0, 10);
            eslg=HEXUtil.bytesToEslg(eslgbyte,5);
        }
        //2个纬度
        byte[] nrltbyte;
        BigDecimal[] nrlt=new BigDecimal[1];
        if(wycd.length==2){
            nrltbyte=new byte[8];
            System.arraycopy(request, 194, nrltbyte, 0, 8);
            nrlt=HEXUtil.bytesToEslg(nrltbyte,4);
        }
        //2个垂直高程
        byte[] inelbyte;
        BigDecimal[] inel=new BigDecimal[1];
        if(wycd.length==2){
            inelbyte=new byte[8];
            System.arraycopy(request,205,inelbyte,0,8);
            inel=HEXUtil.bytesToSafeval(inelbyte);
        }
        //主信道强度
        byte mcnelbyte=request[216];
        String mcnel=Integer.toHexString(mcnelbyte);
        //备用信道强度
        byte scnelbyte=request[220];
        String scnel=Integer.toHexString(scnelbyte);
        //电压
        byte[] volbyte=new byte[2];
        System.arraycopy(request,223,volbyte,0,2);
        String volstr=HEXUtil.bytesToHexString(volbyte,false);
        BigDecimal vol=new BigDecimal(Double.parseDouble(volstr)/100).setScale(2,BigDecimal.ROUND_HALF_UP);
        try {
            Date tmdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tm);
            //实时雨情信息采集
            service.rainTestAnalysis(stcd,tmdate,train,hrain);
            //实时水情信息采集
            service.waterTestAnanlysis(stcd,tmdate,rvsr);
            //大坝安全监测数据采集
            safeservice.sppranalysis(stcd,tmdate,spprcd,sywm);
            safeservice.spprlanalysis(stcd,tmdate,slcd,sll);
            safeservice.srhrdsanalysis(stcd,tmdate,wycd,xhr,yhr,vhr,eslg,nrlt,inel);
            //运行工况数据采集
            safeservice.statusanalysis(stcd, tmdate, mcnel, scnel, vol);
        } catch (ParseException e) {
            logger.error(stcd+":测试报采集日期转换错误",e);
        }
        logger.info(stcd+":测试报监测数据采集入库！");
    }
    //小时报解析34H
    private void analysis34H(byte[] request){
        String stcd=STCD;
        //观测时间
        byte[] tmbyte=new byte[5];
        System.arraycopy(request, 32, tmbyte, 0, 5);
        String tm=HEXUtil.bytesToDatetime(tmbyte);
        //今日降雨量
        byte[] drainbyte=new byte[3];
        System.arraycopy(request, 39, drainbyte, 0, 3);
        String drainstr=HEXUtil.bytesToHexString(drainbyte,false);
        BigDecimal drain=new BigDecimal(((double)Integer.parseInt(drainstr))/10).setScale(1,BigDecimal.ROUND_HALF_UP);
        //累计降雨量
        byte[] trainbyte=new byte[3];
        System.arraycopy(request, 44, trainbyte, 0, 3);
        String trainstr=HEXUtil.bytesToHexString(trainbyte,false);
        BigDecimal train=new BigDecimal(((double)Integer.parseInt(trainstr))/10).setScale(1,BigDecimal.ROUND_HALF_UP);
        //当前小时降雨量
        byte[] hrainbyte=new byte[3];
        System.arraycopy(request, 49, hrainbyte, 0, 3);
        String hrainstr=HEXUtil.bytesToHexString(hrainbyte,false);
        BigDecimal hrain=new BigDecimal(((double) Integer.parseInt(hrainstr))/10).setScale(1,BigDecimal.ROUND_HALF_UP);
        //12个五分钟降雨量
        byte[] frainbyte=new byte[12];
        System.arraycopy(request, 54, frainbyte, 0, 12);
        BigDecimal[] frain=HEXUtil.bytesToFiverain(frainbyte);
        //12个五分钟水位
        byte[] frsvrbyte=new byte[24];
        System.arraycopy(request, 68, frsvrbyte, 0, 24);
        BigDecimal[] frsvr=HEXUtil.bytesToFiverrsvr(frsvrbyte);
        //6个渗压测点编号
        byte[] spprbyte=new byte[24];
        System.arraycopy(request, 95, spprbyte, 0, 24);
        String[] spprcd=HEXUtil.bytesToSpprCd(spprbyte);
        //6个渗压水位
        byte[] sywmbyte;
        BigDecimal[] sywm=new BigDecimal[1];
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
        BigDecimal[] sll=new BigDecimal[1];
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
        BigDecimal[] xhr=new BigDecimal[1];
        if(wycd.length==2){
            xhrbyte=new byte[10];
            System.arraycopy(request, 182, xhrbyte, 0, 10);
            xhr=HEXUtil.bytesToHRval(xhrbyte);
        }
        //2个水平Y位移
        byte[] yhrbyte;
        BigDecimal[] yhr=new BigDecimal[1];
        if(wycd.length==2){
            yhrbyte=new byte[10];
            System.arraycopy(request, 195, yhrbyte, 0, 10);
            yhr=HEXUtil.bytesToHRval(yhrbyte);
        }
        //2个垂直位移
        byte[] vhrbyte;
        BigDecimal[] vhr=new BigDecimal[1];
        if(wycd.length==2){
            vhrbyte=new byte[10];
            System.arraycopy(request, 208, vhrbyte, 0, 10);
            vhr=HEXUtil.bytesToHRval(vhrbyte);
        }
        //2个经度
        byte[] eslgbyte;
        BigDecimal[] eslg=new BigDecimal[1];
        if(wycd.length==2){
            eslgbyte=new byte[10];
            System.arraycopy(request, 221, eslgbyte, 0, 10);
            eslg=HEXUtil.bytesToEslg(eslgbyte,5);
        }
        //2个纬度
        byte[] nrltbyte;
        BigDecimal[] nrlt=new BigDecimal[1];
        if(wycd.length==2){
            nrltbyte=new byte[8];
            System.arraycopy(request, 234, nrltbyte, 0, 8);
            nrlt=HEXUtil.bytesToEslg(nrltbyte,4);
        }
        //2个垂直高程
        byte[] inelbyte;
        BigDecimal[] inel=new BigDecimal[1];
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
        System.arraycopy(request,263,volbyte,0,2);
        String volstr=HEXUtil.bytesToHexString(volbyte,false);
        try {
            BigDecimal vol=new BigDecimal(Double.parseDouble(volstr)/100).setScale(2,BigDecimal.ROUND_HALF_UP);
            Date tmdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tm);
            //实时降雨量采集
            service.rainanalysis(stcd,tmdate,drain,train,hrain,frain);
            //实时水位采集
            service.wateranalysis(stcd,tmdate,frsvr);
            //大坝安全监测数据采集
            if(!(spprcd.length==1 && slcd.length==1 && wycd.length==1)){
                if(safeservice.existspprAllByTime(stcd,tmdate,spprcd,slcd,wycd)){
                    safeservice.sppranalysis(stcd,tmdate,spprcd,sywm);
                    safeservice.spprlanalysis(stcd,tmdate,slcd,sll);
                    safeservice.srhrdsanalysis(stcd,tmdate,wycd,xhr,yhr,vhr,eslg,nrlt,inel);
                }
            }
            //运行工况数据采集
            if(safeservice.existStatus(STCD,tmdate)) {
                safeservice.statusanalysis(stcd, tmdate, mcnel, scnel, vol);
            }
        } catch (ParseException e) {
            logger.error(stcd+":小时报采集日期转换错误",e);
        }
        logger.info(stcd+":小时报监测数据采集入库！");
    }
    //加报解析33H
    private void analysis33H(byte[] request){
        //观测时间
        byte[] tmbyte=new byte[5];
        System.arraycopy(request, 32, tmbyte, 0, 5);
        String tm=HEXUtil.bytesToDatetime(tmbyte);
        try {
            Date tmdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tm);
            //获取加报标识 (雨量、水位、大坝)
            byte[] signbyte=new byte[2];
            System.arraycopy(request, 37, signbyte, 0, 2);
            String sign=HEXUtil.bytesToHexString(signbyte,true);
            byte[] dambyte=new byte[3];
            System.arraycopy(request, 37, dambyte, 0, 3);
            String signdam=HEXUtil.bytesToHexString(dambyte,true);
            if(sign.equals("2619")){
                analysisRain33(tmdate,request);
            }else if(sign.equals("391A")){
                analysisWater33(tmdate,request);
            }else if(signdam.equals("FF11C0")){
                analysisDam33(tmdate,request);
            }
        } catch (ParseException e) {
            logger.error(STCD+":加报采集日期转换错误",e);
        }
    }
    //大坝安全加报
    private void analysisDam33(Date tm,byte[] request){
        //6个渗压测点编号
        byte[] spprbyte=new byte[24];
        System.arraycopy(request, 40, spprbyte, 0, 24);
        String[] spprcd=HEXUtil.bytesToSpprCd(spprbyte);
        //6个渗压水位
        byte[] sywmbyte;
        BigDecimal[] sywm=new BigDecimal[1];
        if(spprcd.length==6){
            sywmbyte=new byte[24];
            System.arraycopy(request, 67, sywmbyte, 0, 24);
            sywm=HEXUtil.bytesToSafeval(sywmbyte);
        }
        //2个渗流测点编号
        byte[] slbyte=new byte[8];
        System.arraycopy(request, 94, slbyte, 0, 8);
        String[] slcd=HEXUtil.bytesToSpprCd(slbyte);
        //2个渗流量
        byte[] sllbyte;
        BigDecimal[] sll=new BigDecimal[1];
        if(slcd.length==2){
            sllbyte=new byte[8];
            System.arraycopy(request, 105, sllbyte, 0, 8);
            sll=HEXUtil.bytesToSafeval(sllbyte);
        }
        //2个位移监测点编号
        byte[] wycdbyte=new byte[8];
        System.arraycopy(request, 116, wycdbyte, 0, 8);
        String[] wycd=HEXUtil.bytesToSpprCd(wycdbyte);
        //2个水平X位移
        byte[] xhrbyte;
        BigDecimal[] xhr=new BigDecimal[1];
        if(wycd.length==2){
            xhrbyte=new byte[10];
            System.arraycopy(request, 127, xhrbyte, 0, 10);
            xhr=HEXUtil.bytesToHRval(xhrbyte);
        }
        //2个水平Y位移
        byte[] yhrbyte;
        BigDecimal[] yhr=new BigDecimal[1];
        if(wycd.length==2){
            yhrbyte=new byte[10];
            System.arraycopy(request, 140, yhrbyte, 0, 10);
            yhr=HEXUtil.bytesToHRval(yhrbyte);
        }
        //2个垂直位移
        byte[] vhrbyte;
        BigDecimal[] vhr=new BigDecimal[1];
        if(wycd.length==2){
            vhrbyte=new byte[10];
            System.arraycopy(request, 153, vhrbyte, 0, 10);
            vhr=HEXUtil.bytesToHRval(vhrbyte);
        }
        //2个经度
        byte[] eslgbyte;
        BigDecimal[] eslg=new BigDecimal[1];
        if(wycd.length==2){
            eslgbyte=new byte[10];
            System.arraycopy(request, 166, eslgbyte, 0, 10);
            eslg=HEXUtil.bytesToEslg(eslgbyte,5);
        }
        //2个纬度
        byte[] nrltbyte;
        BigDecimal[] nrlt=new BigDecimal[1];
        if(wycd.length==2){
            nrltbyte=new byte[8];
            System.arraycopy(request, 179, nrltbyte, 0, 8);
            nrlt=HEXUtil.bytesToEslg(nrltbyte,4);
        }
        //2个垂直高程
        byte[] inelbyte;
        BigDecimal[] inel=new BigDecimal[1];
        if(wycd.length==2){
            inelbyte=new byte[8];
            System.arraycopy(request,190,inelbyte,0,8);
            inel=HEXUtil.bytesToSafeval(inelbyte);
        }
        //电压
        byte[] volbyte=new byte[2];
        System.arraycopy(request,200,volbyte,0,2);
        String volstr=HEXUtil.bytesToHexString(volbyte,false);
        BigDecimal vol=new BigDecimal(Double.parseDouble(volstr)/100).setScale(2,BigDecimal.ROUND_HALF_UP);
        //大坝安全监测数据采集
        if(safeservice.existspprAllByTime(STCD,tm,spprcd,slcd,wycd)) {
            safeservice.sppranalysis(STCD, tm, spprcd, sywm);
            safeservice.spprlanalysis(STCD, tm, slcd, sll);
            safeservice.srhrdsanalysis(STCD, tm, wycd, xhr, yhr, vhr, eslg, nrlt, inel);
        }
        //运行工况数据加报采集
        if(safeservice.existStatus(STCD,tm)){
            safeservice.statusanalysisAdd(STCD,tm,vol);
        }
        logger.info(STCD+":加报大坝安全监测数据采集入库！");
    }
    //雨量加报
    private void analysisRain33(Date tm,byte[] request){
        //累计降雨量
        byte[] trainbyte=new byte[3];
        System.arraycopy(request, 39, trainbyte, 0, 3);
        String trainstr=HEXUtil.bytesToHexString(trainbyte,false);
        BigDecimal train=new BigDecimal(((double)Integer.parseInt(trainstr))/10).setScale(1,BigDecimal.ROUND_HALF_UP);
        //今日降雨量
        byte[] drainbyte=new byte[3];
        System.arraycopy(request, 44, drainbyte, 0, 3);
        String drainstr=HEXUtil.bytesToHexString(drainbyte,false);
        BigDecimal drain=new BigDecimal(((double)Integer.parseInt(drainstr))/10).setScale(1,BigDecimal.ROUND_HALF_UP);
        //电压
        byte[] volbyte=new byte[2];
        System.arraycopy(request,49,volbyte,0,2);
        String volstr=HEXUtil.bytesToHexString(volbyte,false);
        BigDecimal vol=new BigDecimal(Double.parseDouble(volstr)/100).setScale(2,BigDecimal.ROUND_HALF_UP);
        //雨量加报采集
        service.rainAddAnalysis(STCD,tm,train,drain);
        //运行工况数据加报采集
        if(safeservice.existStatus(STCD,tm)) {
            safeservice.statusanalysisAdd(STCD, tm, vol);
        }
        logger.info(STCD+":加报雨量监测数据采集入库！");
    }
    //水位加报
    private void analysisWater33(Date tm,byte[] request){
        //瞬时水位
        byte[] rvsrbyte=new byte[3];
        System.arraycopy(request, 39, rvsrbyte, 0, 3);
        String rvsrstr=HEXUtil.bytesToHexString(rvsrbyte,false);
        BigDecimal rvsr=new BigDecimal(((double)Integer.parseInt(rvsrstr))/100).setScale(2,BigDecimal.ROUND_HALF_UP);
        //电压
        byte[] volbyte=new byte[2];
        System.arraycopy(request,44,volbyte,0,2);
        String volstr=HEXUtil.bytesToHexString(volbyte,false);
        BigDecimal vol=new BigDecimal(Double.parseDouble(volstr)/100).setScale(2,BigDecimal.ROUND_HALF_UP);
        //水位加报采集
        service.wateranalysisAdd(STCD,tm,rvsr);
        //运行工况数据加报采集
        if(safeservice.existStatus(STCD,tm)) {
            safeservice.statusanalysisAdd(STCD, tm, vol);
        }
        logger.info(STCD+":加报水位监测数据采集入库！");
    }
    //图像小时(加)报 小时报为true,加报为false
    private void analysisJpgE4E3H(byte[] request,boolean hoursign){
        //站点编码
        String stcd=STCD;
        //发报时间
        byte[] tmbyte=new byte[6];
        System.arraycopy(request, 16, tmbyte, 0, 6);
        String tm=HEXUtil.bytesToDatetime(tmbyte);
        //图像数据长度
        byte[] lenbyte=new byte[1];
        System.arraycopy(request, 31, lenbyte, 0, 1);
        String lenstr=HEXUtil.bytesToHexString(lenbyte,false);
        int imglen=HEXUtil.hex2Dec(lenstr);
        //图像地址连接
        byte[] urlbyte=new byte[imglen];
        System.arraycopy(request, 32, urlbyte, 0, imglen);
        String urlstr=HEXUtil.bytesToHexString(urlbyte,true);
        String imgurl=HEXUtil.hexStringToString(urlstr);
        //电压
        byte[] volbyte=new byte[2];
        System.arraycopy(request, 32+imglen+2, volbyte, 0, 2);
        String volstr=HEXUtil.bytesToHexString(volbyte,false);
        BigDecimal vol=new BigDecimal(Double.parseDouble(volstr)/100).setScale(2,BigDecimal.ROUND_HALF_UP);
        try {
            Date tmdate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tm);
            //图像信息采集
            service.imageanalysis(stcd,tmdate,imgurl);
            //运行工况数据加报采集
            if(safeservice.existStatus(STCD,tmdate)) {
                safeservice.statusanalysisAdd(STCD, tmdate, vol);
            }
        } catch (ParseException e) {
            if(hoursign){
                logger.error(STCD+":图像小时报采集日期转换错误",e);
            }else{
                logger.error(STCD+":图像加报采集日期转换错误",e);
            }

        }
        if(hoursign){
            logger.info(stcd+":图像小时报数据采集入库！");
        }else{
            logger.info(stcd+":图像加报数据采集入库！");
        }

    }
}
