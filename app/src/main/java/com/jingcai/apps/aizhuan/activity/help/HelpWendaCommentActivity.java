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
import com.jingcai.apps.aizhuan.adapter.help.HelpCommentAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Request;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaCommentActivity extends BaseActivity {
    private static final String TAG = "HelpWendaComment";
    private MessageHandler messageHandler;
    private XListView groupListView;
    private HelpCommentAdapter commentAdapter;
    private int mCurrentStart = 0;  //当前的开始

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.help_wenda_comment);

        initHeader();

        initView();
        initGroupData();
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
                        List<Base04Response.Body.Region> list = (List<Base04Response.Body.Region>) msg.obj;
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
