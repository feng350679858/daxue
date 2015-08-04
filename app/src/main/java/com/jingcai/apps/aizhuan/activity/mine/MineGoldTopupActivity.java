package com.jingcai.apps.aizhuan.activity.mine;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
public class MineGoldTopupActivity extends BaseActivity {
    private final String TAG = "MineGoldTopupActivity";
    private static final int REQUEST_CODE_CHOICE_ACCOUNT = 1;
    private MessageHandler messageHandler;

    private TextView mWithDrawRMB;
    private EditText mInputCount;
    private Button mWithdrawSubmit;

    private AzService azService;
    private ListView mListView;
    private Account04Response.Account04Body.Bank selectedBank;
    private AccountChoiceListAdapter mListAdapter;

    private float mEnableGoldCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_account_topup);
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);
        initHeader();
        initView();
        initData();
    }


    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("充值");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mInputCount = (EditText) findViewById(R.id.et_mine_gold_topup_count);
        mWithdrawSubmit = (Button) findViewById(R.id.btn_mine_gold_topup_submit);
        mWithDrawRMB = (TextView) findViewById(R.id.tv_mine_gold_topup_money);

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
                    mWithDrawRMB.setText(StringUtil.money(inputCount)+"元");
                    mWithdrawSubmit.setEnabled(true);
                } else {
                    mWithdrawSubmit.setEnabled(false);
                }
            }

        });

        mWithdrawSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCountStr = mInputCount.getText().toString();
                if (selectedBank == null) {
                    showToast("请选择账户");
                    return;
                }
                if (Integer.parseInt(inputCountStr) < 10) {
                    showToast("至少充值10元");
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
                azService = new AzService(MineGoldTopupActivity.this);

                Account04Request request = new Account04Request();
                Account04Request.Student student = request.new Student();

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
                    showToast("第三方金融渠道列表失败");
                    Log.i(TAG, "第三方金融渠道列表失败：" + msg.obj);
                    break;
                }
                case 2: {
                    fillBalance((ArrayList<Account01Response.Account01Body.Wallet>) msg.obj);
                    break;
                }
                case 3: {
                    showToast("获取余额失败");
                    Log.i(TAG, "获取余额失败：" + msg.obj);
                    break;
                }
//                case 4: {
//                    showToast("请求已提交，等待审核！");
//                    MineGoldTopupActivity.this.finish();
//                    break;
//                }
//                case 5: {
//                    showToast("提现失败");
//                    Log.i(TAG, "提现失败：" + msg.obj);
//                    break;
//                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private void fillBankInfo(List<Account04Response.Account04Body.Bank> banks) {
        if (null == banks || 0 == banks.size()) {
            return;
        }
        selectedBank=banks.get(0);
        mListAdapter = new AccountChoiceListAdapter(this, 0);
        mListView = (ListView) findViewById(R.id.lv_mine_account_topup_choice_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListAdapter.setSelectednum(position);
                selectedBank=(Account04Response.Account04Body.Bank)mListAdapter.getItem(position);
                mListAdapter.notifyDataSetChanged();
            }
        });
        mListView.setAdapter(mListAdapter);
        mListAdapter.setData(banks);
        mListAdapter.notifyDataSetChanged();
    }

    private void fillBalance(ArrayList<Account01Response.Account01Body.Wallet> wallets) {
        for (int i = 0; i < wallets.size(); i++) {
            if ("gold".equals(wallets.get(i).getCode())) {
                mEnableGoldCount = Float.parseFloat(wallets.get(i).getCredit());
                String gold = StringUtil.getFormatFloat(mEnableGoldCount, "#,###");
                ((TextView) findViewById(R.id.tv_mine_gold_topup_rest)).setText(gold + "金");
            }
        }
    }
}
