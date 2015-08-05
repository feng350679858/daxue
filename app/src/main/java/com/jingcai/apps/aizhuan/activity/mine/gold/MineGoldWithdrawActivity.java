package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Request;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Response;
import com.jingcai.apps.aizhuan.service.business.account.account03.Account03Request;
import com.jingcai.apps.aizhuan.service.business.account.account03.Account03Response;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Request;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.LocalValUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/16.
 */
public class MineGoldWithdrawActivity extends BaseActivity{
    private final String TAG = "MineGoldWithdraw";
    private static final int REQUEST_CODE_CHOICE_ACCOUNT = 1;
    private MessageHandler messageHandler;

    private EditText mInputCount;
    private Button mWithdrawSubmit;
    private TextView mNotEnoughText;
    private RelativeLayout empty_item,bank_item;
    private AzService azService;
    private BitmapUtil mBitmapUtil;
    private Account04Response.Account04Body.Bank mCurrentBank;
    private float mEnableGoldCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_withdraw);
        mBitmapUtil = new BitmapUtil(this);
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);
        initHeader();

        initView();
        initData();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("提现");

        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mInputCount = (EditText) findViewById(R.id.input);
        mWithdrawSubmit = (Button) findViewById(R.id.gold_withdraw);
        mNotEnoughText = (TextView) findViewById(R.id.tv_mine_account_withdraw_balance_not_enough);
        empty_item=(RelativeLayout)findViewById(R.id.empty_item);
        bank_item=(RelativeLayout)findViewById(R.id.bank_item);
        mInputCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputCount = s.toString();
                if (StringUtil.isNotEmpty(inputCount) && !"0.0".equals(StringUtil.money(inputCount))) {
                    if (Float.parseFloat(StringUtil.money(inputCount)) > mEnableGoldCount) {
                        mNotEnoughText.setVisibility(View.VISIBLE);
                        mWithdrawSubmit.setEnabled(false);
                    } else {
                        mNotEnoughText.setVisibility(View.GONE);
                        mWithdrawSubmit.setEnabled(true);
                    }
                } else {
                    mWithdrawSubmit.setEnabled(false);
                    mNotEnoughText.setVisibility(View.GONE);
                }
            }

        });

        mWithdrawSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCountStr = mInputCount.getText().toString();
                if (mCurrentBank == null) {
                    showToast("请选择账户");
                    return;
                }
                if (Integer.parseInt(inputCountStr) < 300) {
                    showToast("至少提现300金");
                    return;
                }

            }
        });


    }

    private void initData() {
        showProgressDialog("数据加载中...");
        initBankData();
        initBalanceData();

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
                            messageHandler.postMessage(3, result.getMessage());
                        } else {
                            messageHandler.postMessage(2, resp.getBody().getWallet_list());
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

    private void initBankData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(MineGoldWithdrawActivity.this);

                Account04Request request = new Account04Request();
                Account04Request.Student student = request.new Student();
                student.setStudentid(UserSubject.getStudentid());
                request.setStudent(student);

                azService.doTrans(request, Account04Response.class, new AzService.Callback<Account04Response>() {
                    @Override
                    public void success(Account04Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(7, result.getMessage());
                        } else {
                            messageHandler.postMessage(6, resp.getBody().getBank_list());
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

    private void withdrawProcess(final String payPsw) {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Account03Request acr = new Account03Request();

                Account03Request.Wallet wallet = acr.new Wallet();
                wallet.setCode("gold");
                wallet.setOpmoney(mInputCount.getText().toString());
                acr.setWallet(wallet);

                Account03Request.Bank bank = acr.new Bank();
                bank.setBanktype(mCurrentBank.getType());
                bank.setCardno(mCurrentBank.getCardno());
                bank.setCardtype(mCurrentBank.getCode());
                acr.setBank(bank);

                Account03Request.Student student = acr.new Student();
                student.setPaypassword(payPsw);
                student.setStudentid(UserSubject.getStudentid());
                acr.setStudent(student);

                azService.doTrans(acr, Account03Response.class, new AzService.Callback<Account03Response>() {
                    @Override
                    public void success(Account03Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(5, result.getMessage());
                        } else {
                            messageHandler.postMessage(4);
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

                case 2: {
                    fillBalance((ArrayList<Account01Response.Account01Body.Wallet>) msg.obj);
                    break;
                }
                case 3: {
                    showToast("获取余额失败");
                    Log.i(TAG, "获取余额失败：" + msg.obj);
                    break;
                }
                case 4: {
                    showToast("请求已提交，等待审核！");
                    MineGoldWithdrawActivity.this.finish();
                    break;
                }
                case 5: {
                    showToast("提现失败");
                    Log.i(TAG, "提现失败：" + msg.obj);
                    break;
                }
                case 6: {
                    fillBankInfo((List<Account04Response.Account04Body.Bank>) msg.obj);
                    break;
                }
                case 7: {
                    showToast("金融账户获取失败");
                    Log.i(TAG, "金融账户获取失败：" + msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private void fillBankInfo(List<Account04Response.Account04Body.Bank> banks) {

        if (null!= banks && banks.size() > 0) {
            mCurrentBank = banks.get(0);
            initBankInfo();
        }
        else{
            empty_item.setVisibility(View.VISIBLE);
            bank_item.setVisibility(View.GONE);
        }
    }
    private void initBankInfo(){
        mBitmapUtil.getImage((ImageView)findViewById(R.id.iv_mine_account_choose_list_item_logo),mCurrentBank.getImgurl(),R.drawable.ic_launcher);
        ((TextView) findViewById(R.id.tv_mine_account_choose_list_item_title)).setText(mCurrentBank.getName());
        String cardno = mCurrentBank.getCardno();
        cardno = StringUtil.hiddenPhone(cardno);  //隐藏字符串
        ((TextView) findViewById(R.id.tv_mine_account_choose_list_item_code)).setText(cardno);
        ((ImageView)findViewById(R.id.iv_mine_account_choose_list_item_select)).setImageDrawable(getResources().getDrawable(R.drawable.icon_right_triangle));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOICE_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    mCurrentBank = (Account04Response.Account04Body.Bank) LocalValUtil.getVal();
                    initBankInfo();
                }
                break;
        }
    }

    private void fillBalance(ArrayList<Account01Response.Account01Body.Wallet> wallets) {
        for (int i = 0; i < wallets.size(); i++) {
            if ("gold".equals(wallets.get(i).getCode())) {
                mEnableGoldCount = Float.parseFloat(wallets.get(i).getCredit());
                break;
            }
        }
    }

}
