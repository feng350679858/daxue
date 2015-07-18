package com.jingcai.apps.aizhuan.activity.mine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.mine.activity.MineAccountActivity;
import com.jingcai.apps.aizhuan.activity.mine.activity.SettingsActivity;
import com.jingcai.apps.aizhuan.service.AzService;


/**
 * Created by xiangqili on 2015/7/14.
 */
public class MineIndexActivity extends BaseFragment{
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

            initViewSettings();

            initViewhelp();

            initViewAccount();
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
        ((ImageView)mainView.findViewById(R.id.iv_func)).setVisibility(View.VISIBLE);
        ((ImageView)mainView.findViewById(R.id.iv_func)).setImageDrawable(getResources
                ().getDrawable(R.drawable.icon_index_tab_mine_settings));
    }

    private void initViewSettings(){
        ((ImageView)mainView.findViewById(R.id.iv_func)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViewhelp(){}

    private void initViewAccount(){
        mainView.findViewById(R.id.ll_mine_account).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity,MineAccountActivity.class);
                startActivity(intent);
            }
        });
    }




}