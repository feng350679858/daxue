package com.jingcai.apps.aizhuan.persistence;

/**
 * Created by lejing on 15/4/28.
 */
public class UserSubject {

    private static String studentid;
    private static String phone;
    private static String password;
    private static String name;
    private static String gender;
    private static String logourl;
    private static String schoolname;
    private static String cityname;
    private static String isvisiable;
    private static String level;

    private static boolean loginFlag = false;

    public static boolean isLogin(){
        return loginFlag;
    }

    public static void setLoginFlag(boolean loginFlag) {
        UserSubject.loginFlag = loginFlag;
    }

    public static String getStudentid() {
        return studentid;
    }

    public static void setStudentid(String studentid) {
        UserSubject.studentid = studentid;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        UserSubject.phone = phone;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        UserSubject.password = password;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        UserSubject.name = name;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        UserSubject.gender = gender;
    }

    public static String getLogourl() {
        return logourl;
    }

    public static void setLogourl(String logourl) {
        UserSubject.logourl = logourl;
    }

    public static String getSchoolname() {
        return schoolname;
    }

    public static void setSchoolname(String schoolname) {
        UserSubject.schoolname = schoolname;
    }

    public static String getCityname() {
        return cityname;
    }

    public static void setCityname(String cityname) {
        UserSubject.cityname = cityname;
    }

    public static String getIsvisiable() {
        return isvisiable;
    }

    public static void setIsvisiable(String isvisiable) {
        UserSubject.isvisiable = isvisiable;
    }

    public static String getLevel() {
        return level;
    }


    public static void setLevel(String level) {
        UserSubject.level = level;
    }
}
