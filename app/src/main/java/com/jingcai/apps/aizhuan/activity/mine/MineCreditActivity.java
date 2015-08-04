package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu03.Stu03Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by Administrator on 2015/7/20.
 */
public class MineCreditActivity extends BaseActivity {
    private final String TAG="MineCreditActivity";
    private MessageHandler messageHandler;
    private AzService azService;
    private TextView mTextName;
    private TextView mTextSchoolname;
    private TextView mTextCredit;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_credit);

        messageHandler = new MessageHandler(MineCreditActivity.this);
        azService = new AzService(MineCreditActivity.this);


        initHeader();
        initViews();  //初始化Views
        initData();  //填充数据
        initCredit();

    }

    private void initHeader()
    {
        ((TextView)findViewById(R.id.tv_content)).setText("我的信用");

        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initViews() {
        mTextName = (TextView) findViewById(R.id.tv_mine_credit_name);
        mTextSchoolname = (TextView) findViewById(R.id.tv_mine_credit_school);
      //  mTextCollegename = (TextView) findViewById(R.id.tv_profile_collegename);
        mTextCredit = (TextView) findViewById(R.id.tv_mine_credit_score);
    }


    private void initData() {
        showProgressDialog("数据加载中..");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(0, student);
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

    public void initCredit(){
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu11Request req = new Stu11Request();
                final Stu11Request.Student stu = req.new Student();
                stu.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                req.setStudent(stu);
                azService.doTrans(req, Stu11Response.class, new AzService.Callback<Stu11Response>() {
                    @Override
                    public void success(Stu11Response response) {
                        ResponseResult result = response.getResult();
                        Stu11Response.Stu11Body stu11Body = response.getBody();
                        Stu11Response.Stu11Body.Student student = stu11Body.getStudent();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(2, student);
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

    private void fillStudentInView(Stu02Response.Stu02Body.Student student) {
        mTextName.setText(student.getName());
        mTextSchoolname.setText(student.getSchoolname() + " " + student.getCollegename());
    //   mTextCredit.setText(student2.getScore());

    }

    private void fillCreditInView(Stu11Response.Stu11Body.Student student) {
       //mTextName.setText(student.getName());
      //  mTextSchoolname.setText(student.getSchoolname()+" "+student.getCollegename());
          mTextCredit.setText(student.getScore());

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
                    Stu02Response.Stu02Body.Student student1 = (Stu02Response.Stu02Body.Student) msg.obj;
                    //        Stu11Response.Stu11Body.Student student2 = (Stu11Response.Stu11Body.Student) msg.obj;
                    fillStudentInView(student1);
                    break;
                }
                case 1: {
                    showToast("获取失败");
                    Log.i(TAG,"获取失败:" + msg.obj);
                    break;
                }
                case 2: {
                   // Stu02Response.Stu02Body.Student student1 = (Stu02Response.Stu02Body.Student) msg.obj;
                            Stu11Response.Stu11Body.Student student2 = (Stu11Response.Stu11Body.Student) msg.obj;
                    fillCreditInView(student2);
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }
}
