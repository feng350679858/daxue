package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;

/**
 * Created by Json Ding on 2015/8/5.
 */
public class IdentityAuthenticationActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_identity_authentication);

        initHeader();
    }

    private void initHeader() {
        ((TextView)findViewById(R.id.tv_content)).setText("身份验证");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
