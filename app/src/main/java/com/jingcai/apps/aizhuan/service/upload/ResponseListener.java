package com.jingcai.apps.aizhuan.service.upload;

import com.android.volley.Response;

/**
 * Created by lejing on 15/5/8.
 */
public interface ResponseListener extends Response.ErrorListener {
    void onResponse(String response);
}
