package com.jingcai.apps.aizhuan.activity.mine.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Administrator on 2015/7/15.
 */
public class MineSuggestionActivity extends BaseActivity {

    private MessageHandler messageHandler;


    private EditText mTxtContent;
    private Button mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_suggestion);
        messageHandler = new MessageHandler(this);
        ((TextView)findViewById(R.id.tv_content)).setText("我有话说");
        ((TextView)findViewById(R.id.tv_func)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.tv_content)).setText("提交");
       // findViewById(R.id.tv_info).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回主界面
                finish();
            }
        });
        if(UserSubject.isLogin()){
            initView();  //初始化控件
        }else{
            startActivityForLogin();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mTxtContent = (EditText) findViewById(R.id.et_other_suggestion_content);
        mBtnSubmit = (Button) findViewById(R.id.btn_other_suggestion_submit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AzExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        final AzService azService = new AzService(MineSuggestionActivity.this);
                        Advice03Request req = new Advice03Request();
                        Advice03Request.Suggestion suggestion = req.new Suggestion();
                        suggestion.setChannel(GlobalConstant.TERMINAL_TYPE_ANDROID); //安卓端
                        suggestion.setContent(mTxtContent.getText().toString());
                        suggestion.setType("2"); //建议
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


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx){
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what){
                case 0:{
                    showToast("感谢您提出的建议！");
                    finish();
                    break;
                }
                case 1:{
                    showToast("抱歉，建议提交失败："+ msg.obj);
                    break;
                }

                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }
}
