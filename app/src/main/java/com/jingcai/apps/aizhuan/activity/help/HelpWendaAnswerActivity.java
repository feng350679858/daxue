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
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob36.Partjob36Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob36.Partjob36Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.List;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaAnswerActivity extends BaseActivity {
    private static final String TAG = "HelpWenddaAnswer";
    private String answerid;
    private MessageHandler messageHandler;
    private View tv_reward, tv_reedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerid = getIntent().getStringExtra("answerid");
        if(StringUtil.isEmpty(answerid)){
            finish();
        } else {
            messageHandler = new MessageHandler(this);
            setContentView(R.layout.help_wenda_answer);

            initHeader();

            initData();
        }
    }

    private void initData() {
        if(!actionLock.tryLock()){
            return ;
        }
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob36Request req = new Partjob36Request(answerid);
                new AzService().doTrans(req, Partjob36Response.class, new AzService.Callback<Partjob36Response>() {
                    @Override
                    public void success(Partjob36Response resp) {
                        if("0".equals(resp.getResultCode())){
                            Partjob36Response.Parttimejob job = resp.getBody().getParttimejob();
                            messageHandler.postMessage(0, job);
                        }else {
                            messageHandler.postMessage(1);
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

    private void initView(Partjob36Response.Parttimejob job) {
        final boolean myFlag = UserSubject.getStudentid().equals(job.getSourceid());
        if (myFlag) {
            (tv_reward = findViewById(R.id.cb_wenda_reward)).setVisibility(View.GONE);
            (tv_reedit = findViewById(R.id.cb_wenda_reedit)).setVisibility(View.VISIBLE);
        } else {
            (tv_reward = findViewById(R.id.cb_wenda_reward)).setVisibility(View.VISIBLE);
            (tv_reedit = findViewById(R.id.cb_wenda_reedit)).setVisibility(View.GONE);
        }

        findViewById(R.id.layout_wenda_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myFlag) {//重新编辑
                    startActivity(new Intent(HelpWendaAnswerActivity.this, HelpWendaEditActivity.class));
                }else{//打赏
                    startActivity(new Intent(HelpWendaAnswerActivity.this, HelpWendaRewardActivity.class));
                }
            }
        });

        findViewById(R.id.layout_wenda_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(HelpWendaAnswerActivity.this, HelpWendaCommentActivity.class));
            }
        });
        findViewById(R.id.layout_wenda_comment).setOnClickListener(new View.OnClickListener() {
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
                        Partjob36Response.Parttimejob job = (Partjob36Response.Parttimejob) msg.obj;
                        initView(job);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {

                    } finally {
                        actionLock.unlock();
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
