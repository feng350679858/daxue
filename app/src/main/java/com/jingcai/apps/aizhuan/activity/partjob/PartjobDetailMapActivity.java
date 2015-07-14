package com.jingcai.apps.aizhuan.activity.partjob;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/5/7.
 */
public class PartjobDetailMapActivity extends BaseActivity {
    public static final String PARTJOB_DETAIL_URL = GlobalConstant.h5Url + "/partjob/map";
    private String gisx, gisy;
    private WebView wv_partjob_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gisx = getIntent().getStringExtra("gisx");
        gisy = getIntent().getStringExtra("gisy");
        if(StringUtil.isEmpty(gisx) || StringUtil.isEmpty(gisy)){
            finish();
            return;
        }
        setContentView(R.layout.partjob_detail);
        initHeader();
        initViews();
    }

    private void initHeader(){
        ((TextView)findViewById(R.id.tv_content)).setText("工作地点");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_bird_badge).setVisibility(View.GONE);
        findViewById(R.id.iv_func).setVisibility(View.GONE);
    }
    private String getUrl() {
        String url = PARTJOB_DETAIL_URL + "?gisx=" + gisx + "&gisy=" + gisy;
        if(StringUtil.isNotEmpty(GlobalConstant.gis.getGisx()) && StringUtil.isNotEmpty(GlobalConstant.gis.getGisy())){
            url += "&currgisx=" + GlobalConstant.gis.getGisx() + "&currgisy=" + GlobalConstant.gis.getGisy() ;
        }
        Log.d("==url:", url);
        return url;
    }

    private void initViews() {


        wv_partjob_detail = (WebView) findViewById(R.id.wv_partjob_detail);
        WebSettings settings = wv_partjob_detail.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv_partjob_detail.loadUrl(getUrl());
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (wv_partjob_detail.canGoBack()) {
//                wv_partjob_detail.goBack();//返回上一页面
//                return true;
//            } else {
//                finish();
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
