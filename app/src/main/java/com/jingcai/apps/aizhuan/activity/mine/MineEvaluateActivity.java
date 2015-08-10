package com.jingcai.apps.aizhuan.activity.mine;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.adapter.mine.RemarkListAdapter;
import com.jingcai.apps.aizhuan.service.business.stu.stu12.Stu12Response;
import com.jingcai.apps.aizhuan.util.LocalValUtil;

import java.util.List;

/**
 * Created by Json Ding on 2015/8/10.
 */
public class MineEvaluateActivity extends BaseActivity {

    private static final String TAG = "MineEvaluateActivity";


    private ListView mLvEvaluates;
    private RemarkListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_credit_evalutes);


        initHeader();
        initViews();
        initData();
    }



    private void initData() {
        try {
            List<Stu12Response.Body.Evaluate> val = (List<Stu12Response.Body.Evaluate>) LocalValUtil.getVal();
            mListAdapter.setListData(val);
            mLvEvaluates.setAdapter(mListAdapter);
        } catch (Exception e) {
            Log.w(TAG,"data from ThreadLocal has problem:"+e.getMessage());
        } finally {
            LocalValUtil.setVal(null);
        }
    }

    private void initViews() {
        mLvEvaluates = (ListView) findViewById(R.id.lv_evaluate_list);
        mListAdapter = new RemarkListAdapter(this);
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("更多评价");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
