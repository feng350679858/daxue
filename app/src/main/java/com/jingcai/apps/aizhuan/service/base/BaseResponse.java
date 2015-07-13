package com.jingcai.apps.aizhuan.service.base;

/**
 * Created by lejing on 15/4/27.
 */
public class BaseResponse<T> {
    private T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    private ResponseResult result;

    public ResponseResult getResult() {
        return result;
    }

    public void setResult(ResponseResult result) {
        this.result = result;
    }

    public String getResultCode() {
        if (null != result) {
            return result.getCode();
        }
        return null;
    }

    public String getResultMessage() {
        if (null != result) {
            return result.getMessage();
        }
        return null;
    }
}
