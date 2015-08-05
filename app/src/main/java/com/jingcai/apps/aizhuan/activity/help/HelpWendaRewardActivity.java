package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.common.MoneyWatcher;
import com.jingcai.apps.aizhuan.activity.util.PayInsufficientWin;
import com.jingcai.apps.aizhuan.activity.util.PayPwdWin;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob31.Partjob31Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaRewardActivity extends BaseActivity {
    private static final String TAG = "HelpWendaCommnet";
    private MessageHandler messageHandler;
    private String answerid, receiverid;
    private PayPwdWin payPwdWin;
    private PayInsufficientWin payInsufficientWin;
    private EditText et_reward_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerid = getIntent().getStringExtra("answerid");
        receiverid = getIntent().getStringExtra("receiverid");
        if (StringUtil.isEmpty(answerid) || StringUtil.isEmpty(receiverid)) {
            finish();
        } else {
            messageHandler = new MessageHandler(this);
            setContentView(R.layout.help_wenda_reward);

            initHeader();

            initView();
        }
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("打赏");

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        et_reward_money = (EditText) findViewById(R.id.et_reward_money);
        et_reward_money.addTextChangedListener(new MoneyWatcher());

        findViewById(R.id.btn_reward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float money = 0f;
                final String moneyStr = et_reward_money.getText().toString();
                try {
                    money = Float.parseFloat(moneyStr);
                } catch (Exception e) {
                }
                if (money <= 0) {
                    showToast("请输入有效的金额");
                    return;
                }
                if (null == payPwdWin) {
                    payPwdWin = new PayPwdWin(HelpWendaRewardActivity.this);
                    payPwdWin.setCallback(new PayPwdWin.Callback() {
                        @Override
                        public void call(String pwd) {
                            if(GlobalConstant.debugFlag){
                                messageHandler.postMessage(2);
                                return ;
                            }
                            doReward(pwd, moneyStr);
                        }
                    });
                    payPwdWin.setTitle("确认打赏");
                }
                payPwdWin.showPay(moneyStr);
            }
        });
    }

    private void doReward(final String pwd, final String moneyStr){
        if (!actionLock.tryLock()) return;
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob31Request req = new Partjob31Request();
                Partjob31Request.Parttimejob job = req.new Parttimejob();
                job.setSourceid(UserSubject.getStudentid());
                job.setTargetid(receiverid);
                job.setMoney(moneyStr);
                job.setReftype("1");
                job.setRefid(answerid);
                job.setPaypassword(DES3Util.encrypt(pwd));
                req.setParttimejob(job);
                new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        if("0".equals(resp.getResultCode())){
                            messageHandler.postMessage(0);
                        } else if("S012".equals(resp.getResultCode())){//余额不足
                            messageHandler.postMessage(2);
                        } else {
                            messageHandler.postMessage(1, resp.getResultMessage());
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
                    try {
                        showToast("打赏成功");
                        setResult(RESULT_OK);
                        finish();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast(String.valueOf(msg.obj));
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        if (null == payInsufficientWin) {
                            payInsufficientWin = new PayInsufficientWin(HelpWendaRewardActivity.this);
                        }
                        payInsufficientWin.show();
                    } finally {
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

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (null != payPwdWin && payPwdWin.isShowing()) {
//                payPwdWin.dismiss();
//                return true;
//            }
//            if (null != payInsufficientWin && payInsufficientWin.isShowing()) {
//                payInsufficientWin.dismiss();
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
