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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.help.AbuseReportHandler;
import com.jingcai.apps.aizhuan.adapter.help.AnonHandler;
import com.jingcai.apps.aizhuan.adapter.help.LikeHandler;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
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
    private static final int REQUEST_CODE_ANSWER_EDIT = 1101;
    private static final int REQUEST_CODE_ANSWER_COMMENT = 1102;
    private String answerid;
    private MessageHandler messageHandler;
    private View tv_reward, tv_reedit;
    private Partjob36Response.Parttimejob job;
    private ImageView iv_func;
    private View layout_wenda_help;
    private View layout_wenda_like;
    private View layout_wenda_comment;
    private CheckBox cb_wenda_like;
    private CheckBox cb_wenda_comment;

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

            initView();

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
                Partjob36Request req = new Partjob36Request(answerid, UserSubject.getStudentid());
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
                final PopupWin win = PopupWin.Builder.create(HelpWendaAnswerActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(iv_func)
                        .setContentView(contentView)
                        .build();
                View tv_pop_abuse_report = win.findViewById(R.id.tv_pop_abuse_report);//举报
                TextView tv_pop_anonymous = win.findTextViewById(R.id.tv_pop_anonymous);//使用匿名
                View tv_pop_reedit = win.findViewById(R.id.tv_pop_reedit);//再次编辑
                View tv_pop_reward = win.findViewById(R.id.tv_pop_reward);//打赏

                final boolean selfFlag = UserSubject.getStudentid().equals(job.getSourceid());
                if (selfFlag) {
                    tv_pop_anonymous.setVisibility(View.VISIBLE);
                    tv_pop_reedit.setVisibility(View.VISIBLE);
                    tv_pop_abuse_report.setVisibility(View.GONE);
                    tv_pop_reward.setVisibility(View.GONE);

                    final boolean anonFlag = "1".equals(job.getAnonflag());
                    tv_pop_anonymous.setText(anonFlag ? "取消匿名" : "使用匿名");
                    tv_pop_anonymous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //使用/取消匿名
                            new AnonHandler(HelpWendaAnswerActivity.this).setCallback(new AnonHandler.Callback() {
                                @Override
                                public void call() {
                                    win.dismiss();
                                    job.setAnonflag("1".equals(job.getAnonflag()) ? "0" : "1");
                                    if("1".equals(job.getAnonflag())) {

                                    }
                                    //所在学校->匿名发布、所在学院->null、姓名->改为匿名、头像->默认头像路径
                                }
                            }).click("2", answerid);
                        }
                    });

                    tv_pop_reedit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            win.dismiss();
                            //再次编辑
                            Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaEditActivity.class);
                            intent.putExtra("helpid", job.getQuestionid());
                            intent.putExtra("answerid", answerid);
                            startActivityForResult(intent, REQUEST_CODE_ANSWER_EDIT);
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
                            win.dismiss();
                            //举报求助
                            new AbuseReportHandler(HelpWendaAnswerActivity.this).setCallback(new AbuseReportHandler.Callback() {
                                @Override
                                public void call() {
                                    showToast("举报成功");
                                }
                            }).click(job.getSourceid(), "3", answerid);
                        }
                    });


                    tv_pop_reward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //打赏
                            Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaRewardActivity.class);
                            intent.putExtra("answerid", answerid);
                            intent.putExtra("receiverid", job.getSourceid());
                            startActivity(intent);
                        }
                    });
                }
                win.show(Gravity.TOP | Gravity.RIGHT, dp10_px, dp10_px * 6);
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

    private void initView(){
        layout_wenda_like = findViewById(R.id.layout_wenda_like);
        cb_wenda_like = (CheckBox)findViewById(R.id.cb_wenda_like);
        layout_wenda_comment = findViewById(R.id.layout_wenda_comment);
        cb_wenda_comment = (CheckBox)findViewById(R.id.cb_wenda_comment);

        layout_wenda_help = findViewById(R.id.layout_wenda_help);
        tv_reward = findViewById(R.id.cb_wenda_reward);
        tv_reedit = findViewById(R.id.cb_wenda_reedit);

        layout_wenda_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != job) {
                    new LikeHandler(HelpWendaAnswerActivity.this).setCallback(new LikeHandler.Callback() {
                        @Override
                        public void like(String praiseid, CheckBox checkBox) {
                            job.setPraiseflag("1");
                            job.setPraiseid(praiseid);
                            job.setPraisecount(checkBox.getText().toString());
                        }

                        @Override
                        public void unlike(CheckBox checkBox) {
                            job.setPraiseflag("0");
                            job.setPraiseid(null);
                            job.setPraisecount(checkBox.getText().toString());
                        }
                    }).click("3", answerid, job.getPraiseid(), cb_wenda_like);
                }
            }
        });

        layout_wenda_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaCommentActivity.class);
                intent.putExtra("answerid", answerid);
                startActivityForResult(intent, REQUEST_CODE_ANSWER_COMMENT);
            }
        });

        if(GlobalConstant.debugFlag){//TODO delete
            layout_wenda_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast("------debug------");
                    boolean selfFlag = false;
                    if (selfFlag) {//重新编辑
                        Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaEditActivity.class);
                        intent.putExtra("helpid", "00000001");
                        intent.putExtra("answerid", answerid);
                        startActivityForResult(intent, REQUEST_CODE_ANSWER_EDIT);
                    } else {//打赏
                        Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaRewardActivity.class);
                        intent.putExtra("answerid", answerid);
                        intent.putExtra("receiverid", "00000001");
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void initViewData() {
        if(null == job) return ;

        iv_func.setVisibility(View.VISIBLE);

        //点赞
        cb_wenda_like.setText(job.getPraisecount());
        cb_wenda_like.setChecked("1".equals(job.getPraiseflag()));//本人是否已经点赞
        //评论
        cb_wenda_comment.setText(job.getCommentcount());

        final boolean selfFlag = UserSubject.getStudentid().equals(job.getSourceid());
        if (selfFlag) {
            tv_reward.setVisibility(View.GONE);
            tv_reedit.setVisibility(View.VISIBLE);
        } else {
            tv_reward.setVisibility(View.VISIBLE);
            tv_reedit.setVisibility(View.GONE);
        }

        layout_wenda_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selfFlag) {//重新编辑
                    Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaEditActivity.class);
                    intent.putExtra("helpid", job.getQuestionid());
                    intent.putExtra("answerid", answerid);
                    startActivityForResult(intent, REQUEST_CODE_ANSWER_EDIT);
                } else {//打赏
                    Intent intent = new Intent(HelpWendaAnswerActivity.this, HelpWendaRewardActivity.class);
                    intent.putExtra("answerid", answerid);
                    intent.putExtra("receiverid", job.getSourceid());
                    startActivity(intent);
                }
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
            case REQUEST_CODE_ANSWER_COMMENT:{
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
                        initViewData();
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
