package com.boyu.dao;

import com.boyu.dboper.C3p0Utils;
import com.boyu.dboper.DruidUtils;
import com.boyu.pojo.StAlarmInfo;
import com.boyu.pojo.StPptnR;
import com.boyu.pojo.StRsvrR;
import com.boyu.pojo.WaterParam;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 插入水雨情监测数据
 */
public class WaterARainDao {
    static Logger logger = Logger.getLogger(WaterARainDao.class.getName());
    /**
     * 获取下行报文流水号
     * @return
     */
    public int getSerialnumber(){
        int serialnumber=1;
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        ResultSet res;
        String sql_find = "select NEXT VALUE FOR dbo.sequence_serialnum";
        try {
            pstm = conn.prepareStatement(sql_find);
            res = pstm.executeQuery();
            if (res.next()) {
                serialnumber=res.getInt(1);
            }
            DruidUtils.closeAll(conn,pstm,res);
        } catch (SQLException e) {
            logger.error("获取流水号失败",e);
        }
        return serialnumber;
    }

    /**
     * 获取实时雨情信息
     * @param stcd
     * @return
     */
    public StPptnR getRealRain(String stcd){
        StPptnR rain=new StPptnR();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        ResultSet res;
        String sql_find = "select TM,TOTAL,JSSIGN from ST_PPTN_R1 where STCD=?";
        try {
            pstm=conn.prepareStatement(sql_find);
            pstm.setString(1,stcd);
            res=pstm.executeQuery();
            if(res.next()){
                rain.setTm(res.getTimestamp(1));
                rain.setTotal(res.getBigDecimal(2));
                rain.setJssign(res.getString(3));
            }
            DruidUtils.closeAll(conn,pstm,res);
        } catch (SQLException e) {
            logger.error(stcd+":获取实时雨情信息失败",e);
        }
        return rain;
    }

