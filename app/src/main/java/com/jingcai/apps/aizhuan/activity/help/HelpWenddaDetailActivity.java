package com.jingcai.apps.aizhuan.activity.help;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWenddaDetailActivity extends BaseActivity {

    private Button btn_wenda_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help_wenda_deploy);
//        messageHandler = new MessageHandler(this);

        initHeader();
        initView();
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("发布求问");

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {

//        btn_wenda_help = (Button) findViewById(R.id.btn_wenda_help);
//
//        btn_wenda_help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
    }
}
