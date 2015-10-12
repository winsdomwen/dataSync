package com.gci.quartz.cron.service;

 /**
  * 构造者模式中的指挥者
  * 
  */
public class ExpressionDirector {
    public ExpressionDirector() {
    }

    public void make(ExpressionBuilder builder) {
        builder.makeSecond();
        builder.makeMinute();
        builder.makeHour();
        builder.makeDay();
        builder.makeMonth();
        builder.makeWeek();
    }
}