    /**
     * 插入小时报雨量采集数据
     */
    public void insertHourRain(StPptnR realinfo, List<StPptnR>hisinfo,List<StPptnR> upinfo){
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            if(realinfo.getPdr().doubleValue()==0){
                String sql_real="update ST_PPTN_R1 set JSSIGN=? where STCD=?";
                pstm = conn.prepareStatement(sql_real);
                pstm.setString(1,realinfo.getJssign());
                pstm.setString(2,realinfo.getStcd());
                pstm.executeUpdate();
            }else {
                String sql_real = "update ST_PPTN_R1 set TM=?,DRP=?,PDR=?,DYP=?,TOTAL=?,INTV=?,JSSIGN=? where STCD=?";
                pstm = conn.prepareStatement(sql_real);
                pstm.setTimestamp(1, new java.sql.Timestamp(realinfo.getTm().getTime()));
                pstm.setBigDecimal(2, realinfo.getDrp());
                pstm.setBigDecimal(3, realinfo.getPdr());
                pstm.setBigDecimal(4, realinfo.getDyp());
                pstm.setBigDecimal(5, realinfo.getTotal());
                pstm.setBigDecimal(6, realinfo.getIntv());
                pstm.setString(7, realinfo.getJssign());
                pstm.setString(8, realinfo.getStcd());
                pstm.executeUpdate();
            }
            if(upinfo!=null && upinfo.size()>0){
                String sql_up="update ST_PPTN_R set DRP=? where STCD=? and TM=?";
                pstm=conn.prepareStatement(sql_up);
                for(int j=0;j<upinfo.size();j++){
                    StPptnR uprain=upinfo.get(j);
                    pstm.setBigDecimal(1,uprain.getDrp());
                    pstm.setString(2,uprain.getStcd());
                    pstm.setTimestamp(3,new java.sql.Timestamp(uprain.getTm().getTime()));
                    if(upinfo.size()>1){
                        pstm.addBatch();
                    }
                }
                if(upinfo.size()>1){
                    pstm.executeBatch();
                    pstm.clearBatch();
                }else{
                    pstm.executeUpdate();
                }
            }
            if(hisinfo!=null && hisinfo.size()>0){
                String sql_his="insert into ST_PPTN_R(STCD,TM,DRP,DYP)values(?,?,?,?)";
                pstm=conn.prepareStatement(sql_his);
                for(int i=0;i<hisinfo.size();i++){
                    StPptnR hisrain=hisinfo.get(i);
                    pstm.setString(1,hisrain.getStcd());
                    pstm.setTimestamp(2,new java.sql.Timestamp(hisrain.getTm().getTime()));
                    pstm.setBigDecimal(3,hisrain.getDrp());
                    pstm.setBigDecimal(4,hisrain.getDyp());
                    if(hisinfo.size()>1){
                        pstm.addBatch();
                    }
                }
                if(hisinfo.size()>1){
                    pstm.executeBatch();
                    pstm.clearBatch();
                }else{
                    pstm.executeUpdate();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(realinfo.getStcd()+":小时报实时雨量采集失败！",e);
        }
    }
    /**
     * 插入加报雨量采集数据
     */
    public void insertAddRain(StPptnR realinfo){
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql_real = "update ST_PPTN_R1 set TM=?,DRP=?,PDR=?,DYP=?,TOTAL=?,INTV=?,JSSIGN=JSSIGN+? where STCD=?";
            pstm = conn.prepareStatement(sql_real);
            pstm.setTimestamp(1, new java.sql.Timestamp(realinfo.getTm().getTime()));
            pstm.setBigDecimal(2, realinfo.getDrp());
            pstm.setBigDecimal(3, realinfo.getPdr());
            pstm.setBigDecimal(4, realinfo.getDyp());
            pstm.setBigDecimal(5, realinfo.getTotal());
            pstm.setBigDecimal(6, realinfo.getIntv());
            pstm.setString(7, realinfo.getJssign());
            pstm.setString(8, realinfo.getStcd());
            pstm.executeUpdate();
            String sql_his="insert into ST_PPTN_R(STCD,TM,DRP,DYP)values(?,?,?,?)";
            pstm=conn.prepareStatement(sql_his);
            pstm.setString(1,realinfo.getStcd());
            pstm.setTimestamp(2,new java.sql.Timestamp(realinfo.getTm().getTime()));
            pstm.setBigDecimal(3,realinfo.getDrp());
            pstm.setBigDecimal(4,realinfo.getDyp());
            pstm.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(realinfo.getStcd()+":加报实时雨量采集失败！",e);
        }
    }
    /**
     * 水位采集前，获取的必须参数
     * @param stcd
     * @param date
     * @param hour
     * @param year
     * @param mon
     * @return
     */
    public WaterParam getWaterParam(String stcd, Date date,int hour,int year,int mon){
        WaterParam param=new WaterParam();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        ResultSet res;
        try {
            String sql_warm="select r.JSSIGN,a.FWL,a.FWL79,a.ZCWL,a.XHWL,r.RZ,r.TM,r.ALARM from (select STCD,JSSIGN,RZ,TM,ALARM from ST_RSVR_R1 where STCD=?) r inner join (select * from ST_RSV_Alarm where STCD=?) a on r.STCD=a.STCD";
            pstm=conn.prepareStatement(sql_warm);
            pstm.setString(1,stcd);
            pstm.setString(2,stcd);
            res=pstm.executeQuery();
            while (res.next()){
                param.setJssign(res.getString(1));
                param.setFwl(res.getBigDecimal(2));
                param.setFwl79(res.getBigDecimal(3));
                param.setZcwl(res.getBigDecimal(4));
                param.setXhwl(res.getBigDecimal(5));
                param.setLastrz(res.getBigDecimal(6));
                param.setLastdate(res.getDate(7));
                param.setAlarm(res.getInt(8));
            }
            String sql_tzrz="select 'h' as bsign,Max_RZ,Min_RZ,RZ,MEMO from ST_RSVR_H where STCD=? and DT=? and TM=? " +
                    "union all select 'd' as bsign,Max_RZ,Min_RZ,RZ,MEMO from ST_RSVR_D where STCD=? and TM=? " +
                    "union all select 'm' as bsign,Max_RZ,Min_RZ,RZ,MEMO from ST_RSVR_M where STCD=? and YR=? and MON=?";
            pstm=conn.prepareStatement(sql_tzrz);
            pstm.setString(1,stcd);
            pstm.setDate(2,new java.sql.Date(date.getTime()));
            pstm.setInt(3,hour);
            pstm.setString(4,stcd);
            pstm.setDate(5,new java.sql.Date(date.getTime()));
            pstm.setString(6,stcd);
            pstm.setInt(7,year);
            pstm.setInt(8,mon);
            res=pstm.executeQuery();
            while (res.next()){
                switch (res.getString(1).charAt(0)){
                    case 'h':param.setHmaxwl(res.getBigDecimal(2));
                             param.setHminwl(res.getBigDecimal(3));
                             param.setHrz(res.getBigDecimal(4));
                             String hmemo=res.getString(5);
                             if(hmemo==null){
                                 param.setHmemo(0);
                             }else{
                                 param.setHmemo(Integer.valueOf(hmemo));
                             }
                             break;
                    case 'd':param.setDmaxwl(res.getBigDecimal(2));
                             param.setDminwl(res.getBigDecimal(3));
                             param.setDrz(res.getBigDecimal(4));
                             String dmemo=res.getString(5);
                             if(dmemo==null){
                                 param.setDmemo(0);
                             }else{
                                 param.setDmemo(Integer.valueOf(dmemo));
                             }
                             break;
                    case 'm':param.setMmaxwl(res.getBigDecimal(2));
                             param.setMminwl(res.getBigDecimal(3));
                             param.setMrz(res.getBigDecimal(4));
                             String mmemo=res.getString(5);
                             if(mmemo==null){
                                 param.setMmemo(0);
                             }else{
                                 param.setMmemo(Integer.valueOf(mmemo));
                             }
                             break;
                }
            }
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(stcd+":实时水位参数获取失败！",e);
        }
        return param;
    }
    //获取上小时历史表水位信息
    public BigDecimal getRzByhistory(String stcd,Date lasttm){
        Connection conn=DruidUtils.getConnection();
        PreparedStatement pstm;
        ResultSet res;
        BigDecimal rz=new BigDecimal(0);
        try {
            String find_sql="select RZ from ST_RSVR_R where TM=? and STCD=?";
            pstm=conn.prepareStatement(find_sql);
            pstm.setTimestamp(1,new java.sql.Timestamp(lasttm.getTime()));
            pstm.setString(2,stcd);
            res=pstm.executeQuery();
            if(res.next()){
                rz= res.getBigDecimal(1);
            }
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(stcd+":获取历史水位失败！",e);
        }
        return rz;
    }
    //小时报水位采集(无数据录入)
    public void insertHourWater(StRsvrR realwater){
        Connection conn=DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            String sql_real="update ST_RSVR_R1 set JSSIGN=? where STCD=?";
            pstm=conn.prepareStatement(sql_real);
            pstm.setString(1,realwater.getJssign());
            pstm.setString(2,realwater.getStcd());
            pstm.executeUpdate();
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(realwater.getStcd()+":小时报实时水位采集失败！",e);
        }
    }
    //小时报水位采集
    public void insertHourWater(StRsvrR realwater, List<StRsvrR> hiswater, StRsvrR hourwater, StRsvrR datewater, StRsvrR monwater, StAlarmInfo alarminfo){
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            //实时水位表
            if(realwater!=null) {
                String sql_real = "update ST_RSVR_R1 set TM=?,RZ=?,CV=?,W=dbo.FUNC_GETKR(?,?),RWPTN=?,JSSIGN=?,ALARM=? where STCD=?";
                pstm = conn.prepareStatement(sql_real);
                pstm.setTimestamp(1, new java.sql.Timestamp(realwater.getTm().getTime()));
                pstm.setBigDecimal(2, realwater.getRz());
                pstm.setBigDecimal(3, realwater.getCv());
                pstm.setBigDecimal(4, realwater.getRz());
                pstm.setString(5, realwater.getStcd());
                pstm.setString(6, realwater.getRwptn());
                pstm.setString(7, realwater.getJssign());
                pstm.setInt(8, realwater.getAlarm());
                pstm.setString(9, realwater.getStcd());
                pstm.executeUpdate();
            }
            //历史水位表
            if(hiswater!=null && hiswater.size()>0){
                String sql_his="insert into ST_RSVR_R(STCD,TM,RZ,CV,W,RWPTN) values(?,?,?,?,dbo.FUNC_GETKR(?,?),?)";
                pstm=conn.prepareStatement(sql_his);
                for(int i=0;i<hiswater.size();i++){
                    StRsvrR hisobj=hiswater.get(i);
                    pstm.setString(1,hisobj.getStcd());
                    pstm.setTimestamp(2,new java.sql.Timestamp(hisobj.getTm().getTime()));
                    pstm.setBigDecimal(3,hisobj.getRz());
                    pstm.setBigDecimal(4,hisobj.getCv());
                    pstm.setBigDecimal(5,hisobj.getRz());
                    pstm.setString(6,hisobj.getStcd());
                    pstm.setString(7,hisobj.getRwptn());
                    if(hiswater.size()>1){
                        pstm.addBatch();
                    }
                }
                if(hiswater.size()>1){
                    pstm.executeBatch();
                    pstm.clearBatch();
                }else{
                    pstm.executeUpdate();
                }
            }
            //小时水位表
            String sql_hwater="";
            //新增
            if(hourwater.getAddsign()==0){
                sql_hwater="insert into ST_RSVR_H(STCD,DT,TM,RZ,CV,Max_RZ,Max_TM,Min_RZ,Min_TM,HW,MEMO) values(?,?,?,?,?,?,?,?,?,dbo.FUNC_GETKR(?,?),?)";
                pstm=conn.prepareStatement(sql_hwater);
                pstm.setString(1,hourwater.getStcd());
                pstm.setDate(2,new java.sql.Date(hourwater.getDate().getTime()));
                pstm.setInt(3,hourwater.getHour());
                pstm.setBigDecimal(4,hourwater.getRz());
                pstm.setBigDecimal(5,hourwater.getCv());
                pstm.setBigDecimal(6,hourwater.getMaxrz());
                pstm.setTimestamp(7,new java.sql.Timestamp(hourwater.getMaxdate().getTime()));
                pstm.setBigDecimal(8,hourwater.getMinrz());
                pstm.setTimestamp(9,new java.sql.Timestamp(hourwater.getMindate().getTime()));
                pstm.setBigDecimal(10,hourwater.getRz());
                pstm.setString(11,hourwater.getStcd());
                pstm.setString(12,hourwater.getMemo());
                pstm.executeUpdate();
            }//修改
            else{
                StringBuffer sb_hwater = new StringBuffer("update ST_RSVR_H set RZ=?,CV=?");
                int maxsign=0,minsign=0;
                if(hourwater.getMaxrz()!=null){
                    sb_hwater.append(",Max_RZ=?,Max_TM=?");
                    maxsign+=2;
                }
                if(hourwater.getMinrz()!=null){
                    sb_hwater.append(",Min_RZ=?,Min_TM=?");
                    minsign+=2;
                }
                sb_hwater.append(",HW=dbo.FUNC_GETKR(?,?),MEMO=? where STCD=? and DT=? and TM=?");
                sql_hwater=sb_hwater.toString();
                pstm=conn.prepareStatement(sql_hwater);
                pstm.setBigDecimal(1,hourwater.getRz());
                pstm.setBigDecimal(2,hourwater.getCv());
                if(hourwater.getMaxrz()!=null){
                    pstm.setBigDecimal(2+maxsign-1,hourwater.getMaxrz());
                    pstm.setTimestamp(2+maxsign,new java.sql.Timestamp(hourwater.getMaxdate().getTime()));
                }
                if(hourwater.getMinrz()!=null){
                    pstm.setBigDecimal(2+maxsign+minsign-1,hourwater.getMinrz());
                    pstm.setTimestamp(2+maxsign+minsign,new java.sql.Timestamp(hourwater.getMindate().getTime()));
                }
                pstm.setBigDecimal(3+maxsign+minsign,hourwater.getRz());
                pstm.setString(4+maxsign+minsign,hourwater.getStcd());
                pstm.setString(5+maxsign+minsign,hourwater.getMemo());
                pstm.setString(6+maxsign+minsign,hourwater.getStcd());
                pstm.setDate(7+maxsign+minsign,new java.sql.Date(hourwater.getDate().getTime()));
                pstm.setInt(8+maxsign+minsign,hourwater.getHour());
                pstm.executeUpdate();
            }
            //日水位表
            String sql_dwater="";
            //新增
            if(datewater.getAddsign()==0){
                sql_dwater="insert into ST_RSVR_D(STCD,TM,RZ,Max_RZ,Max_TM,Min_RZ,Min_TM,DW,MEMO) values(?,?,?,?,?,?,?,dbo.FUNC_GETKR(?,?),?)";
                pstm=conn.prepareStatement(sql_dwater);
                pstm.setString(1,datewater.getStcd());
                pstm.setDate(2,new java.sql.Date(datewater.getDate().getTime()));
                pstm.setBigDecimal(3,datewater.getRz());
                pstm.setBigDecimal(4,datewater.getMaxrz());
                pstm.setTimestamp(5,new java.sql.Timestamp(datewater.getMaxdate().getTime()));
                pstm.setBigDecimal(6,datewater.getMinrz());
                pstm.setTimestamp(7,new java.sql.Timestamp(datewater.getMindate().getTime()));
                pstm.setBigDecimal(8,datewater.getRz());
                pstm.setString(9,datewater.getStcd());
                pstm.setString(10,datewater.getMemo());
                pstm.executeUpdate();
            }else{
                StringBuffer sb_dwater = new StringBuffer("update ST_RSVR_D set RZ=?");
                int maxsign=0,minsign=0;
                if(datewater.getMaxrz()!=null){
                    sb_dwater.append(",Max_RZ=?,Max_TM=?");
                    maxsign+=2;
                }
                if(datewater.getMinrz()!=null){
                    sb_dwater.append(",Min_RZ=?,Min_TM=?");
                    minsign+=2;
                }
                sb_dwater.append(",DW=dbo.FUNC_GETKR(?,?),MEMO=? where STCD=? and TM=?");
                sql_dwater=sb_dwater.toString();
                pstm=conn.prepareStatement(sql_dwater);
                pstm.setBigDecimal(1,datewater.getRz());
                if(datewater.getMaxrz()!=null){
                    pstm.setBigDecimal(1+maxsign-1,datewater.getMaxrz());
                    pstm.setTimestamp(1+maxsign,new java.sql.Timestamp(datewater.getMaxdate().getTime()));
                }
                if(datewater.getMinrz()!=null){
                    pstm.setBigDecimal(1+maxsign+minsign-1,datewater.getMinrz());
                    pstm.setTimestamp(1+maxsign+minsign,new java.sql.Timestamp(datewater.getMindate().getTime()));
                }
                pstm.setBigDecimal(2+maxsign+minsign,datewater.getRz());
                pstm.setString(3+maxsign+minsign,datewater.getStcd());
                pstm.setString(4+maxsign+minsign,datewater.getMemo());
                pstm.setString(5+maxsign+minsign,datewater.getStcd());
                pstm.setDate(6+maxsign+minsign,new java.sql.Date(datewater.getDate().getTime()));
                pstm.executeUpdate();
            }
            //月水位表
            String sql_mwater="";
            //新增
            if(monwater.getAddsign()==0){
                sql_mwater="insert into ST_RSVR_M(STCD,YR,MON,RZ,Max_RZ,Max_TM,Min_RZ,Min_TM,MW,MEMO)values(?,?,?,?,?,?,?,?,dbo.FUNC_GETKR(?,?),?)";
                pstm=conn.prepareStatement(sql_mwater);
                pstm.setString(1,monwater.getStcd());
                pstm.setInt(2,monwater.getYear());
                pstm.setInt(3,monwater.getMon());
                pstm.setBigDecimal(4,monwater.getRz());
                pstm.setBigDecimal(5,monwater.getMaxrz());
                pstm.setTimestamp(6,new java.sql.Timestamp(monwater.getMaxdate().getTime()));
                pstm.setBigDecimal(7,monwater.getMinrz());
                pstm.setTimestamp(8,new java.sql.Timestamp(monwater.getMindate().getTime()));
                pstm.setBigDecimal(9,monwater.getRz());
                pstm.setString(10,monwater.getStcd());
                pstm.setString(11,monwater.getMemo());
                pstm.executeUpdate();
            }//修改
            else{
                StringBuffer sb_mwater = new StringBuffer("update ST_RSVR_M set RZ=?");
                int maxsign=0,minsign=0;
                if(monwater.getMaxrz()!=null){
                    sb_mwater.append(",Max_RZ=?,Max_TM=?");
                    maxsign+=2;
                }
                if(monwater.getMinrz()!=null){
                    sb_mwater.append(",Min_RZ=?,Min_TM=?");
                    minsign+=2;
                }
                sb_mwater.append(",MW=dbo.FUNC_GETKR(?,?),MEMO=?  where STCD=? and YR=? and MON=?");
                sql_mwater=sb_mwater.toString();
                pstm=conn.prepareStatement(sql_mwater);
                pstm.setBigDecimal(1,monwater.getRz());
                if(monwater.getMaxrz()!=null){
                    pstm.setBigDecimal(1+maxsign-1,monwater.getMaxrz());
                    pstm.setTimestamp(1+maxsign,new java.sql.Timestamp(monwater.getMaxdate().getTime()));
                }
                if(monwater.getMinrz()!=null){
                    pstm.setBigDecimal(1+maxsign+minsign-1,monwater.getMinrz());
                    pstm.setTimestamp(1+maxsign+minsign,new java.sql.Timestamp(monwater.getMindate().getTime()));
                }
                pstm.setBigDecimal(2+maxsign+minsign,monwater.getRz());
                pstm.setString(3+maxsign+minsign,monwater.getStcd());
                pstm.setString(4+maxsign+minsign,monwater.getMemo());
                pstm.setString(5+maxsign+minsign,monwater.getStcd());
                pstm.setInt(6+maxsign+minsign,monwater.getYear());
                pstm.setInt(7+maxsign+minsign,monwater.getMon());
                pstm.executeUpdate();
            }
            //站点预警信息
            if(alarminfo!=null && alarminfo.getStcd()!=null){
                String alarm_sql="insert into ST_AlarmInfo(STCD,STTP,Alarm,TM,CONTENTS,MV,AlarmV)values(?,?,?,?,?,?,?)";
                pstm=conn.prepareStatement(alarm_sql);
                    pstm.setString(1, alarminfo.getStcd());
                    pstm.setString(2, alarminfo.getSttp());
                    pstm.setInt(3, alarminfo.getAlarm());
                    pstm.setTimestamp(4, new java.sql.Timestamp(alarminfo.getTm().getTime()));
                    pstm.setString(5, alarminfo.getContent());
                    pstm.setBigDecimal(6, alarminfo.getMv());
                    pstm.setBigDecimal(7, alarminfo.getAlarmv());
                    pstm.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(realwater.getStcd()+":小时报实时水位采集失败！",e);
        }

    }

    public void findStRsvrR() {
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        ResultSet res;

        String sql_find = "select STCD,TM,RZ,W from ST_RSVR_R1 ";

        try {
            pstm = conn.prepareStatement(sql_find);
            res = pstm.executeQuery();
            logger.info("查询实时水位成功!!");
            System.out.println("站点编码\t采集时间\t水位\t库容");
            while (res.next()) {
                System.out.println(res.getString("STCD") + "\t" + res.getString(2) + "\t" + res.getBigDecimal(3)+ "\t" + res.getBigDecimal(4));
            }
        } catch (SQLException e) {
            logger.error("实时水情查询失败！",e);
        }

    }

    public void insertSWKR(){
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql="insert into WRP_RSR_WLSTCPARRL(RSNM,RSCD,WL,STCP,AR,DTUPDT,MEMO)values(null,?,?,?,?,null,null)";
            pstm=conn.prepareStatement(sql);
            BigDecimal sw=new BigDecimal(20.8).setScale(1,BigDecimal.ROUND_HALF_UP);
            for(double i=0.0;i<19.7;i=i+0.1){
                BigDecimal jg=new BigDecimal(i).setScale(1,BigDecimal.ROUND_HALF_UP);
                BigDecimal cursw=sw.add(jg).setScale(1,BigDecimal.ROUND_HALF_UP);
                BigDecimal curkr=jg.multiply(new BigDecimal(8.0391*0.8)).setScale(4,BigDecimal.ROUND_HALF_UP);
                BigDecimal curmj=jg.multiply(new BigDecimal(0.19938*0.54)).setScale(3,BigDecimal.ROUND_HALF_UP);
                pstm.setString(1,"6060001501");
                pstm.setBigDecimal(2,cursw);
                pstm.setBigDecimal(3,curkr);
                pstm.setBigDecimal(4,curmj);
                pstm.addBatch();
            }
            pstm.executeBatch();
            pstm.clearBatch();
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
