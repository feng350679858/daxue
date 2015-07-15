package com.jingcai.apps.aizhuan.activity.message;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.advice.advice02.Advice02Request;
import com.jingcai.apps.aizhuan.service.business.advice.advice02.Advice02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class NotificationDetailActivity extends BaseActivity {
    private MessageHandler messageHandler;
    private String mId;  //列表传过来的id


    private TextView mTvTitle;
    private TextView mTvDate;
    private TextView mTvContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_notification_detail);

        mId = getIntent().getStringExtra("id");
        if(StringUtil.isEmpty(mId)){
            finish();
            return;
        }
        messageHandler = new MessageHandler(this);

        if (checkAndPerformLogin()) {
            initHeader();
            initView();  //初始化控件
            initData();
        }
    }

    private void initHeader() {
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        //需要用到再findViewById，不要需则不调用，提高效率
//        ImageView ivFunc = (ImageView) findViewById(R.id.iv_func);
//        TextView tvFunc = (TextView) findViewById(R.id.tv_func);

        tvTitle.setText("消息详情");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_mine_message_detail_title);
        mTvDate = (TextView) findViewById(R.id.tv_mine_message_detail_date);
        mTvContent = (TextView) findViewById(R.id.tv_mine_message_detail_content);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        showProgressDialog("消息加载中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final AzService azService = new AzService(NotificationDetailActivity.this);
                Advice02Request req = new Advice02Request();
                Advice02Request.Message message = req.new Message();
                message.setId(mId);
                req.setMessage(message);
                Advice02Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);
                azService.doTrans(req, Advice02Response.class, new AzService.Callback<Advice02Response>() {
                    @Override
                    public void success(Advice02Response response) {
                        ResponseResult result = response.getResult();
                        Advice02Response.Advice02Body advice02Body = response.getBody();
                        Advice02Response.Advice02Body.Message callbackData = advice02Body.getMessage();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(0, callbackData);
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
    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    Advice02Response.Advice02Body.Message message = (Advice02Response.Advice02Body.Message) msg.obj;
                    mTvTitle.setText(message.getTitle());
                    mTvContent.setText(message.getContent());
                    mTvDate.setText(message.getCreatedate());
                    break;
                }
                case 1: {
                    showToast("消息读取失败:" + msg.obj);
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }
}
