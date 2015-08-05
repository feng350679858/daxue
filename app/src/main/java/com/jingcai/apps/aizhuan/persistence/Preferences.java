package com.jingcai.apps.aizhuan.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lejing on 15/4/28.
 */
public class Preferences {

    public enum TYPE {
        config, partjob
    }

    private static Context context;
    private static Map<TYPE, Preferences> instanceMap = new HashMap<>();

    private SharedPreferences settings;

    private static final String DEFAULT_LOGOPATH = "";

    protected static final String PARAM_INTROSTATUS = "introstatus";
    protected static final String PARAM_STUDENTID = "studentid";
    protected static final String PARAM_PHONE = "phone";
    protected static final String PARAM_PASSPORD = "password";
    protected static final String PARAM_NAME = "name";
    protected static final String PARAM_GENDER = "gender";
    protected static final String PARAM_LOGOURL = "logourl";
    protected static final String PARAM_SCHOOL_NAME = "schoolname";
    protected static final String PARAM_CITY_NAME = "cityname";
    protected static final String PARAM_LOGIN_FLAG = "loginflag";
    protected static final String PARAM_ONLINE_FLAG = "onlineflag";
    protected static final String PARAM_ISVISIABLE = "isvisiable";
    protected static final String PARAM_LEVEL = "level";
    protected static final String PARAM_HASPAYPASSWORDSET = "ispaypasswordset";

    //系统管理模块
    public interface Sys {
        String WELCOME_LOADING_IMG_URL = "welcome_loading_img_url";         //loading图url
        String PARAM_INTROSTATUS = "intro_flag";                            //是否显示引导页面
    }
    //兼职模块
    public interface Partjob {
        String SEARCH_INDEX = "search_index";                                 //loading图url
        String SEARCH_KEY_PREFIX = "search_key_prefix";                     //loading图url
    }

//    private static boolean introstatus;
//    private static String studentid;
//    private static String phone;
//    private static String password;
//    private static String name;
//    private static String gender;
//    private static String logourl;
//    private static String schoolname;
//    private static String cityname;
//    private static boolean loginflag;
//    private static String isvisiable;
//    private static String level;

    public static void initClass(Context ctx) {
        if (null == context) {
            context = ctx;
        } else {
            throw new RuntimeException("Preferences has been inited");
        }
    }

    public static Preferences getInstance() {
        return getInstance(TYPE.config);
    }

    public synchronized static Preferences getInstance(TYPE type) {
        Preferences preferences = instanceMap.get(type);
        if (null == preferences) {
            preferences = new Preferences(type);
            instanceMap.put(type, preferences);
        }
        return preferences;
    }

    private Preferences(TYPE type) {
        if (null == context) {
            throw new RuntimeException("Preferences has not been inited");
        }
        settings = context.getSharedPreferences(type.name(), Activity.MODE_PRIVATE);
    }

//    public static void clearHistory(Context context) {
//        SharedPreferences historys = context.getSharedPreferences(PREFS_NAME_PARTTIMEJOB_SERACH_HISTORY, Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = historys.edit();
//        editor.putInt("size", 0);
//        editor.commit();
//
//    }
//    public static List<String> loadHistory(Context context) {
//        SharedPreferences historys = context.getSharedPreferences(PREFS_NAME_PARTTIMEJOB_SERACH_HISTORY, Activity.MODE_PRIVATE);
//        int size = historys.getInt("size", 0);
//        List<String> historyList = new ArrayList<>();
//
//        for (int i = 0; i < size; i++) {
//            historyList.add(historys.getString("history_" + i, ""));
//        }
//        return historyList;
//    }


    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defVal) {
        return settings.getString(key, defVal);
    }

    public boolean getBoolean(String key, boolean defVal) {
        return settings.getBoolean(key, defVal);
    }

    public int getInt(String key, int defVal) {
        return settings.getInt(key, defVal);
    }

    public float getFloat(String key, float defVal) {
        return settings.getFloat(key, defVal);
    }

    public long getLong(String key, long defVal) {
        return settings.getLong(key, defVal);
    }

    public boolean update(String key, Object value) {
        if(null == key || null == value){
            return false;
        }
        SharedPreferences.Editor editor = settings.edit();
        if (value instanceof String) {
            editor.putString(key, value.toString());
        } else if (value instanceof Integer) {
            editor.putInt(key, ((Integer) value).intValue());
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, ((Boolean) value).booleanValue());
        } else if (value instanceof Long) {
            editor.putLong(key, ((Long) value).longValue());
        } else if (value instanceof Float) {
            editor.putFloat(key, ((Float) value).floatValue());
        } else {
            editor.putString(key, value.toString());
        }
        return editor.commit();
    }

    public void delete(String key) {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }
}