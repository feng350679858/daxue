package com.jingcai.apps.aizhuan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class DateUtil {

    /**
     * 将时间从旧的格式转换为新的格式,任何异常的发生，均会返回空串
     * @param dateStr 时间字符串
     * @param oldFormatStr dateStr时间的格式
     * @param newFormatStr 需要转换的格式
     * @return 制定格式的时间字符串
     */
    public static String formatDateString(String dateStr,String oldFormatStr,String newFormatStr){
        if(StringUtil.isNotEmpty(dateStr) && StringUtil.isNotEmpty(newFormatStr) && StringUtil.isNotEmpty(oldFormatStr)){
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

}
