package com.boyu.dao;

import com.boyu.dboper.C3p0Utils;
import com.boyu.pojo.StPptnR;
import com.boyu.pojo.WaterParam;
import org.apache.log4j.Logger;

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
        Connection conn = C3p0Utils.getConnection();
        PreparedStatement pstm;
        ResultSet res;
        String sql_find = "select NEXT VALUE FOR dbo.sequence_serialnum";
        try {
            pstm = conn.prepareStatement(sql_find);
            res = pstm.executeQuery();
            if (res.next()) {
                serialnumber=res.getInt(1);
            }
            C3p0Utils.closeAll(conn,pstm,res);
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
        Connection conn = C3p0Utils.getConnection();
        PreparedStatement pstm;
        ResultSet res;
        String sql_find = "select TM,TOTAL,JSSIGN from ST_PPTN_R1 where STCD=?";
        try {
            pstm=conn.prepareStatement(sql_find);
            pstm.setString(1,stcd);
            res=pstm.executeQuery();
            if(res.next()){
                rain.setTm(res.getDate(1));
                rain.setTotal(res.getBigDecimal(2));
                rain.setJssign(res.getString(3));
            }
            C3p0Utils.closeAll(conn,pstm,res);
        } catch (SQLException e) {
            logger.error(stcd+":获取实时雨情信息失败",e);
        }
        return rain;
    }

    /**
     * 插入小时报(加报)雨量采集数据
     */
    public void insertHourRain(StPptnR realinfo, List<StPptnR>hisinfo,List<StPptnR> upinfo){
        Connection conn = C3p0Utils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql_real="update ST_PPTN_R1 set TM=?,DRP=?,PDR=?,DYP=?,TOTAL=?,INTV=?,JSSIGN=? where STCD=?";
            pstm=conn.prepareStatement(sql_real);
            pstm.setDate(1,new java.sql.Date(realinfo.getTm().getTime()));
            pstm.setBigDecimal(2,realinfo.getDrp());
            pstm.setBigDecimal(3,realinfo.getPdr());
            pstm.setBigDecimal(4,realinfo.getDyp());
            pstm.setBigDecimal(5,realinfo.getTotal());
            pstm.setBigDecimal(6,realinfo.getIntv());
            pstm.setString(7,realinfo.getJssign());
            pstm.setString(8,realinfo.getStcd());
            pstm.execute();
            if(upinfo!=null && upinfo.size()>0){
                String sql_up="update ST_PPTN_R set DRP=? where STCD=? and TM=?";
                pstm=conn.prepareStatement(sql_up);
                for(int j=0;j<upinfo.size();j++){
                    StPptnR uprain=upinfo.get(j);
                    pstm.setBigDecimal(1,uprain.getDrp());
                    pstm.setString(2,uprain.getStcd());
                    pstm.setDate(3,new java.sql.Date(uprain.getTm().getTime()));
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
                    pstm.setDate(2,new java.sql.Date(hisrain.getTm().getTime()));
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
            C3p0Utils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(realinfo.getStcd()+":实时雨量采集失败！",e);
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
        Connection conn = C3p0Utils.getConnection();
        PreparedStatement pstm;
        ResultSet res;

        try {
            String sql_warm="select r.JSSIGN,a.FWL,a.FWL79,a.ZCWL,a.XHWL from (select STCD,JSSIGN from ST_RSVR_R1 where STCD=?) r inner join (select * from ST_RSV_Alarm where STCD=?) a on r.STCD=a.STCD";
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
            }
            String sql_tzrz="select 'h' as bsign,Max_RZ,Min_RZ from ST_RSVR_H where STCD=? and DT=? and TM=? " +
                    "union all select 'd' as bsign,Max_RZ,Min_RZ from ST_RSVR_D where STCD=? and TM=? " +
                    "union all select 'm' as bsign,Max_RZ,Min_RZ from ST_RSVR_M where STCD=? and YR=? and MON=?";
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
                             break;
                    case 'd':param.setDmaxwl(res.getBigDecimal(2));
                             param.setDminwl(res.getBigDecimal(3));
                             break;
                    case 'm':param.setMmaxwl(res.getBigDecimal(2));
                             param.setMminwl(res.getBigDecimal(3));
                             break;
                }
            }
            C3p0Utils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(stcd+":实时水位参数获取失败！",e);
        }
        return param;
    }

    public void findStRsvrR() {
        Connection conn = C3p0Utils.getConnection();
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
}
