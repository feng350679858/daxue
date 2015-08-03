package com.jingcai.apps.aizhuan.activity.sys;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;

/**
 * Created by Json Ding on 2015/5/5.
 */
public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_about_us);
        initHeader();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("关于我们");
        findViewById(R.id.iv_func).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
