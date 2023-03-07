package com.boyu.pojo;

import java.math.BigDecimal;
import java.util.Date;
//实时水位采集
public class StRsvrR {
    private Integer id;        //主键ID
    private String stcd;       //站点编号
    private Date tm;           //采集时间  yyyy-MM-dd HH:mm:ss
    private BigDecimal rz;     //水位
    private BigDecimal w;      //库容  小时库容，日库容，月库容
    private BigDecimal cv;     //水位差
    private char rwptn;      //水势
    private Date date;         //采集日期  yyyy-MM-dd
    private int hour;          //小时
    private int year;          //年
    private int mon;           //月
    private BigDecimal minrz;  //最小水位
    private BigDecimal maxrz;  //最大水位
    private Date mindate;      //最小水位日期
    private Date maxdate;      //最大水位日期
    private int addsign;       //insert(0) or update(1)
    private String jssign;     //加时报  时间间隔
    private int alarm;         //实时水位预警等级

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

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    public char getRwptn() {
        return rwptn;
    }

    public void setRwptn(char rwptn) {
        this.rwptn = rwptn;
    }

    public BigDecimal getRz() {
        return rz;
    }

    public void setRz(BigDecimal rz) {
        this.rz = rz;
    }

    public BigDecimal getW() {
        return w;
    }

    public void setW(BigDecimal w) {
        this.w = w;
    }

    public BigDecimal getCv() {
        return cv;
    }

    public void setCv(BigDecimal cv) {
        this.cv = cv;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMon() {
        return mon;
    }

    public void setMon(int mon) {
        this.mon = mon;
    }

    public BigDecimal getMinrz() {
        return minrz;
    }

    public void setMinrz(BigDecimal minrz) {
        this.minrz = minrz;
    }

    public BigDecimal getMaxrz() {
        return maxrz;
    }

    public void setMaxrz(BigDecimal maxrz) {
        this.maxrz = maxrz;
    }

    public Date getMindate() {
        return mindate;
    }

    public void setMindate(Date mindate) {
        this.mindate = mindate;
    }

    public Date getMaxdate() {
        return maxdate;
    }

    public void setMaxdate(Date maxdate) {
        this.maxdate = maxdate;
    }

    public int getAddsign() {
        return addsign;
    }

    public void setAddsign(int addsign) {
        this.addsign = addsign;
    }

    public String getJssign() {
        return jssign;
    }

    public void setJssign(String jssign) {
        this.jssign = jssign;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }
}
