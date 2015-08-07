package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.mine.SafeCheckActivity;
import com.jingcai.apps.aizhuan.activity.util.IOSPopWin;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by Administrator on 2015/7/16.
 */
public class MineModifyPayPasswordActivity extends BaseActivity{

    private IOSPopWin mTipWin;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_account_reset_pay_password);
        mTipWin = new IOSPopWin(this);
        if(!UserSubject.getHaspaypasswordset()){
            showToast("请设置您的支付密码");
            Intent intent = new Intent(MineModifyPayPasswordActivity.this,MineResetPayPasswordActivity.class);
            intent.putExtra(MineResetPayPasswordActivity.INTENT_EXTRA_NAME_STAGE,MineResetPayPasswordActivity.RESET_PASSWORD_STAGE_2);
            startActivity(intent);
        }
        initHeader();
        initView();

    }

    private void initHeader(){
        ((TextView)findViewById(R.id.tv_content)).setText("修改支付密码");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    private void initView(){
        ((TextView) findViewById(R.id.tv_modify_phone)).setText("您正在为"+ StringUtil.hiddenPhone(UserSubject.getPhone())+"修改密码");

        findViewById(R.id.ll_mine_gold_account_remember_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineModifyPayPasswordActivity.this, MineResetPayPasswordActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.ll_mine_gold_account_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIdentity();
            }
        });

        mTipWin.setConfirmButtonClickListener(new IOSPopWin.ConfirmButtonClickListener() {
            @Override
            public void onConfirmButtonClick() {
                Intent intent = new Intent(MineModifyPayPasswordActivity.this, IdentityAuthenticationActivity.class);
                startActivity(intent);
                mTipWin.dismiss();
                finish();
            }
        });
    }

    private void checkIdentity() {
        String idnoauthflag = UserSubject.getIdnoauthflag();
        String tip = null;
        switch (idnoauthflag) {
            case "0":
                tip = getString(R.string.gold_modify_pay_psw_validate_identity_not_pass);
                mTipWin.showWindow("身份验证", tip, "下次再说", "去吧去吧");
                break;
            case "1":
                Intent intent = new Intent(MineModifyPayPasswordActivity.this, SafeCheckActivity.class);
                startActivity(intent);
                break;
            case "2":
                tip = getString(R.string.gold_withdraw_validate_identity_wait);
                mTipWin.showWindow("身份验证", tip, "我知道了");
                break;
        }
    }
}
