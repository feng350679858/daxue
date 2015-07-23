package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by lejing on 15/7/22.
 */
public class HelpEvaluateActivity extends BaseActivity {
    private int[] starIdArr = {R.id.ib_start_0,R.id.ib_start_1,R.id.ib_start_2,R.id.ib_start_3,R.id.ib_start_4};
    private CheckBox[] starViewArr = new CheckBox[starIdArr.length];
    private MessageHandler messageHandler;
    private EditText et_evaluate_content;
    private Button btn_evaluate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.help_evaluate);

        initView();
    }

    private void initView() {
        et_evaluate_content = (EditText) findViewById(R.id.et_evaluate_content);
        et_evaluate_content.setText(String.valueOf(System.currentTimeMillis()));
        Log.d("==", "----------" + this.getTaskId() + "-----" + this);

        findViewById(R.id.btn_evaluate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_evaluate = (Button)findViewById(R.id.btn_evaluate);

        for(int i=0;i<starIdArr.length;i++){
            CheckBox btn = (CheckBox) findViewById(starIdArr[i]);
            starViewArr[i] = btn;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = true;
                    for (int i = 0; i < starIdArr.length; i++) {
                        if (v.getId() == starIdArr[i]) {
                            //starViewArr[i].setChecked(!starViewArr[i].isChecked());
                            flag = false;
                        } else {
                            starViewArr[i].setChecked(flag);
                        }
                    }
                    Log.d("==", "---------"+starViewArr[0].isChecked());
                    btn_evaluate.setEnabled(starViewArr[0].isChecked());
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        et_evaluate_content.setText(String.valueOf(System.currentTimeMillis()));
        Log.d("==", "----------" + this.getTaskId() + "-----" + this);
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
