package com.jingcai.apps.aizhuan.activity.mine.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
/**
 * Created by xiangqili on 2015/7/14.
 */
public class MineIndexActivity extends Activity{
    private View lastPerformItem;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_index);

        initHeader();
    }

    public void initHeader(){
        ((TextView)findViewById(R.id.tv_content)).setText("ÎÒµÄ");
        ((ImageView)findViewById(R.id.ib_back)).setImageDrawable(getResources().getDrawable(R.drawable.icon_index_tab_mine_two_dimensioncode));
    }
}