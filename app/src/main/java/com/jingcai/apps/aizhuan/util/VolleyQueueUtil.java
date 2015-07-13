package com.jingcai.apps.aizhuan.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by lejing on 15/5/8.
 */
public class VolleyQueueUtil {
    private static VolleyQueueUtil instance;
    private RequestQueue queue;

    private VolleyQueueUtil() {
    }

    public static VolleyQueueUtil getInstance() {
        if (null == instance) {
            instance = new VolleyQueueUtil();
        }
        return instance;
    }

    public void init(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue() {
        if (null == queue) {
            throw new RuntimeException("the volley request queue do not initPlatform");
        }
        return queue;
    }
}
