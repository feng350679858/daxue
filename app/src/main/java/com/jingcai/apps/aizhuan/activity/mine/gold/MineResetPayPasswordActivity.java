package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.game.game14.Game14Request;
import com.jingcai.apps.aizhuan.service.business.stu.std07.Stu07Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu07.Stu07Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DES3Util;

public class MineResetPayPasswordActivity extends BaseActivity {
    private static final String TAG = "MineResetPayPassword";

    public static final String INTENT_EXTRA_NAME_STAGE = "stage";
    public static final String INTENT_EXTRA_NAME_NEW_PSW = "new_psw";
    public static final int RESET_PASSWORD_STAGE_1 = 1;  //输入旧密码
    public static final int RESET_PASSWORD_STAGE_2 = 2;  //输入新密码
    public static final int RESET_PASSWORD_STAGE_3 = 3;  //重复新密码
    private int mCurrentStage;  //当前阶段
    private String mNewPsw;  //新密码

    private StringBuilder mPswString;
    private Button mBtnNext;

    private MessageHandler messageHandler;
    private AzService azService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_reset_pay_password);
        unpackIntent();  //解封intent
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);

        initHeader();
        initView();
    }

    private void unpackIntent() {
        final Intent intent = getIntent();
        if (intent == null) {
            Log.w(TAG, "This activity required a intent which has extra name INTENT_EXTRA_NAME_STAGE");
            this.finish();
            return;
        }

        mCurrentStage = intent.getIntExtra(INTENT_EXTRA_NAME_STAGE, RESET_PASSWORD_STAGE_1);
        if (mCurrentStage == RESET_PASSWORD_STAGE_3) {
            mNewPsw = intent.getStringExtra(INTENT_EXTRA_NAME_NEW_PSW);
        }

    }

    private void initHeader() {
        ((ImageView) findViewById(R.id.ib_back)).setImageDrawable(getResources().getDrawable(R.drawable.icon_cancel2));
        TextView tvTile = (TextView) findViewById(R.id.tv_content);
        String title = "";
        switch (mCurrentStage) {
            case RESET_PASSWORD_STAGE_1:
                title = "安全验证";
                break;
            case RESET_PASSWORD_STAGE_2:
            case RESET_PASSWORD_STAGE_3:
                title = "重置支付密码";
                break;
        }
        tvTile.setText(title);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentStage == RESET_PASSWORD_STAGE_3) {
                    Intent intent = new Intent(MineResetPayPasswordActivity.this, MineResetPayPasswordActivity.class);
                    intent.putExtra(INTENT_EXTRA_NAME_STAGE, RESET_PASSWORD_STAGE_2);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    private void initView() {
        mBtnNext = (Button) findViewById(R.id.btn_mine_reset_pay_psw);
        initPswPad();

        //根据当前的场景设置显示文字
        String mainTip = "";
        String subTip = "";
        String btnText = "";
        switch (mCurrentStage){
            case RESET_PASSWORD_STAGE_1:
                mainTip = "原支付密码";
                subTip = "输入原支付密码，完成安全验证";
                btnText = "下一步";
                break;
            case RESET_PASSWORD_STAGE_2:
                mainTip = "请输入6位新支付密码";
                subTip = "输入完成后，您还需再次确认";
                mBtnNext.setVisibility(View.GONE);
                break;
            case RESET_PASSWORD_STAGE_3:
                mainTip = "请再次输入6位新支付密码";
                subTip = "设置后，该密码将成为新的支付密码，请牢记";
                btnText = "完成";
                break;
        }
        mBtnNext.setText(btnText);
        ((TextView) findViewById(R.id.tv_main_tip)).setText(mainTip);
        ((TextView) findViewById(R.id.tv_sub_tip)).setText(subTip);

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentStage == RESET_PASSWORD_STAGE_1) {
                    showProgressDialog("验证支付密码中...");
                    validatePayPassword();
                }
                else if (mCurrentStage == RESET_PASSWORD_STAGE_3) {
                    if(!mNewPsw.equals(mPswString.toString())){
                        showToast("两次密码输入不一致");
                        return;
                    }
                    showProgressDialog("修改支付密码中...");
                    modifyPayPassword();
                }
            }
        });
    }

    /**
     * 检验用户输入的支付密码是否正确
     */
    private void validatePayPassword() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Game14Request req = new Game14Request();
                Game14Request.Student student = req.new Student();

                student.setId(UserSubject.getStudentid());
                student.setPaypassword(DES3Util.encrypt(mPswString.toString()));

                req.setStudent(student);
                azService.doTrans(req, Stu07Response.class, new AzService.Callback<Stu07Response>() {
                    @Override
                    public void success(Stu07Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(3, result.getMessage());
                        } else {
                            messageHandler.postMessage(2);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }

    /**
     * 修改支付密码
     */
    private void modifyPayPassword() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
            Stu07Request req = new Stu07Request();
            Stu07Request.Student student = req.new Student();

            student.setStudentid(UserSubject.getStudentid());
            student.setPhone(UserSubject.getPhone());
            student.setPassword(DES3Util.encrypt(mPswString.toString()));

            req.setStudent(student);
            azService.doTrans(req, Stu07Response.class, new AzService.Callback<Stu07Response>() {
                @Override
                public void success(Stu07Response resp) {
                    ResponseResult result = resp.getResult();
                    if (!"0".equals(result.getCode())) {
                        messageHandler.postMessage(1, result.getMessage());
                    } else {
                        messageHandler.postMessage(0);
                    }
                }

                @Override
                public void fail(AzException e) {
                    messageHandler.postException(e);
                }
            });
            }
        });
    }

    private void initPswPad() {
        mPswString = new StringBuilder();
        View.OnClickListener padListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();

                if (id != R.id.iv_mine_back) {
                    if (!(v instanceof TextView)) {
                        return;
                    }
                    TextView tvNum = (TextView) v;
                    if (mPswString.length() < 6) {
                        mPswString.append(tvNum.getText());
                    }
                } else {
                    if (mPswString.length() <= 0) return;
                    mPswString.deleteCharAt(mPswString.length() - 1);
                }

                for (int i = 1; i <= 6; i++) {
                    try {
                        int viewId = MineResetPayPasswordActivity.this.getResources().getIdentifier("tv_mine_psw_length_" + i, "id", MineResetPayPasswordActivity.this.getPackageName());
                        MineResetPayPasswordActivity.this.findViewById(viewId).setVisibility(i <= mPswString.length() ? View.VISIBLE : View.INVISIBLE);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't find tv_psw_length_" + i + " ,Did you remove it?");
                    }
                }
                if (mPswString.length() == 6) {
                    //如果为第二幕，则输入到了六位自动跳入第三幕
                    if(mCurrentStage == RESET_PASSWORD_STAGE_2){
                        Intent intent = new Intent(MineResetPayPasswordActivity.this,MineResetPayPasswordActivity.class);
                        intent.putExtra(INTENT_EXTRA_NAME_STAGE,RESET_PASSWORD_STAGE_3);
                        intent.putExtra(INTENT_EXTRA_NAME_NEW_PSW,mPswString.toString());
                        startActivity(intent);
                        finish();
                    }else{
                        mBtnNext.setEnabled(true);
                        mBtnNext.setTextColor(getResources().getColor(R.color.important_dark));
                    }
                }else{
                    mBtnNext.setEnabled(false);
                    mBtnNext.setTextColor(getResources().getColor(R.color.assist_grey));
                }
            }
        };
        findViewById(R.id.tv_mine_num_0).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_1).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_2).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_3).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_4).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_5).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_6).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_7).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_8).setOnClickListener(padListener);
        findViewById(R.id.tv_mine_num_9).setOnClickListener(padListener);
        findViewById(R.id.iv_mine_back).setOnClickListener(padListener);
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
                    showToast("修改支付密码成功");
                    MineResetPayPasswordActivity.this.finish();
                    break;
                }
                case 1: {
                    showToast("修改支付密码失败");
                    Log.d(TAG, "修改支付密码失败：" + msg.obj);
                    break;
                }
                case 2: {
                    Intent intent = new Intent(MineResetPayPasswordActivity.this,MineResetPayPasswordActivity.class);
                    intent.putExtra(INTENT_EXTRA_NAME_STAGE,RESET_PASSWORD_STAGE_2);
                    intent.putExtra(INTENT_EXTRA_NAME_NEW_PSW,mPswString.toString());
                    startActivity(intent);
                    MineResetPayPasswordActivity.this.finish();
                    break;
                }
                case 3: {
                    showToast("支付密码错误");
                    Log.d(TAG, "支付密码错误：" + msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}
