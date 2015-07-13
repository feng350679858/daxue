package com.jingcai.apps.aizhuan.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lejing on 15/4/28.
 */
public class Preferences {
    private static final String PREFS_NAME = "config";
    private static final String PREFS_NAME_PARTTIMEJOB_SERACH_HISTORY = "history";
    private static final String PREFS_NAME_LOADING_IMG = "history";

    private static final String DEFAULT_LOGOPATH    = "";

    private static final String PARAM_INTROSTATUS   =   "introstatus";
    private static final String PARAM_STUDENTID     =   "studentid";
    private static final String PARAM_PHONE         =   "phone";
    private static final String PARAM_PASSPORD      =   "password";
    private static final String PARAM_NAME          =   "name";
    private static final String PARAM_GENDER        =   "gender";
    private static final String PARAM_LOGOURL       =   "logourl";
    private static final String PARAM_SCHOOL_NAME   =   "schoolname";
    private static final String PARAM_CITY_NAME     =   "cityname";
    private static final String PARAM_LOGIN_FLAG    =   "loginflag";
    private static final String PARAM_ISVISIABLE    =   "isvisiable";
    private static final String PARAM_LEVEL         =   "level";

    private static boolean introstatus;
    private static String studentid;
    private static String phone;
    private static String password;
    private static String name;
    private static String gender;
    private static String logourl;
    private static String schoolname;
    private static String cityname;
    private static boolean loginflag;
    private static String isvisiable;
    private static String level;


    public static void loadSettings(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        introstatus = settings.getBoolean(PARAM_INTROSTATUS, false);
        studentid = settings.getString(PARAM_STUDENTID, "");
        phone = settings.getString(PARAM_PHONE, "");
        password = settings.getString(PARAM_PASSPORD, "");
        name = settings.getString(PARAM_NAME, "");
        gender = settings.getString(PARAM_GENDER, "");
        logourl = settings.getString(PARAM_LOGOURL, "");
        schoolname = settings.getString(PARAM_SCHOOL_NAME, "");
        cityname = settings.getString(PARAM_CITY_NAME, "");
        loginflag = settings.getBoolean(PARAM_LOGIN_FLAG, false);
        isvisiable = settings.getString(PARAM_ISVISIABLE, "");
        level = settings.getString(PARAM_LEVEL,"");

        UserSubject.setStudentid(studentid);
        UserSubject.setPhone(phone);
        UserSubject.setPassword(password);
        UserSubject.setName(name);
        UserSubject.setGender(gender);
        UserSubject.setLogourl(logourl);
        UserSubject.setSchoolname(schoolname);
        UserSubject.setCityname(cityname);
        UserSubject.setLoginFlag(loginflag);
        UserSubject.setIsvisiable(isvisiable);
        UserSubject.setLevel(level);

//        String savePath = context.getDir("", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE).toString();
//        File dir = new File(savePath);
//        dir.mkdirs();
    }


    public static void saveSettings(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PARAM_INTROSTATUS, introstatus);
        editor.putString(PARAM_STUDENTID, studentid);
        editor.putString(PARAM_PHONE, phone);
        editor.putString(PARAM_PASSPORD, password);
        editor.putString(PARAM_NAME, name);
        editor.putString(PARAM_GENDER, gender);
        editor.putString(PARAM_LOGOURL, logourl);
        editor.putString(PARAM_SCHOOL_NAME, schoolname);
        editor.putString(PARAM_CITY_NAME, cityname);
        editor.putBoolean(PARAM_LOGIN_FLAG, loginflag);
        editor.putString(PARAM_ISVISIABLE, isvisiable);
        editor.putString(PARAM_LEVEL,level);

        editor.commit();
    }

    public static void addHistory(Context context,String item){
        SharedPreferences historys = context.getSharedPreferences(PREFS_NAME_PARTTIMEJOB_SERACH_HISTORY, Activity.MODE_PRIVATE);
        int size = historys.getInt("size", 0);

        SharedPreferences.Editor editor = historys.edit();
        editor.putString("history_"+size,item);
        editor.putInt("size", ++size);
        editor.commit();
    }

    public static void clearHistory(Context context){
        SharedPreferences historys = context.getSharedPreferences(PREFS_NAME_PARTTIMEJOB_SERACH_HISTORY, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = historys.edit();
        editor.putInt("size", 0);
        editor.commit();

    }

    public static  List<String> loadHistory(Context context){
        SharedPreferences historys = context.getSharedPreferences(PREFS_NAME_PARTTIMEJOB_SERACH_HISTORY, Activity.MODE_PRIVATE);
        int size = historys.getInt("size", 0);
        List<String> historyList = new ArrayList<>();

        for(int i=0; i< size; i++){
            historyList.add(historys.getString("history_"+i,""));
        }
        return historyList;
    }

    public static void loginSuccess(Context context, Stu02Response.Stu02Body.Student stu) {
        studentid = stu.getStudentid();
        phone = stu.getPhone();
        password = stu.getPassword();
        name = stu.getName();
        gender = stu.getGender();
        logourl = stu.getLogopath();
        schoolname = stu.getSchoolname();
       // cityname = stu.getAreaname();
        cityname = "杭州";
        isvisiable = stu.getIsvisiable();
        level = stu.getLevel();
        loginflag = true;

        saveSettings(context);

        UserSubject.setStudentid(studentid);
        UserSubject.setPhone(phone);
        UserSubject.setPassword(password);
        UserSubject.setName(name);
        UserSubject.setGender(gender);
        UserSubject.setLogourl(logourl);
        UserSubject.setSchoolname(schoolname);
        UserSubject.setCityname(cityname);
        UserSubject.setIsvisiable(isvisiable);
        UserSubject.setLevel(level);
        UserSubject.setLoginFlag(true);
    }

    public static void loginFail(Context context) {
        password = null;
        loginflag = false;
        saveSettings(context);
        UserSubject.setName(null);
        UserSubject.setLevel("0");
        UserSubject.setSchoolname(null);
        UserSubject.setLogourl(DEFAULT_LOGOPATH);
        UserSubject.setLoginFlag(false);
    }

    public static boolean hasIntroduced() {
        return introstatus;
    }

    public static void setIntroStatus(Context context) {
        introstatus = true;
        saveSettings(context);
    }

    public static void setLevel(Context context,String lev){
        level = lev;
        saveSettings(context);
    }

    public static void setIsVisiable(Context context, boolean visiable){
        isvisiable = visiable?"1":"0";
        UserSubject.setIsvisiable(isvisiable);
        saveSettings(context);
    }

    public static void setLogopath(Context context,String logopath){
        logourl = logopath;
        saveSettings(context);
    }


    public static String getLoadingImgUrl(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME_LOADING_IMG, Activity.MODE_PRIVATE);
        return sharedPreferences.getString("img_url", null);
    }

    public static void setLoadingImgUrl(Context context, String url){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME_LOADING_IMG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("img_url", url);
        editor.commit();
    }
}