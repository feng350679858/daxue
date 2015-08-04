package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.advice.advice03.Advice03Request;
import com.jingcai.apps.aizhuan.service.business.advice.advice03.Advice03Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by Json Ding on 2015/5/3.
 */
public class ComplainActivity extends BaseActivity {
    private MessageHandler messageHandler;
    private String jobid, joinid;

    private ImageView mImageLogopath;
    private TextView mTxtTitle;
    private EditText mTxtContent;
    private Button mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobid = getIntent().getStringExtra("jobid");
        joinid = getIntent().getStringExtra("joinid");
        if(StringUtil.isEmpty(jobid)|| StringUtil.isEmpty(joinid)){
            finish();
            return;
        }
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.mine_partjob_complain);

        ((TextView)findViewById(R.id.tv_content)).setText("投诉");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(UserSubject.isLogin()){
            initView();  //初始化控件
            initData();  //填充数据
        }else{
            startActivityForLogin();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mImageLogopath = (ImageView) findViewById(R.id.iv_mine_partjob_detail_complain_logopath);
        mTxtTitle = (TextView) findViewById(R.id.tv_mine_partjob_detail_complain_title);
        mTxtContent = (EditText) findViewById(R.id.et_mine_partjob_detail_complain_content);
        mBtnSubmit = (Button) findViewById(R.id.btn_mine_partjob_detail_complain_submit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AzExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        final AzService azService = new AzService(ComplainActivity.this);
                        Advice03Request req = new Advice03Request();
                        Advice03Request.Suggestion suggestion = req.new Suggestion();
                        suggestion.setChannel(GlobalConstant.TERMINAL_TYPE_ANDROID); //安卓端
                        suggestion.setContent(mTxtContent.getText().toString());
                        suggestion.setType("1");
                        suggestion.setJoinid(joinid);
                        suggestion.setJobid(jobid);
                        suggestion.setStudentid(UserSubject.getStudentid());
                        req.setSuggestion(suggestion);
                        azService.doTrans(req, Advice03Response.class,new AzService.Callback<Advice03Response>() {
                            @Override
                            public void success(Advice03Response resp) {
                                ResponseResult result = resp.getResult();
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(1, result.getMessage());
                                } else {
                                    messageHandler.postMessage(0);
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
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        BitmapUtil bitmapUtil = new BitmapUtil(this);
        bitmapUtil.getImage(mImageLogopath, getIntent().getStringExtra("logopath"), true, R.drawable.logo_merchant_default);
        mTxtTitle.setText(getIntent().getStringExtra("title"));
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx){
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what){
                case 0:{
                    showToast("投诉信息提交成功！");
                    finish();
                    break;
                }
                case 1:{
                    showToast("投诉信息提交失败："+ msg.obj);
                    break;
                }

                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }
}
