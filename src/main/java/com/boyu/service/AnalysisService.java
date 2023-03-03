package com.boyu.service;

import com.boyu.dao.WaterARainDao;
import com.boyu.pojo.StPptnR;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取报文后，进行分析（雨水情和大坝安全监测数据入库）
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
                            for(int s=interval+1;s<=12;s++){
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
}
