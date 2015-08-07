package com.jingcai.apps.aizhuan.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
public class ImproveProfileSimpleItemActivity extends BaseActivity {
    private static final String TAG = "ProfileSimpleItem";
    public static final String RETURN_INTENT_NAME_INPUT_DATA = "inputData";

    public static final String  INTENT_NAME_TITLE = "title";
    public static final String  INTENT_NAME_HINT = "hint";
    public static final String  INTENT_NAME_INPUT_TYPE = "inputType";
    public static final String  INTENT_NAME_VALUE = "val";

    private String mIntentTitle;  //标题
    private String mIntentHint;   //Hint
    private String mIntentInputType;  //输入类型
    private String mIntentValue;  //值

    private TextView mTvFunc;
    private EditText mEtField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_profile_improve_simple_item);
        unpackIntent();
        initHeader();
        initViews();
    }

    private void initHeader() {
        mTvFunc = (TextView) findViewById(R.id.tv_func);
        mTvFunc.setText("保存");
        mTvFunc.setVisibility(View.VISIBLE);
        //点击完成返回数据
        mTvFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent();
                String data = mEtField.getText().toString();
                if ("email".equals(mIntentInputType)) {
                    if (!data.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
                        showToast("请输入正确的邮箱地址");
                        return;
                    }
                } else if ("qq".equals(mIntentInputType)) {
                    if (data.length() < 5 || data.length() > 11) {
                        showToast("请输入正确的QQ号");
                        return;
                    }
                }
                backIntent.putExtra(RETURN_INTENT_NAME_INPUT_DATA, data);
                setResult(Activity.RESULT_OK, backIntent);
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_content)).setText(mIntentTitle);
        //点击返回关闭activity
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void unpackIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            Log.w(TAG, "This activity required a intent");
            finish();
            return;
        }

        mIntentTitle = intent.getStringExtra(INTENT_NAME_TITLE);
        mIntentHint = intent.getStringExtra(INTENT_NAME_HINT);
        mIntentInputType = intent.getStringExtra(INTENT_NAME_INPUT_TYPE);
        mIntentValue = intent.getStringExtra(INTENT_NAME_VALUE);
    }

    private void initViews() {

        mEtField = (EditText)findViewById(R.id.tv_mine_profile_improve_item_content);
        mEtField.setText(mIntentValue);
        mEtField.setHint(mIntentHint);
        if("email".equals(mIntentInputType)){
            mEtField.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        }else if("qq".equals(mIntentInputType)){
            mEtField.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        //输入法点击完成
        mEtField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    mTvFunc.performClick();
                }
                return false;
            }
        });
        mEtField.setSelection(mEtField.length());
    }

}

