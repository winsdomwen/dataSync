package com.gci.quartz.cron.vo;

import com.gci.quartz.cron.util.Util;

public class DayVo {
    public DayVo() {
    }

    private Boolean isPer = Boolean.FALSE;

    private Integer[] assign;


    public void setIsPer(Boolean isPer) {
        this.isPer = isPer;
    }

    public Boolean getIsPer() {
        return isPer;
    }

    public void setAssign(Integer[] assign) {
        this.assign = assign;
    }

    public Integer[] getAssign() {
        return assign;
    }

    @Override
    public String toString() {
        return "isPer[" + isPer + "], assign[" + Util.array2String(assign) + "]";
    }
}
