package com.jingcai.apps.aizhuan.activity.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.message.NotificationListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.advice.advice01.Advice01Request;
import com.jingcai.apps.aizhuan.service.business.advice.advice01.Advice01Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.Date;
import java.util.List;

public class MessageNotificationActivity extends BaseActivity implements XListView.IXListViewListener {
    public static final int REQUEST_CODE_MESSAGE = 1;
    private MessageHandler messageHandler;
    private XListView lv_message_center;
    private int mCurrentStart = 0;  //当前的开始
    private NotificationListAdapter mMessageListAdapter;
    private NotificationListAdapter.ViewHolder mCurrentSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_notification);
        messageHandler = new MessageHandler(this);

        initHeader();
        initViews();

        initData();
    }

    private void initHeader() {
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        //需要用到再findViewById，不要需则不调用，提高效率
//        ImageView ivFunc = (ImageView) findViewById(R.id.iv_func);
//        TextView tvFunc = (TextView) findViewById(R.id.tv_func);

        tvTitle.setText("通知");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {


        lv_message_center = (XListView) findViewById(R.id.lv_message_center2);
        lv_message_center.setPullRefreshEnable(true);
        lv_message_center.setPullLoadEnable(true);
        lv_message_center.setXListViewListener(this);
        lv_message_center.setAutoLoadEnable(true);
        lv_message_center.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));

        mMessageListAdapter = new NotificationListAdapter(this);
        lv_message_center.setAdapter(mMessageListAdapter);

        lv_message_center.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Advice01Response.Advice01Body.Message message = mMessageListAdapter.getMessage(position-1);
                if (null != message) {
                    mCurrentSelectedItem = (NotificationListAdapter.ViewHolder) view.getTag();
                    Intent intent = new Intent(MessageNotificationActivity.this, NotificationDetailActivity.class);
                    intent.putExtra("id", message.getId());
                    startActivityForResult(intent, REQUEST_CODE_MESSAGE);
                }
            }
        });
    }

    private void initData() {
        if(actionLock.tryLock()) {
            final Context context = this;
            showProgressDialog("消息加载中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final AzService azService = new AzService(context);
                    final Advice01Request req = new Advice01Request();
                    final Advice01Request.Message message = req.new Message();
                    message.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                    message.setStart(String.valueOf(mCurrentStart));
                    message.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    req.setMessage(message);
                    azService.doTrans(req, Advice01Response.class, new AzService.Callback<Advice01Response>() {
                        @Override
                        public void success(Advice01Response response) {
                            ResponseResult result = response.getResult();

                            if (!"0".equals(result.getCode())) {
                                messageHandler.postMessage(1, result.getMessage());
                            } else {
                                Advice01Response.Advice01Body advice01Body = response.getBody();
                                List<Advice01Response.Advice01Body.Message> messageList = advice01Body.getMessage_list();
                                if (0 == mCurrentStart && messageList.size() < 1) {
                                    messageHandler.postMessage(2);  //本次加载的长度
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
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    try {
                        List<Advice01Response.Advice01Body.Message> list = (List<Advice01Response.Advice01Body.Message>) msg.obj;
                        mMessageListAdapter.addData(list);
                        mMessageListAdapter.notifyDataSetChanged();

                        mCurrentStart += list.size();
                        onLoad();

                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            lv_message_center.setPullLoadEnable(false);
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
//                    break;
                }
                case 2: {
                    try {
                        //列表为空,将列表移除，然后将表示空图加上
                        lv_message_center.setVisibility(View.GONE);
                        displayEmptyView();
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_MESSAGE: {
                //mMessageListAdapter.notifyDataSetChanged();
                mMessageListAdapter.changeTextColorByIsRead(mCurrentSelectedItem, true);
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    //刷新
    @Override
    public void onRefresh() {
        messageHandler.post(new Runnable() {
            @Override
            public void run() {
                mMessageListAdapter.clearData();
                mCurrentStart = 0;
                lv_message_center.setPullLoadEnable(true);
                initData();
            }
        });
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


    private void onLoad() {
        lv_message_center.stopRefresh();
        lv_message_center.stopLoadMore();
        lv_message_center.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
    }

    /**
     * 显示空列表提示布局
     */
    private void displayEmptyView() {
        ((ViewStub) findViewById(R.id.stub_empty_view)).inflate();
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_text);
        tvEmpty.setText(getString(R.string.empty_notification_list_tip) );
        tvEmpty.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.empty_list_message_notification,0,0);

    }
}
