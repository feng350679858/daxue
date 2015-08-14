package com.jingcai.apps.aizhuan.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lejing on 15/7/31.
 */
public class DictUtil {
    public enum Item {
        //性别
        gender(new HashMap<String, String>() {{
            put("0", "只限男生");
            put("1", "只限女生");
            put("2", "男女不限");
        }}),
        //即时帮助-状态
        help_jishi_status(new HashMap<String, String>() {{
            put("0", "派单中");
            put("1", "求助中");
            put("2", "帮助中");
            put("3", "取消中");
            put("4", "已取消");
            put("5", "已帮助");
            put("6", "已结算");
            put("7", "已超时");
        }}),
        //问答帮助-状态
        help_wenda_status(new HashMap<String, String>() {{
            put("2", "帮助中");
            put("3", "取消中");
            put("4", "已取消");
            put("5", "已帮助");
            put("6", "已结算");
        }});


        private Map<String, String> data;
        Item(Map<String, String> map) {
            this.data = map;
        }
    }

    public static String get(Item item, String key) {
        if (null == item) {
            return "";
        }
        String val = item.data.get(key);
        if (null == val) {
            return "";
        }
        return val;
    }
}
