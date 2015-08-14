package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
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
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob22.Partjob22Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob31.Partjob31Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * answerflag true  默认 answerid， receiverid 必须要传
 * false     helpid  money 必须要传
 */
public class HelpWendaRewardActivity extends BaseActivity {
    private boolean answerflag = true;
    private String answerid, receiverid;
    private String helpid, jishiHelpMoney;

    private MessageHandler messageHandler;
    private EditText et_reward_money;
    private boolean updateFlag = false;

    private void finishWithResult() {
        if (updateFlag) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerflag = getIntent().getBooleanExtra("answerflag", answerflag);
        if (answerflag) {
            answerid = getIntent().getStringExtra("answerid");
            receiverid = getIntent().getStringExtra("receiverid");
        } else {
            helpid = getIntent().getStringExtra("helpid");
            jishiHelpMoney = getIntent().getStringExtra("jishiHelpMoney");
        }
        if (answerflag && (StringUtil.isEmpty(answerid) || StringUtil.isEmpty(receiverid))) {
            finishWithResult();
        } else if (!answerflag && (StringUtil.isEmpty(helpid) || StringUtil.isEmpty(jishiHelpMoney))) {
            finishWithResult();
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
                finishWithResult();
            }
        });
    }

    private void initView() {
        et_reward_money = (EditText) findViewById(R.id.et_reward_money);
        if (answerflag) {
            et_reward_money.addTextChangedListener(new MoneyWatcher());
        } else {
            et_reward_money.setText(jishiHelpMoney);
            et_reward_money.setEnabled(false);
        }

        findViewById(R.id.btn_reward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double money = 0f;
                try {
                    money = Double.parseDouble(et_reward_money.getText().toString());
                } catch (Exception e) {
                }
                if (money <= 0) {
                    showToast("请输入有效的金额");
                    return;
                }
                PayPwdWin payPwdWin = new PayPwdWin(HelpWendaRewardActivity.this);
                payPwdWin.setCallback(new PayPwdWin.Callback() {
                    @Override
                    public void call(String pwd) {
                        doReward(pwd, et_reward_money.getText().toString());
                    }
                });
                payPwdWin.setTitle("确认打赏");
                payPwdWin.showPay(money);
            }
        });
    }

    private void doReward(final String pwd, final String moneyStr) {
        if (!actionLock.tryLock()) return;
        if (answerflag) {
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
                            if ("0".equals(resp.getResultCode())) {
                                messageHandler.postMessage(0);
                            } else if ("S012".equals(resp.getResultCode())) {//余额不足
                                messageHandler.postMessage(2);
                            } else {
                                messageHandler.postMessage(1, "打赏失败:" + resp.getResultMessage());
                            }
                        }

                        @Override
                        public void fail(AzException e) {
                            messageHandler.postException(e);
                        }
                    });
                }
            });
        } else {
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Partjob22Request req = new Partjob22Request();
                    Partjob22Request.Parttimejob job = req.new Parttimejob();
                    job.setStudentid(UserSubject.getStudentid());
                    job.setHelpid(helpid);
                    job.setPaypassword(DES3Util.encrypt(pwd));
                    req.setParttimejob(job);
                    new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse resp) {
                            if ("0".equals(resp.getResultCode())) {
                                messageHandler.postMessage(0);
                            } else {
                                messageHandler.postMessage(1, "打赏失败:" + resp.getResultMessage());
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
                        showToast("打赏成功");
                        finishWithResult();
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
                        PayInsufficientWin win = new PayInsufficientWin(HelpWendaRewardActivity.this);
                        win.show();
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
