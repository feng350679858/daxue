package com.jingcai.apps.aizhuan.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.Md5;
import com.jingcai.apps.aizhuan.util.VolleyQueueUtil;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lejing on 15/4/27.
 */
public class AzService {
    private final String TAG = AzService.class.getName();
    private final static String url = GlobalConstant.azserverUrl + "/service/json";
    private final String TYPE_UTF8_CHARSET = "charset=UTF-8";
    private final String BODY_CONTENT_TYPE = "text/plain;charset=UTF-8";
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private RequestQueue queue;

    public AzService() {
        queue = VolleyQueueUtil.getInstance().getRequestQueue();
    }

    public AzService(Context context) {
        this();
    }

    public <Req extends BaseRequest, Resp extends BaseResponse> void doTrans(final Req req, final Class<Resp> cls, final Callback<Resp> callback) {
        if (null == callback) {
            throw new RuntimeException("callback不能为空");
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String respJson) {
                Log.d("==[json received]", respJson);
                Resp res = null;
                try {
                    res = new Gson().fromJson(respJson, (Type) cls);
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
                    } else {
                        try {
                            callback.success(res);
                        } catch (Exception e) {//业务未知异常
                            callback.fail(new AzException(1000, e.getMessage()));
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                callback.fail(new AzException(1000, e.getMessage()));
            }
        }) {
            @Override
            public String getBodyContentType() {
                return BODY_CONTENT_TYPE;
            }

            /**
             * 重写parseNetworkResponse方法改变返回头参数解决乱码问题
             * 主要是看服务器编码，如果服务器编码不是UTF-8的话那么就需要自己转换，反之则不需要
             */
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String type = response.headers.get(HTTP.CONTENT_TYPE);
                    if (type == null) {
                        type = TYPE_UTF8_CHARSET;
                        response.headers.put(HTTP.CONTENT_TYPE, type);
                    } else if (!type.contains("UTF-8")) {
                        type += ";" + TYPE_UTF8_CHARSET;
                        response.headers.put(HTTP.CONTENT_TYPE, type);
                    }
                } catch (Exception e) {
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return getReqBytes(req, getParamsEncoding());
            }
        };
        // 设置超时时间
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
        // 将请求加入队列
        queue.add(stringRequest);
    }

    private byte[] getReqBytes(BaseRequest reqBody, String encoding) {
        RequestHead reqHead = new RequestHead();
        reqHead.setTradecode(reqBody.getTranscode());
        reqHead.setTraceno(String.valueOf(System.currentTimeMillis()));
        reqHead.setChannel(GlobalConstant.TERMINAL_TYPE_ANDROID);
        reqHead.setVersion(GlobalConstant.SDK_VERSION);
        reqHead.setRequesttime(sdf.format(new Date()));
        StringBuffer buff = new StringBuffer(new Gson().toJson(reqBody)).append(reqHead.getChannel()).append(GlobalConstant.SECRET_KEY);
        reqHead.setSign(Md5.encode(buff.toString()));
        RequestData requestData = new RequestData();
        requestData.setHead(reqHead);
        requestData.setBody(reqBody);
        String json = new Gson().toJson(requestData);
        Log.d("==[json send]", json);

        try {
            return json.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("TAG", "编码出错" + encoding);
            return null;
        }
    }

    public interface Callback<T> {
        void success(T resp);

        void fail(AzException e);
    }

}
