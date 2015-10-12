package com.gci.quartz.cron.vo;

import com.gci.quartz.cron.util.Util;

public class WeekVo {
    public WeekVo() {
    }

    private Boolean isUse = Boolean.FALSE;

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

    public void setIsUse(Boolean isUse) {
        this.isUse = isUse;
    }

    public Boolean getIsUse() {
        return isUse;
    }


    @Override
    public String toString() {
        return "isPer:" + isPer + ", isUse:" + isUse + ", assign:[" +
            Util.array2String(assign) + "]";
    }
}
