package com.jingcai.apps.aizhuan.activity.mine.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpListAdapter;
import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpListItem;
import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpResponse;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.business.partjob.Partjob24.Partjob24Request;
import com.jingcai.apps.aizhuan.service.business.partjob.Partjob24.Partjob24Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob18.Partjob18Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob18.Partjob18Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob23.Partjob23Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob23.Partjob23Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob27.Partjob27Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob27.Partjob27Response;
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
    public static final int REQUEST_CODE_PROVIDE_JISIH_REWARD = 1101;
    public static final int REQUEST_CODE_RECEIVE_JISIH_EVALUATE = 1201;
    private MessageHandler messageHandler;
    private XListView groupListView;
    private ViewStub empty_view;
    private MineHelpListAdapter commentAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private boolean provideFlag = true;//true我的帮助 false我的求助
    private boolean jishiFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.mine_help_list);

        provideFlag = getIntent().getBooleanExtra("provideFlag", provideFlag);
        commentAdapter = new MineHelpListAdapter(this);
        commentAdapter.setProvideFlag(provideFlag);
        commentAdapter.setJishiFlag(jishiFlag);

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
            }
        });

        RadioButton rb_jishi = (RadioButton) findViewById(R.id.rb_jishi);
        RadioButton rb_wenda = (RadioButton) findViewById(R.id.rb_wenda);
        if (provideFlag) {
            rb_jishi.setText("我的求助");
            rb_wenda.setText("我的提问");
        } else {
            rb_jishi.setText("我的帮助");
            rb_wenda.setText("我的回答");
        }
    }

    private void initView() {
        RadioGroup rg_title = (RadioGroup) findViewById(R.id.rg_title);
        rg_title.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                jishiFlag = checkedId == R.id.rb_jishi;
                groupListView.setVisibility(View.VISIBLE);
                if (null != empty_view) {
                    empty_view.setVisibility(View.GONE);
                }
                refresh();
            }
        });

        groupListView = (XListView) findViewById(R.id.xlv_list);
        groupListView.setAdapter(commentAdapter);
        groupListView.setPullRefreshEnable(true);
        groupListView.setPullLoadEnable(true);
        groupListView.setAutoLoadEnable(true);
        groupListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                initGroupData();
            }
        });
    }

    private void refresh() {
        commentAdapter.clearData();
        commentAdapter.setJishiFlag(jishiFlag);

        mCurrentStart = 0;
        groupListView.setPullLoadEnable(true);
        initGroupData();
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
                        List<MineHelpListItem> list = (List<MineHelpListItem>) msg.obj;
                        if(null != list) {
                            commentAdapter.addData(list);
                            commentAdapter.notifyDataSetChanged();
                            mCurrentStart += list.size();
                        }
                        onLoad();
                        if (null == list || list.size() < GlobalConstant.PAGE_SIZE) {
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
                case 2:{
                    try {
                        groupListView.setVisibility(View.GONE);
                        if(null == empty_view) {
                            empty_view = (ViewStub) findViewById(R.id.stub_empty_view);
                            empty_view.inflate();
                        }else{
                            empty_view.setVisibility(View.VISIBLE);
                        }
                    }finally {
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
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (provideFlag) {
                    if (jishiFlag) {//我的求助 partjob18
                        Partjob18Request req = new Partjob18Request(UserSubject.getStudentid(), String.valueOf(mCurrentStart), String.valueOf(GlobalConstant.PAGE_SIZE));
                        doTrans(req, Partjob18Response.class);
                    } else {//我的提问 partjob23
                        Partjob23Request req = new Partjob23Request(UserSubject.getStudentid(), String.valueOf(mCurrentStart), String.valueOf(GlobalConstant.PAGE_SIZE));
                        doTrans(req, Partjob23Response.class);
                    }
                } else {
                    if (jishiFlag) {//我的帮助 partjob24
                        Partjob24Request req = new Partjob24Request(UserSubject.getStudentid(), String.valueOf(mCurrentStart), String.valueOf(GlobalConstant.PAGE_SIZE));
                        doTrans(req, Partjob24Response.class);
                    } else {//我的回答 partjob27
                        Partjob27Request req = new Partjob27Request(UserSubject.getStudentid(), String.valueOf(mCurrentStart), String.valueOf(GlobalConstant.PAGE_SIZE));
                        doTrans(req, Partjob27Response.class);
                    }
                }
            }
        });
    }

    private <Resp extends MineHelpResponse> void doTrans(final Partjob18Request req, final Class<Resp> cls) {
        new AzService().doTrans(req, cls, new AzService.Callback<Resp>() {
            @Override
            public void success(Resp resp) {
                if ("0".equals(resp.getResultCode())) {
                    List<MineHelpListItem> list = resp.getList();
                    if ((null == list || list.size() < 1) && 0 == mCurrentStart) {
                        messageHandler.postMessage(2);
                    } else {
                        messageHandler.postMessage(0, list);
                    }
                } else {
                    messageHandler.postMessage(1, resp.getResultMessage());
                }
            }

            @Override
            public void fail(AzException e) {
                actionLock.unlock();
                messageHandler.postException(e);
            }
        });
    }
    private Partjob18Response.Parttimejob provideJishiJob = null;
    public void setCurrentJob(Partjob18Response.Parttimejob job){
        provideJishiJob = job;
    }
    private Partjob24Response.Parttimejob receiveJishiJob = null;
    public void setCurrentJob(Partjob24Response.Parttimejob job) {
        receiveJishiJob= job;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PROVIDE_JISIH_REWARD: {
                if(RESULT_OK == resultCode){
                    if(null != provideJishiJob) {
                        provideJishiJob.setStatus("6");
                        commentAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
            case REQUEST_CODE_RECEIVE_JISIH_EVALUATE: {
                if(RESULT_OK == resultCode){
                    if(null != receiveJishiJob) {
                        receiveJishiJob.setEvelflag("1");
                        commentAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
