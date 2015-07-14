package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMessageFragment extends BaseFragment implements View.OnClickListener {
    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_message_fragment, container, false);
        initView();
        return mBaseView;
    }

    private void initView() {
        mBaseView.findViewById(R.id.button_start_count).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_show_unread).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_disappear).setOnClickListener(this);
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
            case R.id.button_disappear: {
                mainActivity.reset();
                break;
            }
        }
    }
}
