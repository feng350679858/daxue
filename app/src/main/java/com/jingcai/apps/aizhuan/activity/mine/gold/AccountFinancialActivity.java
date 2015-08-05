package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.util.IOSPopWin;
import com.jingcai.apps.aizhuan.adapter.mine.gold.AccountBankListAdapter;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Request;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Response;
import com.jingcai.apps.aizhuan.service.business.account.account06.Account06Request;
import com.jingcai.apps.aizhuan.service.business.account.account06.Account06Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;

import java.util.List;

/**
 * Created by Json Ding on 2015/6/8.
 */
public class AccountFinancialActivity extends BaseActivity implements AccountBankListAdapter.OnSwipeButtonClickListener {

    private ListView mListView;
    private AccountBankListAdapter mListAdapter;
    private MessageHandler messageHandler;

    private IOSPopWin mConfirmWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gold_account_financial);
        messageHandler = new MessageHandler(this);
        mConfirmWin = new IOSPopWin(this);

        initHeader();
        initView();
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    private void initData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AzService azService = new AzService(AccountFinancialActivity.this);

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

    private void initView() {

        mListView = (ListView) findViewById(R.id.lv_account_list);
        mListAdapter = new AccountBankListAdapter(this);
        mListAdapter.setFooterDividerEnabel(false);
        mListAdapter.setOnSwipeButtonClickListener(this);


        findViewById(R.id.ll_gold_account_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountFinancialActivity.this, AccountTypeActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void OnSwipeListButtonClick(final int position, View view) {
        mConfirmWin.showWindow("提示", "确定要删除此账号？", "下次再说", "解解解");
        mConfirmWin.setConfirmButtonClickListener(new IOSPopWin.ConfirmButtonClickListener() {
            @Override
            public void onConfirmButtonClick() {
                unBindAccount(((Account04Response.Account04Body.Bank) mListAdapter.getItem(position)));
                mConfirmWin.dismiss();
            }
        });
    }

    /**
     * 解除绑定账号
     * @param item
     */
    private void unBindAccount(final Account04Response.Account04Body.Bank item) {
//        showProgressDialog("账户绑定中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AzService azService = new AzService(AccountFinancialActivity.this);

                Account06Request request = new Account06Request();
                Account06Request.Student student = request.new Student();
                student.setStudentid(UserSubject.getStudentid());
                request.setStudent(student);

                Account06Request.Bank bank = request.new Bank();
                bank.setBanktype(item.getType());
                bank.setCardtype(item.getCode());
                bank.setCardno(item.getCardno());
                bank.setOpflag("2");
                request.setBank(bank);

                azService.doTrans(request, Account06Response.class, new AzService.Callback<Account06Response>() {
                    @Override
                    public void success(Account06Response resp) {
                        ResponseResult result = resp.getResult();
                        if(!"0".equals(result.getCode())){
                            messageHandler.postMessage(3,result.getMessage());
                        }else{
                            messageHandler.postMessage(2);
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

    private void initHeader() {
        ((TextView)findViewById(R.id.tv_content)).setText("管理金融账号");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    fillBankList((List<Account04Response.Account04Body.Bank>) msg.obj);
                    break;
                }
                case 1: {
                    showToast("金融账户获取失败：" + msg.obj);
                    break;
                }
                case 2:{
                    showToast("解绑成功");
                    initData();
                    break;
                }
                case 3:{
                    showToast("解绑失败："+msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private void fillBankList(List<Account04Response.Account04Body.Bank> bankList) {
        mListAdapter.setData(bankList);
        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
    }
}
