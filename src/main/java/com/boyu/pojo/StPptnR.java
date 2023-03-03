package com.boyu.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实时雨量采集
 */
public class StPptnR {
    private Integer id;       //主键ID
    private String stcd;      //测站编码
    private Date tm;          //采集时间
    private BigDecimal drp;   //时段降水量
    private BigDecimal intv;  //时段长
    private BigDecimal pdr;   //降水历时
    private BigDecimal dyp;   //日降水量
    private BigDecimal total; //累计降雨量
    private String jssign;   //是否加时报 0 小时报  1~12 1个小时内加时报次数

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

    public BigDecimal getDrp() {
        return drp;
    }

    public void setDrp(BigDecimal drp) {
        this.drp = drp;
    }

    public BigDecimal getIntv() {
        return intv;
    }

    public void setIntv(BigDecimal intv) {
        this.intv = intv;
    }

    public BigDecimal getPdr() {
        return pdr;
    }

    public void setPdr(BigDecimal pdr) {
        this.pdr = pdr;
    }

    public BigDecimal getDyp() {
        return dyp;
    }

    public void setDyp(BigDecimal dyp) {
        this.dyp = dyp;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getJssign() {
        return jssign;
    }

    public void setJssign(String jssign) {
        this.jssign = jssign;
    }
}
