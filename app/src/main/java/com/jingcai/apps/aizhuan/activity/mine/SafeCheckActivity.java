package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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
import com.jingcai.apps.aizhuan.activity.mine.gold.IdentityAuthenticationActivity;
import com.jingcai.apps.aizhuan.activity.mine.gold.MineResetPayPasswordActivity;
import com.jingcai.apps.aizhuan.activity.util.PopConfirmWin;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.game.game13.Game13Request;
import com.jingcai.apps.aizhuan.service.business.game.game13.Game13Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;

public class SafeCheckActivity extends BaseActivity {
    private final String TAG = "SafeCheckActivity";
    private AzService azService;
    private MessageHandler messageHandler;

    private TextView mTvName;
    private EditText mEtIdno;
    private Button next;

    private PopConfirmWin popConfirmWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_resetpaypsw2);
        messageHandler = new MessageHandler(this);


        initHeader();
        initViews();

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkIdentity();
    }

    private void checkIdentity() {
        String idnoauthflag = UserSubject.getIdnoauthflag();
        String tip = null;
        switch (idnoauthflag) {
            case "0":
                tip = getString(R.string.gold_modify_pay_psw_validate_identity_not_pass);
                if (null == popConfirmWin) {
                    popConfirmWin = new PopConfirmWin(SafeCheckActivity.this);
                    popConfirmWin.setTitle("身份验证").setContent(tip).setCancelAction("下次再说", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popConfirmWin.dismiss();
                        }
                    }).setOkAction("去吧去吧", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SafeCheckActivity.this, IdentityAuthenticationActivity.class);
                            startActivity(intent);
                            popConfirmWin.dismiss();
                            finish();
                        }
                    });
                }
                popConfirmWin.show();
                break;
            case "2":
                tip = getString(R.string.gold_withdraw_validate_identity_wait);
                popConfirmWin = new PopConfirmWin(SafeCheckActivity.this);
                popConfirmWin.setTitle("身份验证")
                        .setContent(tip)
                        .setCancelAction(null)
                        .setOkAction("我知道了", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });

                popConfirmWin.show();
                break;
        }
    }

    private void initHeader() {
        final ImageView ivBack = (ImageView) findViewById(R.id.ib_back);
        ivBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_cancel2));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_content)).setText("安全验证");
    }

    private void initViews() {
        mTvName = (TextView) findViewById(R.id.name);
        mTvName.setText(UserSubject.getName());
        mEtIdno = (EditText) findViewById(R.id.id_card_number_input);
        mEtIdno.addTextChangedListener(new TextWatcher() {
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
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void resetPayPsw() {
        Intent intent = new Intent(this, MineResetPayPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    private void check() {
        showProgressDialog("安全验证中...");
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(SafeCheckActivity.this);
                Game13Request req = new Game13Request();
                Game13Request.Student student = req.new Student();
                student.setId(UserSubject.getStudentid());
                student.setIdno(mEtIdno.getText().toString());
                req.setStudent(student);
                azService.doTrans(req, Game13Response.class, new AzService.Callback<Game13Response>() {
                    @Override
                    public void success(Game13Response response) {
                        ResponseResult result = response.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(0, result.getMessage());
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
                    showToast("证件号有误");
                    Log.i(TAG, "安全验证失败:" + msg.obj);
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
