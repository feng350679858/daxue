package com.jingcai.apps.aizhuan.activity.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
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
            initHeader();//��ʼ��ͷ��
            initView();  //��ʼ���ؼ�
            initData();  //�������
        }else{
            startActivityForLogin();
        }
    }


    /**
     * ��ʼ��ͷ��
     */
    private void initHeader(){
        ((TextView)findViewById(R.id.tv_content)).setText("ȡ������");
        ((ImageView)findViewById(R.id.iv_func)).setVisibility(View.INVISIBLE);
        ((ImageView)findViewById(R.id.iv_bird_badge)).setVisibility(View.INVISIBLE);
    }
    /**
     * ��ʼ���ؼ�
     */
    private void initView() {
        mTxtMention = (TextView) findViewById(R.id.tv_mine_partjob_cancel_mention);
        mBtnComfirm = (Button) findViewById(R.id.btn_mine_partjob_cancel_confirm);
        mBtnContactMerchant = (Button) findViewById(R.id.btn_mine_partjob_cancel_contact_merchant);

        mBtnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ȡ������
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
                //��ϵ�̼�
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + getIntent().getStringExtra("phone")));
                startActivity(intent);
            }
        });

    }


    /**
     * ��ʼ������
     */
    private void initData() {
        showProgressDialog("���ݶ�ȡ��...");
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
                    //���ݽ�ֹ���ڡ�ʣ��Ȩ�������ʾ��Ϣ
                    fillMention((Partjob08Response.Partjob04Body.Student)msg.obj);
                    break;
                }
                case 1:{
                    showToast("���ݶ�ȡʧ�ܣ�" + msg.obj);
                    break;
                }
                case 2:{
                    showToast("�ύʧ�ܣ�" + msg.obj);
                    break;
                }
                case 3:{
                    showToast("ȡ�������ɹ�");
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
     * �����ʾ��Ϣ
     * @param student
     */
    private void fillMention(Partjob08Response.Partjob04Body.Student student) {
        Date endDate = DateUtil.parseDate(getIntent().getStringExtra("endtime"),"yyyy-MM-dd HH:mm:ss");
        boolean noTimeLeft = endDate.getTime() < System.currentTimeMillis();
        if(noTimeLeft){
            //���˽�ֹ����
            mTxtMention.setText(R.string.mine_partjob_cancel_unable);
        }else{
            //��û����������
            try {
                //ʣ��Ĵ���
                Integer leftCount = Integer.parseInt(student.getLeftcount());
                if(leftCount > 0){
                    mTxtMention.setText(
                            String.format(getResources().getString(R.string.mine_partjob_cancel_have_chance), student.getLeftcount()));
                    mBtnComfirm.setEnabled(true);
                    mBtnComfirm.setTextColor(getResources().getColor(R.color.font_red));
                }else{
                    mTxtMention.setText(R.string.mine_partjob_cancel_no_chance);
                }
            } catch (Exception e) {
                showToast("��ȡʣ���ȡ�������쳣");
            }
        }
    }
}
