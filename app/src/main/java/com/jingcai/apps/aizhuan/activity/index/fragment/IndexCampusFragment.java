package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexCampusFragment extends BaseFragment implements View.OnClickListener{
    private View mBaseView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_campus_fragment,container,false);
        initHeader();
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

    private void initHeader() {
        TextView tvTitle = (TextView) mBaseView.findViewById(R.id.tv_content);
        //需要用到再findViewById，不要需则不调用，提高效率
//        TextView tvFunc = (TextView) findViewById(R.id.tv_func);
        ImageView ivFunc = (ImageView) mBaseView.findViewById(R.id.iv_func);
        ivFunc.setImageResource(R.drawable.icon_index_campus_bird_online);
        ivFunc.setVisibility(View.VISIBLE);
        tvTitle.setText("校园");

        ivFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 上下线
            }
        });
    }

    private void initView() {
        mBaseView.findViewById(R.id.button_start_count).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_show_unread).setOnClickListener(this);
        mBaseView.findViewById(R.id.button_reset).setOnClickListener(this);
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
        }
    }
}
