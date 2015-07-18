package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.mine.activity.MineAccountActivity;
import com.jingcai.apps.aizhuan.activity.mine.activity.MineContactService;
import com.jingcai.apps.aizhuan.activity.mine.activity.MineHelp;
import com.jingcai.apps.aizhuan.activity.mine.activity.MinePersonalDataActivity;
import com.jingcai.apps.aizhuan.activity.mine.activity.MineStudentCertificationActivity;
import com.jingcai.apps.aizhuan.activity.mine.activity.SettingsActivity;
import com.jingcai.apps.aizhuan.service.AzService;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMineFragment extends BaseFragment {

    private View lastPerformItem;
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
                Intent intent = new Intent(baseActivity,MineContactService.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.ll_mine_student_certification).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,MineStudentCertificationActivity.class);
                startActivity(intent);
            }
        });
        mainView.findViewById(R.id.ll_mine_help).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,MineHelp.class);
                startActivity(intent);
            }
        });

        mainView.findViewById(R.id.ll_mine_data).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,MinePersonalDataActivity.class);
                startActivity(intent);
            }
        });
    }



}
