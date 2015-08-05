package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.util.IOSPopWin;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Response;
import com.jingcai.apps.aizhuan.service.business.account.account06.Account06Request;
import com.jingcai.apps.aizhuan.service.business.account.account06.Account06Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.LocalValUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by Json Ding on 2015/6/8.
 */
public class BindAlipayActivity extends BaseActivity {

    private static final String TAG = "BindAlipayActivity";
    private EditText mEtPhone;
    private EditText mEtName;
    private CheckBox mCbTerms;
    private Button mBtnBind;
    private Account04Response.Account04Body.Bank mBank;
    private MessageHandler messageHandler;

    private IOSPopWin mConfirmWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gold_account_add_alipay);
        messageHandler = new MessageHandler(this);
        mBank = (Account04Response.Account04Body.Bank) LocalValUtil.getVal();
        mConfirmWin = new IOSPopWin(this);

        initHeader();

        initView();
    }

    private void initView() {

        mEtPhone = (EditText) findViewById(R.id.et_account_add_alipay_phone);
        mEtName = (EditText) findViewById(R.id.et_account_add_alipay_name);
        mCbTerms = (CheckBox) findViewById(R.id.cb_account_add_alipay_terms);
        mBtnBind = (Button)findViewById(R.id.btn_gold_account_add_alipay_bind) ;
        mEtName.setText(UserSubject.getName());
        mEtName.setEnabled(false);

        findViewById(R.id.tv_account_add_alipay_term).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO H5页面
                showToast("进入条款页面");
            }
        });

        mBtnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAndInitDialog()) {
                    String content = "账号：" + mEtPhone.getText().toString() + "\n姓名："+mEtName.getText().toString();
                    mConfirmWin.showWindow("绑定账号", content,"取消","确定");
                    mConfirmWin.setConfirmButtonClickListener(new IOSPopWin.ConfirmButtonClickListener() {
                        @Override
                        public void onConfirmButtonClick() {
                            bindAccount();
                            mConfirmWin.dismiss();
                        }
                    });
                }
            }
        });

        mCbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBtnBind.setEnabled(isChecked);
            }
        });

    }

    /**
     * 绑定账号
     */
    private void bindAccount() {
        showProgressDialog("账户绑定中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AzService azService = new AzService(BindAlipayActivity.this);

                Account06Request request = new Account06Request();
                Account06Request.Student student = request.new Student();
                student.setStudentid(UserSubject.getStudentid());
                request.setStudent(student);

                Account06Request.Bank bank = request.new Bank();
                bank.setBanktype(mBank.getType());
                bank.setCardtype(mBank.getCode());
                bank.setCardno(mEtPhone.getText().toString());
                bank.setOpflag("1");
                request.setBank(bank);

                azService.doTrans(request, Account06Response.class, new AzService.Callback<Account06Response>() {
                    @Override
                    public void success(Account06Response resp) {
                        ResponseResult result = resp.getResult();
                        if(!"0".equals(result.getCode())){
                            messageHandler.postMessage(1,result.getMessage());
                        }else{
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

    private boolean checkAndInitDialog() {
        String phone = mEtPhone.getText().toString();
        String name = mEtName.getText().toString();

        if(!mCbTerms.isChecked()){
            return false;
        }

        if(StringUtil.isNumber(phone)
                || phone.length() != 11){
            showToast("手机号码格式不正确");
            return false;
        }
        if(StringUtil.isEmpty(name)){
            showToast("姓名不可为空");
            return false;
        }
        return true;
    }

    private void initHeader() {
        ((TextView)findViewById(R.id.tv_content)).setText("绑定账号");
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
                    showToast("支付宝账号绑定成功");
                    BindAlipayActivity.this.finish();
                    break;
                }
                case 1: {
                    showToast("绑定失败");
                    Log.w(TAG,"绑定失败：" + msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
