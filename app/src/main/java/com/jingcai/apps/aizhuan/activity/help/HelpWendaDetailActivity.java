package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.adapter.help.AbuseReportHandler;
import com.jingcai.apps.aizhuan.adapter.help.AnonHandler;
import com.jingcai.apps.aizhuan.adapter.help.CommentItem;
import com.jingcai.apps.aizhuan.adapter.help.HelpCommentAdapter;
import com.jingcai.apps.aizhuan.adapter.help.LikeHandler;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob15.Partjob15Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob15.Partjob15Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob34.Partjob34Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob34.Partjob34Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob35.Partjob35Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaDetailActivity extends BaseActivity {
    private static final int REQUEST_CODE_ANSWER_VIEW1 = 1100;
    private static final int REQUEST_CODE_ANSWER_VIEW2 = 1101;
    private static final int REQUEST_CODE_ANSWER_EDIT = 1102;
    private String helpid;
    private AzExecutor azExecutor = new AzExecutor();
    private AzService azService = new AzService();
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private MessageHandler messageHandler;
    private ImageView iv_func;
    private XListView groupListView;
    private HelpCommentAdapter commentAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private CheckBox cb_wenda_help, cb_wenda_help_my, cb_wenda_comment, cb_wenda_like;
    private TextView tv_main_tip;
    private ImageView civ_head_logo;
    private LevelTextView ltv_level;
    private TextView tv_stu_name, tv_stu_college, tv_deploy_time, tv_detail_content;
    private View layout_wenda_like, layout_wenda_help;
    private Partjob34Response.Parttimejob job;
    private CommentItem selectedRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helpid = getIntent().getStringExtra("helpid");
        if (StringUtil.isEmpty(helpid)) {
            finish();
        } else {
            messageHandler = new MessageHandler(this);
            setContentView(R.layout.help_wenda_detail);

            initHeader();

            initView();

            initGroupData();
        }
    }

    private void finishWithResult() {
        Intent intent = new Intent();
        intent.putExtra("helpflag", job.getHelpflag());
        intent.putExtra("helperid", job.getHelperid());
        intent.putExtra("praiseflag", job.getPraiseflag());
        intent.putExtra("praiseid", job.getPraiseid());
        intent.putExtra("praisecount", job.getPraisecount());
        intent.putExtra("answercount", job.getAnswercount());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("求问详情");

        iv_func = (ImageView) findViewById(R.id.iv_func);
        if (GlobalConstant.debugFlag) {
            iv_func.setVisibility(View.VISIBLE);
        } else {
            iv_func.setVisibility(View.GONE);
        }
        iv_func.setImageResource(R.drawable.icon__header_more);
        iv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == job && !GlobalConstant.debugFlag) {
                    return;
                }
                int dp10_px = HelpWendaDetailActivity.this.getResources().getDimensionPixelSize(R.dimen.dp_10);
                View contentView = LayoutInflater.from(HelpWendaDetailActivity.this).inflate(R.layout.help_wenda_answer_setting_pop, null);
                final PopupWin win = PopupWin.Builder.create(HelpWendaDetailActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(iv_func)
                        .setContentView(contentView)
                        .build();

                TextView tv_pop_anonymous = win.findTextViewById(R.id.tv_pop_anonymous);
                TextView tv_pop_abuse_report = win.findTextViewById(R.id.tv_pop_abuse_report);

                final boolean selfFlag = GlobalConstant.debugFlag ? false : UserSubject.getStudentid().equals(job.getSourceid());
                if (selfFlag) {
                    // 发布作者，使用匿名
                    tv_pop_anonymous.setVisibility(View.VISIBLE);
                    tv_pop_abuse_report.setVisibility(View.GONE);

                    final boolean anonFlag = "1".equals(job.getAnonflag());
                    tv_pop_anonymous.setText(anonFlag ? "取消匿名" : "使用匿名");
                    tv_pop_anonymous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //使用/取消匿名
                            new AnonHandler(HelpWendaDetailActivity.this).setCallback(new AnonHandler.Callback() {
                                @Override
                                public void call(Partjob35Response.Parttimejob job) {
                                    win.dismiss();
                                    HelpWendaDetailActivity.this.job.setAnonflag("1".equals(HelpWendaDetailActivity.this.job.getAnonflag()) ? "0" : "1");
                                    if ("1".equals(HelpWendaDetailActivity.this.job.getAnonflag())) {

                                    }
                                    //所在学校->匿名发布、所在学院->null、姓名->改为匿名、头像->默认头像路径
                                }
                            }).click(anonFlag, "1", helpid);
                        }
                    });
                }
                if (!selfFlag || GlobalConstant.debugFlag) {
                    tv_pop_anonymous.setVisibility(View.GONE);
                    tv_pop_abuse_report.setVisibility(View.VISIBLE);
                    tv_pop_abuse_report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            win.dismiss();
                            //举报求助
                            new AbuseReportHandler(HelpWendaDetailActivity.this).setCallback(new AbuseReportHandler.Callback() {
                                @Override
                                public void call() {
                                    showToast("举报成功");
                                }
                            }).click(job.getSourceid(), "1", helpid);
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
        tv_main_tip = (TextView) findViewById(R.id.tv_main_tip);
        civ_head_logo = (ImageView) findViewById(R.id.civ_head_logo);
        ltv_level = (LevelTextView) findViewById(R.id.ltv_level);
        tv_stu_name = (TextView) findViewById(R.id.tv_stu_name);
        tv_stu_college = (TextView) findViewById(R.id.tv_stu_college);
        tv_deploy_time = (TextView) findViewById(R.id.tv_deploy_time);
        findViewById(R.id.tv_money).setVisibility(View.GONE);
        tv_detail_content = (TextView) findViewById(R.id.tv_detail_content);

        layout_wenda_like = findViewById(R.id.layout_wenda_like);
        layout_wenda_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != job) {
                    new LikeHandler(HelpWendaDetailActivity.this).setCallback(new LikeHandler.Callback() {
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
                    }).click("2", helpid, job.getPraiseid(), cb_wenda_like);
                }
            }
        });
        cb_wenda_like = (CheckBox) findViewById(R.id.cb_wenda_like);
        cb_wenda_comment = (CheckBox) findViewById(R.id.cb_wenda_comment);
        layout_wenda_help = findViewById(R.id.layout_wenda_help);
        cb_wenda_help = (CheckBox) findViewById(R.id.cb_wenda_help);
        cb_wenda_help_my = (CheckBox) findViewById(R.id.cb_wenda_help_my);

        groupListView = (XListView) findViewById(R.id.xlv_list);
        groupListView.setAdapter(commentAdapter = new HelpCommentAdapter(this));
        groupListView.setPullRefreshEnable(true);
        groupListView.setPullLoadEnable(true);
        groupListView.setAutoLoadEnable(true);
        groupListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                commentAdapter.clearData();
                mCurrentStart = 0;
                groupListView.setPullLoadEnable(true);
                initGroupData();
            }

            @Override
            public void onLoadMore() {
                initGroupData();
            }
        });

        commentAdapter.setCallback(new HelpCommentAdapter.Callback() {
            @Override
            public void click(View view, CommentItem region) {
                Intent intent = new Intent(HelpWendaDetailActivity.this, HelpWendaAnswerActivity.class);
                intent.putExtra("answerid", region.getContentid());
                selectedRegion = region;
                startActivityForResult(intent, REQUEST_CODE_ANSWER_VIEW1);
            }

            @Override
            public void like(CheckBox checkBox, final CommentItem region) {
                new LikeHandler(HelpWendaDetailActivity.this).setCallback(new LikeHandler.Callback() {
                    @Override
                    public void like(String praiseid, CheckBox checkBox) {
                        region.setPraiseflag("1");
                        region.setPraiseid(praiseid);
                        region.setPraisecount(checkBox.getText().toString());
                    }

                    @Override
                    public void unlike(CheckBox checkBox) {
                        region.setPraiseflag("0");
                        region.setPraiseid(null);
                        region.setPraisecount(checkBox.getText().toString());
                    }
                }).click("3", region.getContentid(), region.getPraiseid(), checkBox);
            }

            @Override
            public void abuse(CommentItem region) {
                //举报答案
                new AbuseReportHandler(HelpWendaDetailActivity.this).setCallback(new AbuseReportHandler.Callback() {
                    @Override
                    public void call() {
                        showToast("举报成功");
                    }
                }).click(region.getSourceid(), "3", region.getContentid());
            }
        });
    }

    private void setViewData() {
        if (null == job) {
            return;
        }
        iv_func.setVisibility(View.VISIBLE);
        tv_main_tip.setText(job.getTopiccontent());
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
        if ("0".equals(job.getAnswercount()) || StringUtil.isEmpty(job.getAnswercount())) {
            cb_wenda_comment.setText("");
        } else {
            cb_wenda_comment.setText(job.getAnswercount());
        }

        showHelpLayout();
    }

    private void showHelpLayout() {
        final boolean selfHelpFlag = "1".equals(job.getHelpflag());
        if (selfHelpFlag) {
            cb_wenda_help_my.setVisibility(View.VISIBLE);
            cb_wenda_help.setVisibility(View.GONE);
        } else {
            cb_wenda_help_my.setVisibility(View.GONE);
            cb_wenda_help.setVisibility(View.VISIBLE);
        }
        layout_wenda_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selfHelpFlag) {//我的答案
                    Intent intent = new Intent(HelpWendaDetailActivity.this, HelpWendaAnswerActivity.class);
                    intent.putExtra("answerid", job.getHelperid());
                    startActivityForResult(intent, REQUEST_CODE_ANSWER_VIEW2);
                } else {//撰写
                    Intent intent = new Intent(HelpWendaDetailActivity.this, HelpWendaEditActivity.class);
                    intent.putExtra("helpid", helpid);
                    intent.putExtra("answerid", "");
                    startActivityForResult(intent, REQUEST_CODE_ANSWER_EDIT);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ANSWER_VIEW1: {//列表答案
                if (resultCode == RESULT_OK) {
                    //答案信息有改动，刷新本页面
//                    groupListView.autoRefresh();
//                    String praisecount = data.getStringExtra("");
//                    selectedRegion.setPraisecount(praisecount);
//                    String content = data.getStringExtra("");
//                    selectedRegion.setContent(content);

                    selectedRegion.setContent(data.getStringExtra("content"));
                    selectedRegion.setPraiseflag(data.getStringExtra("praiseflag"));
                    selectedRegion.setPraiseid(data.getStringExtra("praiseid"));
                    selectedRegion.setPraisecount(data.getStringExtra("praisecount"));

                    selectedRegion.setSourcename(data.getStringExtra("sourcename"));
                    selectedRegion.setSourceimgurl(data.getStringExtra("sourceimgurl"));
                    selectedRegion.setSourceschool(data.getStringExtra("sourceschool"));
                    selectedRegion.setSourcecollege(data.getStringExtra("sourcecollege"));

                    commentAdapter.notifyDataSetChanged();
                }
                break;
            }
            case REQUEST_CODE_ANSWER_VIEW2: {//我的答案
                if (resultCode == RESULT_OK) {
                    //答案信息有改动，刷新本页面
                    groupListView.autoRefresh();
                }
                break;
            }
            case REQUEST_CODE_ANSWER_EDIT: {
                if (resultCode == RESULT_OK) {
                    //正常发布完成，显示我的答案页面，刷新列表
                    String helperid = data.getStringExtra("helperid");
                    job.setHelpflag("1");
                    job.setHelperid(helperid);
                    showHelpLayout();
                    groupListView.autoRefresh();
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void onLoad() {
        groupListView.stopRefresh();
        groupListView.stopLoadMore();
        groupListView.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
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
                        job = (Partjob34Response.Parttimejob) msg.obj;
                        setViewData();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast("获取求问详情失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        List<CommentItem> list = (List<CommentItem>) msg.obj;
                        commentAdapter.addData(list);
                        commentAdapter.notifyDataSetChanged();
                        mCurrentStart += list.size();
                        onLoad();
                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            groupListView.setPullLoadEnable(false);
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 3: {
                    try {
                        showToast("获取商家失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
//                case 2:{
//                    try {
//                        groupListView.setVisibility(View.GONE);
//                    }finally {
//                        actionLock.unlock();
//                    }
//                    break;
//                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void initGroupData() {
        if (actionLock.tryLock()) {
//            showProgressDialog("获取圈子中...");
            azExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Partjob34Request req = new Partjob34Request();
                    Partjob34Request.Parttimejob job = req.new Parttimejob();
                    job.setQuestionid(helpid);
                    job.setStudentid(UserSubject.getStudentid());
                    req.setParttimejob(job);
                    azService.doTrans(req, Partjob34Response.class, new AzService.Callback<Partjob34Response>() {
                        @Override
                        public void success(Partjob34Response resp) {
                            ResponseResult result = resp.getResult();
                            if ("0".equals(result.getCode())) {
                                Partjob34Response.Parttimejob job = resp.getBody().getParttimejob();
                                messageHandler.postMessage(0, job);
                            } else {
                                messageHandler.postMessage(1, result.getMessage());
                            }
                        }

                        @Override
                        public void fail(AzException e) {
                            messageHandler.postException(e);
                        }
                    });
                }
            });
            azExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
                        List<CommentItem> regionList = new ArrayList<CommentItem>();
                        for (int i = 0; i < 10 && mCurrentStart < 24; i++) {
                            Partjob15Response.Parttimejob region = new Partjob15Response.Parttimejob();
                            region.setContentid("232r3");
                            region.setSourcename("学生" + (i + mCurrentStart));
                            region.setSourcelevel("19");
                            region.setSourceschool("浙江大学");
                            region.setSourcecollege("计算机学院");
                            region.setOptime("20150801233341");
                            if (i % 2 == 0) {
                                region.setPraiseflag("1");
                                region.setPraiseid("23232332");
                            }
                            region.setPraisecount("23");
                            region.setContent("浙江大学" + (i + mCurrentStart));
                            regionList.add(region);
                        }
                        messageHandler.postMessage(2, regionList);
                    } else {
                        Partjob15Request req = new Partjob15Request();
                        Partjob15Request.Parttimejob job = req.new Parttimejob();
                        job.setStudentid(UserSubject.getStudentid());
                        job.setQuestionid(helpid);
                        job.setStart("" + mCurrentStart);
                        job.setLimit(String.valueOf(GlobalConstant.PAGE_SIZE));
                        req.setParttimejob(job);

                        azService.doTrans(req, Partjob15Response.class, new AzService.Callback<Partjob15Response>() {
                            @Override
                            public void success(Partjob15Response response) {
                                ResponseResult result = response.getResult();
                                if ("0".equals(result.getCode())) {
                                    List<Partjob15Response.Parttimejob> parttimejob_list = response.getBody().getParttimejob_list();
                                    if (null == parttimejob_list) {
                                        parttimejob_list = new ArrayList<Partjob15Response.Parttimejob>();
                                    }
                                    messageHandler.postMessage(2, parttimejob_list);
                                } else {
                                    messageHandler.postMessage(3, result.getMessage());
                                }
                            }

                            @Override
                            public void fail(AzException e) {
                                messageHandler.postException(e);
                            }
                        });
                    }
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishWithResult();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
