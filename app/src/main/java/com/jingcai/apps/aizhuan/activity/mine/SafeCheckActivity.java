package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.mine.gold.MineResetPayPasswordActivity;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.game.game13.Game13Request;
import com.jingcai.apps.aizhuan.service.business.game.game13.Game13Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;

public class SafeCheckActivity extends BaseActivity {
    private final String TAG="SafeCheckActivity";
    private AzExecutor azExecutor;
    private AzService azService;
    private MessageHandler messageHandler;

    private TextView name;
    private EditText id_crad_number_input;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_resetpaypsw2);

        messageHandler = new MessageHandler(this);
        initHeader();
        initViews();
    }

    private void initHeader() {
        ((ImageView)findViewById(R.id.ib_back)).setImageDrawable(getResources().getDrawable(R.drawable.icon_cancel2));
        ((ImageView)findViewById(R.id.ib_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_content)).setText("安全验证");
        findViewById(R.id.iv_bird_badge).setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_func).setVisibility(View.INVISIBLE);
    }
    private void initViews(){
        name=(TextView)findViewById(R.id.name);
        name.setText(UserSubject.getName());
        id_crad_number_input=(EditText)findViewById(R.id.id_card_number_input);
        id_crad_number_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 18)
                    next.setEnabled(true);
                else
                    next.setEnabled(false);
            }
        });
        next=(Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }
    private void resetPayPsw(){
        Intent intent=new Intent(this,MineResetPayPasswordActivity.class);
        startActivity(intent);
        finish();
    }
    private void check(){
        showProgressDialog("安全验证中...");
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(SafeCheckActivity.this);
                Game13Request req=new Game13Request();
                Game13Request.Student student=req.new Student();
                student.setId(UserSubject.getStudentid());
                student.setIdno(id_crad_number_input.getText().toString());
                req.setStudent(student);
                azService.doTrans(req, Game13Response.class, new AzService.Callback<Game13Response>() {
                    @Override
                    public void success(Game13Response response) {
                        ResponseResult result = response.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(0,result.getMessage());
                        } else {
                            messageHandler.postMessage(1);
                        }
                    }
                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        }));
    }
    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    showToast("安全验证失败");
                    Log.i(TAG,"安全验证失败:" + msg.obj);
                    break;
                }
                case 1: {
                    showToast("安全验证成功");
                    resetPayPsw();
                    break;
                }

                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }
}
