package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.entity.BadgeBean;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMessageFragment extends BaseFragment implements View.OnClickListener{
    private View mBaseView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_message_fragment,container,false);
        initView();
        return mBaseView;
    }

    private void initView() {
        mBaseView.findViewById(R.id.button_add).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_minus).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_disappear).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(GlobalConstant.ACTION_UPDATE_BADGE);
        BadgeBean badgeBean = null;
        switch (v.getId()){
            case R.id.button_add:
                badgeBean =  new BadgeBean(BadgeBean.Type.NORMAL,11,"MainActivity",R.id.iv_message_badge,true);
                break;
            case R.id.button_minus:
                badgeBean =  new BadgeBean(BadgeBean.Type.NORMAL,-11,"MainActivity",R.id.iv_message_badge,true);
                break;
            case R.id.button_disappear:badgeBean =  new BadgeBean(null,0,"MainActivity",R.id.iv_message_badge,false);
                break;
        }
        intent.putExtra("badgeBean", badgeBean);
        baseActivity.sendBroadcast(intent);
    }
}
