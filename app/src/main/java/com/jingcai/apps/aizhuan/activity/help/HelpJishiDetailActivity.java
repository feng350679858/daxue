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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.util.BaseReceiver;
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.activity.util.PopConfirmWin;
import com.jingcai.apps.aizhuan.adapter.help.AbuseReportHandler;
import com.jingcai.apps.aizhuan.adapter.help.CommentItem;
import com.jingcai.apps.aizhuan.adapter.help.HelpCommentAdapter;
import com.jingcai.apps.aizhuan.adapter.help.LikeHandler;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob13.Partjob13Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob37.Partjob37Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.DictUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lejing on 15/7/21.
 */
public class HelpJishiDetailActivity extends BaseActivity {
    private String helpid, type;
    private AzExecutor azExecutor = new AzExecutor();
    private AzService azService = new AzService();
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private MessageHandler messageHandler;
    private XListView groupListView;
    private HelpCommentAdapter commentAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private CheckBox cb_jishi_help;
    private EditText et_reploy_comment;
    private ImageView iv_func;
    private ImageView civ_head_logo;
    private LevelTextView ltv_level;
    private TextView tv_stu_name;
    private TextView tv_stu_college;
    private TextView tv_deploy_time;
    private TextView tv_money;
    private TextView tv_detail_content;
    private View layout_jishi_like;
    private CheckBox cb_jishi_like;
    private CheckBox cb_jishi_comment;
    private View layout_jishi_help;
    private Partjob19Response.Parttimejob job;
    private TextView tv_gender_limit;
    private PopConfirmWin helpConfirmWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helpid = getIntent().getStringExtra("helpid");
        type = getIntent().getStringExtra("type");//1立即帮助 3公告

        if (StringUtil.isEmpty(helpid)) {
            finish();
        } else {
            if (StringUtil.isEmpty(type)) {
                type = "1";
            }
            messageHandler = new MessageHandler(this);

            setContentView(R.layout.help_jishi_detail);

            initHeader();

            initView();

            initGroupData();
        }
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("求助详情");

