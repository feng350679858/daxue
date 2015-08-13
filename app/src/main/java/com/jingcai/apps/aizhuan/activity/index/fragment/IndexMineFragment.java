package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.mine.MineContactServiceActivity;
import com.jingcai.apps.aizhuan.activity.mine.MineCreditActivity;
import com.jingcai.apps.aizhuan.activity.mine.MinePersonalDataActivity;
import com.jingcai.apps.aizhuan.activity.mine.MineStudentCertificationActivity;
import com.jingcai.apps.aizhuan.activity.mine.MineStudentCertificationStateActivity;
import com.jingcai.apps.aizhuan.activity.mine.MyPartjobListActivity;
import com.jingcai.apps.aizhuan.activity.mine.gold.MineAccountActivity;
import com.jingcai.apps.aizhuan.activity.mine.help.MineHelpListActivity;
import com.jingcai.apps.aizhuan.activity.sys.SettingsActivity;
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu14.Stu14Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu14.Stu14Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMineFragment extends BaseFragment {
    private final String TAG = "IndexMineFragment";

    private LevelTextView level;
    private TextView exp, name, credit_score,mTvAuthState;
    private ProgressBar progressBar;
    private CircleImageView head_logo;
    private ImageView mIvAuthId,mIvAuthStu;

    private View mainView;
    private AzService azService;
    private MessageHandler messageHandler;
    private BitmapUtil mBitmapUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (null == mainView) {
            mainView = inflater.inflate(R.layout.mine_index, null);
            azService = new AzService(baseActivity);
            messageHandler = new MessageHandler(baseActivity);
            mBitmapUtil = new BitmapUtil(baseActivity);
            initHeader();
            initView();
            initDate();
        }
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeView(mainView);
        }
        return mainView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mBitmapUtil.getImage(head_logo, UserSubject.getLogourl(), true, R.drawable.default_head_img);
        initAuthState();
    }

    /**
     * 认证状态
     */
    private void initAuthState() {
        switch (UserSubject.getScnoauthflag()) {
            case "0":
                mTvAuthState.setText("未认证");
                mTvAuthState.setBackgroundResource(R.drawable.tv_grey_rectangle_bg);
                break;
            case "1":
                mTvAuthState.setText("已认证");
                mTvAuthState.setBackgroundResource(R.drawable.tv_yellow_rectangle_bg);
                mIvAuthStu.setVisibility(View.VISIBLE);
                break;
            case "2":
                mTvAuthState.setText("认证中");
                mTvAuthState.setBackgroundResource(R.drawable.tv_yellow_rectangle_bg);
                break;
        }
        if("1".equals(UserSubject.getIdnoauthflag())){
            mIvAuthId.setVisibility(View.VISIBLE);
        }
    }

    private void initHeader() {

        ((TextView) mainView.findViewById(R.id.tv_content)).setText("我的");
        ((ImageView) mainView.findViewById(R.id.ib_back)).setImageDrawable(getResources
                ().getDrawable(R.drawable.icon_index_mine_twodimensioncode));
        ImageView ivFunc = (ImageView) mainView.findViewById(R.id.iv_func);
        ivFunc.setVisibility(View.VISIBLE);
        ivFunc.setImageDrawable(getResources
                ().getDrawable(R.drawable.icon_index_mine_settings));
        ivFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        name = (TextView) mainView.findViewById(R.id.name);
        name.setText(UserSubject.getName());
        head_logo = (CircleImageView) mainView.findViewById(R.id.civ_head_logo);
        mBitmapUtil.getImage(head_logo, UserSubject.getLogourl(), R.drawable.default_head_img);
        level = (LevelTextView) mainView.findViewById(R.id.ltv_level);
        exp = (TextView) mainView.findViewById(R.id.exp);
        progressBar = (ProgressBar) mainView.findViewById(R.id.exp_progressBar);
        credit_score = (TextView) mainView.findViewById(R.id.credit_score);
        mTvAuthState = (TextView) mainView.findViewById(R.id.tv_auth_state);
        mIvAuthId = (ImageView) mainView.findViewById(R.id.iv_auth_id);
        mIvAuthStu = (ImageView) mainView.findViewById(R.id.iv_auth_stu);

        mainView.findViewById(R.id.rl_mine_partjob).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MyPartjobListActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.rl_mine_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MineAccountActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.rl_mine_contact_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MineContactServiceActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.rl_mine_student_certification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String scnoauthflag = UserSubject.getScnoauthflag();
                Intent intent = null;
                if("1".equals(scnoauthflag) || "2".equals(scnoauthflag)){
                    intent = new Intent(baseActivity,MineStudentCertificationStateActivity.class);
                }else{
                    intent = new Intent(baseActivity, MineStudentCertificationActivity.class);
                }
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.rl_mine_help_req).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MineHelpListActivity.class);
                intent.putExtra("provideFlag", true);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.rl_mine_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MineHelpListActivity.class);
                intent.putExtra("provideFlag", false);
                startActivity(intent);
            }
        });

        mainView.findViewById(R.id.ll_mine_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MinePersonalDataActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.rl_mine_credit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MineCreditActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDate() {
        showProgressDialog("数据加载中...");
        initExp();
        initCredit();
    }

    private void initCredit() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(getActivity());
                Stu11Request req = new Stu11Request();
                Stu11Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);
                azService.doTrans(req, Stu11Response.class, new AzService.Callback<Stu11Response>() {
                    @Override
                    public void success(Stu11Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(3, result.getMessage());
                        } else {
                            messageHandler.postMessage(2, resp.getBody().getStudent());
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

    private void initExp() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(getActivity());
                Stu14Request req = new Stu14Request();
                Stu14Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);
                azService.doTrans(req, Stu14Response.class, new AzService.Callback<Stu14Response>() {
                    @Override
                    public void success(Stu14Response resp) {
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
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    fillExp((Stu14Response.Body.Student) msg.obj);
                    break;
                }
                case 1: {
                    showToast("获取经验等级失败");
                    Log.i(TAG, "获取经验等级失败：" + msg.obj);
                    break;
                }
                case 2: {
                    fillCredit((Stu11Response.Body.Student) msg.obj);
                    break;
                }
                case 3: {
                    showToast("获取信用积分失败");
                    Log.i(TAG, "获取信用积分失败：" + msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private void fillExp(Stu14Response.Body.Student student) {
        level.setLevel(Integer.parseInt(student.getLevel()));
        exp.setText("经验值：" + student.getExp() + "[" + student.getPercent() + "%]");
        progressBar.setProgress((int) Float.parseFloat(student.getPercent()));
    }

    private void fillCredit(Stu11Response.Body.Student student) {
        credit_score.setText(student.getScore() + "分");
    }
}
