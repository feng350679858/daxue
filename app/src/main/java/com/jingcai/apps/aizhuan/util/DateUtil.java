package com.jingcai.apps.aizhuan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class DateUtil {
    public static final Calendar calendar = Calendar.getInstance();
    public static final SimpleDateFormat dateFormat14 = new SimpleDateFormat("yyyyMMddHHmmss");
    /**
     * 将时间从旧的格式转换为新的格式,任何异常的发生，均会返回空串
     * @param dateStr 时间字符串
     * @param oldFormatStr dateStr时间的格式
     * @param newFormatStr 需要转换的格式
     * @return 制定格式的时间字符串
     */
    public static String formatDateString(String dateStr,String oldFormatStr,String newFormatStr){
        if(StringUtil.isNotEmpty(dateStr) && StringUtil.isNotEmpty(newFormatStr) &&StringUtil.isNotEmpty(oldFormatStr)){
            try {
                SimpleDateFormat newFormat = new SimpleDateFormat(newFormatStr);
                SimpleDateFormat oldFormat = new SimpleDateFormat(oldFormatStr);
                return newFormat.format(oldFormat.parse(dateStr));
            }catch (Exception e){
                return "";
            }
        }else{
            return "";
        }
    }

    /**
     * 返回指定格式的日期，如果格式不对，返回null;
     * @param dateStr 时间字符串
     * @param format 格式
     * @return 时间
     */
    public static Date parseDate(String dateStr,String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(Date date, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取星期数0-6
     * @return
     */
    public static int getWeek(){
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取星期列表，截至日前的一个星期
     */
    public static ArrayList<String> getWeekList(){
        ArrayList<String> weekStr = new ArrayList<>();
        int curWeek = getWeek();
        for(int i = 0 ; i < 7; i++){
            curWeek ++;
            if(curWeek > 7){
                curWeek %= 7;
            }
            switch (curWeek){
                case Calendar.SUNDAY : weekStr.add("周日");
                    break;
                case Calendar.MONDAY : weekStr.add("周一");
                    break;
                case Calendar.TUESDAY : weekStr.add("周二");
                    break;
                case Calendar.WEDNESDAY : weekStr.add("周三");
                    break;
                case Calendar.THURSDAY : weekStr.add("周四");
                    break;
                case Calendar.FRIDAY : weekStr.add("周五");
                    break;
                case Calendar.SATURDAY : weekStr.add("周六");
                    break;
            }
        }
        return weekStr;
    }

    /**
     * 获取指定月数以前的日期
     * @param month 月数
     * @return Date对象
     */
    public static Date getDateMonthsAgo(int month){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MONTH,-(month));
        return calendar.getTime();
    }

    public static String getWithdrawDate(){
        calendar.setTimeInMillis(System.currentTimeMillis());
        if(calendar.get(Calendar.HOUR_OF_DAY) > 22){
         calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        return formatDate(calendar.getTime(), "yyyy-M-d");
    }

    /**
     * 根据传入的时间，得出距离传入时间的中文表示
     * 例如：
     *      今天 -> HH:mm
     *      昨天 -> 昨天
     *      三天前 -> 3天前
     *      五个月前 ->5个月前
     *      四年前 ->4年前
     * @param dateStr 指定的时间
     * @return 中文表示的时间距离
     */
    public static String getHumanlityDateString(String dateStr){
        try {
            Date date = dateFormat14.parse(dateStr);
            return getHumanlityDateString(date);
        } catch (Exception e) {
            return "";
        }
    }
    /**
     * 根据传入的时间，得出距离传入时间的中文表示
     * 例如：
     *      今天 -> HH:mm
     *      昨天 -> 昨天
     *      三天前 -> 3天前
     *      五个月前 ->5个月前
     *      四年前 ->4年前
     * @param date 指定的时间
     * @return 中文表示的时间距离
     */
    public static String getHumanlityDateString(Date date){
        if(isToday(date)){
            return formatDate(date,"HH:mm");
        }
        if(isToday(new Date(System.currentTimeMillis() - 3600 * 60 * 24))){
            return "昨天";
        }
        int months = monthAgo(date);
        if(months >= 1 &&  months < 12){
            return months+"个月前";
        }
        int years = yearsAgo(date);
        if(years >= 1){
            return years+"年前";
        }
        return "未知";
    }

    public static int yearsAgo(Date date) {
        return monthAgo(date)/12;
    }


    public static int monthAgo(Date date) {
        long current = System.currentTimeMillis();
        long diff = current - date.getTime();
        long fullMonthMilli = 3600*24*30*1000l;
        return (int) (diff/fullMonthMilli);  //多少个月
    }

    /**
     * 判断一个日期是否是今天
     * @param date 指定的日期
     * @return 是否是今天
     */
    public static boolean isToday(Date date) {
        //获取今天的最先时刻、最后时刻
        TimeInfo todayInfo = getTimeInfo(new Date(),TimeInfoScope.DAY);
        long time = date.getTime();
        return time >= todayInfo.startTime && time < todayInfo.endTime;
    }

    /**
     * 获取指定日期的TimeInfo
     * @param date 指定日期
     * @return timeInfo
     */
    private static TimeInfo getTimeInfo(Date date,TimeInfoScope scope) {
        calendar.setTimeInMillis(date.getTime());
        TimeInfo timeInfoDay = new TimeInfo();
        TimeInfo timeInfoMonth = new TimeInfo();
        switch (scope){
            case DAY:
                //获取天的最先时刻
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                timeInfoDay.startTime = calendar.getTimeInMillis();

                //获取天的最后时刻
                calendar.set(Calendar.HOUR_OF_DAY,23);
                calendar.set(Calendar.MINUTE,59);
                calendar.set(Calendar.SECOND,59);
                calendar.set(Calendar.MILLISECOND,999);
                timeInfoDay.endTime = calendar.getTimeInMillis();
            case MONTH:
                //获取月的最先时刻
                calendar.set(Calendar.MONTH,Calendar.JANUARY);
                timeInfoMonth.startTime = calendar.getTimeInMillis();

                calendar.set(Calendar.MONTH,Calendar.DECEMBER);
                timeInfoMonth.endTime = calendar.getTimeInMillis();
                break;
        }
        switch (scope){
            case DAY:
                return timeInfoDay;
            case MONTH:
                return timeInfoMonth;
            default:
                return null;
        }

    }
    /**
     * 一段时间的开始时间和结束时间的封装
     */
    static class TimeInfo{
        long startTime; //开始时间
        long endTime;  //结束时间
    }

    enum TimeInfoScope{
        DAY,MONTH
    }

}
