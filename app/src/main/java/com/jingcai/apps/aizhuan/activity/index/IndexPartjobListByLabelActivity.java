package com.jingcai.apps.aizhuan.activity.index;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.partjob.PartjobDetailActivity;
import com.jingcai.apps.aizhuan.adapter.partjob.PartjobListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob10.Partjob10Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob10.Partjob10Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IndexPartjobListByLabelActivity extends BaseActivity {
    private MessageHandler messageHandler;
    private AzService azService;
    private int mCurrentStart = 0;
    private XListView partjobListView;
    private View layout_empty;
    private PartjobListAdapter partjobListAdapter;
    private String labelid, labelname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);

        labelid = getIntent().getStringExtra("labelid");
        labelname = getIntent().getStringExtra("labelname");
        if(StringUtil.isEmpty(labelid) || StringUtil.isEmpty(labelname)){
            showToast("标签参数错误");
            finish();
            return;
        }

        setContentView(R.layout.index_partjob_list_by_label);
        initHeader();
        initView();

        initData();
    }

    private void initHeader(){
        ((TextView) findViewById(R.id.tv_content)).setText(labelname);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_func).setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_bird_badge).setVisibility(View.INVISIBLE);
    }
    private void initView() {


        partjobListView = (XListView) findViewById(R.id.index_pj_list_lv2);

        partjobListAdapter = new PartjobListAdapter(PartjobListAdapter.AdapterType.IndexLabel, this);
        partjobListView.setAdapter(partjobListAdapter);
        partjobListView.setPullRefreshEnable(true);
        partjobListView.setPullLoadEnable(true);
        partjobListView.setXListViewListener(listViewListener);
        partjobListView.setAutoLoadEnable(true);
        partjobListView.setRefreshTime(getTime());
        partjobListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PartjobListAdapter.ViewHolder holder = (PartjobListAdapter.ViewHolder) view.getTag();
                if (null != holder && null != holder.getPartjob()) {
                    Partjob10Response.Body.Parttimejob partjob = holder.getPartjob();
                    Intent intent = new Intent(IndexPartjobListByLabelActivity.this, PartjobDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("partjobid", partjob.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        layout_empty = findViewById(R.id.layout_empty);
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    private XListView.IXListViewListener listViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            partjobListAdapter.clearData();
            mCurrentStart = 0;
            partjobListView.setPullLoadEnable(true);
            initListData();
        }

        @Override
        public void onLoadMore() {
            initListData();
        }
    };

    private void onLoad() {
        partjobListView.stopRefresh();
        partjobListView.stopLoadMore();
        partjobListView.setRefreshTime(getTime());
    }

    private void initData() {
        initListData();
    }

    private void initListData() {
        if(actionLock.tryLock()) {
            showProgressDialog("消息加载中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final Partjob10Request req = new Partjob10Request();
                    final Partjob10Request.Label label = req.new Label();
                    label.setId(labelid);
                    req.setLabel(label);
                    final Partjob10Request.Parttimejob job = req.new Parttimejob();
                    GlobalConstant.Gis gis = GlobalConstant.getGis();
                    if (gis.isAvail()) {
                        job.setAreacode(gis.getAreacode());
                        job.setAreacode2(gis.getAreacode2());
                        job.setGisx(gis.getGisx());
                        job.setGisy(gis.getGisy());
                        job.setProvincename(gis.getProvincename());
                        job.setCityname(gis.getCityname());
                    } else {
                        job.setAreacode(GlobalConstant.AREA_CODE_HANGZHOU);
                    }
                    job.setStart(String.valueOf(mCurrentStart));
                    job.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    req.setParttimejob(job);
                    azService.doTrans(req, Partjob10Response.class, new AzService.Callback<Partjob10Response>() {
                        @Override
                        public void success(Partjob10Response response) {
                            ResponseResult result = response.getResult();
                            if (!"0".equals(result.getCode())) {
                                messageHandler.postMessage(1, result.getMessage());
                            } else {
                                Partjob10Response.Body body = response.getBody();
                                List<Partjob10Response.Body.Parttimejob> messageList = body.getParttimejob_list();
                                if (0 == mCurrentStart && messageList.size() < 1) {
                                    messageHandler.postMessage(2);//无数据
                                } else {
                                    messageHandler.postMessage(0, messageList);  //本次加载的长度
                                }
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
                        List<Partjob10Response.Body.Parttimejob> list = (List<Partjob10Response.Body.Parttimejob>) msg.obj;
                        partjobListAdapter.addData(list);
                        partjobListAdapter.notifyDataSetChanged();

                        mCurrentStart += list.size();
                        onLoad();

                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            partjobListView.setPullLoadEnable(false);
                        }
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast("获取消息失败:" + msg.obj);
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        partjobListView.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
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
}
