package com.boyu.dboper;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*
Druid连接池的工具类
 */
public class DruidUtils {
    static Logger logger = Logger.getLogger(DruidUtils.class.getName());
    //定义成员变量 DataSource
    private static DataSource ds;
    static {
        //1.加载配置文件
        Properties pro = new Properties();
        try {
            pro.load(DruidUtils.class.getClassLoader().getResourceAsStream("druid.properties"));
            //获取DataSource
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (IOException e) {
            logger.error("数据库连接失败!", e);
        } catch (Exception e) {
            logger.error("配置文件加载失败!", e);
        }
    }
    //获取连接
    public static Connection getConnection(){
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            logger.error("数据库连接失败!", e);
        }
        return null;
    }
    //释放资源
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
    //获取连接池
    public static DataSource getDataSource(){
        return ds;
    }
}
