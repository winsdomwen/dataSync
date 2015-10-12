package com.gci.quartz.cron.test;
import com.gci.quartz.cron.service.ExpressionService;
import com.gci.quartz.cron.util.DateFormatUtil;
import com.gci.quartz.cron.vo.DayVo;
import com.gci.quartz.cron.vo.HourVo;
import com.gci.quartz.cron.vo.MinuteCycleVo;
import com.gci.quartz.cron.vo.MinuteVo;
import com.gci.quartz.cron.vo.MonthVo;
import com.gci.quartz.cron.vo.WeekVo;



import java.text.ParseException;

import java.util.Date;
import java.util.List;

public class TestService {
    private String cronExpression;

    public TestService() {
    }

    public static void main(String[] args) {
        //设置分钟,从4分开始，每5分钟循环一次
        MinuteVo minuteVo = new MinuteVo();
        minuteVo.setMcVo(new MinuteCycleVo(4, 5));
       minuteVo.setMcVo(new MinuteCycleVo());

        //设置小时，为每天1点和2点
        HourVo hourVo = new HourVo();
        hourVo.setAssign(new Integer[] { 1, 2 });

        //设置日，为每月的2，4号
        DayVo dayVo = new DayVo();
        dayVo.setAssign(new Integer[] { 2, 4 });

        //设置月，为每月
        MonthVo monthVo = new MonthVo();
        monthVo.setIsPer(Boolean.TRUE);

        //不设置周
        WeekVo weekVo = new WeekVo();
        weekVo.setIsUse(Boolean.FALSE);

        String expression =
            ExpressionService.getExpression(minuteVo, hourVo, dayVo, monthVo,
                                            weekVo);
        
        System.out.println(expression);
        
        expression="0 0 12 ? * 2,3,4";
        // 获取8次下次执行时间
        List<Date> dates =
            ExpressionService.getNextTriggerTimes(expression, 100); //0 0-3 14 * * ?

        for (Date d : dates) {
            System.out.println(DateFormatUtil.format("yyyy-MM-dd HH:mm:ss",
                                                     d));
        }
        
        
        
//        //测试类似行事日历的功能
//        System.out.println("===================================");
//        System.out.println("测试类似行事日历的功能:");
//        Date startDate = null;
//        Date endDate = null;
//        Date checkDate = null;
//        try {
//            startDate = DateFormatUtil.parse("yyyy-MM-dd HH:mm:ss", "2014-01-26 00:00:01");
//            endDate = DateFormatUtil.parse("yyyy-MM-dd HH:mm:ss", "2014-07-31 11:59:59");
//            checkDate = DateFormatUtil.parse("yyyy-MM-dd HH:mm:ss", "2014-07-29 14:02:00");
//        } catch (ParseException e) {
//        }
//        List<Date> times = ExpressionService.getTriggerTimesInTimeZone(startDate, endDate, expression);
//        for (Date d : times) {
//            System.out.println(DateFormatUtil.format("yyyy-MM-dd HH:mm:ss",
//                                                     d));
//        }       
//            
//        boolean b = ExpressionService.isTrigger(startDate, endDate, checkDate, expression);
//        System.out.println(b);
    }
}
