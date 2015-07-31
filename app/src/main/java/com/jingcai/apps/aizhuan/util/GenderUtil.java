package com.jingcai.apps.aizhuan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class GenderUtil {
    private static final Map<String, String> map = new HashMap<String, String>(){
        {
            this.put("0", "只限男生");
            this.put("1", "只限女生");
            this.put("2", "男女不限");
        }
    };

    public static String get(String key){
        String val = map.get(key);
        if(null == val){
            return "";
        }
        return val;
    }
}