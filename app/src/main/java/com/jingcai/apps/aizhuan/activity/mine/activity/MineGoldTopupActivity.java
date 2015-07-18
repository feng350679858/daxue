package com.jingcai.apps.aizhuan.activity.mine.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.gold.AccountChoiceListAdapter;
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
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.util.LocalValUtil;
import com.jingcai.apps.aizhuan.util.PixelUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/16.
 */
public class MineGoldTopupActivity extends BaseActivity implements ListView.OnItemClickListener{
    private static final int REQUEST_CODE_CHOICE_ACCOUNT = 1;
    private MessageHandler messageHandler;

    private TextView mWithDrawRMB;
    private EditText mInputCount;
    private Button mWithdrawSubmit;
    private TextView mNotEnoughText;

    private AzService azService;
    private ListView mListView;

    private AccountChoiceListAdapter mListAdapter;

    private Account04Response.Account04Body.Bank mCurrentBank;

    private float mEnableGoldCount;
    private boolean isResultResume;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_account_topup);
        initHeader();


        messageHandler = new MessageHandler(this);
        azService = new AzService(this);
        initHeader();

        initView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Account04Response.Account04Body.Bank selectedBank = (Account04Response.Account04Body.Bank) mListAdapter.getItem(position);
        MineGoldTopupActivity.this.setResult(RESULT_OK, new Intent());
        LocalValUtil.setVal(selectedBank);
    }
    @Override
    protected void onResume() {
        if(!isResultResume){
            initData();
        }
        isResultResume = false;
        super.onResume();
    }


    private void initData() {
        showProgressDialog("���ݼ�����...");
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
                azService = new AzService( MineGoldTopupActivity.this);

                Account04Request request = new Account04Request();
                Account04Request.Student student = request.new Student();
                student.setStudentid(UserSubject.getStudentid());
                request.setStudent(student);

                azService.doTrans(request, Account04Response.class, new AzService.Callback<Account04Response>() {
                    @Override
                    public void success(Account04Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(0, resp.getBody().getBank_list());
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


    private void initView()
    {
        mInputCount = (EditText)findViewById(R.id.et_mine_gold_topup_count);
        mWithdrawSubmit = (Button) findViewById(R.id.btn_mine_gold_topup_submit);
        mWithDrawRMB = (TextView) findViewById(R.id.tv_mine_gold_topup_money);
        mListView = (ListView)findViewById(R.id.lv_mine_account_topup_choice_list);
        mListAdapter = new AccountChoiceListAdapter(this,mCurrentBank);
        mListAdapter.setFooterDividerEnabel(false);
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
                if (StringUtil.isNotEmpty(inputCount)) {
                    int count = Integer.parseInt(inputCount);
                    float rmb = count / 10.0f;
                        mWithDrawRMB.setText(String.valueOf(rmb));
                        mWithDrawRMB.setTextColor( MineGoldTopupActivity.this.getResources().getColor(R.color.important_dark));
                } else {
                    mWithDrawRMB.setText("0");
                    mWithDrawRMB.setTextColor( MineGoldTopupActivity.this.getResources().getColor(R.color.important_dark));
                    mNotEnoughText.setVisibility(View.GONE);
                    mWithdrawSubmit.setEnabled(false);
                }
            }

        });

        mWithdrawSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCountStr = mInputCount.getText().toString();
                if (mCurrentBank == null) {
                    showToast("��ѡ���˻�");
                    return;
                }
                if (StringUtil.isEmpty(inputCountStr) || ("0".equals(inputCountStr))) {
                    showToast("��������");
                    return;
                }
                View dialogView = LayoutInflater.from( MineGoldTopupActivity.this).inflate(R.layout.mine_gold_account_withdraw_pay_psw_dialog, null);
                final PopupWindow popupWindow = new PopupWindow(dialogView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                final EditText txtPassword = (EditText) dialogView.findViewById(R.id.et_account_withdraw_pay_psw);
                final View decorView =  MineGoldTopupActivity.this.getWindow().getDecorView();
                dialogView.findViewById(R.id.btn_account_withdraw_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ObjectAnimator.ofFloat(decorView, "alpha", 0.5f, 1.0f).setDuration(500).start();
                        popupWindow.dismiss();

                    }
                });
                dialogView.findViewById(R.id.btn_account_withdraw_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String payPsw = txtPassword.getText().toString();
                        withdrawProcess(DES3Util.encrypt(payPsw));
                        ObjectAnimator.ofFloat(decorView, "alpha", 0.5f, 1.0f).setDuration(500).start();
                        popupWindow.dismiss();
                    }
                });
                dialogView.findViewById(R.id.et_account_withdraw_pay_psw).setFocusable(true);
                ((TextView) dialogView.findViewById(R.id.tv_account_withdraw_title)).setText("������֧������");
                popupWindow.setFocusable(true);
                popupWindow.setAnimationStyle(R.style.main_menu_animstyle);
                popupWindow.showAtLocation(decorView, Gravity.CENTER_HORIZONTAL, 0, PixelUtil.px2dip( MineGoldTopupActivity.this, 200f));

                ObjectAnimator.ofFloat(decorView, "alpha", 1.0f, 0.5f).setDuration(500).start();
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

    private void initHeader()
    {
        ((TextView)findViewById(R.id.tv_content)).setText("��ֵ");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void fillBalance(ArrayList<Account01Response.Account01Body.Wallet> wallets) {
        for (int i = 0; i < wallets.size(); i++) {
            if ("gold".equals(wallets.get(i).getCode())) {
                mEnableGoldCount = Float.parseFloat(wallets.get(i).getCredit());
                String gold = StringUtil.getFormatFloat(mEnableGoldCount, "#,###");
                ((TextView) findViewById(R.id.tv_mine_gold_rest)).setText(gold + "��");
            }
        }
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
                    fillBankInfo(((List<Account04Response.Account04Body.Bank>) msg.obj));
                    break;
                }
                case 1: {
                    showToast("��ȡ�˻�ʧ�ܣ�"+msg.obj);
                    break;
                }
                case 2:{
                    fillBalance((ArrayList<Account01Response.Account01Body.Wallet>)msg.obj);
                    break;
                }
                case 3:{
                    showToast("��ȡ���ʧ�ܣ�"+msg.obj);
                    break;
                }
                case 4:{
                    showToast("�������ύ���ȴ���ˣ�");
                    MineGoldTopupActivity.this.finish();
                    break;
                }
                case 5:{
                    showToast("����ʧ�ܣ�"+msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
    private void fillBankInfo(List<Account04Response.Account04Body.Bank> banks) {

        if(banks.size() > 0){
            mCurrentBank = banks.get(0);
            initBankInfo();
        }else{

        }
    }
    private void initBankInfo() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_CHOICE_ACCOUNT:
                if(resultCode == RESULT_OK){
                    isResultResume = true;
                    mCurrentBank = (Account04Response.Account04Body.Bank) LocalValUtil.getVal();
                    initBankInfo();
                }
                break;
        }
    }
}
