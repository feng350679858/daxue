package com.jingcai.apps.aizhuan.activity.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.adapter.help.AbuseReportHandler;
import com.jingcai.apps.aizhuan.adapter.help.AnonHandler;
import com.jingcai.apps.aizhuan.adapter.help.LikeHandler;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob35.Partjob35Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob36.Partjob36Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob36.Partjob36Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaAnswerActivity extends BaseActivity {
    private static final int REQUEST_CODE_ANSWER_EDIT = 1101;
    private static final int REQUEST_CODE_ANSWER_COMMENT = 1102;
    private String answerid;
    private MessageHandler messageHandler;
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private View tv_reward, tv_reedit;
    private Partjob36Response.Parttimejob job;
    private View layout_wenda_help;
    private View layout_wenda_like;
    private View layout_wenda_comment;
    private CheckBox cb_wenda_like;
    private CheckBox cb_wenda_comment;
    private CircleImageView civ_head_logo;
    private LevelTextView ltv_level;
    private TextView tv_stu_name;
    private TextView tv_deploy_time;
    private TextView tv_stu_college;
    private TextView tv_detail_content;
    private ImageView iv_func;
    private boolean updateFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerid = getIntent().getStringExtra("answerid");
        if (StringUtil.isEmpty(answerid)) {
            finishWithResult();
        } else {
            messageHandler = new MessageHandler(this);
            setContentView(R.layout.help_wenda_answer);

            initHeader();

            initView();

            initData();
        }
    }

    private void initData() {
        if (!actionLock.tryLock()) {
            return;
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
                if (null == job) {
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
                                public void call(Partjob35Response.Parttimejob job2) {
                                    win.dismiss();
                                    job.setSourceimgurl(job2.getSourceimgurl());
                                    job.setSourcename(job2.getSourcename());
                                    job.setSourceschool(job2.getSourceschool());
                                    job.setSourcecollege(job2.getSourcecollege());
                                    job.setAnonflag(job2.getAnonflag());
                                    initViewData();

                                    updateFlag = true;
                                    //所在学校->匿名发布、所在学院->null、姓名->改为匿名、头像->默认头像路径
                                }
                            }).click(!anonFlag, "2", answerid);
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
                } else {
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
                            intent.putExtra("answerflag", true);
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
                finishWithResult();
            }
        });
    }

    private void initView() {
        civ_head_logo = (CircleImageView) findViewById(R.id.civ_head_logo);
        ltv_level = (LevelTextView) findViewById(R.id.ltv_level);
        tv_stu_name = (TextView) findViewById(R.id.tv_stu_name);
        tv_deploy_time = (TextView) findViewById(R.id.tv_deploy_time);
        tv_stu_college = (TextView) findViewById(R.id.tv_stu_college);
        tv_detail_content = (TextView) findViewById(R.id.tv_detail_content);

        layout_wenda_like = findViewById(R.id.layout_wenda_like);
        cb_wenda_like = (CheckBox) findViewById(R.id.cb_wenda_like);
        layout_wenda_comment = findViewById(R.id.layout_wenda_comment);
        cb_wenda_comment = (CheckBox) findViewById(R.id.cb_wenda_comment);

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
                            updateFlag = true;
                        }

                        @Override
                        public void unlike(CheckBox checkBox) {
                            job.setPraiseflag("0");
                            job.setPraiseid(null);
                            job.setPraisecount(checkBox.getText().toString());
                            updateFlag = true;
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
    }

    private void initViewData() {
        if (null == job) return;
        iv_func.setVisibility(View.VISIBLE);

        bitmapUtil.getImage(civ_head_logo, job.getSourceimgurl(), R.drawable.default_head_img);
        try {
            ltv_level.setLevel(Integer.parseInt(job.getSourcelevel()));
        } catch (Exception e) {
        }
        tv_stu_name.setText(job.getSourcename());
        if (UserSubject.getSchoolname().equals(job.getSourceschool())) {
            tv_stu_college.setText(job.getSourcecollege());
        } else {
            tv_stu_college.setText(job.getSourceschool());
        }
        tv_deploy_time.setText(DateUtil.getHumanlityDateString(job.getOptime()));
        tv_detail_content.setText(job.getContent());

        //点赞
        if ("0".equals(job.getPraisecount()) || StringUtil.isEmpty(job.getPraisecount())) {
            cb_wenda_like.setText("");
        } else {
            cb_wenda_like.setText(job.getPraisecount());
        }
        cb_wenda_like.setChecked("1".equals(job.getPraiseflag()));//本人是否已经点赞
        //评论
        if ("0".equals(job.getCommentcount()) || StringUtil.isEmpty(job.getCommentcount())) {
            cb_wenda_comment.setText("");
        } else {
            cb_wenda_comment.setText(job.getCommentcount());
        }

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
                    intent.putExtra("answerflag", true);
                    intent.putExtra("answerid", answerid);
                    intent.putExtra("receiverid", job.getSourceid());
                    startActivity(intent);
                }
            }
        });

        //打赏别人的引导，作者显示再次编辑
        if (!selfFlag && !Preferences.getInstance(Preferences.TYPE.guide).getBoolean(Preferences.Guide.PARAM_GUIDE_WENDA_ANSWER, false)) {
            final View stub_guide_view = ((ViewStub) findViewById(R.id.stub_guide)).inflate();
            stub_guide_view.findViewById(R.id.iv_guide_reward).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Preferences.getInstance(Preferences.TYPE.guide).update(Preferences.Guide.PARAM_GUIDE_WENDA_ANSWER, true);
                    stub_guide_view.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ANSWER_EDIT: {
                if (Activity.RESULT_OK == resultCode) {
                    //更新内容和匿名状态
                    initData();
                    updateFlag = true;
                }
                break;
            }
            case REQUEST_CODE_ANSWER_COMMENT: {
                if (Activity.RESULT_OK == resultCode) {
                    //更新评论数量
                    String commentCount = data.getStringExtra("commentCount");
                    cb_wenda_comment.setText(commentCount);
                    //initData();
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

    private void finishWithResult() {
        if (updateFlag) {
            Intent intent = new Intent();
            intent.putExtra("answerid", answerid);
            intent.putExtra("content", tv_detail_content.getText().toString());
            intent.putExtra("praiseflag", job.getPraiseflag());
            intent.putExtra("praiseid", job.getPraiseid());
            intent.putExtra("praisecount", job.getPraisecount());

            intent.putExtra("sourcename", job.getSourcename());
            intent.putExtra("sourceimgurl", job.getSourceimgurl());
            intent.putExtra("sourceschool", job.getSourceschool());
            intent.putExtra("sourcecollege", job.getSourcecollege());

            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
