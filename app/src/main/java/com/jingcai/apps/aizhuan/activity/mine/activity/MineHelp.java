package com.jingcai.apps.aizhuan.activity.mine.activity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.base.BaseFragmentActivity;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexCampusFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMessageFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMineFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMoneyFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexReleaseFragment;
import com.jingcai.apps.aizhuan.activity.mine.fragment.MineAskFragment;
import com.jingcai.apps.aizhuan.activity.mine.fragment.MineHelpFrament;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2015/7/18.
 */
public class MineHelp extends BaseFragmentActivity {

   MineHelpFrament mineHelpFrament=new MineHelpFrament();
    MineAskFragment mineAskFragment = new MineAskFragment();
  //  private final Class[] mTabFragmentClassArr = {IndexCampusFragment.class, IndexMessageFragment.class, IndexMoneyFragment.class, IndexMineFragment.class, IndexReleaseFragment.class};
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_help);


        initHeader();
        initView();
    }

    private void initHeader()
    {
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.ll_mine_fragment_container, mineHelpFrament).commit();
    }

    private void initView()
    {
       findViewById(R.id.btn_switch2).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FragmentManager fragmentManager = getSupportFragmentManager();
               FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
               fragmentTransaction.replace(R.id.ll_mine_fragment_container, mineAskFragment);
               fragmentTransaction.commit();
           }
       });
        findViewById(R.id.btn_switch1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.ll_mine_fragment_container, mineHelpFrament);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


}
