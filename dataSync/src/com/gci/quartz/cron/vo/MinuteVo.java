package com.gci.quartz.cron.vo;

import com.gci.quartz.cron.util.Util;

public class MinuteVo {
    
    public MinuteVo() {
    }
    private Integer[] assign;
    
    private MinuteCycleVo mcVo;

    public void setAssign(Integer[] assign) {
        this.assign = assign;
    }

    public Integer[] getAssign() {
        return assign;
    }

    public void setMcVo(MinuteCycleVo mcVo) {
        this.mcVo = mcVo;
    }

    public MinuteCycleVo getMcVo() {
        return mcVo;
    }

    @Override
    public String toString() {
        return "assign:[" + Util.array2String(assign) + "], mcVo:" + mcVo;
    }
}
