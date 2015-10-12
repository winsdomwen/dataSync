package com.gci.quartz.cron.service;

import com.gci.quartz.cron.util.Constant;

/**
 * Cron表达式bean类，也是构造者模式中的产品类
 *
 */
public class Expression {

    private String secondText;
    private String minuteText;
    private String hourText;
    private String dayText;
    private String monthText;
    private String weekText;

    public Expression() {
    }

    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public String getSecondText() {
        return secondText;
    }

    public void setMinuteText(String minuteText) {
        this.minuteText = minuteText;
    }

    public String getMinuteText() {
        return minuteText;
    }

    public void setHourText(String hourText) {
        this.hourText = hourText;
    }

    public String getHourText() {
        return hourText;
    }

    public void setDayText(String dayText) {
        this.dayText = dayText;
    }

    public String getDayText() {
        return dayText;
    }

    public void setMonthText(String monthText) {
        this.monthText = monthText;
    }

    public String getMonthText() {
        return monthText;
    }

    public void setWeekText(String weekText) {
        this.weekText = weekText;
    }

    public String getWeekText() {
        return weekText;
    }

    @Override
    public String toString() {
        return secondText + " "  + minuteText + " " + hourText + " " + dayText + " " + monthText + " "  + weekText;
    }
}
