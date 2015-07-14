package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexReleaseFragment;

/**
 * Created by lejing on 15/7/14.
 */
public class JishiHelpDeployActivity extends BaseActivity {
    private MessageHandler messageHandler;
    private Button btn_jishi_help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help_jishi_deploy);
        messageHandler = new MessageHandler(this);

        initView();
    }

    private void initView(){
        btn_jishi_help = (Button)findViewById(R.id.btn_jishi_help);

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
