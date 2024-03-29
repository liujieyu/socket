package com.boyu.dao;

import com.boyu.dboper.C3p0Utils;
import com.boyu.dboper.DruidUtils;
import com.boyu.pojo.StStationStatus;
import com.boyu.pojo.WrpDfrSrhrds;
import com.boyu.pojo.WrpSpgSppr;
import com.boyu.pojo.WrpSpgSpprl;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 插入大坝安全监测数据
 */
public class DamSafeDao {
    static Logger logger = Logger.getLogger(DamSafeDao.class.getName());

    /**
     * 大坝安全监测 该时间是否已插入了数据
     * @param mstm
     * @return
     */
    public Integer getSpprAllCount(Date mstm,List<String> spprlist,List<String> sllist,List<String> wylist){
        int count=0;
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        ResultSet rs;
        int paramcount=0;
        String sql_query="";
           if(spprlist.size()>0){
               String spprcds="";
               for(int i=0;i<spprlist.size();i++){
                   spprcds+="'"+spprlist.get(i)+"'";
                   if(i<spprlist.size()-1){
                       spprcds+=",";
                   }
               }
               sql_query+="select count(*) from WRP_SPG_SPPR1 where MSTM=? and MPCD in("+spprcds+")";
               paramcount++;
           }
           if(sllist.size()>0){
               if(spprlist.size()>0){
                   sql_query+=" union all ";
               }
               String sllcds="";
               for(int i=0;i<sllist.size();i++){
                   sllcds+="'"+sllist.get(i)+"'";
                   if(i<sllist.size()-1){
                       sllcds+=",";
                   }
               }
               sql_query+="select count(*) from WRP_SPG_SPPR_L1 where MSTM=? and MPCD in("+sllcds+")";
               paramcount++;
           }
           if(wylist.size()>0){
               if(sllist.size()>0){
                   sql_query+=" union all ";
               }
               String wycds="";
               for(int i=0;i<wylist.size();i++){
                   wycds+="'"+wylist.get(i)+"'";
                   if(i<wylist.size()-1){
                       wycds+=",";
                   }
               }
               sql_query+="select count(*) from WRP_DFR_SRHRDS1 where MSTM=? and MPCD in("+wycds+")";
               paramcount++;
           }
        try {
            pstm=conn.prepareStatement(sql_query);
            for(int i=1;i<=paramcount;i++){
                pstm.setTimestamp(i,new java.sql.Timestamp(mstm.getTime()));
            }
            rs=pstm.executeQuery();
            while (rs.next()){
                count+=rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("查询大坝安全监测记录数失败！",e);
        }
        return count;
    }

    /**
     * 插入渗压监测数据
     * @param spprlist
     */
    public void insertSpprInfo(String stcd,List<WrpSpgSppr> spprlist){
        int count=spprlist.size();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql_his="insert into WRP_SPG_SPPR(MPCD,MSTM,SPPRWM) values(?,?,?)";
            pstm=conn.prepareStatement(sql_his);
            for(int i=0;i<spprlist.size();i++){
                WrpSpgSppr sppr=spprlist.get(i);
                pstm.setString(1,sppr.getMpcd());
                pstm.setTimestamp(2,new java.sql.Timestamp(sppr.getMstm().getTime()));
                pstm.setBigDecimal(3,sppr.getSpprwm());
                if(count>1){
                    pstm.addBatch();
                }
            }
            if(count>1){
                pstm.executeBatch();
                pstm.clearBatch();
            }else{
                pstm.executeUpdate();
            }
            String sql_real="update WRP_SPG_SPPR1 set MSTM=?,SPPRWM=? where MPCD=?";
            pstm=conn.prepareStatement(sql_real);
            for(int i=0;i<spprlist.size();i++){
                WrpSpgSppr sppr=spprlist.get(i);
                pstm.setTimestamp(1,new java.sql.Timestamp(sppr.getMstm().getTime()));
                pstm.setBigDecimal(2,sppr.getSpprwm());
                pstm.setString(3,sppr.getMpcd());
                if(count>1){
                    pstm.addBatch();
                }
            }
            if(count>1){
                pstm.executeBatch();
                pstm.clearBatch();
            }else{
                pstm.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(stcd+"渗压水位采集失败！",e);
        }
    }

    /**
     * 插入渗流量监测数据
     * @param stcd
     * @param spprllist
     */
    public void insertSpprLInfo(String stcd, List<WrpSpgSpprl> spprllist){
        int count=spprllist.size();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql_his="insert into WRP_SPG_SPPR_L(MPCD,MSTM,SPPRWL) values(?,?,?)";
            pstm=conn.prepareStatement(sql_his);
            for(int i=0;i<count;i++){
                WrpSpgSpprl spprl=spprllist.get(i);
                pstm.setString(1,spprl.getMpcd());
                pstm.setTimestamp(2,new java.sql.Timestamp(spprl.getMstm().getTime()));
                pstm.setBigDecimal(3,spprl.getSpprwl());
                if(count>1){
                    pstm.addBatch();
                }
            }
            if(count>1){
                pstm.executeBatch();
                pstm.clearBatch();
            }else{
                pstm.executeUpdate();
            }
            String sql_real="update WRP_SPG_SPPR_L1 set MSTM=?,SPPRWL=? where MPCD=?";
            pstm=conn.prepareStatement(sql_real);
            for(int i=0;i<count;i++){
                WrpSpgSpprl spprl=spprllist.get(i);
                pstm.setTimestamp(1,new java.sql.Timestamp(spprl.getMstm().getTime()));
                pstm.setBigDecimal(2,spprl.getSpprwl());
                pstm.setString(3,spprl.getMpcd());
                if(count>1){
                    pstm.addBatch();
                }
            }
            if(count>1){
                pstm.executeBatch();
                pstm.clearBatch();
            }else{
                pstm.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(stcd+"渗流量采集失败！",e);
        }
    }

    /**
     * 表面变形监测数据插入
     * @param stcd
     * @param hrdslist
     */
    public void insertSrhvrdsInfo(String stcd, List<WrpDfrSrhrds> hrdslist){
        int count=hrdslist.size();
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql_hish="insert into WRP_DFR_SRHRDS(MPCD,MSTM,XHRDS,YHRDS,ESLG,NRLT)values(?,?,?,?,?,?)";
            pstm=conn.prepareStatement(sql_hish);
            for(int i=0;i<count;i++){
                WrpDfrSrhrds hrds=hrdslist.get(i);
                pstm.setString(1,hrds.getMpcd());
                pstm.setTimestamp(2,new java.sql.Timestamp(hrds.getMstm().getTime()));
                pstm.setBigDecimal(3,hrds.getXhrds());
                pstm.setBigDecimal(4,hrds.getYhrds());
                pstm.setBigDecimal(5,hrds.getEslg());
                pstm.setBigDecimal(6,hrds.getNrlt());
                if(count>1){
                    pstm.addBatch();
                }
            }
            if(count>1){
                pstm.executeBatch();
                pstm.clearBatch();
            }else{
                pstm.executeUpdate();
            }
            String sql_hisv="insert into WRP_DFR_SRVRDS(MPCD,MSTM,VRDS,INEL)values(?,?,?,?)";
            pstm=conn.prepareStatement(sql_hisv);
            for(int i=0;i<count;i++){
                WrpDfrSrhrds hrds=hrdslist.get(i);
                pstm.setString(1,hrds.getMpcd());
                pstm.setTimestamp(2,new java.sql.Timestamp(hrds.getMstm().getTime()));
                pstm.setBigDecimal(3,hrds.getVrds());
                pstm.setBigDecimal(4,hrds.getInel());
                if(count>1){
                    pstm.addBatch();
                }
            }
            if(count>1){
                pstm.executeBatch();
                pstm.clearBatch();
            }else{
                pstm.executeUpdate();
            }
            String sql_realh="update WRP_DFR_SRHRDS1 set MSTM=?,XHRDS=?,YHRDS=?,ESLG=?,NRLT=? where MPCD=?";
            pstm=conn.prepareStatement(sql_realh);
            for(int i=0;i<count;i++) {
                WrpDfrSrhrds hrds = hrdslist.get(i);
                pstm.setTimestamp(1,new java.sql.Timestamp(hrds.getMstm().getTime()));
                pstm.setBigDecimal(2,hrds.getXhrds());
                pstm.setBigDecimal(3,hrds.getYhrds());
                pstm.setBigDecimal(4,hrds.getEslg());
                pstm.setBigDecimal(5,hrds.getNrlt());
                pstm.setString(6,hrds.getMpcd());
                if(count>1){
                    pstm.addBatch();
                }
            }
            if(count>1){
                pstm.executeBatch();
                pstm.clearBatch();
            }else{
                pstm.executeUpdate();
            }
            String sql_realv="update WRP_DFR_SRVRDS1 set MSTM=?,VRDS=?,INEL=? where MPCD=?";
            pstm=conn.prepareStatement(sql_realv);
            for(int i=0;i<count;i++) {
                WrpDfrSrhrds hrds = hrdslist.get(i);
                pstm.setTimestamp(1, new java.sql.Timestamp(hrds.getMstm().getTime()));
                pstm.setBigDecimal(2,hrds.getVrds());
                pstm.setBigDecimal(3,hrds.getInel());
                pstm.setString(4,hrds.getMpcd());
                if(count>1){
                    pstm.addBatch();
                }
            }
            if(count>1){
                pstm.executeBatch();
                pstm.clearBatch();
            }else{
                pstm.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(stcd+"表面变形位移数据采集失败！",e);
        }
    }
    //判断当前时间的运行工况监测数据是否存在（防治重复插入）
    public Integer getstatuscount(String stcd, Date tm){
        Integer count=0;
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        ResultSet rs;
        String sql="select count(*) from ST_StationStatus_R where STCD=? and TM=?";
        try {
            pstm=conn.prepareStatement(sql);
            pstm.setString(1,stcd);
            pstm.setTimestamp(2,new java.sql.Timestamp(tm.getTime()));
            rs=pstm.executeQuery();
            if(rs.next()){
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error(stcd+"运行工况数据获取失败！",e);
        }
        return count;
    }
    //运行工况数据采集(小时报)
    public void insertStatusInfo(StStationStatus status){
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql_his="insert into ST_StationStatus_H(STCD,TM,VOLTP,VOL,CS,RFT,MMCSQ,MSCSQ)values(?,?,?,?,?,?,?,?)";
            pstm=conn.prepareStatement(sql_his);
            pstm.setString(1,status.getStcd());
            pstm.setTimestamp(2,new java.sql.Timestamp(status.getTm().getTime()));
            pstm.setInt(3,1);
            pstm.setBigDecimal(4,status.getVol());
            pstm.setInt(5,status.getCs());
            pstm.setInt(6,status.getRft());
            pstm.setString(7,status.getMmcsq());
            pstm.setString(8,status.getMscsq());
            pstm.executeUpdate();
            String sql_real="update ST_StationStatus_R set TM=?,VOLTYPE=?,VOL=?,CS=?,RFT=?,MMCSQ=?,MSCSQ=? where STCD=?";
            pstm=conn.prepareStatement(sql_real);
            pstm.setTimestamp(1,new java.sql.Timestamp(status.getTm().getTime()));
            pstm.setInt(2,status.getVoltype());
            pstm.setBigDecimal(3,status.getVol());
            pstm.setInt(4,status.getCs());
            pstm.setInt(5,status.getRft());
            pstm.setString(6,status.getMmcsq());
            pstm.setString(7,status.getMscsq());
            pstm.setString(8,status.getStcd());
            pstm.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(status.getStcd()+"运行工况数据小时报采集失败！",e);
        }
    }
    //运行工况数据采集(加报)
    public void insertStatusInfoByAdd(StStationStatus status){
        Connection conn = DruidUtils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql_real="update ST_StationStatus_R set TM=?,VOLTYPE=?,VOL=?,CS=?,RFT=? where STCD=?";
            pstm=conn.prepareStatement(sql_real);
            pstm.setTimestamp(1,new java.sql.Timestamp(status.getTm().getTime()));
            pstm.setInt(2,status.getVoltype());
            pstm.setBigDecimal(3,status.getVol());
            pstm.setInt(4,status.getCs());
            pstm.setInt(5,status.getRft());
            pstm.setString(6,status.getStcd());
            pstm.executeUpdate();
            String sql_his="insert into ST_StationStatus_H(STCD,TM,VOLTP,VOL,CS,RFT,MMCSQ,MSCSQ) select STCD,?,?,?,?,?,MMCSQ,MSCSQ from ST_StationStatus_R where STCD=?";
            pstm=conn.prepareStatement(sql_his);
            pstm.setTimestamp(1,new java.sql.Timestamp(status.getTm().getTime()));
            pstm.setInt(2,1);
            pstm.setBigDecimal(3,status.getVol());
            pstm.setInt(4,status.getCs());
            pstm.setInt(5,status.getRft());
            pstm.setString(6,status.getStcd());
            pstm.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            DruidUtils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(status.getStcd()+"运行工况数据加报采集失败！",e);
        }
    }
}
