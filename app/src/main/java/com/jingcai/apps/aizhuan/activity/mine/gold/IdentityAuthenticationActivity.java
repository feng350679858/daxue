package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by Json Ding on 2015/8/5.
 */
public class IdentityAuthenticationActivity extends BaseActivity{

    private EditText mEtName;
    private EditText mEtIdNo;
    private TextView mTvIdFront;
    private TextView mTvIdBehind;
    private Button mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_identity_authentication);

        initHeader();
        initViews();
    }

    private void initViews() {
        mEtName = (EditText) findViewById(R.id.et_authentication_name);
        mEtIdNo = (EditText) findViewById(R.id.et_authentication_idno);
        mTvIdFront = (TextView) findViewById(R.id.tv_identity_card_front);
        mTvIdBehind = (TextView) findViewById(R.id.tv_identity_card_behind);
        mBtnSubmit = (Button) findViewById(R.id.btn_authentication_submit);

        mEtIdNo.setTransformationMethod(new StringUtil.AllCapTransformationMethod());  //小写自动转大写

        initEvents();
    }

    private void initEvents() {

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
