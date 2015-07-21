package com.jingcai.apps.aizhuan.activity.index;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/5/7.
 */
public class IndexBannerDetailActivity extends BaseActivity {
    private String title, url;
    private WebView wv_partjob_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");

        setContentView(R.layout.partjob_detail);
        if (StringUtil.isEmpty(title) || StringUtil.isEmpty(url)) {
            finish();
            return;
        }
        initHeader();
        initViews();
    }

    private void initHeader(){
        ((TextView) findViewById(R.id.tv_content)).setText(title);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_func).setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_bird_badge).setVisibility(View.INVISIBLE);
    }
    private void initViews() {

        wv_partjob_detail = (WebView) findViewById(R.id.wv_partjob_detail);
        WebSettings settings = wv_partjob_detail.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv_partjob_detail.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wv_partjob_detail.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wv_partjob_detail.canGoBack()) {
                wv_partjob_detail.goBack();//返回上一页面
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
