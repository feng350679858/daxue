package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.jingcai.apps.aizhuan.service.business.partjob.partjob06.Partjob06Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob06.Partjob06Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob08.Partjob08Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob08.Partjob08Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;

import java.util.Date;

/**
 * Created by chenchao on 2015/7/13.
 */
public class PartjobCancelActivity extends BaseActivity{
    private final String TAG="PartjobCancelActivity";
    private MessageHandler messageHandler;

    private TextView mTxtMention;
    private Button mBtnComfirm;
    private Button mBtnContactMerchant;
    private AzService azService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partjob_cancel);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        messageHandler = new MessageHandler(this);
        azService = new AzService(PartjobCancelActivity.this);
        if(UserSubject.isLogin()){
            initHeader();//初始化头部
            initView();  //初始化控件
            initData();  //填充数据
        }else{
            startActivityForLogin();
        }
    }


    /**
     * 初始化头部
     */
    private void initHeader(){
        ((TextView)findViewById(R.id.tv_content)).setText("取消报名");
        ((ImageView)findViewById(R.id.iv_func)).setVisibility(View.INVISIBLE);
        ((ImageView)findViewById(R.id.iv_bird_badge)).setVisibility(View.INVISIBLE);
    }
    /**
     * 初始化控件
     */
    private void initView() {
        mTxtMention = (TextView) findViewById(R.id.tv_mine_partjob_cancel_mention);
        mBtnComfirm = (Button) findViewById(R.id.btn_confirm_false);
        mBtnComfirm.setText("我意已决");
        mBtnComfirm.setEnabled(false);
        mBtnContactMerchant = (Button) findViewById(R.id.btn_confirm_true);
        mBtnContactMerchant.setText("联系商家");
        mBtnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消报名
                Partjob06Request req = new Partjob06Request();
                Partjob06Request.Joininfo joininfo = req.new Joininfo();
                joininfo.setId(getIntent().getStringExtra("jobid"));
                joininfo.setStudentid(UserSubject.getStudentid());
                req.setJoininfo(joininfo);
                azService.doTrans(req, Partjob06Response.class, new AzService.Callback<Partjob06Response>() {
                    @Override
                    public void success(Partjob06Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(2, result.getMessage());
                        } else {
                            messageHandler.postMessage(3);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });

            }
        });

        mBtnContactMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //联系商家
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + getIntent().getStringExtra("phone")));
                startActivity(intent);
            }
        });

    }


    /**
     * 初始化数据
     */
    private void initData() {
        showProgressDialog("数据读取中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob08Request req = new Partjob08Request();
                Partjob08Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);
                azService.doTrans(req, Partjob08Response.class, new AzService.Callback<Partjob08Response>() {
                    @Override
                    public void success(Partjob08Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(0, resp.getBody().getStudent());
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


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx){
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what){
                case 0:{
                    //根据截止日期、剩余权限填充提示信息
                    fillMention((Partjob08Response.Partjob04Body.Student)msg.obj);
                    break;
                }
                case 1:{
                    showToast("数据读取失败");
                    Log.i(TAG,"数据读取失败：" + msg.obj);
                    break;
                }
                case 2:{
                    showToast("提交失败");
                    Log.i(TAG,"提交失败：" + msg.obj);
                    break;
                }
                case 3:{
                    showToast("取消报名成功");
                    finish();
                    break;
                }
                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }

    /**
     * 填充提示信息
     * @param student
     */
    private void fillMention(Partjob08Response.Partjob04Body.Student student) {
        Date endDate = DateUtil.parseDate(getIntent().getStringExtra("endtime"));
        boolean noTimeLeft = endDate.getTime() < System.currentTimeMillis();
        if(noTimeLeft){
            //过了截止日期
            mTxtMention.setText(R.string.mine_partjob_cancel_unable);

        }else{
            //还没过截至日期
            try {
                //剩余的次数
                Integer leftCount = Integer.parseInt(student.getLeftcount());
                if(leftCount > 0){
                    mTxtMention.setText(
                            String.format(getResources().getString(R.string.mine_partjob_cancel_have_chance), student.getLeftcount()));
                    mBtnComfirm.setEnabled(true);
                }else{
                    mTxtMention.setText(R.string.mine_partjob_cancel_no_chance);
                    mBtnComfirm.setTextColor(getResources().getColor(R.color.assist_grey));

                }
            } catch (Exception e) {
                showToast("获取剩余可取消次数异常");
            }
        }
    }
}
