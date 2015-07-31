package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaAnswerActivity extends BaseActivity {
    private static final String TAG = "HelpWenddaAnswer";
    private MessageHandler messageHandler;
    private View tv_reward, tv_reedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.help_wenda_answer);

        initHeader();

        initView();
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("详细答案");

        final ImageView iv_func = (ImageView) findViewById(R.id.iv_func);
        iv_func.setVisibility(View.VISIBLE);
        iv_func.setImageResource(R.drawable.icon__header_more);
        iv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dp10_px = HelpWendaAnswerActivity.this.getResources().getDimensionPixelSize(R.dimen.dp_10);
                Log.d(TAG, "---------" + dp10_px);
                View contentView = LayoutInflater.from(HelpWendaAnswerActivity.this).inflate(R.layout.help_wenda_answer_setting_pop, null);
                PopupWin groupWin = PopupWin.Builder.create(HelpWendaAnswerActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(iv_func)
                        .setContentView(contentView)
                        .build();
                {
                    View tv_pop_abuse_report = groupWin.findViewById(R.id.tv_pop_abuse_report);//举报
                    tv_pop_abuse_report.setVisibility(View.VISIBLE);
                    tv_pop_abuse_report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("==", "-----------tv_pop_abuse_report---");
                        }
                    });
                }
                {
                    View tv_pop_anonymous = groupWin.findViewById(R.id.tv_pop_anonymous);//使用匿名
                    tv_pop_anonymous.setVisibility(View.VISIBLE);
                    tv_pop_anonymous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("==", "-----------tv_pop_anonymous---");
                        }
                    });
                }
                {
                    View tv_pop_reedit = groupWin.findViewById(R.id.tv_pop_reedit);//再次编辑
                    tv_pop_reedit.setVisibility(View.VISIBLE);
                    tv_pop_reedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("==", "-----------tv_pop_reedit---");
                        }
                    });
                }
                {
                    View tv_pop_reward = groupWin.findViewById(R.id.tv_pop_reward);//打赏
                    tv_pop_reward.setVisibility(View.VISIBLE);
                    tv_pop_reward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("==", "-----------tv_pop_reward---");
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
        boolean flag = System.currentTimeMillis() % 2 == 0;
        if (flag) {
            (tv_reward = findViewById(R.id.tv_reward)).setVisibility(View.VISIBLE);
            (tv_reedit = findViewById(R.id.tv_reedit)).setVisibility(View.GONE);

            //打赏
            findViewById(R.id.layout_wenda_answer_reward).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HelpWendaAnswerActivity.this, HelpWendaRewardActivity.class));
                }
            });
        } else {
            (tv_reward = findViewById(R.id.tv_reward)).setVisibility(View.GONE);
            (tv_reedit = findViewById(R.id.tv_reedit)).setVisibility(View.VISIBLE);

            //重新编辑
            findViewById(R.id.layout_wenda_answer_reward).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HelpWendaAnswerActivity.this, HelpWendaEditActivity.class));
                }
            });
        }

        findViewById(R.id.layout_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HelpWendaAnswerActivity.this, HelpWendaCommentActivity.class));
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
