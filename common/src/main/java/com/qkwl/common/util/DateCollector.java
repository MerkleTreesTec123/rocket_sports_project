package com.qkwl.common.util;

//import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * 数据采集工具
 * Created by ZKF on 2017/7/24.
 */
public class DateCollector {

    /**
     * 获取时间区间（上一小时的0分0秒 <= 时间区间 < 当前小时的0分0秒）
     * @return
     */
    public static Map<String, Object> getTimeInterval(){
        Map<String, Object> map = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        map.put("end", DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 1);
        map.put("begin", DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        map.put("index", cal.get(Calendar.HOUR_OF_DAY));
        return map;
    }


    /**
     * 获取某一天的开始时间和下一天的开始时间
     * @return
     */
    public static Map<String, Object> getDayInterval(Date date){
        Map<String, Object> map = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        map.put("begin", DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        map.put("end", DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        return map;
    }

    /**
     * 获取某天所在这周的开始时间和下一周的开始时间
     * @return
     */
    public static Map<String, Object> getWeekInterval(Date date){
        Map<String, Object> map = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        map.put("begin", DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 7);
        map.put("end", DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        return map;
    }

    /**
     * 获取某天所在这月的开始时间和下一月的开始时间
     * @return
     */
    public static Map<String, Object> getMonthInterval(Date date){
        Map<String, Object> map = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        map.put("begin", DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        map.put("end", DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
        return map;
    }

    /**
     * 获取某日期是当周第几天
     */
    public static Integer getDayOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7:cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 获取某日期是当月第几天
     */
    public static Integer getDayOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 获取某日期的下一天日期
     */
    public static String getNextDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        return DateUtils.format(cal.getTime(), DateUtils.YYYY_MM_DD);
    }

    public static void main(String[] args){
//        Map<String, Object> map = getDayInterval(DateUtils.parse("2017-07-25", DateUtils.YYYY_MM_DD));
//        System.out.println(JSON.toJSONString(map));
//        map = getWeekInterval(DateUtils.parse("2017-07-25", DateUtils.YYYY_MM_DD));
//        System.out.println(JSON.toJSONString(map));
//        map = getMonthInterval(DateUtils.parse("2017-07-25", DateUtils.YYYY_MM_DD));
//        System.out.println(JSON.toJSONString(map));
//          Integer day = getDayOfWeek(DateUtils.parse("2017-07-25", DateUtils.YYYY_MM_DD));
//          System.out.println(day);
//          day = getDayOfMonth(DateUtils.parse("2017-07-25", DateUtils.YYYY_MM_DD));
//          System.out.println(day);
//          String date = getNextDay(DateUtils.parse("2017-07-31", DateUtils.YYYY_MM_DD));
//          System.out.println(date);
    }



}
