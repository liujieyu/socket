package com.boyu.dboper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import com.mchange.v2.c3p0.ComboPooledDataSource;
public class C3p0Utils {
    static Logger logger = Logger.getLogger(C3p0Utils.class.getName());
    //通过标识名来创建相应连接池
    static ComboPooledDataSource dataSource = new ComboPooledDataSource("sqlserver");
    /**
     * //从连接池中取用一个连接
     * @return
     */
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            logger.error("数据库连接失败!", e);

        }
        return null;
    }
    /**
     * 释放连接回连接池
     */
    public static void closeAll(Connection conn, PreparedStatement pst, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("数据集ResultSet关闭失败!", e);

            }
        }
        if (pst != null) {
            try {
                pst.close();
            } catch (SQLException e) {
                logger.error("预处理Preparestatement关闭失败!", e);

            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("数据库连接关闭失败!", e);

            }
        }
    }
}