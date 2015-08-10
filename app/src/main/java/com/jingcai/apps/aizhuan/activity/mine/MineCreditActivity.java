package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.RemarkListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.persistence.db.Database;
import com.jingcai.apps.aizhuan.persistence.vo.ContactInfo;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu12.Stu12Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu12.Stu12Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.LocalValUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MineCreditActivity extends BaseActivity {
    private final String TAG = "MineCreditActivity";

    public static final String INTENT_NAME_STUDENT_ID = "studentid";

    private MessageHandler messageHandler;
    private AzService azService;
    private TextView mTvName;
    private CircleImageView mIvLogo;
    private TextView mTvSchoolName;
    private TextView mTvCredit;
    private TextView mTvCollegeName;
    private TextView mTvMore;
    private ListView mListRemark;
    private RemarkListAdapter mListAdapter;

    private BitmapUtil mBitmapUtil;
    private volatile List<Stu12Response.Body.Evaluate> mRemarkList;

    private String studentid;  //如果有studentid,那么则显示其他人的信用
    private boolean showMine = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_credit);

        messageHandler = new MessageHandler(MineCreditActivity.this);
        azService = new AzService(MineCreditActivity.this);
        mBitmapUtil = new BitmapUtil(this);
        unpackIntent();
        initHeader();
        initViews();  //初始化Views
        initData();

    }

    /**
     * 如果有studentid 传过来，则显示其他的人信用页面
     * 没有则显示自己的信用页面
     */
    private void unpackIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            studentid = intent.getStringExtra(INTENT_NAME_STUDENT_ID);
            if(StringUtil.isNotEmpty(studentid)){
                showMine = false;
            }
        }
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText(showMine ? "我的信用" : "个人信用");
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
        mListAdapter = new RemarkListAdapter(this);

    }

    public void initData() {
        if(showMine) {
            mTvName.setText(UserSubject.getName());
            mTvSchoolName.setText(UserSubject.getSchoolname());
            mTvCollegeName.setText(UserSubject.getCollegename());
            mBitmapUtil.getImage(mIvLogo, UserSubject.getLogourl(), true, R.drawable.default_head_img);
        }else{
            initProfileData();
        }

        initCreditData();
        initRemarkData();
    }

    /**
     * 资料
     */
    private void initProfileData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(studentid);
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        final Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if("0".equals(result.getCode())) {
                            messageHandler.postMessage(6,student);
                        }else{
                            messageHandler.postMessage(5,result.getMessage());
                        }
                    }
                    @Override
                    public void fail(AzException e) {
                        Log.e(TAG,"Transcode : stu02 failed.Code:"+e.getCode()+",Message:"+e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * 评论
     */
    private void initRemarkData() {
        if(GlobalConstant.debugFlag){
            List<Stu12Response.Body.Evaluate> evaluates = new ArrayList<>();
            for(int i = 0 ; i < 10; i++){
                Stu12Response.Body.Evaluate evaluate = new Stu12Response.Body.Evaluate();
                evaluate.setContent("这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容这是内容内容内容");
                evaluate.setEvaluateid("evaluateid" + i);
                evaluate.setOptime("20150809231135");
                evaluate.setScore(String.valueOf(i % 5 + 1));
                evaluate.setSourceid("7a82a05512bf411c9dd2f318f8798a3e");
                evaluate.setSourceimgurl(UserSubject.getLogourl());
                evaluate.setSourcename("丁" + i);
                evaluate.setTitle("这是标题这是标题这是标题" + i);
                evaluates.add(evaluate);
            }
            messageHandler.postMessage(4, evaluates);
        }else{
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Stu12Request req = new Stu12Request();
                    final Stu12Request.Student stu = req.new Student();

                    stu.setStudentid(showMine ? UserSubject.getStudentid() : studentid);  //从UserSubject中获取studentId
                    req.setStudent(stu);
                    azService.doTrans(req, Stu12Response.class, new AzService.Callback<Stu12Response>() {

                        @Override
                        public void success(Stu12Response resp) {
                            ResponseResult result = resp.getResult();
                            Stu12Response.Body stu12Body = resp.getBody();
                            List<Stu12Response.Body.Evaluate> evaluates = stu12Body.getEvaluate_list();
                            if (null == evaluates) {
                                evaluates = new ArrayList<>();
                            }
                            if (!"0".equals(result.getCode())) {
                                messageHandler.postMessage(3, result.getMessage());
                            } else {
                                messageHandler.postMessage(4, evaluates);
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

    }

    /**
     * 评分
     */
    private void initCreditData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu11Request req = new Stu11Request();
                final Stu11Request.Student stu = req.new Student();
                stu.setStudentid(showMine ? UserSubject.getStudentid() : studentid);  //从UserSubject中获取studentId
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
                case 3 :{
                    showToast("获取评语失败");
                    Log.i(TAG, "获取评语失败:" + msg.obj);
                    break;
                }
                case 4:{
                    List<Stu12Response.Body.Evaluate> evaluates = (List<Stu12Response.Body.Evaluate>) msg.obj;
                    fillTopOneEvaluate(evaluates);
                    break;
                }case 5:{
                    showToast("获取个人资料失败");
                    Log.i(TAG, "获取个人资料失败:" + msg.obj);
                    break;
                }case 6:{
                    Stu02Response.Stu02Body.Student student = (Stu02Response.Stu02Body.Student) msg.obj;
                    fillStudentProfile(student);
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    /**
     * 填充学生信息
     * @param student stu02
     */
    private void fillStudentProfile(Stu02Response.Stu02Body.Student student) {
        mTvName.setText(student.getName());
        mTvSchoolName.setText(student.getSchoolname());
        mTvCollegeName.setText(student.getCollegename());
        mBitmapUtil.getImage(mIvLogo, student.getLogopath(), true, R.drawable.default_head_img);
        updateDbContact(student);
    }

    private void updateDbContact(Stu02Response.Stu02Body.Student student) {
        Database db = Database.getInstance(getApplicationContext());
        db.open();
        final List<ContactInfo> contactInfos
                = db.fetchContactsInfoByStudentId(UserSubject.getStudentid(), student.getStudentid());
        if(contactInfos.size() > 0){
            ContactInfo c = contactInfos.get(0);
            c.setName(student.getName());
            c.setLogourl(student.getLogopath());
            db.updateContactInfo(UserSubject.getStudentid(),c);
        }
    }

    /**
     * 获取最上一条评价并显示，根据条目数量改变底部查看更多的显示
     * @param evaluates
     */
    private void fillTopOneEvaluate(final List<Stu12Response.Body.Evaluate> evaluates) {
        mRemarkList = evaluates;
        final int size = evaluates.size();
        if(size <= 1){
            mTvMore.setText("没有更多评语");
            mTvMore.setTextColor(getResources().getColor(R.color.assist_grey));
            mTvMore.setEnabled(false);
        }else{
            mTvMore.setText("查看更多评语");
            mTvMore.setTextColor(getResources().getColor(R.color.main_yellow));
            mTvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mRemarkList == null){
                        showToast("评语读取中，请稍等");
                    }else{
                        LocalValUtil.setVal(evaluates);
                        Intent intent = new Intent(MineCreditActivity.this,MineEvaluateActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        if(size >= 1){
            ArrayList<Stu12Response.Body.Evaluate> topEvaluate = new ArrayList<>(evaluates.subList(0,1));
            mListAdapter.setListData(topEvaluate);
            mListRemark.setAdapter(mListAdapter);
        }
    }
}
