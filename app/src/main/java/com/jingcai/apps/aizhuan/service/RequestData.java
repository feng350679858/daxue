package com.jingcai.apps.aizhuan.service;

import com.google.gson.annotations.SerializedName;

public class RequestData {
    private RequestHead head;
    @SerializedName("body")
    private Object result;

    public RequestHead getHead() {
        return head;
    }

    public void setHead(RequestHead head) {
        this.head = head;
    }

    public Object getBody() {
        return result;
    }

    public void setBody(Object body) {
        this.result = body;
    }
}