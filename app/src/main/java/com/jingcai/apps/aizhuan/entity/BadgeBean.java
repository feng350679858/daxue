package com.jingcai.apps.aizhuan.entity;

import java.io.Serializable;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class BadgeBean implements Serializable {
    private Type type;
    private int count;
    private String targetActivity;
    private int targetViewId;
    private boolean setVisiable;

    public BadgeBean(Type type, int count, String targetActivity, int targetViewId, boolean setVisiable) {
        this.type = type;
        this.count = count;
        this.targetActivity = targetActivity;
        this.targetViewId = targetViewId;
        this.setVisiable = setVisiable;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
    }

    public int getTargetViewId() {
        return targetViewId;
    }

    public void setTargetViewId(int targetViewId) {
        this.targetViewId = targetViewId;
    }

    public boolean isSetVisiable() {
        return setVisiable;
    }

    public void setSetVisiable(boolean setVisiable) {
        this.setVisiable = setVisiable;
    }

    public enum Type {
        TEXT, NORMAL
    }
}
