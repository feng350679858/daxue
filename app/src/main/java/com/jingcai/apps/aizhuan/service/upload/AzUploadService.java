package com.jingcai.apps.aizhuan.service.upload;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.VolleyQueueUtil;

import java.io.ByteArrayOutputStream;

/**
 * Created by lejing on 15/5/8.
 */
public class AzUploadService {
    private final static String url = GlobalConstant.azserverUrl + "/service/json/uploadImg";
    private RequestQueue queue;

    public AzUploadService() {
        queue = VolleyQueueUtil.getInstance().getRequestQueue();
    }

    public void doTrans(String studentid, Bitmap bitmap, final AzUploadService.Callback callback) {
        MultipartRequest multipartRequest = new MultipartRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String respJson) {
                Log.d("==[json received]", respJson);
                ImageResponse res = null;
                try {
                    res = new Gson().fromJson(respJson, ImageResponse.class);
                } catch (Exception e) {
                    res = null;
                }
                if (null == res) {
                    callback.fail(new AzException(1001, "解析字符串失败：" + respJson));
                } else {
                    ResponseResult result = res.getResult();
                    if ("10005".equals(result.getCode())) {//未登录，异常
                        callback.fail(new AzException(1002, "系统未登录"));
                    } else if ("10006".equals(result.getCode())) {//超时
                        callback.fail(new AzException(1003, "请求超时"));
                    } else if("0".equals(result.getCode())){
                        try {
                            callback.success(res.getBody().getFile().getPath());
                        } catch (Exception e) {//业务未知异常
                            callback.fail(new AzException(1000, e.getMessage()));
                        }
                    }else{
                        callback.fail(new AzException(1000, "上传图片异常"));
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                callback.fail(new AzException(1000, e.getMessage()));
            }
        });
        // 获取MultipartEntity对象
        MultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
        multipartEntity.addStringPart("studentid", studentid);
        multipartEntity.addImagePart("headImg", bitmapToBytes(bitmap));
        // 将请求添加到队列中
        queue.add(multipartRequest);
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    public interface Callback {
        void success(String logopath);

        void fail(AzException e);
    }
}
