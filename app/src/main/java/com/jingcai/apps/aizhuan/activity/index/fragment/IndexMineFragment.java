package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.mine.gold.MineAccountActivity;
import com.jingcai.apps.aizhuan.activity.mine.MineContactServiceActivity;
import com.jingcai.apps.aizhuan.activity.mine.MineCreditActivity;
import com.jingcai.apps.aizhuan.activity.mine.MinePersonalDataActivity;
import com.jingcai.apps.aizhuan.activity.mine.MineStudentCertificationActivity;
import com.jingcai.apps.aizhuan.activity.mine.MyPartjobListActivity;
import com.jingcai.apps.aizhuan.activity.sys.SettingsActivity;
import com.jingcai.apps.aizhuan.activity.mine.help.MineHelpListActivity;
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.util.AzExecutor;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMineFragment extends BaseFragment {

    private LevelTextView level;
    private TextView exp;
    private View mainView;
    private AzService azService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (null == mainView) {
            mainView = inflater.inflate(R.layout.mine_index, null);
            azService = new AzService(baseActivity);
            initHeader();
            initView();
  //          initDate();
        }
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeView(mainView);
        }
        return mainView;
    }

    private void initHeader(){
        ((TextView)mainView.findViewById(R.id.tv_content)).setText("我的");
        ((ImageView)mainView.findViewById(R.id.ib_back)).setImageDrawable(getResources
                ().getDrawable(R.drawable.icon_index_tab_mine_twodimensioncode));
        ImageView ivFunc = (ImageView) mainView.findViewById(R.id.iv_func);
        ivFunc.setVisibility(View.VISIBLE);
        ivFunc.setImageDrawable(getResources
                ().getDrawable(R.drawable.icon_index_tab_mine_settings));
        ivFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        level=(LevelTextView)mainView.findViewById(R.id.ltv_level);
        exp=(TextView)mainView.findViewById(R.id.exp);
        mainView.findViewById(R.id.ll_mine_partjob).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,MyPartjobListActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.ll_mine_account).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,MineAccountActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.ll_mine_contact_service).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,MineContactServiceActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.ll_mine_student_certification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MineStudentCertificationActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.ll_mine_help_req).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MineHelpListActivity.class);
                intent.putExtra("provideFlag", true);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.ll_mine_help).setOnClickListener(new View.OnClickListener() {
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
        mainView.findViewById(R.id.ll_mine_credit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MineCreditActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initDate(){
        showProgressDialog("数据加载中...");
        initExp();
    }
    private void initExp(){
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(getActivity());
//                Stu14Request req=new Stu14Request();
//                Account04Request request = new Account04Request();
//                Account04Request.Student student = request.new Student();
//                student.setStudentid(UserSubject.getStudentid());
//                request.setStudent(student);
//
//                azService.doTrans(request, Account04Response.class, new AzService.Callback<Account04Response>() {
//                    @Override
//                    public void success(Account04Response resp) {
//                        ResponseResult result = resp.getResult();
//                        if (!"0".equals(result.getCode())) {
//                            messageHandler.postMessage(7, result.getMessage());
//                        } else {
//
//                            messageHandler.postMessage(6, resp.getBody().getBank_list());
//                        }
//                    }
//
//                    @Override
//                    public void fail(AzException e) {
//                        messageHandler.postException(e);
//                    }
//                });
            }
        });
    }

}