        iv_func = (ImageView) findViewById(R.id.iv_func);
        iv_func.setVisibility(View.GONE);

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initView() {
        TextView tv_main_tip = (TextView) findViewById(R.id.tv_main_tip);
        tv_main_tip.setText("1".equals(type) ? "跑腿" : "公告");
        civ_head_logo = (ImageView) findViewById(R.id.civ_head_logo);
        ltv_level = (LevelTextView) findViewById(R.id.ltv_level);
        tv_stu_name = (TextView) findViewById(R.id.tv_stu_name);
        tv_stu_college = (TextView) findViewById(R.id.tv_stu_college);
        tv_deploy_time = (TextView) findViewById(R.id.tv_deploy_time);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_detail_content = (TextView) findViewById(R.id.tv_detail_content);
        tv_gender_limit = (TextView) findViewById(R.id.tv_gender_limit);

        layout_jishi_like = findViewById(R.id.layout_jishi_like);
        layout_jishi_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == job) return;
                new LikeHandler(HelpJishiDetailActivity.this).setCallback(new LikeHandler.Callback() {
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
                }).click("1".equals(type) ? "1" : "5", helpid, job.getPraiseid(), cb_jishi_like);
            }
        });
        cb_jishi_like = (CheckBox) findViewById(R.id.cb_jishi_like);
        cb_jishi_comment = (CheckBox) findViewById(R.id.cb_jishi_comment);

        cb_jishi_help = (CheckBox) findViewById(R.id.cb_jishi_help);
        layout_jishi_help = findViewById(R.id.layout_jishi_help);

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
                boolean selected = region.isSelected();
                commentAdapter.clearSelected();
                if (!selected) {
                    region.setSelected(!selected);
                    et_reploy_comment.setHint("回复：" + region.getSourcename());
                } else {
                    et_reploy_comment.setHint("评论");
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void like(CheckBox checkBox, final CommentItem region) {
                new LikeHandler(HelpJishiDetailActivity.this).setCallback(new LikeHandler.Callback() {
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
                }).click("1", helpid, region.getPraiseid(), checkBox);
            }

            @Override
            public void abuse(CommentItem region) {
                //举报答案
                new AbuseReportHandler(HelpJishiDetailActivity.this).setCallback(new AbuseReportHandler.Callback() {
                    @Override
                    public void call() {
                        showToast("举报成功");
                    }
                }).click(region.getSourceid(), "2", region.getContentid());
            }
        });

        et_reploy_comment = (EditText) findViewById(R.id.et_reploy_comment);
    }

    private void setViewData() {
        if (null == job) {
            return;
        }
        final boolean selfFlag = UserSubject.getStudentid().equals(job.getSourceid());
        iv_func.setVisibility(View.VISIBLE);
        iv_func.setImageResource(R.drawable.icon__header_more);
        iv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == job) {
                    return;
                }
                int dp10_px = HelpJishiDetailActivity.this.getResources().getDimensionPixelSize(R.dimen.dp_10);
                View contentView = LayoutInflater.from(HelpJishiDetailActivity.this).inflate(R.layout.help_wenda_answer_setting_pop, null);
                PopupWin groupWin = PopupWin.Builder.create(HelpJishiDetailActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(iv_func)
                        .setContentView(contentView)
                        .build();

                View tv_pop_abuse_report = groupWin.findViewById(R.id.tv_pop_abuse_report);
                View tv_pop_share = groupWin.findViewById(R.id.tv_pop_share);

                if (selfFlag || "3".equals(type)) {//公告不能举报
                    tv_pop_abuse_report.setVisibility(View.GONE);
                } else {
                    tv_pop_abuse_report.setVisibility(View.VISIBLE);
                    tv_pop_abuse_report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//举报
                            //举报求助
                            new AbuseReportHandler(HelpJishiDetailActivity.this).setCallback(new AbuseReportHandler.Callback() {
                                @Override
                                public void call() {
                                    showToast("举报成功");
                                }
                            }).click(job.getSourceid(), "1", helpid);
                        }
                    });
                }
                {
                    tv_pop_share.setVisibility(View.VISIBLE);
                    tv_pop_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//TODO 分享
                            Log.d("==", "-----------tv_pop_share---");
//                            UmengShareUtil umengShareUtil = new UmengShareUtil(HelpJishiDetailActivity.this);
//                            umengShareUtil.setShareContent("兼职分享", msg + getShareUrl(), getShareUrl(), logopath);
//                            umengShareUtil.openShare();
                        }
                    });
                }

                groupWin.show(Gravity.TOP | Gravity.RIGHT, dp10_px, dp10_px * 6);
            }
        });

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
        tv_money.setText(StringUtil.getPrintMoney(job.getMoney()));
        tv_detail_content.setText(job.getPubliccontent());
        tv_gender_limit.setText(DictUtil.get(DictUtil.Item.gender, job.getGenderlimit()));

        //点赞
        if ("0".equals(job.getPraisecount()) || StringUtil.isEmpty(job.getPraisecount())) {
            cb_jishi_like.setText("");
        } else {
            cb_jishi_like.setText(job.getPraisecount());
        }
        cb_jishi_like.setChecked("1".equals(job.getPraiseflag()));//本人是否已经点赞
        //评论
        if ("0".equals(job.getCommentcount()) || StringUtil.isEmpty(job.getCommentcount())) {
            cb_jishi_comment.setText("");
        } else {
            cb_jishi_comment.setText(job.getCommentcount());
        }

        //即时帮助-求助中,helperid为空表示有还未有人请求帮助
        if(!selfFlag && "1".equals(type) && "1".equals(job.getStatus())) {
            cb_jishi_help.setText("帮TA");
            layout_jishi_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 检查是否可以帮助，可以帮助显示确认对话框
                    if (!actionLock.tryLock()) return;
                    azExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Partjob37Request req = new Partjob37Request(helpid, UserSubject.getStudentid());
                            azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                                @Override
                                public void success(BaseResponse resp) {
                                    ResponseResult result = resp.getResult();
                                    if ("0".equals(result.getCode())) {
                                        messageHandler.postMessage(11);//检查通过，显示确认对话框
                                    } else {
                                        messageHandler.postMessage(9, "接单失败:" + resp.getResultMessage());
                                    }
                                }

                                @Override
                                public void fail(AzException e) {
                                }
                            });
                        }
                    });
                }
            });
        } else {//显示状态
            cb_jishi_help.setText(DictUtil.get(DictUtil.Item.help_jishi_status, job.getStatus()));
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
                        job = (Partjob19Response.Parttimejob) msg.obj;
                        setViewData();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 9: {
                    try {
                        showToast(String.valueOf(msg.obj));
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
                case 11: {
                    try {
                        if (null == helpConfirmWin) {
                            //显示立即帮助确认对话框
                            helpConfirmWin = new PopConfirmWin(HelpJishiDetailActivity.this);
                            helpConfirmWin.setTitle("确认接单").setContent("你确定现在有时间与精力帮助这位同学？").setOkAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doJishiHelp();
                                }
                            });
                        }
                        helpConfirmWin.show();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 13: {
                    try {
                        showToast("接单成功");
                        if (null != helpConfirmWin) {
                            helpConfirmWin.dismiss();
                        }
                        job.setStatus("2");//状态设置为帮助中
                        cb_jishi_help.setText(DictUtil.get(DictUtil.Item.help_jishi_status, job.getStatus()));
                        layout_jishi_help.setOnClickListener(null);
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

    private void initGroupData() {
        if (!actionLock.tryLock()) {
            return;
        }
        //获取详情
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Partjob19Request req = new Partjob19Request();
                Partjob19Request.Parttimejob job = req.new Parttimejob();
                job.setType(type);//1：即时型求助   3：公告求助
                job.setStudentid(UserSubject.getStudentid());
                job.setHelpid(helpid);
                req.setParttimejob(job);

                azService.doTrans(req, Partjob19Response.class, new AzService.Callback<Partjob19Response>() {
                    @Override
                    public void success(Partjob19Response response) {
                        ResponseResult result = response.getResult();
                        if ("0".equals(result.getCode())) {
                            Partjob19Response.Parttimejob job = response.getBody().getParttimejob();
                            messageHandler.postMessage(0, job);
                        } else {
                            messageHandler.postMessage(9, "获取详情失败:" + result.getMessage());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });
        //获取评论列表
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Partjob29Request req = new Partjob29Request();
                Partjob29Request.Parttimejob job = req.new Parttimejob();
                job.setReceiverid(UserSubject.getStudentid());
                job.setTargettype("1".equals(type) ? "1" : "5");//1求助 5求助公告
                job.setTargetid(helpid);
                job.setCommenttype("1");//1评论
                job.setStart("" + mCurrentStart);
                job.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                req.setParttimejob(job);
                azService.doTrans(req, Partjob29Response.class, new AzService.Callback<Partjob29Response>() {
                    @Override
                    public void success(Partjob29Response response) {
                        ResponseResult result = response.getResult();
                        if ("0".equals(result.getCode())) {
                            List<Partjob29Response.Parttimejob> parttimejob_list = response.getBody().getParttimejob_list();
                            if (null == parttimejob_list) {
                                parttimejob_list = new ArrayList<Partjob29Response.Parttimejob>();
                            }
                            messageHandler.postMessage(2, parttimejob_list);
                        } else {
                            messageHandler.postMessage(9, "获取评论失败:" + result.getMessage());
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

    @Override
    protected void onStart() {
        super.onStart();
        String notiicationtype = getIntent().getStringExtra("notiicationtype");
        Intent intent = new Intent(BaseReceiver.ACTION_JISHI_DISPATCH_REQUEST);
        intent.putExtra("notiicationtype", notiicationtype);
        if ("2".equals(notiicationtype)) {
            intent.putExtra("helpid", helpid);
            intent.putExtra("studentid", getIntent().getStringExtra("studentid"));
            intent.putExtra("timestamp", getIntent().getLongExtra("timestamp", -1));
        } else if ("3".equals(notiicationtype)) {
            intent.putExtra("helpid", helpid);
            intent.putExtra("studentid", getIntent().getStringExtra("studentid"));
        }
        sendBroadcast(intent);
    }

    private void doJishiHelp() {
        if (!actionLock.tryLock()) return;
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Partjob13Request req = new Partjob13Request();
                Partjob13Request.Parttimejob job = req.new Parttimejob();
                job.setHelpid(helpid);
                job.setSourceid(UserSubject.getStudentid());
                req.setParttimejob(job);

                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse response) {
                        ResponseResult result = response.getResult();
                        if ("0".equals(result.getCode())) {
                            messageHandler.postMessage(13);
                        } else {
                            messageHandler.postMessage(9, "接单失败:" + result.getMessage());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                    }
                });
            }
        });
    }
}
