package com.jingcai.apps.aizhuan.entity;

/**
 * Created by Json Ding on 2015/7/13.
 */
public class MessageCategoryBean {
    private int logoResId;
    private String title;
    private int badgeCount;

    public MessageCategoryBean(){}

    public MessageCategoryBean(int logoResId, String title, int badgeCount) {
        this.logoResId = logoResId;
        this.title = title;
        this.badgeCount = badgeCount;
    }

    public int getLogoResId() {
        return logoResId;
    }

    public void setLogoResId(int logoResId) {
        this.logoResId = logoResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }
}
