package com.jingcai.apps.aizhuan.activity.partjob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.jingcai.apps.aizhuan.util.UmengShareUtil;

/**
 * Created by chenchao on 15/7/13.
 */
public class PartjobJoinSuccessActivity extends BaseActivity {
    private UmengShareUtil umengShareUtil;
    private String partjobid;
    private String tel;
    private String msg;
    private String url;
    private String logopath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        partjobid = getIntent().getStringExtra("partjobid");
        tel = getIntent().getStringExtra("tel");
        msg = getIntent().getStringExtra("msg");
        url = getIntent().getStringExtra("url");
        logopath = getIntent().getStringExtra("logopath");
        umengShareUtil = new UmengShareUtil(PartjobJoinSuccessActivity.this);
        setContentView(R.layout.activity_partjob_join_success);
        initHeader();
        initViews();
    }

    private void initHeader(){
        ((TextView)findViewById(R.id.tv_content)).setText("报名成功");
        findViewById(R.id.ib_back).setVisibility(View.GONE);
        ((ImageView)findViewById(R.id.iv_func)).setVisibility(View.INVISIBLE);
        ((ImageView)findViewById(R.id.iv_bird_badge)).setVisibility(View.INVISIBLE);
        TextView tv_info=(TextView)findViewById(R.id.tv_func);
        tv_info.setVisibility(View.VISIBLE);
        tv_info.setText("完成");
        tv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initViews() {

        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengShareUtil.setShareContent("兼职分享", msg, url, logopath);
                umengShareUtil.openShare();
            }
        });

        findViewById(R.id.btn_connect_merchant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + tel));
                startActivity(intent);
            }
        });
    }
}