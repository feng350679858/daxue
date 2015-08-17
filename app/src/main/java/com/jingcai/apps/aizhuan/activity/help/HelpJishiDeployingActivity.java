package com.jingcai.apps.aizhuan.activity.help;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob20.Partjob20Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/7/22.
 */
public class HelpJishiDeployingActivity extends BaseActivity {
    public static final String ACTION_JISHI_DEPLOY_SUCCESS = "action_jishi_deploy_success";  //更新badge的广播
    private String helpid;
    private MessageHandler messageHandler;
    private boolean stopFlag = false;

    private TextView tv_cancel;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            {
                int notifactionid = intent.getIntExtra("notifactionid", -1);
                if(notifactionid>0) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(notifactionid);
                }
            }
            String deployedHelpid = intent.getStringExtra("helpid");
            if (helpid.equals(deployedHelpid)) {
                String studentid = intent.getStringExtra("studentid");
                messageHandler.postMessage(2, studentid);
            }
        }
    };
    private TextView tv_left_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpid = getIntent().getStringExtra("helpid");
        if (StringUtil.isEmpty(helpid)) {
            finish();
        } else {
            Log.d("==", "---------helpid="+helpid);
            messageHandler = new MessageHandler(this);

            setContentView(R.layout.help_jishi_deploying);

            initView();

            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 59; i > 0; i--) {
                            if(stopFlag){
                                return;
                            }
                            messageHandler.postMessage(3, i);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    messageHandler.postMessage(3, -1);
                }
            });
        }
    }

    private void initView() {
        tv_left_time = (TextView) findViewById(R.id.tv_left_time);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCancel();
            }
        });
        ImageView tv_loading = (ImageView) findViewById(R.id.tv_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) tv_loading.getDrawable();
        animationDrawable.start();

//        tv_loading.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ACTION_JISHI_DEPLOY_SUCCESS);
//                intent.putExtra("helpid", helpid);
//                sendBroadcast(intent);
//            }
//        });
    }

    private void doCancel() {
        if (!actionLock.tryLock()) return;
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob20Request req = new Partjob20Request();
                Partjob20Request.Parttimejob job = req.new Parttimejob();
                job.setHelpid(helpid);
                job.setPeroid("0");//0：发布过程中取消  1：发布成功后取消
                job.setStudentid(UserSubject.getStudentid());
                req.setParttimejob(job);
                new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(0);
                        } else {
                            messageHandler.postMessage(1, "取消求助失败:" + resp.getResultMessage());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    try {
                        showToast("成功取消求助");
                        finish();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast(String.valueOf(msg.obj), 0);
                        finish();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    String studentid = String.valueOf(msg.obj);
                    Intent intent = new Intent(HelpJishiDeployingActivity.this, HelpJishiDeploySuccessActivity.class);
                    intent.putExtra("helpid", helpid);
                    intent.putExtra("studentid", studentid);
                    startActivity(intent);
                    finish();
                    break;
                }
                case 3: {
                    int leftseconds = (int) msg.obj;
                    if(leftseconds>0){
                        tv_left_time.setText(leftseconds + "s");
                    }else {
                        tv_left_time.setText("0s");
                        Intent intent = new Intent(HelpJishiDeployingActivity.this, HelpJishiDeployFailActivity.class);
                        intent.putExtra("helpid", helpid);
                        startActivity(intent);
                        finish();
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        registerReceiver(mReceiver, new IntentFilter(ACTION_JISHI_DEPLOY_SUCCESS));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopFlag = true;
        super.onDestroy();
    }
}
