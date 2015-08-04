package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.std07.Stu07Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu07.Stu07Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by Administrator on 2015/7/20.
 */
public class MineResetpaypswActivity extends BaseActivity {
    private static final String TAG = "MineResetpaypswActivity";

    private StringBuilder mPayPsw;
    private View mPopupPayPswContentView;
    PopupWin mPopWin;
    private Button button;

    private MessageHandler messageHandler;
    private AzService azService;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_reset_pay_psw);
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);

        initHeader();

        initView();
    }

    private void initHeader()
    {
        button = (Button)findViewById(R.id.btn_mine_reset_pay_psw);
        ((TextView)findViewById(R.id.tv_content)).setText("重置支付密码");
        ( (ImageView)findViewById(R.id.ib_back)).setImageDrawable(getResources().getDrawable(R.drawable.icon_cancel2));
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setVisibility(View.GONE);
    }

    private void initView(){

        //mPopupPayPswContentView = View.inflate(MineResetpaypswActivity.this, R.layout.mine_pop_reset_pay_psw_input, null);
        // View parentView =MineResetpaypswActivity.this.getWindow().getDecorView();
        mPayPsw = new StringBuilder();

       // mPopWin = PopupWin.Builder.create(MineResetpaypswActivity.this)
       //         .setParentView(parentView)
       //         .setContentView(mPopupPayPswContentView)
       //         .build();

        View.OnClickListener padListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();

                if (id != R.id.iv_mine_back) {

                    if (!(v instanceof TextView)) {
                        return;
                    }
                    TextView tvNum = (TextView) v;
                    if (mPayPsw.length() <= 6) {
                        mPayPsw.append(tvNum.getText());
                    }
                } else {

                    if (mPayPsw.length() <= 0) return;
                    mPayPsw.deleteCharAt(mPayPsw.length() - 1);
                }

                for (int i = 1; i <= 6; i++) {
                    //showToast("123456");
                    try {
                        int viewId = MineResetpaypswActivity.this.getResources().getIdentifier("tv_mine_psw_length_" + i, "id",MineResetpaypswActivity.this.getPackageName());
                        MineResetpaypswActivity.this.findViewById(viewId).setVisibility(i <= mPayPsw.length() ? View.VISIBLE : View.INVISIBLE);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't find tv_psw_length_" + i + " ,Did you remove it?");
                    }
                }
                if(mPayPsw.length() == 6){
                    showToast("密码输入完毕");
                  //  mPopWin.dismiss();
                    button.setVisibility(View.VISIBLE);
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.tv_finish_reset_pay_psw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  View toastRoot = getLayoutInflater().inflate(R.layout.mine_reset_pay_psw_toast, null);
               // TextView message = (TextView) toastRoot.findViewById(R.id.tv_reset_pay_psw_toast);
               // message.setText("My Toast");

                Toast toastStart = new Toast(MineResetpaypswActivity.this);
                toastStart.setGravity(Gravity.TOP, 0, 10);
                toastStart.setDuration(Toast.LENGTH_LONG);
                toastStart.setView(toastRoot);
                toastStart.show();*/
                new AzExecutor().execute(new Runnable() {
                    @Override
                    public void run() {

                        Stu07Request req = new Stu07Request();
                        Stu07Request.Student student = req.new Student();

                        student.setStudentid(UserSubject.getStudentid());
                        student.setPhone(UserSubject.getPhone());
                        student.setPassword(DES3Util.encrypt(mPayPsw.toString()));

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
        });

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
                    MineResetpaypswActivity.this.finish();
                    View toastRoot = getLayoutInflater().inflate(R.layout.mine_reset_pay_psw_toast, null);
                    // TextView message = (TextView) toastRoot.findViewById(R.id.tv_reset_pay_psw_toast);
                    // message.setText("My Toast");

                    Toast toastStart = new Toast(MineResetpaypswActivity.this);
                    toastStart.setGravity(Gravity.TOP, 0, 10);
                    toastStart.setDuration(Toast.LENGTH_LONG);
                    toastStart.setView(toastRoot);
                    toastStart.show();
                    break;
                }
                case 1: {
                    showToast("修改支付密码失败");
                    Log.i(TAG,"修改支付密码失败：" + msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}
