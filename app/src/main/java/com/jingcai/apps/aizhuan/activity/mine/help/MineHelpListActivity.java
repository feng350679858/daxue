package com.jingcai.apps.aizhuan.activity.mine.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpListAdapter;
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
 * 我的帮助
 * Created by lejing on 15/7/23.
 */
public class MineHelpListActivity extends BaseActivity {

    private MessageHandler messageHandler;
    private XListView groupListView;
    private MineHelpListAdapter commentAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private boolean provideFlag = true;//true我的帮助 false我的求助
    private boolean jishiFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.mine_help_list);

        provideFlag = getIntent().getBooleanExtra("provideFlag", true);
        commentAdapter = new MineHelpListAdapter(this);
        commentAdapter.setProvideFlag(provideFlag);
        commentAdapter.setJishiFlag(true);

        initHeader();

        initView();

        initGroupData();
    }

    private void initHeader() {
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MineHelpListActivity.this, MineHelpListActivity.class);
                intent.putExtra("provideFlag", !provideFlag);
                startActivity(intent);
            }
        });

        RadioButton rb_jishi = (RadioButton) findViewById(R.id.rb_jishi);
        RadioButton rb_wenda = (RadioButton) findViewById(R.id.rb_wenda);
        if (provideFlag) {
            rb_jishi.setText("我的帮助");
            rb_wenda.setText("我的回答");
        } else {
            rb_jishi.setText("我的求助");
            rb_wenda.setText("我的提问");
        }
    }

    private void initView() {
        RadioGroup rg_title = (RadioGroup) findViewById(R.id.rg_title);
        rg_title.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                jishiFlag = checkedId == R.id.rb_jishi;
                groupListView.autoRefresh();
            }
        });

        groupListView = (XListView) findViewById(R.id.xlv_list);
        groupListView.setAdapter(commentAdapter);
        groupListView.setPullRefreshEnable(true);
        groupListView.setPullLoadEnable(true);
        groupListView.setAutoLoadEnable(true);

        commentAdapter.setJishiFlag(true);

        groupListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                commentAdapter.clearData();
                commentAdapter.setJishiFlag(jishiFlag);

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
                case 1: {
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
        //TODO 接入服务端接口
        if (actionLock.tryLock()) {
            showProgressDialog("获取圈子中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
                        List<Base04Response.Body.Region> regionList = new ArrayList<Base04Response.Body.Region>();
                        for (int i = 0; i < 10 && mCurrentStart < 24; i++) {
                            Base04Response.Body.Region region = new Base04Response.Body.Region();
                            region.setRegionid("" + (i + mCurrentStart));
                            if (provideFlag) {
                                if (jishiFlag) {
                                    region.setRegionname("我的帮助我的帮助我的帮助\n我的帮助\n我的帮助我的帮助" + (i + mCurrentStart));
                                } else {
                                    region.setRegionname("我的回答我的回答我的回答\n我的回答\n我的回答我的回答" + (i + mCurrentStart));
                                }
                            } else {
                                if (jishiFlag) {
                                    region.setRegionname("我的求助我的求助我的求助\n我的求助\n我的求助我的求助" + (i + mCurrentStart));
                                } else {
                                    region.setRegionname("我的提问我的提问我的提问\n我的提问\n我的提问我的提问" + (i + mCurrentStart));
                                }
                            }
                            regionList.add(region);
                        }
                        try { Thread.sleep(1000); } catch (InterruptedException e) { }
                        messageHandler.postMessage(0, regionList);
                    } else {
                        final AzService azService = new AzService(MineHelpListActivity.this);
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
