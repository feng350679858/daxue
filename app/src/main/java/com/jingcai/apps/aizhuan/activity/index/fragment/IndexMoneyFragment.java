package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingcai.apps.aizhuan.R;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMoneyFragment extends Fragment {

    private View mBaseView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_money_fragment,container,false);
        return mBaseView;
    }
}
