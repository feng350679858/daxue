package com.jingcai.apps.aizhuan.service.upload;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * MultipartRequest，返回的结果是String格式的
 *
 * @author mrsimple
 */
public class MultipartRequest extends Request<String> {
    private final String TYPE_UTF8_CHARSET = "charset=UTF-8";
    private final String BODY_CONTENT_TYPE = "text/plain;charset=UTF-8";
    private final Response.Listener mListener;
    private final MultipartEntity mMultiPartEntity = new MultipartEntity();

    public MultipartRequest(String url, Response.Listener mListener, Response.ErrorListener listener) {
        super(Method.POST, url, listener);
        this.mListener = mListener;;
        //设置请求的响应事件，因为文件上传需要较长的时间，所以在这里加大了，设为10秒
        setRetryPolicy(new DefaultRetryPolicy(10*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * @return
     */
    public MultipartEntity getMultiPartEntity() {
        return mMultiPartEntity;
    }

    @Override
    public String getBodyContentType() {
        return mMultiPartEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 将mMultiPartEntity中的参数写入到bos中
            mMultiPartEntity.writeTo(bos);
        } catch (IOException e) {
            Log.e("", "IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
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
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

//    {
//        MultipartRequest multipartRequest = new MultipartRequest("http://服务器地址", new Response.Listener() {
//
//            @Override
//            public void onStart() {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onComplete(int stCode, String response, String errMsg) {
//
//            }
//        });
//        // 获取MultipartEntity对象
//        MultipartEntity multipartEntity = multipartRequest.getMultiPartEntity();
//        multipartEntity.addStringPart("content", "hello");
//        //
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
//        // bitmap参数
//        multipartEntity.addBinaryPart("images", bitmapToBytes(bitmap));
//        // 文件参数
//        multipartEntity.addFilePart("images", new File("storage/emulated/0/test.jpg"));
//
//        // 构建请求队列
//        RequestQueue queue = RequestQueue.newRequestQueue(Context);
//        // 将请求添加到队列中
//        queue.addRequest(multipartRequest);
//    }
}