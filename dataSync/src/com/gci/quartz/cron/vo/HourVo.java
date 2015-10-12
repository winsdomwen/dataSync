package com.gci.quartz.cron.vo;

import com.gci.quartz.cron.util.Util;

public class HourVo {
    public HourVo() {
    }
    
    private Boolean isPer = Boolean.FALSE;
    
    private Integer[] assign;

    public void setAssign(Integer[] assign) {
        this.assign = assign;
    }

    public void setIsPer(Boolean isPer) {
        this.isPer = isPer;
    }

    public Boolean getIsPer() {
        return isPer;
    }

    @Override
    public String toString() {
        return "isPer:" + isPer +", assign:[" + Util.array2String(assign) + "]";
    }

    public Integer[] getAssign() {
        return assign;
    }
}
