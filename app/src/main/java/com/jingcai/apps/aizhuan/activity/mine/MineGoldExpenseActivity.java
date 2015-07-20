package com.jingcai.apps.aizhuan.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;

/**
 * Created by Administrator on 2015/7/18.
 */
public class MineGoldExpenseActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_account_expense);

        initHeader();
    }

    private void initHeader()
    {
        ((TextView)findViewById(R.id.tv_content)).setText("֧支出记录");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
