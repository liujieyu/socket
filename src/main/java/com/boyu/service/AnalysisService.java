package com.boyu.service;

import com.boyu.dao.WaterARainDao;
import com.boyu.pojo.StAlarmInfo;
import com.boyu.pojo.StPptnR;
import com.boyu.pojo.StRsvrR;
import com.boyu.pojo.WaterParam;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取报文后，进行分析（雨水情监测数据入库）
 */
public class AnalysisService {
    static Logger logger = Logger.getLogger(AnalysisService.class.getName());
    //雨水情信息入库
    private WaterARainDao waterARainDao=new WaterARainDao();
    //小时报雨情信息分析
    public void rainanalysis(String stcd,String tmstr, BigDecimal drain,BigDecimal train,BigDecimal hrain,BigDecimal[] frain){
        StPptnR lastrain=waterARainDao.getRealRain(stcd);
        try {
            Date tm=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tmstr);
            //1小时内没有加时报
            if(lastrain.getJssign().length()==1 && lastrain.getJssign().equals("0")){
                StPptnR realrain=new StPptnR();
                realrain.setStcd(stcd);
                realrain.setTm(tm);
                realrain.setDrp(hrain);
                realrain.setDyp(drain);
                realrain.setTotal(train);
                realrain.setIntv(new BigDecimal(1.0));
                realrain.setPdr(new BigDecimal(1.0));
                realrain.setJssign("0");
                List<StPptnR> hislist=new ArrayList<StPptnR>();
                for (int i=0;i<frain.length;i++){
                    StPptnR hisrain=new StPptnR();
                    hisrain.setStcd(stcd);
                    hisrain.setTm(new Date(tm.getTime()-(60-(i+1)*5)*60*1000));
                    hisrain.setDrp(frain[i]);
                    hisrain.setDyp(realrain.getDyp());
                    hislist.add(hisrain);
                }
                waterARainDao.insertHourRain(realrain,hislist,null);
            }else{
                long minutes=(tm.getTime()-lastrain.getTm().getTime())/(60*1000);
                BigDecimal intv=new BigDecimal((double)minutes/60);
                BigDecimal pdr=new BigDecimal((double)minutes/100);
                StPptnR realrain=new StPptnR();
                realrain.setStcd(stcd);
                realrain.setTm(tm);
                realrain.setDrp(hrain);
                realrain.setDyp(drain);
                realrain.setTotal(train);
                realrain.setIntv(intv);
                realrain.setPdr(pdr);
                realrain.setJssign("0");
                String[] jbje=lastrain.getJssign().substring(1).split("");
                //新增的5分钟雨量
                List<Integer> insertlist=new ArrayList<Integer>();
                //修改的5分钟雨量
                List<Integer> updatelist=new ArrayList<Integer>();
                for(int i=0;i<jbje.length;i++){
                    int interval=Integer.parseInt(jbje[i],16)-1;
                    if(i==0){
                        if(interval>0){
                            for(int s=0;s<interval;s++){
                                insertlist.add(s);
                            }
                            updatelist.add(interval);
                        }
                    }else{
                        int last=Integer.parseInt(jbje[i-1],16)-1;
                        if(interval-last>1){
                            for(int s=last+1;s<interval;s++){
                                insertlist.add(s);
                            }
                            updatelist.add(interval);
                        }
                        if(i==jbje.length-1){
                            for(int s=interval+1;s<12;s++){
                                insertlist.add(s);
                            }
                        }
                    }
                }
                List<StPptnR> hislist=new ArrayList<StPptnR>();
                for(int i=0;i<insertlist.size();i++){
                    int inteval=insertlist.get(i);
                    StPptnR hisrain=new StPptnR();
                    hisrain.setStcd(stcd);
                    hisrain.setTm(new Date(tm.getTime()-(60-(inteval+1)*5)*60*1000));
                    hisrain.setDrp(frain[inteval]);
                    hisrain.setDyp(realrain.getDyp());
                    hislist.add(hisrain);
                }
                List<StPptnR> uplist=new ArrayList<StPptnR>();
                for(int i=0;i<updatelist.size();i++){
                    int inteval=updatelist.get(i);
                    StPptnR hisrain=new StPptnR();
                    hisrain.setStcd(stcd);
                    hisrain.setTm(new Date(tm.getTime()-(60-(inteval+1)*5)*60*1000));
                    hisrain.setDrp(frain[inteval]);
                    hisrain.setDyp(realrain.getDyp());
                    uplist.add(hisrain);
                }
                waterARainDao.insertHourRain(realrain,hislist,uplist);
            }
        } catch (ParseException e) {
            logger.error("采集日期转换错误",e);
        }
    }
    //加时报雨情信息分析


    //小时报水情信息分析
    public void wateranalysis(String stcd,String tmstr,BigDecimal[] frsvr){
        //实时水情采集
        StRsvrR realwater=new StRsvrR();
        //历史水情采集
        List<StRsvrR> hislist=new ArrayList<StRsvrR>();
        //小时水情采集
        StRsvrR hourwater=new StRsvrR();
        //日水情采集
        StRsvrR daywater=new StRsvrR();
        //月水情采集
        StRsvrR monwater=new StRsvrR();
        //站点预警信息采集
        StAlarmInfo alarminfo=new StAlarmInfo();
        try {
            Date nowtm=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tmstr);
            Date ltm=new Date(nowtm.getTime()-1*60*60*1000);
            String ltmstr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ltm);
            String datestr=ltmstr.split(" ")[0];
            String timestr=ltmstr.split(" ")[1];
            int year=Integer.parseInt(datestr.substring(0,4));
            int mon=Integer.parseInt(datestr.substring(6,8));
            int hour=Integer.parseInt(timestr.substring(0,2));
            Date date=new SimpleDateFormat("yyyy-MM-dd").parse(ltmstr);
            WaterParam param=waterARainDao.getWaterParam(stcd,date,hour,year,mon);
            if(param.getJssign().length()==0 && param.getJssign().equals("0")){
                BigDecimal minrz=frsvr[0],maxrz=frsvr[0],sumrz=frsvr[0];
                Date mindate=new Date(nowtm.getTime()-(60-(0+1)*5)*60*1000),maxdate=new Date(nowtm.getTime()-(60-(0+1)*5)*60*1000);
                for(int i=0;i<frsvr.length;i++){
                    StRsvrR hiswater=new StRsvrR();
                    hiswater.setStcd(stcd);
                    hiswater.setTm(new Date(nowtm.getTime()-(60-(i+1)*5)*60*1000));
                    hiswater.setRz(frsvr[i]);
                    BigDecimal cv;
                    if(i==0){
                        cv=new BigDecimal(frsvr[i].doubleValue()-param.getLastrz().doubleValue());
                    }else{
                        cv=new BigDecimal(frsvr[i].doubleValue()-frsvr[i-1].doubleValue());
                        sumrz=new BigDecimal(sumrz.doubleValue()+frsvr[i].doubleValue());
                        if(minrz.doubleValue()>frsvr[i].doubleValue()){
                            minrz=frsvr[i];
                            mindate=hiswater.getTm();
                            if(i==frsvr.length-1){
                                mindate=new Date(nowtm.getTime()-1000);
                            }
                        }
                        if(maxrz.doubleValue()<frsvr[i].doubleValue()){
                            maxrz=frsvr[i];
                            maxdate=hiswater.getTm();
                            if(i==frsvr.length-1){
                                maxdate=new Date(nowtm.getTime()-1000);
                            }
                        }
                    }
                    hiswater.setCv(cv);
                    if(cv.doubleValue()==0){
                        hiswater.setRwptn('6');
                    }else if(cv.doubleValue()>0){
                        hiswater.setRwptn('5');
                    }else{
                        hiswater.setRwptn('4');
                    }
                    hiswater.setJssign("0");
                    hislist.add(hiswater);
                }
                BigDecimal avgrz=new BigDecimal(sumrz.doubleValue()/frsvr.length);
                //实时水位采集
                realwater=hislist.get(hislist.size()-1);
                BigDecimal xxwater;
                if (mon >= 7 && mon <= 9) {
                    xxwater = param.getFwl79();
                } else {
                    xxwater = param.getFwl();
                }
                int alarm = setRealAlarm(realwater, xxwater, param);
                realwater.setAlarm(alarm);
                //小时水位采集
                hourwater.setAddsign(0);
                hourwater.setRz(avgrz);
                hourwater.setMaxrz(maxrz);
                hourwater.setMaxdate(maxdate);
                hourwater.setMinrz(minrz);
                hourwater.setMindate(mindate);
                hourwater.setDate(date);
                hourwater.setHour(hour);
                hourwater.setStcd(stcd);
                //日水位采集
                daywater.setDate(date);
                daywater.setStcd(stcd);
                daywater.setRz(avgrz);
                if(param.getDmaxwl()==null){
                    daywater.setAddsign(0);
                    daywater.setMaxrz(maxrz);
                    daywater.setMaxdate(maxdate);
                    daywater.setMinrz(minrz);
                    daywater.setMindate(mindate);
                }else{
                    daywater.setAddsign(1);
                    if(param.getDmaxwl().doubleValue()<maxrz.doubleValue()){
                        daywater.setMaxrz(maxrz);
                        daywater.setMaxdate(maxdate);
                    }
                    if(param.getDminwl().doubleValue()>minrz.doubleValue()){
                        daywater.setMinrz(minrz);
                        daywater.setMindate(mindate);
                    }
                }
                //月水位采集
                monwater.setYear(year);
                monwater.setMon(mon);
                monwater.setStcd(stcd);
                monwater.setRz(avgrz);
                if(param.getMmaxwl()==null){
                    monwater.setAddsign(0);
                    monwater.setMaxrz(maxrz);
                    monwater.setMaxdate(maxdate);
                    monwater.setMinrz(minrz);
                    monwater.setMindate(mindate);
                }else{
                    monwater.setAddsign(1);
                    if(param.getMmaxwl().doubleValue()<maxrz.doubleValue()){
                        monwater.setMaxrz(maxrz);
                        monwater.setMaxdate(maxdate);
                    }
                    if(param.getMminwl().doubleValue()>minrz.doubleValue()){
                        monwater.setMinrz(minrz);
                        monwater.setMindate(mindate);
                    }
                }
                waterARainDao.insertHourWater(realwater,hislist,hourwater,daywater,monwater,null);
            }else{
                String[] jbjg=param.getJssign().substring(1).split("");
                List<Integer> insertlist=new ArrayList<Integer>();
                for(int i=0;i<jbjg.length;i++){
                    int inteval=Integer.parseInt(jbjg[i],16)-1;
                    if(i==0){
                        if(inteval>0){
                            for(int j=0;j<inteval;j++){
                                insertlist.add(j);
                            }
                        }
                    }else{
                        int lastval=Integer.parseInt(jbjg[i-1],16)-1;
                        if(inteval-lastval>1){
                            for(int j=lastval+1;j<inteval;j++){
                                insertlist.add(j);
                            }
                        }
                        if(i==jbjg.length-1){
                            for(int j=inteval+1;j<12;j++){
                                insertlist.add(j);
                            }
                        }
                    }
                }
                //加报已经将小时数据全部插入  将加时报标识清空为0
                if(insertlist.size()==0){
                    realwater.setJssign("0");
                    realwater.setStcd(stcd);
                    waterARainDao.insertHourWater(realwater);
                }else{
                    BigDecimal minrz=frsvr[insertlist.get(0)],maxrz=frsvr[insertlist.get(0)],sumrz=frsvr[insertlist.get(0)];
                    Date mindate=new Date(nowtm.getTime()-(60-(insertlist.get(0)+1)*5)*60*1000),maxdate=new Date(nowtm.getTime()-(60-(insertlist.get(0)+1)*5)*60*1000);
                    for(int h=0;h<insertlist.size();h++){
                        int interval=insertlist.get(h);
                        BigDecimal rz=frsvr[interval];
                        StRsvrR hiswater=new StRsvrR();
                        hiswater.setStcd(stcd);
                        hiswater.setRz(rz);
                        hiswater.setTm(new Date(nowtm.getTime()-(60-(interval+1)*5)*60*1000));
                        BigDecimal cv;
                        if(interval==0){
                            BigDecimal lastrz=waterARainDao.getRzByhistory(stcd,ltm);
                            cv=new BigDecimal(rz.doubleValue()-lastrz.doubleValue());
                        }else{
                            BigDecimal lastrz=frsvr[interval-1];
                            cv=new BigDecimal(rz.doubleValue()-lastrz.doubleValue());
                            sumrz=new BigDecimal(sumrz.doubleValue()+frsvr[interval].doubleValue());
                            if(minrz.doubleValue()>frsvr[interval].doubleValue()){
                                minrz=frsvr[interval];
                                mindate=hiswater.getTm();
                                if(interval==frsvr.length-1){
                                    mindate=new Date(nowtm.getTime()-1000);
                                }
                            }
                            if(maxrz.doubleValue()<frsvr[interval].doubleValue()){
                                maxrz=frsvr[interval];
                                maxdate=hiswater.getTm();
                                if(interval==frsvr.length-1){
                                    maxdate=new Date(nowtm.getTime()-1000);
                                }
                            }
                        }
                        hiswater.setCv(cv);
                        if(cv.doubleValue()==0){
                            hiswater.setRwptn('6');
                        }else if(cv.doubleValue()>0){
                            hiswater.setRwptn('5');
                        }else{
                            hiswater.setRwptn('4');
                        }
                        hiswater.setJssign("0");
                        hislist.add(hiswater);
                    }
                    BigDecimal avgrz=new BigDecimal(sumrz.doubleValue()/insertlist.size());
                    //小时水位采集
                    hourwater.setAddsign(1);
                    hourwater.setRz(avgrz);
                    if(param.getHminwl().doubleValue()>minrz.doubleValue()){
                        hourwater.setMinrz(minrz);
                        hourwater.setMindate(mindate);
                    }
                    hourwater.setDate(date);
                    hourwater.setHour(hour);
                    hourwater.setStcd(stcd);
                    //日水位采集
                    daywater.setDate(date);
                    daywater.setStcd(stcd);
                    daywater.setRz(avgrz);
                    daywater.setAddsign(1);
                    if(param.getDminwl().doubleValue()>minrz.doubleValue()){
                        daywater.setMinrz(minrz);
                        daywater.setMindate(mindate);
                    }
                    //月水位采集
                    monwater.setYear(year);
                    monwater.setMon(mon);
                    monwater.setStcd(stcd);
                    monwater.setRz(avgrz);
                    monwater.setAddsign(1);
                    if(param.getMminwl().doubleValue()>minrz.doubleValue()){
                        monwater.setMinrz(minrz);
                        monwater.setMindate(mindate);
                    }
                    //实时水位采集（当前实时水位与数据库中实时水位时间一致）
                    if(Integer.parseInt(jbjg[jbjg.length-1],16)==12){
                        realwater.setJssign("0");
                        realwater.setStcd(stcd);
                        waterARainDao.insertHourWater(realwater);
                        waterARainDao.insertHourWater(null,hislist,hourwater,daywater,monwater,null);
                    }else{
                        //实时水位采集
                        realwater=hislist.get(hislist.size()-1);
                        BigDecimal xxwater;
                        if (mon >= 7 && mon <= 9) {
                            xxwater = param.getFwl79();
                        } else {
                            xxwater = param.getFwl();
                        }
                        int alarm = setRealAlarm(realwater, xxwater, param);
                        realwater.setAlarm(alarm);
                        //站点预警信息
                        if(param.getAlarm()>alarm){
                            alarminfo.setStcd(stcd);
                            alarminfo.setSttp("RR");
                            alarminfo.setAlarm(alarm);
                            alarminfo.setTm(realwater.getTm());
                            switch (alarm){
                                case 1:alarminfo.setMv(new BigDecimal(realwater.getRz().doubleValue()-param.getXhwl().doubleValue()));
                                       alarminfo.setContent("超校核水位"+alarminfo.getMv()+"m");
                                       alarminfo.setAlarmv(param.getXhwl());
                                    break;
                                case 2:alarminfo.setMv(new BigDecimal(realwater.getRz().doubleValue()-param.getZcwl().doubleValue()));
                                       alarminfo.setContent("超正常蓄水位"+alarminfo.getMv()+"m");
                                       alarminfo.setAlarmv(param.getZcwl());
                                    break;
                                case 3:alarminfo.setMv(new BigDecimal(realwater.getRz().doubleValue()-xxwater.doubleValue()));
                                       alarminfo.setContent("超汛限水位"+alarminfo.getMv()+"m");
                                       alarminfo.setAlarmv(xxwater);
                                    break;
                            }
                        }
                        waterARainDao.insertHourWater(realwater,hislist,hourwater,daywater,monwater,alarminfo);
                    }
                }

            }
        } catch (ParseException e) {
            logger.error("采集日期转换错误",e);
        }
    }

    private int setRealAlarm(StRsvrR realwater, BigDecimal xxwater, WaterParam param) {
        int alarm = 4;
        if (realwater.getRz().doubleValue() >= xxwater.doubleValue() && realwater.getRz().doubleValue() < param.getZcwl().doubleValue()) {
            alarm = 3;
        } else if (realwater.getRz().doubleValue() >= param.getZcwl().doubleValue() && realwater.getRz().doubleValue() <= param.getXhwl().doubleValue()) {
            alarm = 2;
        } else if (realwater.getRz().doubleValue() > param.getXhwl().doubleValue()) {
            alarm = 1;
        }
        return alarm;
    }
}
