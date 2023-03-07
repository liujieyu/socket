package com.boyu.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 渗压水位监测数据
 */
public class WrpSpgSppr {
    private Integer id;         //主键ID
    private String mpcd;        //测点编号
    private Date mstm;          //采集时间
    private BigDecimal spprwm;  //渗压水位
    private BigDecimal tm;      //温度

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

    public BigDecimal getSpprwm() {
        return spprwm;
    }

    public void setSpprwm(BigDecimal spprwm) {
        this.spprwm = spprwm;
    }

    public BigDecimal getTm() {
        return tm;
    }

    public void setTm(BigDecimal tm) {
        this.tm = tm;
    }
}
