package com.boyu.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 表面变形水平位移、垂直位移监测数据
 */
public class WrpDfrSrhrds {
    private Integer id;       //主键ID
    private String mpcd;      //测点编号
    private Date mstm;        //采集时间
    private BigDecimal xhrds; //X向水平位移
    private BigDecimal yhrds; //Y向水平位移
    private BigDecimal eslg;  //经度
    private BigDecimal nrlt;  //纬度
    private BigDecimal vrds;  //垂直位移
    private BigDecimal inel;  //测量高程

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

    public BigDecimal getXhrds() {
        return xhrds;
    }

    public void setXhrds(BigDecimal xhrds) {
        this.xhrds = xhrds;
    }

    public BigDecimal getYhrds() {
        return yhrds;
    }

    public void setYhrds(BigDecimal yhrds) {
        this.yhrds = yhrds;
    }

    public BigDecimal getEslg() {
        return eslg;
    }

    public void setEslg(BigDecimal eslg) {
        this.eslg = eslg;
    }

    public BigDecimal getNrlt() {
        return nrlt;
    }

    public void setNrlt(BigDecimal nrlt) {
        this.nrlt = nrlt;
    }

    public BigDecimal getVrds() {
        return vrds;
    }

    public void setVrds(BigDecimal vrds) {
        this.vrds = vrds;
    }

    public BigDecimal getInel() {
        return inel;
    }

    public void setInel(BigDecimal inel) {
        this.inel = inel;
    }
}
