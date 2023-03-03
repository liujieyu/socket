package com.boyu.pojo;

import java.math.BigDecimal;

/**
 * 水位采集前需要的参数
 */
public class WaterParam {
    private String jssign;     //加时报标识
    private BigDecimal fwl;    //4-6月汛限水位
    private BigDecimal fwl79;  //7-9月汛限水位
    private BigDecimal zcwl;   //正常蓄水位
    private BigDecimal xhwl;   //校核水位
    private BigDecimal hmaxwl; //小时最高水位
    private BigDecimal hminwl; //小时最低水位
    private BigDecimal dmaxwl; //日最高水位
    private BigDecimal dminwl; //日最低水位
    private BigDecimal mmaxwl; //月最高水位
    private BigDecimal mminwl; //月最低水位

    public String getJssign() {
        return jssign;
    }

    public void setJssign(String jssign) {
        this.jssign = jssign;
    }

    public BigDecimal getFwl() {
        return fwl;
    }

    public void setFwl(BigDecimal fwl) {
        this.fwl = fwl;
    }

    public BigDecimal getFwl79() {
        return fwl79;
    }

    public void setFwl79(BigDecimal fwl79) {
        this.fwl79 = fwl79;
    }

    public BigDecimal getZcwl() {
        return zcwl;
    }

    public void setZcwl(BigDecimal zcwl) {
        this.zcwl = zcwl;
    }

    public BigDecimal getXhwl() {
        return xhwl;
    }

    public void setXhwl(BigDecimal xhwl) {
        this.xhwl = xhwl;
    }

    public BigDecimal getHmaxwl() {
        return hmaxwl;
    }

    public void setHmaxwl(BigDecimal hmaxwl) {
        this.hmaxwl = hmaxwl;
    }

    public BigDecimal getHminwl() {
        return hminwl;
    }

    public void setHminwl(BigDecimal hminwl) {
        this.hminwl = hminwl;
    }

    public BigDecimal getDmaxwl() {
        return dmaxwl;
    }

    public void setDmaxwl(BigDecimal dmaxwl) {
        this.dmaxwl = dmaxwl;
    }

    public BigDecimal getDminwl() {
        return dminwl;
    }

    public void setDminwl(BigDecimal dminwl) {
        this.dminwl = dminwl;
    }

    public BigDecimal getMmaxwl() {
        return mmaxwl;
    }

    public void setMmaxwl(BigDecimal mmaxwl) {
        this.mmaxwl = mmaxwl;
    }

    public BigDecimal getMminwl() {
        return mminwl;
    }

    public void setMminwl(BigDecimal mminwl) {
        this.mminwl = mminwl;
    }
}
