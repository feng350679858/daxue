package com.jingcai.apps.aizhuan.service;

import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

/**
 * Created by lejing on 15/1/28.
 */
public class RequestHead {
    private String tradecode;
    private String traceno;
    private String channel;
    private String version;
    private String requesttime;
    private String sign;

    public String getTradecode() {
        return tradecode;
    }

    public void setTradecode(String tradecode) {
        this.tradecode = tradecode;
    }

    public String getTraceno() {
        return traceno;
    }

    public void setTraceno(String traceno) {
        this.traceno = traceno;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getVersion() {
        return GlobalConstant.SDK_VERSION;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRequesttime() {
        return requesttime;
    }

    public void setRequesttime(String requesttime) {
        this.requesttime = requesttime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
