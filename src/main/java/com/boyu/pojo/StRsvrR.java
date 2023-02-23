package com.boyu.pojo;

import java.util.Date;
//实时水位采集
public class StRsvrR {
    private Integer id;    //主键ID
    private String stnm;   //站点名称
    private String stcd;   //站点编号
    private Date tm;       //采集时间
    private Float rz;      //水位
    private Float w;       //库容
    private String rwptn;  //水势

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
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

    public Float getRz() {
        return rz;
    }

    public void setRz(Float rz) {
        this.rz = rz;
    }

    public Float getW() {
        return w;
    }

    public void setW(Float w) {
        this.w = w;
    }

    public String getRwptn() {
        return rwptn;
    }

    public void setRwptn(String rwptn) {
        this.rwptn = rwptn;
    }
}
