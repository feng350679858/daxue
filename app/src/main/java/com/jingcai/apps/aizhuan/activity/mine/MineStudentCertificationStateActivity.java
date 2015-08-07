package com.jingcai.apps.aizhuan.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.persistence.UserSubject;

/**
 * Created by Json Ding on 2015/8/7.
 */
public class MineStudentCertificationStateActivity extends BaseActivity {

    private TextView mTvDiagram;
    private TextView mTvName;
    private TextView mTvCampus;
    private TextView mTvCollege;
    private TextView mTvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_student_certification_state);

        initHeader();
        initViews();
        initData();
    }

    private void initData() {
        final String scnoauthflag = UserSubject.getScnoauthflag();
        if("1".equals(scnoauthflag)){
            mTvDiagram.setText("你已通过实名认证");
            mTvDiagram.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.student_auth_success, 0, 0);
            mTvState.setText("已认证");
        }else if("2".equals(scnoauthflag)){
            mTvDiagram.setText("你正在实名认证中");
            mTvDiagram.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.student_auth_waiting,0,0);
            mTvState.setText("认证中");
        }

        mTvName.setText(UserSubject.getName());
        mTvCampus.setText(UserSubject.getSchoolname());
        mTvCollege.setText(UserSubject.getCollegename());
    }

    private void initViews() {
        mTvDiagram = (TextView) findViewById(R.id.tv_student_auth_diagram);
        mTvName = (TextView) findViewById(R.id.tv_student_auth_name);
        mTvCampus = (TextView) findViewById(R.id.tv_student_auth_campus);
        mTvCollege = (TextView) findViewById(R.id.tv_student_auth_college);
        mTvState = (TextView) findViewById(R.id.tv_student_auth_state);
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("学生认证֤");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
