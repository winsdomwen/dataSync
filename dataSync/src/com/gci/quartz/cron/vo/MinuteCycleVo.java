package com.gci.quartz.cron.vo;

public class MinuteCycleVo {

    private Integer start = 0;

    private Integer rate = 5;

    public MinuteCycleVo(Integer start, Integer rate) {
        this.start = start;
        this.rate = rate;
    }

    public MinuteCycleVo() {
    }

    @Override
    public String toString() {
        return "start:" + start + ", rate:" + rate;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getStart() {
        return start;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getRate() {
        return rate;
    }
}
