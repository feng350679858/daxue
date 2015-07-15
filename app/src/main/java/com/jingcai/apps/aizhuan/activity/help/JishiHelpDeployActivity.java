package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.util.PopupListDialog;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lejing on 15/7/14.
 */
public class JishiHelpDeployActivity extends BaseActivity {
    private MessageHandler messageHandler;
    private Button btn_jishi_help;
    private EditText et_content, et_secret, et_pay_money;
    private TextView tv_gender, tv_group, tv_friend, tv_end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help_jishi_deploy);
        messageHandler = new MessageHandler(this);

        initView();
    }

    private void initView(){
        et_content = (EditText) findViewById(R.id.et_content);
        et_secret = (EditText) findViewById(R.id.et_secret);
        tv_gender = (TextView) findViewById(R.id.et_gender);
        tv_group = (TextView) findViewById(R.id.et_group);
        tv_friend = (TextView) findViewById(R.id.et_friend);
        et_pay_money = (EditText) findViewById(R.id.et_pay_money);
        tv_end_time = (TextView) findViewById(R.id.et_end_time);

        btn_jishi_help = (Button)findViewById(R.id.btn_jishi_help);

        tv_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new LinkedHashMap<String, String>();
                map.put("10", "10分钟");
                map.put("30", "30分钟");
                map.put("60", "1小时");
                map.put("180", "3小时");
                map.put("360", "6小时");
                map.put("720", "12小时");
                map.put("1440", "24小时");
                map.put("-1", "自定义");
                PopupListDialog popupDialog = new PopupListDialog(JishiHelpDeployActivity.this)
                        .setData(map)
                        .setCallback(new PopupListDialog.Callback() {
                            @Override
                            public void select(String key, String val) {
                                showToast(val);
                            }
                        })
                        .build();
                popupDialog.show();
            }
        });

        btn_jishi_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionLock.tryLock()){
                    messageHandler.postMessage(1);
                }
            }
        });
    }

    class MessageHandler extends BaseHandler{
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    showToast("发布即时帮助成功！");
                    finish();
                    actionLock.unlock();
                    break;
                }
                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }
}
