package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.gold.AccountChoiceListAdapter;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Response;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Request;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.LocalValUtil;

import org.bouncycastle.jce.provider.symmetric.ARC4;

import java.util.ArrayList;
import java.util.List;

public class BankSelectActivity extends BaseActivity {
    private final String TAG="BankSelectActivity";
    private MessageHandler messageHandler;
    private AzService azService;

    private RelativeLayout empty_item;
    private ListView mListView;
    private AccountChoiceListAdapter mListAdapter;

    private boolean isResume = false;
    private Account04Response.Account04Body.Bank selectedBank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_select);
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);

        selectedBank=(Account04Response.Account04Body.Bank)LocalValUtil.getVal();

        initHeader();
        initView();
        initBankData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isResume) {
            showProgressDialog("数据加载中...");
            initBankData();
            isResume = true;
        }

    }

    private void initHeader() {
        ((TextView) findViewById(R.id.content)).setText("选择帐号");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        empty_item = (RelativeLayout) findViewById(R.id.empty_item);
        empty_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BankSelectActivity.this, AccountTypeActivity.class);
                startActivity(intent);
                isResume = true;
            }
        });
    }
    private void initBankData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(BankSelectActivity.this);

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
    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {

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
    private void fillBankInfo(final List<Account04Response.Account04Body.Bank> banks) {
        if (null == banks || 0 == banks.size()) {
            return;
        }
        mListAdapter = new AccountChoiceListAdapter(this,selectedBank);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocalValUtil.setVal(banks.get(position));
                setResult(RESULT_OK);
                finish();
            }
        });

        mListAdapter.setData(banks);
        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
    }
}
