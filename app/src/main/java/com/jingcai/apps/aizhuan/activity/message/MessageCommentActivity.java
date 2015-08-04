package com.jingcai.apps.aizhuan.activity.message;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 赞与评论均为此Activity
 * 若传入的INTENT_NAME_ACTIVITY_FLAG 为
 * INTENT_VALUE_ACTIVITY_COMMENT 则为 评论
 * INTENT_VALUE_ACTIVITY_COMMEND 则为 赞
 * 其他，则会自动关闭此activity
 * Created by Json Ding on 2015/7/14.
 */
public class MessageCommentActivity extends BaseActivity {
    public static final String INTENT_NAME_ACTIVITY_FLAG = "activity_flag";
    public static final int INTENT_VALUE_ACTIVITY_FLAG_COMMENT = 0;
    public static final int INTENT_VALUE_ACTIVITY_FLAG_COMMEND = 1;

    private static final String TAG = "MessageCommendActivity";
    private XListView mLvComments;
    private CommentListAdapter mListAdapter;
    private int mCurrentStart = 0;
    private AzService azService;
    private MessageHandler messageHandler;
    private int activityFlag;  //打开的是哪一个activity
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_comment);

        azService = new AzService(this);
        messageHandler = new MessageHandler(this);

        unpackIntent();
        initHeader();
        initView();
        initData();
    }

    private void unpackIntent() {
        final Intent intent = getIntent();
        if (intent == null) {
            Log.w(TAG, MessageCommentActivity.class.getSimpleName() + " required a intent to open it");
            this.finish();
            return;
        }
        activityFlag = intent.getIntExtra(INTENT_NAME_ACTIVITY_FLAG, INTENT_VALUE_ACTIVITY_FLAG_COMMENT);
        if(activityFlag != INTENT_VALUE_ACTIVITY_FLAG_COMMENT &&
                activityFlag != INTENT_VALUE_ACTIVITY_FLAG_COMMEND){
            Log.w(TAG, MessageCommentActivity.class.getSimpleName() + " get a unknown intent value.");
            this.finish();
        }
    }

    private void initData() {
        if(actionLock.tryLock()) {
            showProgressDialog(String.format("%s努力加载中...",title));
            loadCommend();
        }
    }

    private void loadCommend() {
        if(GlobalConstant.debugFlag){
            //测试数据
            List<Partjob29Response.Partjob29Body.Parttimejob> parttimejob_list = new ArrayList<>();
            Partjob29Response.Partjob29Body.Parttimejob parttimejob = null;

            for(int i = 0 ; i < 10; i++){
                parttimejob = new Partjob29Response.Partjob29Body.Parttimejob();
                if(activityFlag == INTENT_VALUE_ACTIVITY_FLAG_COMMENT){
                    parttimejob.setContent("这是内容这是内容这是内容这是内容这是内容这是内容这是内容这是内容");
                    parttimejob.setOptype("1");
                }else if(activityFlag == INTENT_VALUE_ACTIVITY_FLAG_COMMEND){
                    parttimejob.setOptype("2");
                }
                parttimejob.setOptime("20150731111111");
                parttimejob.setSourceid("sourceid:" + i);
                parttimejob.setSourcename("丁" + i);
                parttimejob.setSourceimgurl("http://img0.imgtn.bdimg.com/it/u=1259311097,2736957493&fm=21&gp=0.jpg");
                parttimejob.setContentid(String.valueOf(i));
                parttimejob.setSourcelevel(String.valueOf(i));
                if(i%3==0) {
                    Partjob29Response.Partjob29Body.Parttimejob.Refcomment refcomment = new Partjob29Response.Partjob29Body.Parttimejob.Refcomment();
                    refcomment.setRefcontent("这是引用这是引用的内容这是引用的内容这是引用的内容这是引用的内容的内容");
                    refcomment.setRefid("studentid:" + i);
                    parttimejob.setRefcomment(refcomment);
                }
                Partjob29Response.Partjob29Body.Parttimejob.Reftarget reftarget = new Partjob29Response.Partjob29Body.Parttimejob.Reftarget();
                reftarget.setImgurl("http://img0.imgtn.bdimg.com/it/u=3201629386,3592649916&fm=11&gp=0.jpg");
                reftarget.setPubliccontent("我是引用这是引用的内容这是引用的内容这是引用的内容这是引用的内容这是引用的内容这是引用的内容这是引用的内容这是引用的内容内容");
                reftarget.setStudentname("林" + i);
                reftarget.setTargetid("targetid:" + i);
                reftarget.setTargettype(String.valueOf((i%2+1)));
                parttimejob.setReftarget(reftarget);

                parttimejob_list.add(parttimejob);
                if(mCurrentStart >= 30 && i==5){
                    break;
                }
            }
            messageHandler.postMessage(0, parttimejob_list);
        }else{
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
        // TODO: 2015/8/1 加入未读赞数统计
        tvTitle.setText(title = (activityFlag == INTENT_VALUE_ACTIVITY_FLAG_COMMEND ? "赞" : "评论"));
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
                            Log.w(TAG,"the response list of Partjob29Response.Partjob29Body.Parttimejob is null.");
                            return;
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
                        showToast(String.format("获取%s失败",title));
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
