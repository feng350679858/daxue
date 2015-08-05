package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.help.AbuseReportHandler;
import com.jingcai.apps.aizhuan.adapter.help.CommentItem;
import com.jingcai.apps.aizhuan.adapter.help.HelpCommentAdapter;
import com.jingcai.apps.aizhuan.adapter.help.LikeHandler;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob12.Partjob12Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob12.Partjob12Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaCommentActivity extends BaseActivity {
    private String answerid;
    private String commentCount = null;
    private String selectCommentId = null;
    private MessageHandler messageHandler;
    private XListView groupListView;
    private HelpCommentAdapter commentAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private EditText et_reploy_comment;
    private Button btn_wenda_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerid = getIntent().getStringExtra("answerid");
        if (StringUtil.isEmpty(answerid)) {
            finishWithResult();
        } else {
            messageHandler = new MessageHandler(this);
            setContentView(R.layout.help_wenda_comment);

            initHeader();

            initView();
            initGroupData();
        }
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("评论");

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult();
            }
        });
    }

    private void finishWithResult() {
        if (null != commentCount) {
            Intent intent = new Intent();
            intent.putExtra("commentCount", commentCount);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void initView() {
        et_reploy_comment = (EditText) findViewById(R.id.et_reploy_comment);
        btn_wenda_comment = (Button) findViewById(R.id.btn_wenda_comment);
        btn_wenda_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doComment();
            }
        });

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
                    selectCommentId = region.getContentid();
                    et_reploy_comment.setHint("回复：" + region.getSourcename());
                } else {
                    selectCommentId = null;
                    et_reploy_comment.setHint("评论");
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void like(CheckBox checkBox, final CommentItem region) {
                new LikeHandler(HelpWendaCommentActivity.this).setCallback(new LikeHandler.Callback() {
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
                }).click("4", region.getContentid(), region.getPraiseid(), checkBox);//评论
            }

            @Override
            public void abuse(CommentItem region) {
                //举报答案
                new AbuseReportHandler(HelpWendaCommentActivity.this).setCallback(new AbuseReportHandler.Callback() {
                    @Override
                    public void call() {
                        showToast("举报成功");
                    }
                }).click(region.getSourceid(), "2", region.getContentid());
            }
        });
    }

    private void doComment() {
        if (!actionLock.tryLock()) return;
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob12Request req = new Partjob12Request();
                Partjob12Request.Parttimejob job = req.new Parttimejob();
                job.setSourceid(UserSubject.getStudentid());
                if (null == selectCommentId) {
                    job.setTargettype("3");//1：求助  2：问题 3：答案 4：评论本身 5、求助公告
                    job.setTargetid(answerid);
                } else {
                    job.setTargettype("4");
                    job.setTargetid(selectCommentId);
                }
                job.setOptype("1");//评论类型 1：评论 2：点赞
                job.setContent(et_reploy_comment.getText().toString());
                req.setParttimejob(job);
                new AzService().doTrans(req, Partjob12Response.class, new AzService.Callback<Partjob12Response>() {
                    @Override
                    public void success(Partjob12Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            String praisecount = resp.getBody().getParttimejob().getPraisecount();
                            messageHandler.postMessage(3, praisecount);
                        } else {
                            messageHandler.postMessage(4, "取消赞失败");
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
                case 1: {
                    try {
                    } finally {
                        actionLock.unlock();
                    }
                }
                case 3: {
                    try {
                        commentCount = String.valueOf(msg.obj);
                        groupListView.autoRefresh();
                    } finally {
                        actionLock.unlock();
                    }
                }
                case 4: {
                    try {
                    } finally {
                        actionLock.unlock();
                    }
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void initGroupData() {
        if (actionLock.tryLock()) {
            showProgressDialog("获取圈子中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
                        List<CommentItem> regionList = new ArrayList<CommentItem>();
                        for (int i = 0; i < 10 && mCurrentStart < 24; i++) {
                            Partjob29Response.Parttimejob region = new Partjob29Response.Parttimejob();
                            region.setSourcename("学生" + (i + mCurrentStart));
                            region.setSourcelevel("19");
                            region.setSourceschool("浙江大学");
                            region.setSourcecollege("计算机学院");
                            region.setOptime("20150801233341");
                            if (i % 4 == 0) {
                                region.setRefcomment(new Partjob29Response.Refcomment());
                                region.getRefcomment().setRefname("花仙子");
                                region.getRefcomment().setRefcontent("你是一个大SB");
                            }
                            if (i % 2 == 0) {
                                region.setPraiseflag("1");
                                region.setPraiseid("23232332");
                            }
                            region.setPraisecount("23");
                            region.setContent("浙江大学浙江大学浙江大学浙江大学\n浙江大学" + (i + mCurrentStart));
                            regionList.add(region);
                        }
                        messageHandler.postMessage(0, regionList);
                    } else {
                        Partjob29Request req = new Partjob29Request();
                        Partjob29Request.Parttimejob job = req.new Parttimejob();
                        job.setSourceid(UserSubject.getStudentid());
                        job.setTargettype("3");//3答案
                        job.setTargetid(answerid);
                        job.setCommenttype("1");//1评论
                        job.setStart("" + mCurrentStart);
                        job.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                        req.setParttimejob(job);
                        new AzService().doTrans(req, Partjob29Response.class, new AzService.Callback<Partjob29Response>() {
                            @Override
                            public void success(Partjob29Response response) {
                                ResponseResult result = response.getResult();
                                if ("0".equals(result.getCode())) {
                                    List<Partjob29Response.Parttimejob> parttimejob_list = response.getBody().getParttimejob_list();
                                    messageHandler.postMessage(0, parttimejob_list);
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
