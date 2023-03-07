package com.boyu.dao;

import com.boyu.dboper.C3p0Utils;
import com.boyu.pojo.WrpSpgSppr;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 插入大坝安全监测数据
 */
public class DamSafeDao {
    static Logger logger = Logger.getLogger(DamSafeDao.class.getName());

    /**
     * 插入渗压监测数据
     * @param spprlist
     */
    public void insertSpprInfo(String stcd,List<WrpSpgSppr> spprlist){
        int count=spprlist.size();
        Connection conn = C3p0Utils.getConnection();
        PreparedStatement pstm;
        try {
            conn.setAutoCommit(false);
            String sql_his="insert into WRP_SPG_SPPR(MPCD,MSTM,SPPRWM) values(?,?,?)";
            pstm=conn.prepareStatement(sql_his);
            for(int i=0;i<spprlist.size();i++){
                WrpSpgSppr sppr=spprlist.get(i);
                pstm.setString(1,sppr.getMpcd());
                pstm.setDate(2,new java.sql.Date(sppr.getMstm().getTime()));
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
                pstm.setDate(1,new java.sql.Date(sppr.getMstm().getTime()));
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
            C3p0Utils.closeAll(conn,pstm,null);
        } catch (SQLException e) {
            logger.error(stcd+"渗压水位采集失败！",e);
        }
    }
}
