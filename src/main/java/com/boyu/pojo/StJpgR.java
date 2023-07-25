package com.boyu.pojo;

import java.util.Date;

/**
 * 图像信息
 */
public class StJpgR {
    private String stcd;      //测站编码
    private Date tm;          //发报时间
    private String Save_Path; //图像获取路径

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

    public String getSave_Path() {
        return Save_Path;
    }

    public void setSave_Path(String save_Path) {
        Save_Path = save_Path;
    }
}
