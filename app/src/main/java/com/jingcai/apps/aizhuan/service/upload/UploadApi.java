package com.jingcai.apps.aizhuan.service.upload;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moon.zhong on 2015/3/3.
 */
public class UploadApi {
    private final static String url = GlobalConstant.azserverUrl + "/service/json/upload";

    private Context context;
    private RequestQueue queue;

    public UploadApi(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
        // 开始发起请求
        //queue.start();
    }

    /**
     * 上传图片接口
     *
     * @param bitmap   需要上传的图片
     * @param listener 请求回调
     */
    public void uploadImg(Context context, Bitmap bitmap, ResponseListener listener) {
        List<FormImage> imageList = new ArrayList<FormImage>();
        imageList.add(new FormImage(bitmap));
        Request request = new PostUploadRequest(url, imageList, listener);
        this.queue = Volley.newRequestQueue(context);
        this.queue.add(request);
    }
}