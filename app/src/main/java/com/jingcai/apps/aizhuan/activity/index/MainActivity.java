package com.jingcai.apps.aizhuan.activity.index;

import android.app.ActivityManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragmentActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDeployActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpWendaDeployActivity;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexCampusFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMessageFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMineFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMoneyFragment;
import com.jingcai.apps.aizhuan.activity.mine.ProfileImproveActivity;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.local.UnreadMsgService;
import com.jingcai.apps.aizhuan.util.HXHelper;
import com.jingcai.apps.aizhuan.util.PopupWin;

import java.util.List;

/**
 * Created by Json Ding on 2015/7/9.
 */
public class MainActivity extends BaseFragmentActivity {
    private static final String TAG = "MainActivity";
    private static MainActivity instance;
    //Tab相关
    private LinearLayout mLlCampus, mLlMessgage, mLlMoney, mLlMine;
    private ImageButton mBtnRelease;
    private int mCurrentTabIndex;

    private final int[] mNormalTabIconDrawableIds = {R.drawable.icon_index_tab_campus_normal, R.drawable.icon_index_tab_message_normal, R.drawable.icon_index_tab_money_normal, R.drawable.icon_index_tab_mine_normal};
    private final int[] mFocusedTabIconDrawableIds = {R.drawable.icon_index_tab_campus_focused, R.drawable.icon_index_tab_message_focused, R.drawable.icon_index_tab_money_focused, R.drawable.icon_index_tab_mine_focused};
    private final ImageView[] mIconViewArr = new ImageView[mNormalTabIconDrawableIds.length];

    private final Class[] mTabFragmentClassArr = {IndexCampusFragment.class, IndexMessageFragment.class, IndexMoneyFragment.class, IndexMineFragment.class};
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

            if (v.getId() == R.id.ll_message && !Preferences.getInstance(Preferences.TYPE.guide).getBoolean(Preferences.Guide.PARAM_GUIDE_SYS_MESSAGE, false)) {
                final View stub_guide_message = ((ViewStub) findViewById(R.id.stub_guide_message)).inflate();
                stub_guide_message.findViewById(R.id.iv_guide_message).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Preferences.getInstance(Preferences.TYPE.guide).update(Preferences.Guide.PARAM_GUIDE_SYS_MESSAGE, true);
                        stub_guide_message.setVisibility(View.GONE);
                    }
                });
            }
        }
    };
    private View.OnClickListener releaseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            View parentView = MainActivity.this.getWindow().getDecorView();
            View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.index_release_fragment, null);

            final PopupWin selfdeftimeWin = PopupWin.Builder.create(MainActivity.this)
                    .setParentView(parentView)
                    .setContentView(contentView)
                    .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .build();
            String html = "正有<font color='red'>100+位</font>在线帮助您";
            ((TextView) selfdeftimeWin.findViewById(R.id.tv_school_num)).setText(android.text.Html.fromHtml(html));
            selfdeftimeWin.setAction(R.id.btn_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selfdeftimeWin.dismiss();
                }
            }).setAction(R.id.layout_wenda, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, HelpWendaDeployActivity.class);
                    startActivity(intent);
                    selfdeftimeWin.dismiss();
                }
            }).setAction(R.id.layout_jishi, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, HelpJishiDeployActivity.class);
                    startActivity(intent);
                    selfdeftimeWin.dismiss();
                }
            });
            selfdeftimeWin.show();
        }
    };
    private ImageView iv_campus_badge;
    private ImageView iv_message_badge;
    private View stub_guide_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.main_activity);
        initView();
    }

    private void isLevel() {
        String level = UserSubject.getLevel();
        Log.i(TAG, level);
        if ("0".equals(level)) {
            Intent intent = new Intent(MainActivity.this, ProfileImproveActivity.class);
            startActivity(intent);
        }
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
        mBtnRelease.setOnClickListener(releaseClickListener);

        mIconViewArr[0] = (ImageView) findViewById(R.id.iv_campus);
        mIconViewArr[1] = (ImageView) findViewById(R.id.iv_message);
        mIconViewArr[2] = (ImageView) findViewById(R.id.iv_money);
        mIconViewArr[3] = (ImageView) findViewById(R.id.iv_mine);

        iv_campus_badge = (ImageView) findViewById(R.id.iv_campus_badge);
        iv_message_badge = (ImageView) findViewById(R.id.iv_message_badge);

        initService();

        mCurrentTabIndex = 0;
        //将点击的Icon设置为focused状态，其他设置为normal状态
        focusTapedIcon(mCurrentTabIndex);
        //切换当前显示的Fragment到指定的fragment
        changeFragment(mCurrentTabIndex);

        if (!Preferences.getInstance(Preferences.TYPE.guide).getBoolean(Preferences.Guide.PARAM_GUIDE_MAIN_ACTIVITY, false)) {
            stub_guide_view = ((ViewStub) findViewById(R.id.stub_guide)).inflate();
            stub_guide_view.findViewById(R.id.iv_guide_online).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideGuide();
                }
            });
            stub_guide_view.findViewById(R.id.iv_guide_deploy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideGuide();
                }
            });
        }
    }

    private void hideGuide() {
        Preferences.getInstance(Preferences.TYPE.guide).update(Preferences.Guide.PARAM_GUIDE_MAIN_ACTIVITY, true);
        stub_guide_view.setVisibility(View.GONE);
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                unreadMsgService = ((UnreadMsgService.SimpleBinder) service).getService();
                unreadMsgService.startCount();
            }
        };

        bindService(new Intent(MainActivity.this, UnreadMsgService.class), serviceConnection, Context.BIND_AUTO_CREATE);


