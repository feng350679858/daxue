package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu03.Stu03Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.view.ClearableEditText;

public class ProfileImproveActivity extends BaseActivity {
    private TextView name;
    private ClearableEditText name_input;
    private TextView gender, gender_input;
    private TextView location, location_input;
    private TextView spreading_code;
    private ClearableEditText spreading_code_input;
    private TextView warning;
    private Button next;
    private PopupWin genderWin,areaWin;

    private AzService azService;
    private MessageHandler messageHandler;

    private Stu03Request.Student student03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_improve);
        initHeader();
        initView();
        initDate();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("完善资料");
        findViewById(R.id.iv_bird_badge).setVisibility(View.GONE);
        findViewById(R.id.iv_func).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回主界面
                finish();
            }
        });
    }

    private void initView() {
        name = (TextView) findViewById(R.id.profile_improve_name);
        name_input = (ClearableEditText) findViewById(R.id.profile_improve_name_input);
        gender = (TextView) findViewById(R.id.profile_improve_gender);
        gender_input = (TextView) findViewById(R.id.profile_improve_gender_input);
        gender_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == genderWin) {
                    View parentView = ProfileImproveActivity.this.getWindow().getDecorView();
                    View contentView = LayoutInflater.from(ProfileImproveActivity.this).inflate(R.layout.gender_popupwin, null);

                    genderWin = PopupWin.Builder.create(ProfileImproveActivity.this)
                            .setParentView(parentView)
                            .setContentView(contentView)
                            .build();
                    contentView.findViewById(R.id.gender_popupwin_male).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gender_input.setText("男");
                            genderWin.dismiss();
                        }
                    });
                    contentView.findViewById(R.id.gender_popupwin_female).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gender_input.setText("女");
                            genderWin.dismiss();
                        }
                    });
                    contentView.findViewById(R.id.gender_popupwin_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            genderWin.dismiss();
                        }
                    });
                }
                genderWin.show();
            }
        });
        location = (TextView) findViewById(R.id.profile_improve_location);
        location_input = (TextView) findViewById(R.id.profile_improve_location_input);
        location_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        spreading_code = (TextView) findViewById(R.id.profile_improve_spreading_code);
        spreading_code_input = (ClearableEditText) findViewById(R.id.profile_improve_spreading_code_input);
        warning = (TextView) findViewById(R.id.profile_improve_warning);
        next = (Button) findViewById(R.id.profile_improve_next);
        //在TextVew中加入图片
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.msg_state_fail_resend);
        ImageSpan imgSpan = new ImageSpan(this, b);
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        warning.setText(spanString);
        warning.append(getResources().getString(R.string.profile_improve_warning));
    }

    private void initDate() {
        showProgressDialog("数据加载中...");
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(ProfileImproveActivity.this);
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(UserSubject.getStudentid());
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(0);
                        } else {
                            messageHandler.postMessage(1, student);
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
                    showToast("获取失败:" + msg.obj);
                    break;
                }
                case 1: {
                    Stu02Response.Stu02Body.Student student = (Stu02Response.Stu02Body.Student) msg.obj;
                    fillStudentInView(student);
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void fillStudentInView(Stu02Response.Stu02Body.Student student) {
        name.setVisibility(View.VISIBLE);
        if (null != student.getName() && !"".equals(student.getName())) {
            name_input.setText(student.getName());
            name_input.setEnabled(false);
        }
        gender.setVisibility(View.VISIBLE);
        if (null != student.getGender() && !"".equals((student.getGender()))) {
            if ("0".equals(student.getGender()))
                gender_input.setText("男");
            else
                gender_input.setText("女");
            gender_input.setEnabled(false);
        }
        location.setVisibility(View.VISIBLE);
        if(null!=student.getAreaname()&&!"".equals(student.getAreaname())){
            location_input.setText(student.getAreaname());
            location_input.setEnabled(false);
        }
        spreading_code.setVisibility(View.VISIBLE);
        if(null!=student.getPromotioncode()&&!"".equals(student.getPromotioncode())){
            spreading_code_input.setText(student.getPromotioncode());
            spreading_code_input.setEnabled(false);
        }
    }
}
