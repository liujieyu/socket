package com.boyu.dao;

import com.boyu.dboper.C3p0Utils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StRsvrRDao {
    static Logger logger = Logger.getLogger(StRsvrRDao.class.getName());
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
