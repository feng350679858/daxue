package com.jingcai.apps.aizhuan.activity.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.base.BaseFragmentActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDetailActivity;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob38.Partjob38Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;

/**
 * Created by lejing on 15/8/15.
 */
public class BaseReceiver extends BroadcastReceiver {
    public static final String ACTION_JISHI_DISPATCH_REQUEST = "action_jishi_dispath_request";
    private Activity baseActivity;
    private BaseReceiverHander baseHandler;
    private final NotificationManager notificationManager;

    public BaseReceiver(Activity activity) {
        this.baseActivity = activity;
        this.baseHandler = new BaseReceiverHander(activity);
        notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        {
            int notifactionid = intent.getIntExtra("notifactionid", -1);
            if (notifactionid > 0) {
                notificationManager.cancel(notifactionid);
            }
        }
        String notiicationtype = intent.getStringExtra("notiicationtype");
        if("2".equals(notiicationtype)) {//用户即时型求助发布成功 推送帮助者
            String helpid = intent.getStringExtra("helpid");
            String studentid = intent.getStringExtra("studentid");
            long timestamp = intent.getLongExtra("timestamp", -1);
            doReceiveHelp(helpid, studentid, timestamp);
        }else if("3".equals(notiicationtype)) {//用户即时型求助被人主动接单 推送发单者
            String helpid = intent.getStringExtra("helpid");
            String studentid = intent.getStringExtra("studentid");
            baseHandler.postMessage(31, new Object[]{helpid, studentid});
        }
    }

    private void doReceiveHelp(final String helpid, final String studentid, long timestamp) {
        /**
         * 超过30秒，直接超时
         */
        if(timestamp>0 && (System.currentTimeMillis() - timestamp)>10 * 1000){
            baseHandler.postMessage(22, new Object[]{helpid, studentid});
            Log.d("==", "------请求接单，超过30秒，直接超时");
            return;
        }
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob38Request req = new Partjob38Request(helpid, studentid);
                new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        if ("0".equals(resp.getResultCode())) {
                            baseHandler.postMessage(21, new Object[]{helpid, studentid});
                        } else {
                            baseHandler.postMessage(22, new Object[]{helpid, studentid});
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        baseHandler.postMessage(22, new Object[]{helpid, studentid});
                    }
                });
            }
        });
    }

    class BaseReceiverHander extends BaseHandler {
        public BaseReceiverHander(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 21: {
                    try {
                        Object[] objs = (Object[]) msg.obj;
                        String helpid = String.valueOf(objs[0]);
                        String studentid = String.valueOf(objs[1]);
//                        IOSPopWin win = new IOSPopWin(baseFragmentActivity);
//                        win.showWindow("你收到一条新的求助", "求助。。。。", "查看详情");
                        PopJishiDispatchRequestWin win = new PopJishiDispatchRequestWin(baseActivity);
                        win.setTitle("你收到一条新的求助");
                        win.setActionText("确认接单");
                        win.show(studentid);
                    } finally {
                    }
                    break;
                }
                case 22: {
                    try {
                        Object[] objs = (Object[]) msg.obj;
                        //String helpid = String.valueOf(objs[0]);
                        String studentid = String.valueOf(objs[1]);
                        PopJishiDispatchRequestWin win = new PopJishiDispatchRequestWin(baseActivity);
                        win.setTitle("你收到的单子已超时");
                        win.setActionText("真遗憾啊");
                        win.show(studentid);
                    } finally {
                    }
                    break;
                }
                case 31: {
                    try {
                        Object[] objs = (Object[]) msg.obj;
                        final String helpid = String.valueOf(objs[0]);
                        String studentid = String.valueOf(objs[1]);
                        final PopJishiDispatchRequestWin win = new PopJishiDispatchRequestWin(baseActivity);
                        win.setTitle("你的求助已被接单");
                        win.setActionText("查看详情");
                        win.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                win.dismiss();
                                Intent intent = new Intent(baseActivity, HelpJishiDetailActivity.class);
                                intent.putExtra("helpid", helpid);
                                intent.putExtra("type", "1");
                                baseActivity.startActivity(intent);
                            }
                        });
                        win.show(studentid);
                    } finally {
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

}