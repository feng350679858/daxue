package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.util.IOSPopWin;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Request;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Response;
import com.jingcai.apps.aizhuan.service.business.account.account05.Account05Request;
import com.jingcai.apps.aizhuan.service.business.account.account05.Account05Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xiangqili on 2015/7/15.
 */
public class MineAccountActivity extends BaseActivity {

    private static final String TAG = "MineAccountActivity";
    private AzService azService;
    private MessageHandler messageHandler;
    private TextView mTvIncome;
    private TextView mTvBalance;
    private TextView mTvFreeze;

    private IOSPopWin mTipWin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_index);
        azService = new AzService(this);
        messageHandler = new MessageHandler(this);
        mTipWin = new IOSPopWin(this);
        initHeader();
        initView();
    }

    public void initHeader() {
        findViewById(R.id.tv_content).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    private void initView() {
        initComponent();
        initEvents();
    }

    /**
     * 检查是否通过身份验证
     */
    private void checkIdentity() {
        String idnoauthflag = UserSubject.getIdnoauthflag();
        String tip = null;
        switch (idnoauthflag){
            case "0":
                tip = getString(R.string.gold_withdraw_validate_identity_not_pass);
                mTipWin.showWindow("身份验证",tip,"下次再说","去吧去吧");
                break;
            case "1":
                Intent intent = new Intent(MineAccountActivity.this, MineGoldWithdrawActivity.class);
                startActivity(intent);
                break;
            case "2":
                tip = getString(R.string.gold_withdraw_validate_identity_wait);
                mTipWin.showWindow("身份验证", tip,"我知道了");
                break;
        }
    }



    private void initComponent() {
        mTvIncome = (TextView) findViewById(R.id.tv_mine_gold_index_income);
        mTvBalance = (TextView) findViewById(R.id.tv_mine_gold_index_balance);
        mTvFreeze = (TextView) findViewById(R.id.tv_mine_gold_index_freeze);
    }

    private void initEvents() {

        //跳转的Activity
        View.OnClickListener activityChangeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                Activity thisActivity = MineAccountActivity.this;
                switch (v.getId()) {
                    case R.id.rl_gold_account_expense: //支出记录
                        intent = new Intent(thisActivity, MineGoldExpenseActivity.class);
                        break;
                    case R.id.rl_mine_gold_account_income: //收入记录
                        intent = new Intent(thisActivity, MineGoldIncomeActivity.class);
                        break;
                    case R.id.rl_account_reset_pay_psw: //修改支付密码
                        intent = new Intent(thisActivity, MineModifyPayPasswordActivity.class);
                        break;
                    case R.id.rl_manage_financial_account://管理金融账号
                        intent = new Intent(thisActivity,AccountFinancialActivity.class);
                        break;
                    case R.id.btn_mine_gold_account_withdraw://取款
                        checkIdentity();
                        break;
                    case R.id.btn_mine_gold_account_topup://充值
                        intent = new Intent(thisActivity, MineGoldRechargeActivity.class);
                        break;
                }
                if(null != intent) {
                    startActivity(intent);
                }
            }
        };

        findViewById(R.id.rl_gold_account_expense).setOnClickListener(activityChangeListener);
        findViewById(R.id.rl_mine_gold_account_income).setOnClickListener(activityChangeListener);
        findViewById(R.id.rl_account_reset_pay_psw).setOnClickListener(activityChangeListener);
        findViewById(R.id.rl_manage_financial_account).setOnClickListener(activityChangeListener);
        findViewById(R.id.btn_mine_gold_account_withdraw).setOnClickListener(activityChangeListener);
        findViewById(R.id.btn_mine_gold_account_topup).setOnClickListener(activityChangeListener);

        mTipWin.setButtonClickListener(new IOSPopWin.ButtonClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                Intent intent = new Intent(MineAccountActivity.this, IdentityAuthenticationActivity.class);
                startActivity(intent);
                mTipWin.dismiss();
                finish();
            }
        });
    }

    private void initData() {
        initBalanceData();
        initIncomeData();
    }


    private void fillIncome(ArrayList<Account05Response.Account05Body.Account> accounts) {
        float incomeThisWeek = 0f;
        float money = 0f;
        if (null != accounts) {
            for (int i = 0; i < 7; i++) {
                money = Float.parseFloat(accounts.get(i).getOpmoney());
                incomeThisWeek += money;
            }
        }

        mTvIncome.setText(StringUtil.getPrintMoney(incomeThisWeek));
    }

    private void initBalanceData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Account01Request req = new Account01Request();
                Account01Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);

                azService.doTrans(req, Account01Response.class, new AzService.Callback<Account01Response>() {
                    @Override
                    public void success(Account01Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(0, resp.getBody().getWallet_list());
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
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    fillBalance((ArrayList<Account01Response.Account01Body.Wallet>) msg.obj);
                    break;
                }
                case 1: {
                    showToast("账户余额获取失败");
                    Log.w(TAG, "账户余额获取失败:" + msg.obj);
                    break;
                }
                case 2: {
                    fillIncome((ArrayList<Account05Response.Account05Body.Account>) msg.obj);
                    break;
                }
                case 3: {
                    showToast("账户收入获取失败");
                    Log.w(TAG, "账户收入获取失败:" + msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }


    private void fillBalance(ArrayList<Account01Response.Account01Body.Wallet> wallets) {
        if (null != wallets) {
            for (int i = 0; i < wallets.size(); i++) {
                if ("freeze".equals(wallets.get(i).getCode())) {
                    mTvFreeze.setText(StringUtil.getPrintMoney(wallets.get(i).getCredit()));
                } else if ("gold".equals(wallets.get(i).getCode())) {
                    mTvBalance.setText(StringUtil.getPrintMoney(wallets.get(i).getCredit()));
                }
            }
        }
    }

    private void initIncomeData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Account05Request req = new Account05Request();

                Account05Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);

                Account05Request.Account account = req.new Account();
                account.setBegindate(DateUtil.formatDate(new Date(System.currentTimeMillis() - 3600l * 24l * 6l * 1000l), "yyyyMMdd"));
                account.setEnddate(DateUtil.formatDate(new Date(), "yyyyMMdd"));
                account.setWalletcode("gold");
                req.setAccount(account);

                azService.doTrans(req, Account05Response.class, new AzService.Callback<Account05Response>() {
                    @Override
                    public void success(Account05Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(3, result.getMessage());
                        } else {
                            messageHandler.postMessage(2, resp.getBody().getAccount_list());
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
