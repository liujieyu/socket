package com.boyu.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 运行工况监测数据
 */
public class StStationStatus {
    private Integer id;           //主键ID
    private String stcd;          //站点编号
    private Date tm;              //采集时间
    private Integer voltype;      //正常电压范围类型
    private BigDecimal vol;       //电压
    private Integer cs;           //通讯是否正常 1正常  0不正常
    private Integer rft;          //故障标志 1正常 0不正常
    private String mmcsq;         //主信道强度
    private String mscsq;         //备用信道强度

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

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }

    public Integer getCs() {
        return cs;
    }

    public void setCs(Integer cs) {
        this.cs = cs;
    }

    public Integer getRft() {
        return rft;
    }

    public void setRft(Integer rft) {
        this.rft = rft;
    }

    public String getMmcsq() {
        return mmcsq;
    }

    public void setMmcsq(String mmcsq) {
        this.mmcsq = mmcsq;
    }

    public String getMscsq() {
        return mscsq;
    }

    public void setMscsq(String mscsq) {
        this.mscsq = mscsq;
    }

    public Integer getVoltype() {
        return voltype;
    }

    public void setVoltype(Integer voltype) {
        this.voltype = voltype;
    }
}
