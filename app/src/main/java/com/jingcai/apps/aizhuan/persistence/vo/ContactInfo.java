package com.jingcai.apps.aizhuan.persistence.vo;

/**
 * 消息中会话列表的联系人信息
 * Created by Json Ding on 2015/7/27.
 */
public class ContactInfo {
    private String studentid;
    private String name;
    private String logourl;
    private long lastUpdate;

    public ContactInfo() {
        this.lastUpdate = System.currentTimeMillis();
    }

    public ContactInfo(String studentid, String name, String logourl) {

        this(studentid,name,logourl,System.currentTimeMillis());
    }

    public ContactInfo(String studentid, String name, String logoUrl, long lastUpdate) {
        this.studentid = studentid;
        this.name = name;
        this.logourl = logoUrl;
        this.lastUpdate = lastUpdate;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }
}
