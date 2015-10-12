package com.gci.quartz.cron.service;

import com.gci.quartz.cron.exception.ExpressionException;
import com.gci.quartz.cron.vo.DayVo;
import com.gci.quartz.cron.vo.HourVo;
import com.gci.quartz.cron.vo.MinuteCycleVo;
import com.gci.quartz.cron.vo.MinuteVo;
import com.gci.quartz.cron.vo.MonthVo;
import com.gci.quartz.cron.vo.WeekVo;

/**
 * 构造者模式的表达式构造者
 *
 */
public class ExpressionBuilder {

    private MinuteVo minuteVo;

    private HourVo hourVo;

    private DayVo dayVo;

    private MonthVo monthVo;

    private WeekVo weekVo;

    private Expression expression = new Expression();

    public ExpressionBuilder() {
        minuteVo = new MinuteVo();

        hourVo = new HourVo();
        dayVo = new DayVo();
        monthVo = new MonthVo();
        weekVo = new WeekVo();
    }

    public void makeSecond() {
        expression.setSecondText("0");
    }

    public void makeMinute() {
        MinuteCycleVo mcv = minuteVo.getMcVo();
        Integer[] assign = minuteVo.getAssign();
        if (mcv == null && (assign == null || assign.length == 0)) {
            throw new ExpressionException("没有对分钟进行设置!");
        }

        if (mcv != null) {
            int start = mcv.getStart();
            int rate = mcv.getRate();
            expression.setMinuteText(start + "/" + rate);
            return;
        }

        if (assign.length > 60) {
            throw new ExpressionException("指定的分钟数大于60个!");
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0, len = assign.length; i < len; i++) {
            if (assign[i] > 59) {
                throw new ExpressionException("指定的分钟大于60!");
            }
            buf.append(assign[i]);
            if (i != (assign.length - 1)) {
                buf.append(",");
            }
        }
        expression.setMinuteText(buf.toString());
    }

    public void makeHour() {
        Boolean isPer = hourVo.getIsPer();
        Integer[] assign = hourVo.getAssign();

        if (isPer == Boolean.FALSE && (assign == null || assign.length == 0)) {
            throw new ExpressionException("没有对小时进行设置!");
        }

        if (isPer == Boolean.TRUE) {
            expression.setHourText("*");
            return;
        }

        if (assign.length > 24) {
            throw new ExpressionException("指定的小时数大于23个!");
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0, len = assign.length; i < len; i++) {
            if (assign[i] > 23) {
                throw new ExpressionException("指定的小时大于23!");
            }
            buf.append(assign[i]);
            if (i != (assign.length - 1)) {
                buf.append(",");
            }
        }
        expression.setHourText(buf.toString());
    }

    public void makeDay() {
        Boolean isPer = dayVo.getIsPer();
        Integer[] assign = dayVo.getAssign();
        if (isPer == Boolean.FALSE && (assign == null || assign.length == 0)) {
            throw new ExpressionException("没有对日进行设置");
        }
        if (isPer == Boolean.TRUE) {
            expression.setDayText("*");
            return;
        }
        if (assign.length > 31) {
            throw new ExpressionException("指定的日期数大于31个!");
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0, len = assign.length; i < len; i++) {
            if (assign[i] > 31) {
                throw new ExpressionException("指定的日期大于31!");
            }
            buf.append(assign[i]);
            if (i != (assign.length - 1)) {
                buf.append(",");
            }
        }
        expression.setDayText(buf.toString());
    }

    public void makeMonth() {
        Boolean isPer = monthVo.getIsPer();
        Integer[] assign = monthVo.getAssign();

        if (isPer == Boolean.FALSE && (assign == null || assign.length == 0)) {
            throw new ExpressionException("没有对月进行设置!");
        }
        if (isPer == Boolean.TRUE) {
            expression.setMonthText("*");
            return;
        }
        if (assign.length > 12) {
            throw new ExpressionException("指定的月份数大于12个!");
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0, len = assign.length; i < len; i++) {
            if (assign[i] > 12) {
                throw new ExpressionException("指定的月份大于12!");
            }
            buf.append(assign[i]);
            if (i != (assign.length - 1)) {
                buf.append(",");
            }
        }
        expression.setMonthText(buf.toString());
    }

    public void makeWeek() {
        Boolean isUseWeek = weekVo.getIsUse();
        if (isUseWeek == Boolean.FALSE) {
            expression.setWeekText("?");
            return;
        }

        Boolean isPer = weekVo.getIsPer();
        Integer[] assign = weekVo.getAssign();
        if (isPer == Boolean.FALSE && (assign == null || assign.length == 0)) {
            throw new ExpressionException("没有对周进行设置");
        }

        if (isPer == Boolean.TRUE) {
            expression.setWeekText("*");
            return;
        }

        if (assign.length > 7) {
            throw new ExpressionException("指定的礼拜数大于7个!");
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0, len = assign.length; i < len; i++) {
            if (assign[i] > 7) {
                throw new ExpressionException("指定的周大于7!");
            }
            buf.append(assign[i]);
            if (i != (assign.length - 1)) {
                buf.append(",");
            }
        }
        expression.setWeekText(buf.toString());
    }

    public Expression getExpression() {
        return expression;
    }

    public void setMinuteVo(MinuteVo minuteVo) {
        this.minuteVo = minuteVo;
    }

    public MinuteVo getMinuteVo() {
        return minuteVo;
    }

    public void setHourVo(HourVo hourVo) {
        this.hourVo = hourVo;
    }

    public HourVo getHourVo() {
        return hourVo;
    }

    public void setDayVo(DayVo dayVo) {
        this.dayVo = dayVo;
    }

    public DayVo getDayVo() {
        return dayVo;
    }

    public void setMonthVo(MonthVo monthVo) {
        this.monthVo = monthVo;
    }

    public MonthVo getMonthVo() {
        return monthVo;
    }

    public void setWeekVo(WeekVo weekVo) {
        this.weekVo = weekVo;
    }

    public WeekVo getWeekVo() {
        return weekVo;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