//        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_BADGE));
        HXHelper.getInstance().regNewMessageReceiver(this, broadcastReceiver, 3);
    }

    @Override
    protected void onResume() {
        Log.d("==", "--------------onResume-----");
        super.onResume();
        isLevel();//判断是否完善资料
    }

    @Override
    protected void onPause() {
        Log.d("==", "--------------onPause-----");
        super.onPause();

    }

    @Override
    protected void onStart() {
        Log.d("==", "--------------onStart-----");
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_BADGE));
        HXHelper.getInstance().regNewMessageReceiver(this, broadcastReceiver, 3);
        if (null != unreadMsgService) {
            unreadMsgService.startCount();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("==", "--------------onStop-----");
        if (!isAppOnForeground()) {//app 进入后台
            Log.d("==", "--------------进入后台-----");
            unreadMsgService.freezeCount();
        }
        unregisterReceiver(mReceiver);
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("==", "--------------onDestroy-----");
        unreadMsgService.startCount();
        unbindService(serviceConnection);
        super.onDestroy();
    }

    public static void logout(){
        if(null != instance){
            instance.finish();
        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the device
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
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
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if ("0".equals(type)) {
                int count = intent.getIntExtra("count", 0);
                iv_campus_badge.setVisibility(count > 0 ? View.VISIBLE : View.INVISIBLE);
            } else if ("1".equals(type)) {
                int count = intent.getIntExtra("count", 0);
                iv_message_badge.setVisibility(count > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        }
    };

    /**
     * 新消息广播接收器
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //收到未读消息，显示未读提示
            Log.d(TAG, "MainActivity receive a new Message from " + intent.getStringExtra("from"));
            if (HXHelper.getInstance().getAllUnreadMsgCount() > 0) {
                showUnread("1");
            }
        }
    };

    public void startCount() {
        unreadMsgService.startCount();
    }

    public void freezeCount(String type) {
        unreadMsgService.freezeCount();
    }

    /**
     * 显示未读
     *
     * @param type
     */
    public void showUnread(String type) {
        unreadMsgService.showUnread(type);
    }

    /**
     * 清除未读
     *
     * @param type
     */
    public void markAsRead(String type) {
        unreadMsgService.markAsRead(type);
    }
}
