package com.gci.quartz.cron.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;

import com.gci.quartz.cron.exception.ExpressionException;
import com.gci.quartz.cron.util.Constant;
import com.gci.quartz.cron.util.DateFormatUtil;
import com.gci.quartz.cron.vo.DayVo;
import com.gci.quartz.cron.vo.HourVo;
import com.gci.quartz.cron.vo.MinuteVo;
import com.gci.quartz.cron.vo.MonthVo;
import com.gci.quartz.cron.vo.WeekVo;

/**
 * 接口服务类
 *
 */
public class ExpressionService {

    public static final Logger LOGGER =
        Logger.getLogger(ExpressionService.class);

    private ExpressionService() {
    }


    /**
     * 获取cron表达式
     *
     * @param minuteVo Minute value object
     * @param hourVo Hour value object
     * @param dayVo Day value object
     * @param monthVo Month value object
     * @param weekVo  Week value object
     *
     * @return Cron Expression
     */
    public static String getExpression(MinuteVo minuteVo, HourVo hourVo,
                                       DayVo dayVo, MonthVo monthVo,
                                       WeekVo weekVo) {
        ExpressionBuilder builder = new ExpressionBuilder();
        if (minuteVo != null) {
            builder.setMinuteVo(minuteVo);
        }
        if (hourVo != null) {
            builder.setHourVo(hourVo);
        }
        if (dayVo != null) {
            builder.setDayVo(dayVo);
        }
        if (monthVo != null) {
            builder.setMonthVo(monthVo);
        }
        if (weekVo != null) {
            builder.setWeekVo(weekVo);
        }

        ExpressionDirector ed = new ExpressionDirector();
        ed.make(builder);
        LOGGER.debug(builder.getExpression().toString());
        return builder.getExpression().toString();
    }

    /**
     * 获取下次触发时间列表
     *
     * @param cronExpression Cron expression
     * @param times Times
     * @return  A set of times.
     */
    public static List<Date> getNextTriggerTimes(String cronExpression,
                                                 int times) {
        CronExpression exp;
        List<Date> nextTriggerTimes;

        try {
            exp = new CronExpression(cronExpression);
        } catch (ParseException e) {
            LOGGER.error(
                             "转换字符串到cron表达式失败!", e);
            throw new ExpressionException("转换字符串到cron表达式失败!",
                                          e);
        }
        if (!CronExpression.isValidExpression(exp.getCronExpression())) {
            LOGGER.error(
                             "验证表达式[" + cronExpression + "]!");
            throw new ExpressionException("验证表达式[" +
                                          cronExpression + "]!");
        }
        if (times <= 0) {
            LOGGER.error( "验证时间[" + times + "]!");
            throw new ExpressionException("验证时间[" + times + "]!");
        }


        nextTriggerTimes = new ArrayList<Date>(times);
        Date dd = new Date();
        for (int i = 0; i < times; i++) {
            dd = exp.getNextValidTimeAfter(dd);
            nextTriggerTimes.add(dd);
        }
        return nextTriggerTimes;
    }

    /**
     *获取一个时间段的所有触发时间
     *
     * @param startDate开始时间
     * @param endDate 结束时间
     * @param cronExpression Cron expression
     * @return 触发时间列表
     */
    public static List<Date> getTriggerTimesInTimeZone(Date startDate,
                                                       Date endDate,
                                                       String cronExpression) {
        CronExpression exp;
        List<Date> triggerTimes = new ArrayList<Date>();

        if (startDate.after(endDate)) {
            LOGGER.error(
                             "开始时间[" + dateFormat(startDate) + "大于结束时间+" +
                             dateFormat(endDate) + "+!");
            throw new ExpressionException("开始时间[" +
                                          dateFormat(startDate) +
                                          "大于结束时间+" +
                                          dateFormat(endDate) + "+!");
        }

        try {
            exp = new CronExpression(cronExpression);
        } catch (ParseException e) {
            LOGGER.error(
                             "转换字符串" + cronExpression + " 到cron表达式失败!");
            throw new ExpressionException("转换字符串 " + cronExpression +
                                          " 到cron表达式失败!", e);
        }

        if (!CronExpression.isValidExpression(exp.getCronExpression())) {
            LOGGER.error(
                             "验证表达式[" + cronExpression + "]!");
            throw new ExpressionException("验证表达式[" +
                                          cronExpression + "]!");
        }

        Date nextTriggerDate = startDate;
        while (nextTriggerDate.before(endDate)) {
            nextTriggerDate = exp.getNextValidTimeAfter(nextTriggerDate);
            triggerTimes.add(nextTriggerDate);
        }
        return triggerTimes;

    }

    /**
     *检查一个时间在一个周期内是否会触发
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param checkDate 被检查的时间点
     * @param cronExpression Cron expression
     * @return true or false
     */
    public static boolean isTrigger(Date startDate, Date endDate,
                                    Date checkDate, String cronExpression) {
        CronExpression exp;
        boolean triggerFlag = false;

        if (startDate.after(endDate)) {
            LOGGER.error(
                             "开始时间[" + dateFormat(startDate) + "不能大于结束时间+" +
                             dateFormat(endDate) + "+!");
        }

        if (checkDate.before(startDate) || checkDate.after(endDate)) {
            LOGGER.error(
                             "检查的时间点不在开始时间[" + dateFormat(startDate) +
                             "] 和结束时间" + dateFormat(endDate) +
                             " 之间!");
            throw new ExpressionException("检查的时间点不在开始时间[" +
                                          dateFormat(startDate) +
                                          "] 和结束时间" +
                                          dateFormat(endDate) + " 之间!");
        }

        try {
            exp = new CronExpression(cronExpression);
        } catch (ParseException e) {
            LOGGER.error(
                             "转换字符串" + cronExpression + " 到cron表达式失败!");
            throw new ExpressionException("转换字符串" + cronExpression +
                                          " 到cron表达式失败!", e);
        }

        if (!CronExpression.isValidExpression(exp.getCronExpression())) {
            throw new ExpressionException("验证表达式[" +
                                          cronExpression + "]!");
        }

        Date nextTriggerDate = startDate;
        while (nextTriggerDate.before(endDate)) {
            nextTriggerDate = exp.getNextValidTimeAfter(nextTriggerDate);
            if (nextTriggerDate.equals(checkDate)) {
                triggerFlag = true;
                break;
            }
        }
        return triggerFlag;
    }

    private static String dateFormat(Date date) {
        return DateFormatUtil.format(Constant.DEFAULT_DATE_FORMAT, date);
    }
}
