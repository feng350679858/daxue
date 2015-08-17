package com.jingcai.apps.aizhuan.activity.help;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob20.Partjob20Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/7/22.
 */
public class HelpJishiDeployFailActivity extends BaseActivity {
    private String helpid;
    private MessageHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpid = getIntent().getStringExtra("helpid");
        if (StringUtil.isEmpty(helpid)) {
            finish();
        } else {
            messageHandler = new MessageHandler(this);

            setContentView(R.layout.help_jishi_deploy_fail);

            initView();
        }
    }

    private void initView() {
        TextView tv_finish = (TextView) findViewById(R.id.tv_finish);
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpJishiDeployFailActivity.this, HelpJishiDetailActivity.class);
                intent.putExtra("helpid", helpid);
                intent.putExtra("type", "1");
                startActivity(intent);
                finish();
            }
        });
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
}
