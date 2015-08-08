package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.partjob.PartjobListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob01.Partjob01Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob03.Partjob03Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob03.Partjob03Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPartjobListActivity extends BaseActivity implements AdapterView.OnItemClickListener,XListView.IXListViewListener {
    private final String TAG="MyPartjobListActivity";
    private MessageHandler messageHandler;
    private PartjobListAdapter partjobListAdapter;
    private XListView mListMinePartjob;
    private View layout_empty;  //列表为空,显示这个view
    private int mCurrentStart = 0;  //当前的开始

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partjob_list);
        messageHandler = new MessageHandler(this);
        initHeader();
        initView();
        initData();
    }

    private void initHeader(){
        ((TextView) findViewById(R.id.tv_content)).setText("我的兼职");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_func).setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_bird_badge).setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化视图
     */
    private void initView() {
//        layout_empty = findViewById(R.id.layout_empty);
//        ((ImageView)findViewById(R.id.iv_empty)).setImageResource(R.drawable.mine_partjob_list_empty);
//        ((TextView)findViewById(R.id.tv_empty)).setText("暂时没有兼职，要马上行动哦！");

        mListMinePartjob = (XListView) findViewById(R.id.lv_mine_partjob);
        mListMinePartjob.setPullRefreshEnable(true);
        mListMinePartjob.setPullLoadEnable(true);
        mListMinePartjob.setAutoLoadEnable(true);
        mListMinePartjob.setXListViewListener(this);
        mListMinePartjob.setOnItemClickListener(this);

        partjobListAdapter = new PartjobListAdapter(PartjobListAdapter.AdapterType.MinePartjob,this);
        mListMinePartjob.setAdapter(partjobListAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        final Context context =this;
        if(actionLock.tryLock()) {
            showProgressDialog("兼职加载中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final AzService azService = new AzService(context);
                    final Partjob03Request req = new Partjob03Request();
                    final Partjob03Request.Joininfo joininfo = new Partjob03Request.Joininfo();
                    joininfo.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                    joininfo.setStart(String.valueOf(mCurrentStart));
                    joininfo.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    req.setJoininfo(joininfo);
                    azService.doTrans(req, Partjob03Response.class, new AzService.Callback<Partjob03Response>() {
                        @Override
                        public void success(Partjob03Response response) {
                            ResponseResult result = response.getResult();
                            if (!"0".equals(result.getCode())) {
                                messageHandler.postMessage(1, result.getMessage());
                            } else {
                                Partjob03Response.Partjob03Body partjob03Body = response.getBody();
                                List<Partjob01Response.Body.Parttimejob> joininfoList = partjob03Body.getJoininfo_list();
                                if(joininfoList==null)
                                    joininfoList=new ArrayList<>();
                                if (0 == mCurrentStart && joininfoList.size() < 1) {
                                    messageHandler.postMessage(2);
                                } else {
                                    messageHandler.postMessage(0, joininfoList);
                                }
                            }
                        }

                        @Override
                        public void fail(AzException e) {
                            finishLoading();
                            messageHandler.postException(e);
                        }
                    });
                }
            });
        }
    }

    /**
     * 兼职列表点击事件处理
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PartjobListAdapter.ViewHolder mCurrentSelectedItem = (PartjobListAdapter.ViewHolder)view.getTag();
        if(null == mCurrentSelectedItem || null == mCurrentSelectedItem.getPartjob()){
            return ;
        }
        String jobId = mCurrentSelectedItem.getPartjob().getId();
        if(StringUtil.isNotEmpty(jobId)){
            Intent intent = new Intent(this, MyPartjobDetailActivity.class);
            intent.putExtra("id", jobId);
            startActivity(intent);
        }
    }

    //刷新
    @Override
    public void onRefresh() {
        messageHandler.post(new Runnable() {
            @Override
            public void run() {
                partjobListAdapter.clearData();
                mCurrentStart = 0;
                mListMinePartjob.setPullLoadEnable(true);
                initData();
            }
        });
    }

    private void finishLoading() {
        mListMinePartjob.stopRefresh();
        mListMinePartjob.stopLoadMore();
        mListMinePartjob.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
    }

    @Override
    public void onResume() {
        onRefresh();
        super.onResume();
    }

    //加载更多
    @Override
    public void onLoadMore() {
        messageHandler.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0 : {
                    try {
                        //将数据填充到ListView中
                        List<Partjob01Response.Body.Parttimejob> list = (List<Partjob01Response.Body.Parttimejob>) msg.obj;
                        partjobListAdapter.addData(list);
                        partjobListAdapter.notifyDataSetChanged();
                        mCurrentStart += list.size();
                        finishLoading();
                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            mListMinePartjob.setPullLoadEnable(false);
                        }
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1 : {
                    try {
                        finishLoading();
                        showToast("获取兼职失败");
                        Log.i(TAG,"获取兼职失败:" + msg.obj);
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2 : {
                    try {
                        //列表为空,将列表移除，然后将表示空图加上
                        mListMinePartjob.setVisibility(View.GONE);
                        ((ViewStub) findViewById(R.id.stub_empty_view)).inflate();
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                    break;
                }
            }
        }
    }

}
