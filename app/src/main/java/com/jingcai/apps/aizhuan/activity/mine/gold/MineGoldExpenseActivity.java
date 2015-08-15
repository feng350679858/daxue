package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.gold.AccountStreamListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account02.Account02Request;
import com.jingcai.apps.aizhuan.service.business.account.account02.Account02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class MineGoldExpenseActivity extends BaseActivity implements XListView.IXListViewListener {
    private final String TAG = "MineGoldExpenseActivity";
    private XListView mListView;
    private MessageHandler messageHandler;
    private AccountStreamListAdapter mListAdapter;
    private View layout_empty;  //列表为空,显示这个view
    private int mCurrentStart = 0;  //当前的开始

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_account_steam_detail);
        messageHandler = new MessageHandler(this);
        initHeader();

        initView();
        initData();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("支出记录");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
//        layout_empty = findViewById(R.id.layout_empty);
//        ((ImageView)findViewById(R.id.iv_empty)).setImageResource(R.drawable.ic_launcher);
//        ((TextView) findViewById(R.id.tv_empty)).setText("没有支出数据！");

        mListView = (XListView) findViewById(R.id.lv_account_stream_detail_list);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);

        mListAdapter = new AccountStreamListAdapter(this);
        mListView.setAdapter(mListAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        final Context context = this;
        if (actionLock.tryLock()) {
            showProgressDialog("流水加载中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final AzService azService = new AzService(context);
                    final Account02Request req = new Account02Request();
                    final Account02Request.Student student = req.new Student();
                    student.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                    req.setStudent(student);

                    Account02Request.Account account = req.new Account();
                    account.setStart(String.valueOf(mCurrentStart));
                    account.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    account.setWalletcode("cash");
                    account.setOptype("debit");
                    account.setEnddate(DateUtil.formatDate(new Date(), "yyyyMMdd"));
                    account.setBegindate(DateUtil.formatDate(DateUtil.getDateMonthsAgo(1), "yyyyMMdd"));
                    req.setAccount(account);

                    azService.doTrans(req, Account02Response.class, new AzService.Callback<Account02Response>() {
                        @Override
                        public void success(Account02Response response) {
                            ResponseResult result = response.getResult();
                            if (!"0".equals(result.getCode())) {
                                messageHandler.postMessage(1, result.getMessage());
                            } else {

                                Account02Response.Account02Body account02Body = response.getBody();
                                ArrayList<Account02Response.Account02Body.Account> accountList = account02Body.getAccount_list();
                                if (null == accountList) {
                                    accountList = new ArrayList<>();
                                }

                                if (0 == mCurrentStart && accountList.size() < 1) {
                                    messageHandler.postMessage(2);
                                } else {
                                    messageHandler.postMessage(0, accountList);
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


    private void finishLoading() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
    }

    @Override
    public void onRefresh() {
        messageHandler.post(new Runnable() {
            @Override
            public void run() {
                mListAdapter.clearData();
                mCurrentStart = 0;
                mListView.setPullLoadEnable(true);
                initData();
            }
        });
    }

    @Override
    protected void onResume() {
        onRefresh();
        super.onResume();
    }

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
                case 0: {
                    try {
                        //将数据填充到ListView中
                        List<Account02Response.Account02Body.Account> list = (List<Account02Response.Account02Body.Account>) msg.obj;
                        mListAdapter.addData(list);
                        mListAdapter.notifyDataSetChanged();
                        mCurrentStart += list.size();
                        finishLoading();
                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            mListView.setPullLoadEnable(false);
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        finishLoading();
                        showToast("获取支出失败");
                        Log.i(TAG, "获取支出失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        //列表为空,将列表移除，然后将表示空图加上
                        mListView.setVisibility(View.GONE);
                        ((ViewStub) findViewById(R.id.stub_empty_view)).inflate();
                    } finally {
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
