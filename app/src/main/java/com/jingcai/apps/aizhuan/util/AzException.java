package com.jingcai.apps.aizhuan.util;

/**
 * Created by lejing on 15/4/28.
 */
public class AzException extends Exception {
    private int code;
    private String message;

    public AzException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
