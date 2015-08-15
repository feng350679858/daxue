package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.message.MessageConversationActivity;
import com.jingcai.apps.aizhuan.entity.ConversationBean;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lejing on 15/7/22.
 */
public class HelpJishiDeploySuccessActivity extends BaseActivity {
    private String helpid, studentid;
    private MessageHandler messageHandler;
    private Stu02Response.Stu02Body.Student student;
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private TextView tv_stu_name;
    private TextView tv_stu_school;
    private TextView tv_stu_college;
    private TextView tv_stu_score;
    private CircleImageView civ_head_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpid = getIntent().getStringExtra("helpid");
        studentid = getIntent().getStringExtra("studentid");
        if (StringUtil.isEmpty(helpid) || StringUtil.isEmpty(studentid)) {
            finish();
        } else {
            messageHandler = new MessageHandler(this);

            setContentView(R.layout.help_jishi_deploy_success);

            initView();

            initData();
        }
    }

    private void initData() {
        if (!actionLock.tryLock()) return;
        getStudentInfo();
        getStudentScore();
    }

    private void initView() {
        TextView tv_finish = (TextView) findViewById(R.id.tv_finish);
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        civ_head_logo = (CircleImageView)findViewById(R.id.civ_head_logo);
        tv_stu_name = (TextView) findViewById(R.id.tv_stu_name);
        tv_stu_school = (TextView) findViewById(R.id.tv_stu_school);
        tv_stu_college = (TextView) findViewById(R.id.tv_stu_college);
        tv_stu_score = (TextView) findViewById(R.id.tv_stu_score);

        findViewById(R.id.btn_im).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == student) return;
                Intent intent = new Intent(HelpJishiDeploySuccessActivity.this, MessageConversationActivity.class);
                ConversationBean cb = new ConversationBean(student.getStudentid(), student.getLogopath(), student.getName());
                intent.putExtra("conversationBean", cb);
                startActivity(intent);
//                finish();
            }
        });
        findViewById(R.id.btn_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == student) {;
                    showToast("无法获取电话号码", 0);
                    return;
                }
                Uri uri = Uri.parse("smsto:" + student.getPhone());
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                //sendIntent.putExtra("sms_body", "-------");
                startActivity(sendIntent);
            }
        });
        findViewById(R.id.btn_tel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == student) {
                    showToast("无法获取电话号码", 0);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + student.getPhone()));
                startActivity(intent);
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
                case 11: {
                    student = (Stu02Response.Stu02Body.Student) msg.obj;
                    bitmapUtil.getImage(civ_head_logo, student.getLogopath(), true, R.drawable.default_head_img);
                    tv_stu_name.setText(student.getName());
                    tv_stu_school.setText(student.getSchoolname());
                    tv_stu_college.setText(student.getCollegename());
                    break;
                }
                case 21: {
                    Stu11Response.Body.Student student = (Stu11Response.Body.Student) msg.obj;
                    tv_stu_score.setText(student.getScore());
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void getStudentInfo() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(studentid);
                req.setStudent(stu);
                new AzService().doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        final Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if ("0".equals(result.getCode())) {
                            messageHandler.postMessage(11, student);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                    }
                });
            }
        });
    }

    private void getStudentScore() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu11Request req = new Stu11Request();
                Stu11Request.Student student = req.new Student();
                student.setStudentid(studentid);
                req.setStudent(student);
                new AzService().doTrans(req, Stu11Response.class, new AzService.Callback<Stu11Response>() {
                    @Override
                    public void success(Stu11Response resp) {
                        ResponseResult result = resp.getResult();
                        if ("0".equals(result.getCode())) {
                            messageHandler.postMessage(21, resp.getBody().getStudent());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                    }
                });
            }
        });
    }
}
