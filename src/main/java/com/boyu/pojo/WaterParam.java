package com.boyu.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 水位采集前需要的参数
 */
public class WaterParam {
    private Date lastdate;     //上一条记录的日期
    private  BigDecimal lastrz;//上一条记录的水位
    private String jssign;     //加时报标识
    private BigDecimal fwl;    //4-6月汛限水位
    private BigDecimal fwl79;  //7-9月汛限水位
    private BigDecimal zcwl;   //正常蓄水位
    private BigDecimal xhwl;   //校核水位
    private BigDecimal hmaxwl; //小时最高水位
    private BigDecimal hminwl; //小时最低水位
    private BigDecimal hrz;    //小时平均水位
    private int hmemo;         //小时水位操作次数
    private BigDecimal dmaxwl; //日最高水位
    private BigDecimal dminwl; //日最低水位
    private BigDecimal drz;    //日平均水位
    private int dmemo;         //日水位操作次数
    private BigDecimal mmaxwl; //月最高水位
    private BigDecimal mminwl; //月最低水位
    private BigDecimal mrz;    //月平均水位
    private int mmemo;         //月水位操作次数
    private Integer alarm;     //预警等级

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

    public BigDecimal getLastrz() {
        return lastrz;
    }

    public void setLastrz(BigDecimal lastrz) {
        this.lastrz = lastrz;
    }

    public Date getLastdate() {
        return lastdate;
    }

    public void setLastdate(Date lastdate) {
        this.lastdate = lastdate;
    }

    public Integer getAlarm() {
        return alarm;
    }

    public void setAlarm(Integer alarm) {
        this.alarm = alarm;
    }

    public BigDecimal getHrz() {
        return hrz;
    }

    public void setHrz(BigDecimal hrz) {
        this.hrz = hrz;
    }

    public int getHmemo() {
        return hmemo;
    }

    public void setHmemo(int hmemo) {
        this.hmemo = hmemo;
    }

    public BigDecimal getDrz() {
        return drz;
    }

    public void setDrz(BigDecimal drz) {
        this.drz = drz;
    }

    public int getDmemo() {
        return dmemo;
    }

    public void setDmemo(int dmemo) {
        this.dmemo = dmemo;
    }

    public BigDecimal getMrz() {
        return mrz;
    }

    public void setMrz(BigDecimal mrz) {
        this.mrz = mrz;
    }

    public int getMmemo() {
        return mmemo;
    }

    public void setMmemo(int mmemo) {
        this.mmemo = mmemo;
    }
}
