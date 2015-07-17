package com.jingcai.apps.aizhuan.jpush;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by Administrator on 2015/7/17.
 */
public class JpushUtil {
    private final String TAG = "JpushUtil";
    private final int MSG_SET_ALIAS = 101;
    private Context ctx;

    public JpushUtil(Context c) {
        this.ctx = c;
    }

    private TagAliasCallback aliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0: {
                    Log.i(TAG, "Set tag and alias success");
                    break;
                }
                case 6002: {
                    Log.i(TAG, "Failed to set alias and tags due to timeout. Try again after 30s.");
                    if (isConnected(ctx)) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 30);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;
                }
                default:
                    Log.e(TAG, "Failed with errorCode = " + code);
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(ctx, (String) msg.obj, null, aliasCallback);
                    break;
                default: {
                    Log.i(TAG, "Unhandled msg - " + msg.what);
                }
            }
        }
    };

    private boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public void init() {
        JPushInterface.init(ctx); // 初始化 JPush
        if (GlobalConstant.debugFlag) {
            JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
        }
        //晚上10：30点到第二天早上8：30点为静音时段
        JPushInterface.setSilenceTime(ctx, 22, 30, 8, 30);
    }

    public void login(String studentid) {
        JPushInterface.setAliasAndTags(ctx, studentid, null, aliasCallback);
        if (JPushInterface.isPushStopped(ctx)) {
            JPushInterface.resumePush(ctx);
        }
    }

    public void logout() {
        //退出登录后，停止推送
        if (!JPushInterface.isPushStopped(ctx)) {
            JPushInterface.stopPush(ctx);
        }
    }
}
