package com.jingcai.apps.aizhuan.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;

/**
 * Created by Administrator on 2015/7/22.
 */
public class InproveProfileSimpleItemActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_profile_improve_simple_item);

        initViews();
    }

    private void initViews() {
        final Intent intent = getIntent();

        final TextView confirmView = (TextView) findViewById(R.id.tv_func);
        final EditText fieldText = (EditText)findViewById(R.id.tv_mine_profile_improve_item_content);

        ((TextView)findViewById(R.id.tv_func)).setText(intent.getStringExtra("title"));
        ((TextView)findViewById(R.id.tv_func)).setVisibility(View.VISIBLE);
        confirmView.setText("保存");
        fieldText.setText(intent.getStringExtra("val"));
        fieldText.setHint(intent.getStringExtra("hint"));

        String type = intent.getStringExtra("inputType");
        if("email".equals(type)){
            fieldText.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        }else if("qq".equals(type)){
            fieldText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        //点击完成返回数据
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                String type = intent.getStringExtra("inputType");
                String data = fieldText.getText().toString();
                if("email".equals(type)){
                    if(!data.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")){
                        showToast("请输入正确的邮箱地址");
                        return;
                    }
                }else if("qq".equals(type)){
                    if(data.length()<5 || data.length() >11){
                        showToast("请输入正确的QQ号");
                        return;
                    }
                }
                backIntent.putExtra("inputData",data);
                setResult(Activity.RESULT_OK,backIntent);
                finish();
            }
        });

        //点击返回关闭activity
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //输入法点击完成
        fieldText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    confirmView.performClick();
                }
                return false;
            }
        });
    }

}

