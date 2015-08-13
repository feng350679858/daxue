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
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lejing on 15/7/22.
 */
public class HelpEvaluateActivity extends BaseActivity {
    private int[] starIdArr = {R.id.ib_start_0,R.id.ib_start_1,R.id.ib_start_2,R.id.ib_start_3,R.id.ib_start_4};
    private CheckBox[] starViewArr = new CheckBox[starIdArr.length];
    private MessageHandler messageHandler;
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private EditText et_evaluate_content;
    private Button btn_evaluate;
    private ImageView iv_close;
    private CircleImageView civ_head_logo;
    private TextView tv_stu_name;
    private TextView tv_stu_school;
    private TextView tv_stu_college;
    private TextView tv_stu_score;
    private TextView tv_help_content;
    private boolean forceflag;
    private String targetid;
    private String targetimgurl;
    private String targetname;
    private String targetschool;
    private String targetcollege;
    private String content;
    private String targettype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.help_evaluate);

        forceflag = getIntent().getBooleanExtra("forceflag", true);
        content = getIntent().getStringExtra("content");
        targetid = getIntent().getStringExtra("targetid");
        targettype = getIntent().getStringExtra("targettype");//1：学生 2：联系人 3：商家
        targetimgurl = getIntent().getStringExtra("targetimgurl");
        targetname = getIntent().getStringExtra("targetname");
        targetschool = getIntent().getStringExtra("targetschool");
        targetcollege = getIntent().getStringExtra("targetcollege");

        initView();

        setValues();

    }

    private void setValues(){
        iv_close.setVisibility(forceflag ? View.GONE : View.VISIBLE);
        if(!forceflag){
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        tv_help_content.setText(content);
        bitmapUtil.getImage(civ_head_logo, targetimgurl, true, R.drawable.default_head_img);
        tv_stu_name.setText(targetname);
        tv_stu_school.setText(targetschool);
        tv_stu_college.setText(targetcollege);

        getScore();
    }

    /**
     * 获取信用分
     */
    private void getScore() {

    }

    private void initView() {
        iv_close = (ImageView) findViewById(R.id.iv_close);
        civ_head_logo = (CircleImageView) findViewById(R.id.civ_head_logo);
        tv_stu_name = (TextView) findViewById(R.id.tv_stu_name);
        tv_stu_school = (TextView) findViewById(R.id.tv_stu_school);
        tv_stu_college = (TextView) findViewById(R.id.tv_stu_college);
        tv_stu_score = (TextView) findViewById(R.id.tv_stu_score);
        tv_help_content = (TextView) findViewById(R.id.tv_help_content);
        et_evaluate_content = (EditText) findViewById(R.id.et_evaluate_content);

        btn_evaluate = (Button)findViewById(R.id.btn_evaluate);
        btn_evaluate.setEnabled(false);
        btn_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for(int i=0;i<starIdArr.length;i++){
            CheckBox btn = (CheckBox) findViewById(starIdArr[i]);
            starViewArr[i] = btn;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = true;
                    for (int i = 0; i < starIdArr.length; i++) {
                        if (v.getId() == starIdArr[i]) {
                            flag = false;
                        } else {
                            starViewArr[i].setChecked(flag);
                        }
                    }
                    btn_evaluate.setEnabled(starViewArr[0].isChecked());
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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
                    finish();
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
            if(forceflag) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
