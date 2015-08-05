package com.jingcai.apps.aizhuan.activity.mine.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.util.PayPwdWin;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;

import java.util.Date;

/**
 * Created by lejing on 15/7/24.
 */
public class MineHelpJishiActivity extends BaseActivity {
    private boolean provideFlag;
    private MessageHandler messageHandler;
    private CheckBox cb_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.mine_help_jishi);

        provideFlag = getIntent().getBooleanExtra("provideFlag", true);

        initHeader();

        initView();

        new AzExecutor().execute(timeTask);
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("求助详情");

        final ImageView iv_func = (ImageView) findViewById(R.id.iv_func);
        iv_func.setVisibility(View.VISIBLE);
        iv_func.setImageResource(R.drawable.icon__header_more);
        iv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dp10_px = MineHelpJishiActivity.this.getResources().getDimensionPixelSize(R.dimen.dp_10);
                Log.d("==", "---------" + dp10_px);
                View contentView = LayoutInflater.from(MineHelpJishiActivity.this).inflate(R.layout.help_wenda_answer_setting_pop, null);
                PopupWin groupWin = PopupWin.Builder.create(MineHelpJishiActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(iv_func)
                        .setContentView(contentView)
                        .build();
                {
                    View tv_pop_abuse_report = groupWin.findViewById(R.id.tv_pop_abuse_report);
                    tv_pop_abuse_report.setVisibility(View.VISIBLE);
                    tv_pop_abuse_report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//举报
                            Log.d("==", "-----------tv_pop_abuse_report---");
                        }
                    });
                }
                {
                    View tv_pop_share = groupWin.findViewById(R.id.tv_pop_share);
                    tv_pop_share.setVisibility(View.VISIBLE);
                    tv_pop_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//分享
                            Log.d("==", "-----------tv_pop_share---");
                        }
                    });
                }
                groupWin.show(Gravity.TOP | Gravity.RIGHT, dp10_px, dp10_px * 6);
            }
        });

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        cb_tip = (CheckBox)findViewById(R.id.cb_tip);
//        cb_tip.setChecked(false);

        findViewById(R.id.btn_view_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayPwdWin payPwdWin = new PayPwdWin(MineHelpJishiActivity.this);
                payPwdWin.setTitle("确认结算");
                payPwdWin.setCallback(new PayPwdWin.Callback() {
                    @Override
                    public void call(String pwd) {
                        showToast("----------");
                    }
                });
                payPwdWin.showPay("3.51元");
            }
        });
        findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentView = MineHelpJishiActivity.this.getWindow().getDecorView();
                View contentView = LayoutInflater.from(MineHelpJishiActivity.this).inflate(R.layout.mine_help_jishi_connect, null);

                PopupWin groupWin = PopupWin.Builder.create(MineHelpJishiActivity.this)
                        .setParentView(parentView)
                        .setContentView(contentView)
                        .build();
                groupWin.show();
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
                    cb_tip.setText(DateUtil.formatDate(new Date(), "剩余时间 HH:mm:ss"));
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private boolean timerStopFlag = false;
    private Runnable timeTask = new Runnable() {
        @Override
        public void run() {
            Log.d("==", "-------start timer------");
            while (!timerStopFlag){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messageHandler.postMessage(0);
            }
            Log.d("==", "-------stop timer------");
        }
    };

    @Override
    protected void onStop() {
        timerStopFlag = true;
        super.onStop();
    }
}
