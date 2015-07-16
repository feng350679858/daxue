package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.help.JishiHelpDeployActivity;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexReleaseFragment extends Fragment implements View.OnClickListener{
    private View mBaseView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_release_fragment,container,false);
        initView();
        return mBaseView;


//        if (null == mainView) {
//            mBaseView = inflater.inflate(R.layout.index_campus_fragment, null);
//            //initView();
//        }
//        ViewGroup parent = (ViewGroup) mainView.getParent();
//        if (parent != null) {
//            parent.removeView(mBaseView);
//        }
//        return mBaseView;
    }

    private void initView() {
        mBaseView.findViewById(R.id.button_start_count).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_show_unread).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_reset).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_jishi_help).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_wenda_help).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MainActivity mainActivity = (MainActivity) getActivity();
        switch (v.getId()) {
            case R.id.button_start_count: {
                mainActivity.startCount();
                break;
            }
            case R.id.button_show_unread: {
                mainActivity.showUnread();
                break;
            }
            case R.id.button_reset: {
                mainActivity.reset();
                break;
            }
            case R.id.button_jishi_help: {
                Intent intent = new Intent(this.getActivity(), JishiHelpDeployActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button_wenda_help: {

                break;
            }
        }
    }
}
