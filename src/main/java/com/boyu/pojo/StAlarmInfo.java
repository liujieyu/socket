package com.boyu.pojo;

import java.math.BigDecimal;
import java.util.Date;

//站点预警信息
public class StAlarmInfo {
    private Integer id;        //主键ID
    private String stcd;       //站点编号
    private String sttp;       //站类
    private Integer alarm;     //预警等级 1 2 3 4
    private Date tm;           //预警日期
    private String content;    //预警内容
    private BigDecimal mv;     //水位
    private BigDecimal alarmv;  //预警水位

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getSttp() {
        return sttp;
    }

    public void setSttp(String sttp) {
        this.sttp = sttp;
    }

    public Integer getAlarm() {
        return alarm;
    }

    public void setAlarm(Integer alarm) {
        this.alarm = alarm;
    }

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BigDecimal getMv() {
        return mv;
    }

    public void setMv(BigDecimal mv) {
        this.mv = mv;
    }

    public BigDecimal getAlarmv() {
        return alarmv;
    }

    public void setAlarmv(BigDecimal alarmv) {
        this.alarmv = alarmv;
    }
}
