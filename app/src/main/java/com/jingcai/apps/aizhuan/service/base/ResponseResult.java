package com.jingcai.apps.aizhuan.service.base;

public class ResponseResult {
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String toString(){
        return "code:"+code + "\t"+"message:"+message;
    }
}