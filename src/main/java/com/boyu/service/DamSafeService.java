package com.boyu.service;

import com.boyu.dao.DamSafeDao;
import com.boyu.pojo.StStationStatus;
import com.boyu.pojo.WrpDfrSrhrds;
import com.boyu.pojo.WrpSpgSppr;
import com.boyu.pojo.WrpSpgSpprl;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取报文后，进行分析（大坝安全监测数据入库）
 */
public class DamSafeService {
    static Logger logger = Logger.getLogger(DamSafeService.class.getName());
    //大坝安全监测信息入库
    private DamSafeDao damSafeDao=new DamSafeDao();
    //渗流压力监测信息分析
    public void sppranalysis(String stcd, Date mstm, String[] spprcd, BigDecimal[] sywm){
        if(spprcd.length==1){
            return;
        }
        List<WrpSpgSppr> spprlist=new ArrayList<WrpSpgSppr>();
        for(int i=0;i<spprcd.length;i++){
            if(!spprcd[i].equals("00000000")){
                WrpSpgSppr sppr=new WrpSpgSppr();
                sppr.setMpcd(spprcd[i]);
                sppr.setSpprwm(sywm[i].setScale(3,BigDecimal.ROUND_HALF_UP));
                sppr.setMstm(mstm);
                spprlist.add(sppr);
            }
        }
        damSafeDao.insertSpprInfo(stcd,spprlist);
    }
    //渗流量监测数据分析
    public void spprlanalysis(String stcd, Date mstm, String[] slcd,BigDecimal[] sll){
        if(slcd.length==1) {
            return;
        }
        List<WrpSpgSpprl> spprllist=new ArrayList<WrpSpgSpprl>();
        for(int i=0;i<slcd.length;i++){
            if(!slcd[i].equals("00000000")){
                WrpSpgSpprl spprl=new WrpSpgSpprl();
                spprl.setMpcd(slcd[i]);
                spprl.setMstm(mstm);
                spprl.setSpprwl(sll[i].setScale(3,BigDecimal.ROUND_HALF_UP));
                spprllist.add(spprl);
            }
        }
        damSafeDao.insertSpprLInfo(stcd,spprllist);
    }
    //表面变形监测数据分析
    public void srhrdsanalysis(String stcd, Date mstm,String[] wycd,BigDecimal[] xhr,BigDecimal[] yhr,BigDecimal[] vhr,BigDecimal[] eslg,BigDecimal[] nrlt,BigDecimal[] inel){
        if(wycd.length==1){
            return;
        }
        List<WrpDfrSrhrds> listhrds=new ArrayList<WrpDfrSrhrds>();
        for(int i=0;i<wycd.length;i++){
            if(!wycd[i].equals("00000000")){
                WrpDfrSrhrds hrds=new WrpDfrSrhrds();
                hrds.setMpcd(wycd[i]);
                hrds.setMstm(mstm);
                hrds.setXhrds(xhr[i].setScale(2,BigDecimal.ROUND_HALF_UP));
                hrds.setYhrds(yhr[i].setScale(2,BigDecimal.ROUND_HALF_UP));
                hrds.setVrds(vhr[i].setScale(2,BigDecimal.ROUND_HALF_UP));
                hrds.setEslg(eslg[i].setScale(6,BigDecimal.ROUND_HALF_UP));
                hrds.setNrlt(nrlt[i].setScale(6,BigDecimal.ROUND_HALF_UP));
                hrds.setInel(inel[i].setScale(3,BigDecimal.ROUND_HALF_UP));
                listhrds.add(hrds);
            }
        }
        damSafeDao.insertSrhvrdsInfo(stcd,listhrds);
    }
    //该时间的站点的运行工况是否存在
    public boolean existStatus(String stcd,Date tm){
        int count=damSafeDao.getstatuscount(stcd,tm);
        if(count>0)
            return false;
        else
            return true;
    }
    //运行工况监测数据分析（小时报）
    public void statusanalysis(String stcd,Date tm,String mcnel,String scnel,BigDecimal vol){
        StStationStatus status=new StStationStatus();
        status.setStcd(stcd);
        status.setTm(tm);
        status.setVoltype(1);
        status.setRft(1);
        status.setVol(vol);
        status.setCs(1);
        status.setMmcsq(mcnel);
        status.setMscsq(scnel);
        damSafeDao.insertStatusInfo(status);
    }
    //运行工况监测数据分析（加报）
    public void statusanalysisAdd(String stcd,Date tm,BigDecimal vol){
        StStationStatus status=new StStationStatus();
        status.setStcd(stcd);
        status.setTm(tm);
        status.setVoltype(1);
        status.setRft(1);
        status.setVol(vol);
        status.setCs(1);
        damSafeDao.insertStatusInfoByAdd(status);
    }
    //监测站点通讯中断
    public void statuscomloss(String stcd,Date tm){
        StStationStatus status=new StStationStatus();
        status.setStcd(stcd);
        status.setTm(tm);
        status.setVoltype(1);
        status.setRft(1);
        status.setCs(1);
        damSafeDao.insertStatusInfo(status);
    }
}
