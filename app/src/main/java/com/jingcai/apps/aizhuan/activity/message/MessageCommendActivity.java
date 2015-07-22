package com.jingcai.apps.aizhuan.activity.message;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.message.CommentListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.Date;
import java.util.List;

/**
 * Created by Json Ding on 2015/7/14.
 */
public class MessageCommendActivity extends BaseActivity {

    private static final String TAG = "MessageCommendActivity";
    private XListView mLvComments;
    private CommentListAdapter mListAdapter;
    private int mCurrentStart = 0;
    private AzService azService;
    private MessageHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_comment);

        azService = new AzService(this);
        messageHandler = new MessageHandler(this);

        initHeader();
        initView();
        initData();
    }

    private void initData() {
        if(actionLock.tryLock()) {
            showProgressDialog("评论努力加载中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final Partjob29Request req = new Partjob29Request();
                    Partjob29Request.Parttimejob parttimejob = req.new Parttimejob();

                    parttimejob.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    parttimejob.setStart(String.valueOf(mCurrentStart));
                    parttimejob.setReceiverid(UserSubject.getStudentid());

                    req.setParttimejob(parttimejob);
                    azService.doTrans(req, Partjob29Response.class,new AzService.Callback<Partjob29Response>() {
                        @Override
                        public void success(Partjob29Response resp) {
                            ResponseResult result = resp.getResult();
                            if ("0".equals(result.getCode())) {
                                Partjob29Response.Partjob29Body body = resp.getBody();
                                List<Partjob29Response.Partjob29Body.Parttimejob> parttimejob_list = body.getParttimejob_list();
                                if(0 == mCurrentStart && parttimejob_list.size()<1){
                                    messageHandler.postMessage(2);
                                }else {
                                    messageHandler.postMessage(0, parttimejob_list);
                                }
                            } else {
                                messageHandler.postMessage(1, result.getMessage());
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

    private void initHeader() {
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        //需要用到再findViewById，不要需则不调用，提高效率
//        ImageView ivFunc = (ImageView) findViewById(R.id.iv_func);
//        TextView tvFunc = (TextView) findViewById(R.id.tv_func);

        tvTitle.setText("赞");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mLvComments = (XListView) findViewById(R.id.lv_comments);
        mListAdapter = new CommentListAdapter(this);
        mLvComments.setAutoLoadEnable(true);
        mLvComments.setPullLoadEnable(true);
        mLvComments.setPullRefreshEnable(true);
        mLvComments.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mListAdapter.clearData();
                mCurrentStart = 0;
                mLvComments.setPullLoadEnable(true);
                initData();
            }

            @Override
            public void onLoadMore() {
                initData();
            }
        });

        mLvComments.setAdapter(mListAdapter);
    }

    private void onLoad() {
        mLvComments.stopRefresh();
        mLvComments.stopLoadMore();
        mLvComments.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what){
                case 0: {
                    try {
                        List<Partjob29Response.Partjob29Body.Parttimejob> list = (List<Partjob29Response.Partjob29Body.Parttimejob>) msg.obj;
                        if(list == null){
                            Log.e(TAG,"the response list of Partjob29Response.Partjob29Body.Parttimejob is null.");
                        }
                        mListAdapter.addData(list);
                        mListAdapter.notifyDataSetChanged();

                        mCurrentStart += list.size();
                        onLoad();

                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            mLvComments.setPullLoadEnable(false);
                        }
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try{
                        showToast("获取评论失败:" + msg.obj);
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try{
                        mLvComments.setVisibility(View.GONE);
                        displayEmptyView();
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }

    /**
     * 显示空列表提示布局
     */
    private void displayEmptyView() {
        ((ViewStub) findViewById(R.id.stub_empty_view)).inflate();
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_text);
        tvEmpty.setText(getString(R.string.empty_merchant_list_tip));
        tvEmpty.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.empty_list_message_merchant,0,0);
    }

}
