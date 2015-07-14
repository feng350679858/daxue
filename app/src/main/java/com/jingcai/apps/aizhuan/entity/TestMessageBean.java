package com.jingcai.apps.aizhuan.entity;

/**
 * 这个对象为测试对象，暂时的替代从服务端取来的消息对象<br/>
 * 请勿使用这个类
 * Created by Json Ding on 2015/7/13.
 */
@Deprecated
public class TestMessageBean {
    private String logourl;
    private String name;
    private String content;
    private String time;
    private String unread;

    public TestMessageBean(){}

    public TestMessageBean(String logourl, String name, String content, String time, String unread) {
        this.logourl = logourl;
        this.name = name;
        this.content = content;
        this.time = time;
        this.unread = unread;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }
}
