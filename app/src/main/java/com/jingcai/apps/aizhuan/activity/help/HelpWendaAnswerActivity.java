package com.jingcai.apps.aizhuan.activity.help;

import android.app.Activity;
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
    private static final int REQUEST_CODE_ANSWER_EDIT = 1101;
    private static final int REQUEST_CODE_ANSWER_REWARD = 1102;
    private String answerid;
    private MessageHandler messageHandler;
    private View tv_reward, tv_reedit;
    private Partjob36Response.Parttimejob job;
    private ImageView iv_func;

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
                        if ("0".equals(resp.getResultCode())) {
                            Partjob36Response.Parttimejob job = resp.getBody().getParttimejob();
                            messageHandler.postMessage(0, job);
                        } else {
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

        iv_func = (ImageView) findViewById(R.id.iv_func);
        iv_func.setVisibility(View.GONE);
        iv_func.setImageResource(R.drawable.icon__header_more);
        iv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == job){
                    return;
                }
                int dp10_px = HelpWendaAnswerActivity.this.getResources().getDimensionPixelSize(R.dimen.dp_10);
                View contentView = LayoutInflater.from(HelpWendaAnswerActivity.this).inflate(R.layout.help_wenda_answer_setting_pop, null);
                PopupWin groupWin = PopupWin.Builder.create(HelpWendaAnswerActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(iv_func)
                        .setContentView(contentView)
                        .build();
                View tv_pop_abuse_report = groupWin.findViewById(R.id.tv_pop_abuse_report);//举报
                TextView tv_pop_anonymous = groupWin.findTextViewById(R.id.tv_pop_anonymous);//使用匿名
                View tv_pop_reedit = groupWin.findViewById(R.id.tv_pop_reedit);//再次编辑
                View tv_pop_reward = groupWin.findViewById(R.id.tv_pop_reward);//打赏

                final boolean myFlag = UserSubject.getStudentid().equals(job.getSourceid());
                if (myFlag) {
                    tv_pop_anonymous.setVisibility(View.VISIBLE);
                    tv_pop_reedit.setVisibility(View.VISIBLE);
                    tv_pop_abuse_report.setVisibility(View.GONE);
                    tv_pop_reward.setVisibility(View.GONE);

                    final boolean anonFlag = "1".equals(job.getAnonflag());
                    tv_pop_anonymous.setText(anonFlag?"取消匿名":"使用匿名");
                    tv_pop_anonymous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("==", "-----------tv_pop_anonymous---");
                        }
                    });

                    tv_pop_reedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("==", "-----------tv_pop_reedit---");
                        }
                    });
                }else{
                    tv_pop_anonymous.setVisibility(View.GONE);
                    tv_pop_reedit.setVisibility(View.GONE);
                    tv_pop_abuse_report.setVisibility(View.VISIBLE);
                    tv_pop_reward.setVisibility(View.VISIBLE);

                    tv_pop_abuse_report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("==", "-----------tv_pop_abuse_report---");
                        }
                    });


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
        iv_func.setVisibility(View.VISIBLE);

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
                    Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaEditActivity.class);
                    intent.putExtra("helpid", job.getQuestionid());
                    intent.putExtra("answerid", answerid);
                    startActivityForResult(intent, REQUEST_CODE_ANSWER_EDIT);
                } else {//打赏
                    Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaRewardActivity.class);
                    //intent.putExtra("helpid", job.getQuestionid());
                    intent.putExtra("answerid", answerid);
                    startActivityForResult(intent, REQUEST_CODE_ANSWER_REWARD);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_ANSWER_EDIT:{
                if(Activity.RESULT_OK == resultCode){
                    //TODO 撰写变为我的答案
                }
                break;
            }
            case REQUEST_CODE_ANSWER_REWARD:{
                if(Activity.RESULT_OK == resultCode){
                    //TODO
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
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
                        job = (Partjob36Response.Parttimejob) msg.obj;
                        initView();
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
