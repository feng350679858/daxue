package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;

/**
 * Created by Administrator on 2015/7/16.
 */
public class MineResetPasswordActivity extends BaseActivity{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_account_reset_pay_password);

        initHeader();

        initView();
    }

    private void initHeader()
    {
        ((TextView)findViewById(R.id.tv_content)).setText("修改支付密码");

    }

    private void initView()
    {
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
       findViewById(R.id.ll_mine_gold_account_remember_password).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MineResetPasswordActivity.this,MineResetpaypswActivity.class);
               startActivity(intent);
           }
       });
        findViewById(R.id.ll_mine_gold_account_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineResetPasswordActivity.this,SafeCheckActivity.class);
                startActivity(intent);
            }
        });
    }
}
