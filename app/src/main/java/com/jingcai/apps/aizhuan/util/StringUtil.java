package com.jingcai.apps.aizhuan.util;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.Log;

import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class StringUtil {
    private static DecimalFormat moneyFormat = new DecimalFormat(".0");
    private static DecimalFormat distnceFormat = new DecimalFormat("0.##");
    public static boolean isNotEmpty(String str){
        if(null != str && !"".equals(str)){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isEmpty(String str){
        return !isNotEmpty(str);
    }

    public static String valueOf(Object obj){
        if(null == obj){
            return "";
        }
        return String.valueOf(obj);
    }

    public static String money(String str){
        if(isEmpty(str)){
            return "0.0";
        }
        try {
            return moneyFormat.format(Double.parseDouble(str));
        }catch (Exception e){
            return str;
        }
    }
    public static String distance(String str){
        if(isEmpty(str)){
            return "";
        }
        try {
            return distnceFormat.format(Double.parseDouble(str));
        }catch (Exception e){
            return str;
        }
    }

    /**
     * 在raw中获取字符串
     * @param context 上下文
     * @param rawId 资源id
     * @return 字符串
     */
    public static String loadStringInRaw(Context context,@RawRes int rawId){
        InputStream inputStream = context.getResources().openRawResource(rawId);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder line = new StringBuilder();
        String str = "";
        try {
            while((str = bufferedReader.readLine()) != null){
                line.append(str);
                line.append("<br>");
            }
            return line.toString();
        } catch (IOException e) {
            Log.e(GlobalConstant.packageName,"load file from raw document occur exception.");
            e.printStackTrace();
        } finally {
            if(inputStream != null ){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(GlobalConstant.packageName,"close file input stream occur exception.");
                }
            }
        }
        return null;
    }

    /**
     * 格式化金额字符串
     * @param moneyStr 金额字符串
     * @return 格式化字符串
     */
    public static String getPrintMoney(String moneyStr){
        float money = 0;
        try {
            money = Float.parseFloat(moneyStr);
        }catch (Exception e){}
        return getPrintMoney(money);
    }
    /**
     * 格式化金额字符串
     * @param money 金额
     * @return 格式化字符串
     */
    public static String getPrintMoney(float money){
        return getFormatFloat(money, "#,##0.00");
    }
    /**
     * 根据传入的浮点数和规范公式返回格式化的数字字符串
     * 规范公式查看
     * @see java.text.DecimalFormat
     * @param value 浮点数
     * @param formater 规范公式
     * @return 格式化字符串
     */
    public static String getFormatFloat(float value,String formater){
        try {
            DecimalFormat decimalFormat = new DecimalFormat(formater);
            return decimalFormat.format(value);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(GlobalConstant.packageName,"method getFormatFloat has a invalid formater");
        }
        return null;
    }

    /**
     * 判断字符串是否为数字
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNumber(String str){
        return str.matches("^\\d+$");
    }

    /**
     * 将传入的电话号码隐藏4-8位
     * @param phone 电话字符串
     * @return 隐藏电话字符串
     */
    public static String hiddenPhone(String phone){
        if(phone.length() < 11){
            return "位数不足11位";
        }
        char[] chars = phone.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i>=3 && i<=6){
                chars[i] = '*';
            }
        }
        return new String(chars);
    }
}
