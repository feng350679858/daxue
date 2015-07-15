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
    public static Calendar calendar = Calendar.getInstance();
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
        return formatDate(calendar.getTime(),"yyyy-M-d");
    }
}
