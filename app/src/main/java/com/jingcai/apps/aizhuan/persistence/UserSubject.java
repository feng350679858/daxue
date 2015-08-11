package com.jingcai.apps.aizhuan.persistence;

import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;

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
    private static boolean haspaypasswordset;
    private static boolean loginFlag = false;
    private static boolean onlineFlag = false;
    private static String idnoauthflag;
    private static String scnoauthflag;
    private static String collegename;

    public static boolean isLogin() {
        return loginFlag;
    }

    private static void setLoginFlag(boolean loginFlag) {
        UserSubject.loginFlag = loginFlag;
    }

    public static String getStudentid() {
        return studentid;
    }

    private static void setStudentid(String studentid) {
        UserSubject.studentid = studentid;
    }

    public static String getPhone() {
        return phone;
    }

    private static void setPhone(String phone) {
        UserSubject.phone = phone;
    }

    public static String getPassword() {
        return password;
    }

    private static void setPassword(String password) {
        UserSubject.password = password;
    }

    public static String getName() {
        return name;
    }

    private static void setName(String name) {
        UserSubject.name = name;
    }

    public static String getGender() {
        return gender;
    }

    private static void setGender(String gender) {
        UserSubject.gender = gender;
    }

    public static String getLogourl() {
        return logourl;
    }

    private static void setLogourl(String logourl) {
        UserSubject.logourl = logourl;
    }

    public static String getSchoolname() {
        return schoolname;
    }

    private static void setSchoolname(String schoolname) {
        UserSubject.schoolname = schoolname;
    }

    public static String getCityname() {
        return cityname;
    }

    private static void setCityname(String cityname) {
        UserSubject.cityname = cityname;
    }

    public static String getIsvisiable() {
        return isvisiable;
    }

    private static void setIsvisiable(String isvisiable) {
        UserSubject.isvisiable = isvisiable;
    }

    public static String getLevel() {
        return level;
    }

    private static void setLevel(String level) {
        UserSubject.level = level;
    }

    public static boolean getOnlineFlag() {
        return onlineFlag;
    }

    public static boolean getHaspaypasswordset() {
        return haspaypasswordset;
    }

    private static void setHaspaypasswordset(boolean haspaypasswordset) {
        UserSubject.haspaypasswordset = haspaypasswordset;
    }

    public static String getIdnoauthflag() {
        return idnoauthflag;
    }

    private static void setIdnoauthflag(String idnoauthflag) {
        UserSubject.idnoauthflag = idnoauthflag;
    }

    public static String getScnoauthflag() {
        return scnoauthflag;
    }

    private static void setScnoauthflag(String scnoauthflag) {
        UserSubject.scnoauthflag = scnoauthflag;
    }

    public static String getCollegename() {
        return collegename;
    }

    private static void setCollegename(String collegename) {
        UserSubject.collegename = collegename;
    }

    public static void init(Preferences instance) {
        loginFlag = instance.getBoolean(Preferences.PARAM_LOGIN_FLAG, false);
        if(!loginFlag){
            name = null;
            level = "0";
            schoolname = null;
            logourl = null;
            onlineFlag = false;
        }else{
            name = instance.getString(Preferences.PARAM_NAME, "");
            level = instance.getString(Preferences.PARAM_LEVEL, "");
            schoolname = instance.getString(Preferences.PARAM_SCHOOL_NAME, "");
            logourl = instance.getString(Preferences.PARAM_LOGOURL, "");
            onlineFlag = instance.getBoolean(Preferences.PARAM_ONLINE_FLAG, false);
        }
        studentid = instance.getString(Preferences.PARAM_STUDENTID, "");
        phone = instance.getString(Preferences.PARAM_PHONE, "");
        password = instance.getString(Preferences.PARAM_PASSPORD, "");
        gender = instance.getString(Preferences.PARAM_GENDER, "");
        cityname = instance.getString(Preferences.PARAM_CITY_NAME, "");
        isvisiable = instance.getString(Preferences.PARAM_ISVISIABLE, "");
        haspaypasswordset = instance.getBoolean(Preferences.PARAM_HASPAYPASSWORDSET, false);
        idnoauthflag = instance.getString(Preferences.PARAM_IDNOAUTHFLAG, "0");
        scnoauthflag = instance.getString(Preferences.PARAM_SCNOAUTHFLAG, "0");
        collegename = instance.getString(Preferences.PARAM_COLLEGENAME,"");
    }


    public static void loginSuccess(Stu02Response.Stu02Body.Student stu) {
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
        loginFlag = true;
        onlineFlag = "1".equals(stu.getOnlineflag());
        haspaypasswordset = "1".equals(stu.getHaspaypasswordset());
        idnoauthflag = stu.getIdnoauthflag()==null?"0":stu.getIdnoauthflag();
        scnoauthflag = stu.getScnoauthflag()==null?"0":stu.getScnoauthflag();
        collegename = stu.getCollegename();

        Preferences pref = Preferences.getInstance();
        pref.update(Preferences.PARAM_STUDENTID, studentid);
        pref.update(Preferences.PARAM_PHONE, phone);
        pref.update(Preferences.PARAM_PASSPORD, password);
        pref.update(Preferences.PARAM_NAME, name);
        pref.update(Preferences.PARAM_GENDER, gender);
        pref.update(Preferences.PARAM_LOGOURL, logourl);
        pref.update(Preferences.PARAM_SCHOOL_NAME, schoolname);
        pref.update(Preferences.PARAM_CITY_NAME, cityname);
        pref.update(Preferences.PARAM_ISVISIABLE, isvisiable);
        pref.update(Preferences.PARAM_LEVEL, level);
        pref.update(Preferences.PARAM_LOGIN_FLAG, loginFlag);
        pref.update(Preferences.PARAM_ONLINE_FLAG, onlineFlag);
        pref.update(Preferences.PARAM_HASPAYPASSWORDSET, haspaypasswordset);
        pref.update(Preferences.PARAM_IDNOAUTHFLAG,idnoauthflag);
        pref.update(Preferences.PARAM_SCNOAUTHFLAG,scnoauthflag);
        pref.update(Preferences.PARAM_COLLEGENAME,collegename);
    }


    public static void loginFail() {
        name = null;
        level = "0";
        schoolname = null;
        logourl = null;
        loginFlag = false;
        onlineFlag = false;

        Preferences.getInstance().update(Preferences.PARAM_LOGIN_FLAG, loginFlag);
    }

    /**
     * 修改上下线状态
     * @param onlineFlag
     */
    public static void setOnlineFlag(boolean onlineFlag) {
        UserSubject.onlineFlag = onlineFlag;
        Preferences pref = Preferences.getInstance();
        pref.update(Preferences.PARAM_ONLINE_FLAG, onlineFlag);
    }

    /**
     * 已设置支付密码
     */
    public static void setHasPayPassword(){
        UserSubject.haspaypasswordset = true;
        Preferences pref = Preferences.getInstance();
        pref.update(Preferences.PARAM_HASPAYPASSWORDSET, true);
    }

    /**
     * 更改头像
     */
    public static void updateLogourl(String logourl){
        UserSubject.setLogourl(logourl);
        Preferences pref = Preferences.getInstance();
        pref.update(Preferences.PARAM_LOGOURL, logourl);
    }

    /**
     *更新身份验证Flag状态
     * 0-否 1-是 2-认证中
     * @param flag flag
     */
    public static void updateIdAuthFlag(String flag){
        UserSubject.setIdnoauthflag(flag);
        Preferences pref = Preferences.getInstance();
        pref.update(Preferences.PARAM_IDNOAUTHFLAG, flag);
    }

    /**
     *更新学生验证Flag状态
     * 0-否 1-是 2-认证中
     * @param flag flag
     */
    public static void updateScAuthFlag(String flag){
        UserSubject.setScnoauthflag(flag);
        Preferences pref = Preferences.getInstance();
        pref.update(Preferences.PARAM_SCNOAUTHFLAG, flag);
    }
    /**
     *更改level
     */
    public static void setLevel(){
        UserSubject.level = "1";
        Preferences pref = Preferences.getInstance();
        pref.update(Preferences.PARAM_LEVEL, true);
    }
}
