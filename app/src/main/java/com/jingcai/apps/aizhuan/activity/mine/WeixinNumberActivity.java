package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.ShowBigImage;

/**
 * Created by Administrator on 2015/7/22.
 */
public class WeixinNumberActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_contact_service_weixin);

        initHeader();
        initViews();

    }

    private void initViews() {
        findViewById(R.id.ll_subscribe_number).setOnClickListener(this);
        findViewById(R.id.ll_service_number).setOnClickListener(this);

    }

    private void initHeader() {
        ((TextView)findViewById(R.id.tv_content)).setText("微信公众账号");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(WeixinNumberActivity.this, ShowBigImage.class);
        switch (v.getId()){
            case R.id.ll_subscribe_number:
                intent.putExtra("default_image",R.drawable.contact_service_weixin_subscribe);
                break;
            case R.id.ll_service_number:
                intent.putExtra("default_image",R.drawable.contact_service_weixin_service);
                break;
        }
        startActivity(intent);
    }
}
