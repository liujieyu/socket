package com.boyu.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 渗流监测数据采集
 */
public class WrpSpgSpprl {
    private Integer id;        //主键ID
    private String mpcd;       //测点编号
    private Date mstm;         //采集时间
    private BigDecimal spprwl; //渗流量
    private BigDecimal tm;     //渗流水温

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMpcd() {
        return mpcd;
    }

    public void setMpcd(String mpcd) {
        this.mpcd = mpcd;
    }

    public Date getMstm() {
        return mstm;
    }

    public void setMstm(Date mstm) {
        this.mstm = mstm;
    }

    public BigDecimal getSpprwl() {
        return spprwl;
    }

    public void setSpprwl(BigDecimal spprwl) {
        this.spprwl = spprwl;
    }

    public BigDecimal getTm() {
        return tm;
    }

    public void setTm(BigDecimal tm) {
        this.tm = tm;
    }
}
