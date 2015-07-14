package com.jingcai.apps.aizhuan.entity;

/**
 * Created by Json Ding on 2015/7/14.
 */
public class TestCommentsBean {
    private String logourl;
    private String name;
    private String level;
    private String content;
    private String time;

    private String reply;
    private String refname;
    private String reflogourl;
    private String refcontent;

    public TestCommentsBean(){}

    public TestCommentsBean(String logourl, String name, String level, String content, String time, String reply, String refname, String reflogourl, String refcontent) {
        this.logourl = logourl;
        this.name = name;
        this.level = level;
        this.content = content;
        this.time = time;
        this.reply = reply;
        this.refname = refname;
        this.reflogourl = reflogourl;
        this.refcontent = refcontent;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getRefname() {
        return refname;
    }

    public void setRefname(String refname) {
        this.refname = refname;
    }

    public String getReflogourl() {
        return reflogourl;
    }

    public void setReflogourl(String reflogourl) {
        this.reflogourl = reflogourl;
    }

    public String getRefcontent() {
        return refcontent;
    }

    public void setRefcontent(String refcontent) {
        this.refcontent = refcontent;
    }
}
