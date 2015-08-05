package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
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
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Request;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
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
    private MessageHandler messageHandler;
    private XListView groupListView;
    private HelpCommentAdapter commentAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private EditText et_reploy_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerid = getIntent().getStringExtra("answerid");
        if(StringUtil.isEmpty(answerid)){
            finish();
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
                finish();
            }
        });
    }

    private void initView() {
        et_reploy_comment = (EditText) findViewById(R.id.et_reploy_comment);

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
                    et_reploy_comment.setHint("回复：" + region.getContent());
                } else {
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
                }).click("", "2", region.getContentid());
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
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void initGroupData() {
        //TODO 接入服务端接口
        if (actionLock.tryLock()) {
            showProgressDialog("获取圈子中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
                        List<Base04Response.Body.Region> regionList = new ArrayList<Base04Response.Body.Region>();
                        for (int i = 0; i < 10 && mCurrentStart < 15; i++) {
                            Base04Response.Body.Region region = new Base04Response.Body.Region();
                            region.setRegionid("" + (i + mCurrentStart));
                            region.setRegionname("浙江大学" + (i + mCurrentStart));
                            regionList.add(region);
                        }
                        messageHandler.postMessage(0, regionList);
                    } else {
                        final AzService azService = new AzService(HelpWendaCommentActivity.this);
                        final Base04Request req = new Base04Request();
                        final Base04Request.Region region = req.new Region();
                        region.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                        region.setAreacode(GlobalConstant.gis.getAreacode());
                        region.setStart(String.valueOf(mCurrentStart));
                        region.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                        req.setRegion(region);
                        azService.doTrans(req, Base04Response.class, new AzService.Callback<Base04Response>() {
                            @Override
                            public void success(Base04Response response) {
                                ResponseResult result = response.getResult();
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(1, result.getMessage());
                                } else {
                                    Base04Response.Body partjob07Body = response.getBody();
                                    List<Base04Response.Body.Region> regionList = partjob07Body.getRegion_list();
                                    //if (regionList.size() < 1 && 0 == mCurrentStart) {
                                    //    messageHandler.postMessage(2);
                                    //} else {
                                    messageHandler.postMessage(0, regionList);
                                    //}
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
}
