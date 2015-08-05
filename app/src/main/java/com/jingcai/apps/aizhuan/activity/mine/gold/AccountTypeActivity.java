package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.gold.AccountTypeListAdapter;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Request;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.LocalValUtil;

import java.util.List;

/**
 * Created by Json Ding on 2015/6/8.
 */
public class AccountTypeActivity extends BaseActivity implements ListView.OnItemClickListener{

    private ListView mListView;
    private AccountTypeListAdapter mListAdapter;

    private MessageHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_account_type);
        messageHandler = new MessageHandler(this);

        initHeader();

        initView();

        initData();

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_account_type_list);
        mListView.setOnItemClickListener(this);
        mListAdapter = new AccountTypeListAdapter(this);
        mListAdapter.setFooterDividerEnabel(false);

    }

    private void initData() {
//        showProgressDialog("账户类型读取中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AzService azService = new AzService(AccountTypeActivity.this);

                Account04Request request = new Account04Request();

                azService.doTrans(request, Account04Response.class, new AzService.Callback<Account04Response>() {
                    @Override
                    public void success(Account04Response resp) {
                        ResponseResult result = resp.getResult();
                        if(!"0".equals(result.getCode())){
                            messageHandler.postMessage(1,result.getMessage());
                        }else{
                            messageHandler.postMessage(0,resp.getBody().getBank_list());
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
        ((TextView)findViewById(R.id.tv_content)).setText("绑定账号类型");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AccountTypeListAdapter.ViewHolder holder = (AccountTypeListAdapter.ViewHolder) view.getTag();
        Account04Response.Account04Body.Bank bank = holder.getBank();

        String code = bank.getCode();
        switch (code){
            case "AliPay":
                Intent intent = new Intent(AccountTypeActivity.this,BindAlipayActivity.class);
                LocalValUtil.setVal(bank);
                startActivity(intent);
                AccountTypeActivity.this.finish();
                break;
            //TODO 其他类型
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
                    fillBankList((List<Account04Response.Account04Body.Bank>) msg.obj);
                    break;
                }
                case 1: {
                    showToast("账户类型获取失败："+msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private void fillBankList(List<Account04Response.Account04Body.Bank> banks) {
        mListAdapter.setData(banks);
        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
    }
}
