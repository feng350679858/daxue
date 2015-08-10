package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2015/7/20.
 */
public class MineCreditActivity extends BaseActivity {
    private final String TAG = "MineCreditActivity";
    private MessageHandler messageHandler;
    private AzService azService;
    private TextView mTvName;
    private CircleImageView mIvLogo;
    private TextView mTvSchoolName;
    private TextView mTvCredit;
    private TextView mTvCollegeName;
    private TextView mTvMore;
    private ListView mListRemark;

    private BitmapUtil mBitmapUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_credit);

        messageHandler = new MessageHandler(MineCreditActivity.this);
        azService = new AzService(MineCreditActivity.this);
        mBitmapUtil = new BitmapUtil(this);
        initHeader();
        initViews();  //初始化Views
        initData();

    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("我的信用");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        mTvName = (TextView) findViewById(R.id.tv_mine_credit_name);
        mTvSchoolName = (TextView) findViewById(R.id.tv_mine_credit_school);
        mTvCollegeName = (TextView) findViewById(R.id.tv_mine_credit_college);
        mTvCredit = (TextView) findViewById(R.id.tv_mine_credit_score);
        mIvLogo = (CircleImageView) findViewById(R.id.iv_mine_credit_avatar);
        mTvMore = (TextView) findViewById(R.id.tv_more_remark);
        mListRemark = (ListView) findViewById(R.id.lv_single_remark_list);
    }

    public void initData() {
        mTvName.setText(UserSubject.getName());
        mTvSchoolName.setText(UserSubject.getSchoolname());
        mTvCollegeName.setText(UserSubject.getCollegename());
        mBitmapUtil.getImage(mIvLogo, UserSubject.getLogourl(), true, R.drawable.default_head_img);

        initCreditData();
    }

    private void initCreditData() {
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
                        Stu11Response.Body stu11Body = response.getBody();
                        Stu11Response.Body.Student student = stu11Body.getStudent();
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

    private void fillCreditInView(Stu11Response.Body.Student student) {
        if(null != student){
            mTvCredit.setText(student.getScore());
        }
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 1: {
                    showToast("个人信用分数获取失败");
                    Log.i(TAG, "个人信用分数获取失败:" + msg.obj);
                    break;
                }
                case 2: {
                    Stu11Response.Body.Student student2 = (Stu11Response.Body.Student) msg.obj;
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
