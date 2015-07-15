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
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexCampusFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMessageFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMineFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMoneyFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexReleaseFragment;
import com.jingcai.apps.aizhuan.service.local.UnreadMsgService;

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

    private final int[] mNormalTabIconDrawableIds = {R.drawable.icon_index_tab_campus_normal, R.drawable.icon_index_tab_message_normal, R.drawable.icon_index_tab_money_normal, R.drawable.icon_index_tab_mine_normal};
    private final int[] mFocusedTabIconDrawableIds = {R.drawable.icon_index_tab_campus_focused, R.drawable.icon_index_tab_message_focused, R.drawable.icon_index_tab_money_focused, R.drawable.icon_index_tab_mine_focused};
    private final ImageView[] mIconViewArr = new ImageView[mNormalTabIconDrawableIds.length];

    private final Class[] mTabFragmentClassArr = {IndexReleaseFragment.class, IndexMessageFragment.class, IndexMoneyFragment.class, IndexMineFragment.class, IndexCampusFragment.class};
    private final Fragment[] mTabFragmentArr = new Fragment[mTabFragmentClassArr.length];

    private ServiceConnection serviceConnection;
    private UnreadMsgService unreadMsgService;

    private View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
    private ImageView iv_campus_badge;
    private TextView tv_message_num_badge;

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
        mBtnRelease.setOnClickListener(mTabClickListener);

        mIconViewArr[0] = (ImageView) findViewById(R.id.iv_campus);
        mIconViewArr[1] = (ImageView) findViewById(R.id.iv_message);
        mIconViewArr[2] = (ImageView) findViewById(R.id.iv_money);
        mIconViewArr[3] = (ImageView) findViewById(R.id.iv_mine);

        iv_campus_badge = (ImageView) findViewById(R.id.iv_campus_badge);
        tv_message_num_badge = (TextView) findViewById(R.id.tv_message_num_badge);

        initService();

        mCurrentTabIndex = 0;
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
                unreadMsgService = ((UnreadMsgService.SimpleBinder) service).getService();
            }
        };

        bindService(new Intent(MainActivity.this, UnreadMsgService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_BADGE));
        if (null != unreadMsgService) {
            unreadMsgService.startCount();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        unreadMsgService.reset();
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unreadMsgService.startCount();
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private void changeFragment(int tabIndex) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {
            if (null == mTabFragmentArr[tabIndex]) {
                mTabFragmentArr[tabIndex] = (Fragment) mTabFragmentClassArr[tabIndex].newInstance();
            }
            fragmentTransaction.replace(R.id.ll_fragment_container, mTabFragmentArr[tabIndex]);
        } catch (Exception e) {
            Log.e(TAG, "MainActivity changeFragment get a new Fragment failed");
        }
        fragmentTransaction.commit();
//        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 将选中的TabIcon设置为获取焦点状态，其他设置为普通状态
     *
     * @param tabIndex 选中的index
     */
    private void focusTapedIcon(int tabIndex) {
        for (int i = 0; i < mNormalTabIconDrawableIds.length; i++) {
            if (i != tabIndex) {
                mIconViewArr[i].setImageResource(mNormalTabIconDrawableIds[i]);
            } else {
                mIconViewArr[i].setImageResource(mFocusedTabIconDrawableIds[i]);
            }
        }
    }


    public static final String ACTION_UPDATE_BADGE = "action_update_badge";  //更新badge的广播
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if ("0".equals(type)) {
                int count = intent.getIntExtra("count", 0);
                iv_campus_badge.setVisibility(count > 0 ? View.VISIBLE : View.INVISIBLE);
            } else if ("1".equals(type)) {
                int count = intent.getIntExtra("count", 0);
                if (count > 0) {
                    tv_message_num_badge.setVisibility(View.VISIBLE);
                    tv_message_num_badge.setText(String.valueOf(count));
                } else {
                    tv_message_num_badge.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    public void startCount() {
        unreadMsgService.startCount();
    }

    public void showUnread() {
        unreadMsgService.showUnread();
    }

    public void reset() {
        unreadMsgService.markAsRead();
        unreadMsgService.reset();
    }
}
