package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.util.PayPwdWin;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaRewardActivity extends BaseActivity {
    private static final String TAG = "HelpWendaCommnet";
    private MessageHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.help_wenda_reward);

        initHeader();

        initView();
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("打赏");

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private PayPwdWin payPwdWin;
    private void initView() {
        findViewById(R.id.btn_reward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == payPwdWin) {
                    payPwdWin = new PayPwdWin(HelpWendaRewardActivity.this);
                    payPwdWin.setCallback(new PayPwdWin.Callback() {
                        @Override
                        public void finishInput(String pwd) {
                            showToast("密码输入完毕");
                        }
                    });
                    payPwdWin.setTitle("支付密码输入");
                }
                payPwdWin.show();
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
