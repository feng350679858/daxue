package com.jingcai.apps.aizhuan.activity.index;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragmentActivity;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMessageFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMineFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMoneyFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexCampusFragment;
import com.jingcai.apps.aizhuan.entity.BadgeBean;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.local.LocalService;

/**
 * Created by Json Ding on 2015/7/9.
 */
public class MainActivity extends BaseFragmentActivity {
    private static final String TAG = "MainActivity";

    //Tab相关
    private LinearLayout mLlCampus;
    private LinearLayout mLlMessgage;
    private LinearLayout mLlMoney;
    private LinearLayout mLlMine;
    private ImageButton mBtnRelease;
    private int mCurrentTabIndex;

    private final int[] mNormalTabIconDrawableIds = {R.drawable.icon_index_tab_campus_normal,R.drawable.icon_index_tab_message_normal,R.drawable.icon_index_tab_money_normal,R.drawable.icon_index_tab_mine_normal};
    private final int[] mFocusedTabIconDrawableIds = {R.drawable.icon_index_tab_campus_focused,R.drawable.icon_index_tab_message_focused,R.drawable.icon_index_tab_money_focused,R.drawable.icon_index_tab_mine_focused};
    private final Class[] mTabFragmentClassArr = {IndexCampusFragment.class, IndexMessageFragment.class, IndexMoneyFragment.class, IndexMineFragment.class};
    private final Fragment[] mTabFragmentArr = new Fragment[mNormalTabIconDrawableIds.length];
    private final ImageView[] mIconViewArr = new ImageView[mNormalTabIconDrawableIds.length];

    private ServiceConnection serviceConnection;
    private LocalService.SimpleBinder serviceBinder;

    private View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentTabIndex = 0;
            try {
                mCurrentTabIndex = Integer.parseInt(v.getTag().toString());
            } catch (NumberFormatException e) {
                Log.e(TAG, "DO NOT set any other view to this OnClickListener.");
                return;
            }
            //将点击的Icon设置为focused状态，其他设置为normal状态
            focusTapedIcon(mCurrentTabIndex);
            //切换当前显示的Fragment到指定的fragment
            changeFragment(mCurrentTabIndex);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
    }

    private void initView() {
        mLlCampus = (LinearLayout) findViewById(R.id.ll_campus);
        mLlMessgage = (LinearLayout) findViewById(R.id.ll_message);
        mLlMoney = (LinearLayout) findViewById(R.id.ll_money);
        mLlMine = (LinearLayout) findViewById(R.id.ll_mine);
        mBtnRelease = (ImageButton) findViewById(R.id.btn_release);

        mLlCampus.setOnClickListener(mTabClickListener);
        mLlMessgage.setOnClickListener(mTabClickListener);
        mLlMoney.setOnClickListener(mTabClickListener);
        mLlMine.setOnClickListener(mTabClickListener);

        initTabIconViews();

        initService();


        //将点击的Icon设置为focused状态，其他设置为normal状态
        focusTapedIcon(mCurrentTabIndex);
        //切换当前显示的Fragment到指定的fragment
        changeFragment(mCurrentTabIndex);
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                serviceBinder = (LocalService.SimpleBinder)service;
                Log.v(TAG, "3 + 5 = " + serviceBinder.add(3, 5));
                Log.v(TAG, serviceBinder.getService().toString());
            }
        };
        bindService(new Intent(MainActivity.this, LocalService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        registerReceiver(mReceiver, new IntentFilter(GlobalConstant.ACTION_UPDATE_BADGE));
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        unbindService(serviceConnection);
        super.onStop();
    }

    private void initTabIconViews() {
        mIconViewArr[0] = (ImageView) findViewById(R.id.iv_campus);
        mIconViewArr[1] = (ImageView) findViewById(R.id.iv_message);
        mIconViewArr[2] = (ImageView) findViewById(R.id.iv_money);
        mIconViewArr[3] = (ImageView) findViewById(R.id.iv_mine);
    }

    private void changeFragment(int tabIndex) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        try {
            if(null == mTabFragmentArr[tabIndex]){
                mTabFragmentArr[tabIndex] = (Fragment) mTabFragmentClassArr[tabIndex].newInstance();
            }
            fragmentTransaction.replace(R.id.ll_fragment_container, mTabFragmentArr[tabIndex]);
        } catch (Exception e) {
            Log.e(TAG,"MainActivity changeFragment get a new Fragment failed");
        }
        fragmentTransaction.commit();
//        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 将选中的TabIcon设置为获取焦点状态，其他设置为普通状态
     * @param tabIndex 选中的index
     */
    private void focusTapedIcon(int tabIndex) {
        for (int i = 0; i < mNormalTabIconDrawableIds.length; i++) {
            if(i != tabIndex){
                mIconViewArr[i].setImageResource(mNormalTabIconDrawableIds[i]);
            }else{
                mIconViewArr[i].setImageResource(mFocusedTabIconDrawableIds[i]);
            }
        }
    }


    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = intent.getIntExtra("count", 0);
            TextView view = (TextView)findViewById(R.id.tv_message_num_badge);
            if(count>0){
                view.setVisibility(View.VISIBLE);
                view.setText(String.valueOf(count));
            }else{
                view.setVisibility(View.INVISIBLE);
            }
//            if(null != intent){
//                BadgeBean badgeBean = (BadgeBean) intent.getSerializableExtra("badgeBean");
//                if(null != badgeBean){
//                    if(getClass().getSimpleName().equals(badgeBean.getTargetActivity())){
//                        int id = badgeBean.getTargetViewId();
//                        View view = findViewById(id);
//                        view.setVisibility(badgeBean.isSetVisiable() ? View.VISIBLE : View.INVISIBLE);
//                        if(badgeBean.getType() == BadgeBean.Type.TEXT){
//                            TextView tv = (TextView)view;
//                            int former;
//                            try {
//                                former = Integer.parseInt(tv.getText().toString());
//                            }catch (NumberFormatException e){
//                                Log.e(TAG, "The number in badge has a invalid format.");
//                                former = 0;
//                            }
//                            former += badgeBean.getCount();
//                            if(former <= 0){
//                                tv.setVisibility(View.INVISIBLE);
//                                tv.setText("");
//                            }else{
//                                tv.setText(String.valueOf(former));
//                            }
//
//                        }
//                    }
//                }
//            }
        }
    };

    public void startCount() {
        serviceBinder.startCount();
    }

    public void showUnread() {
        serviceBinder.showUnread();
    }

    public void reset() {
        serviceBinder.reset();
    }
}
