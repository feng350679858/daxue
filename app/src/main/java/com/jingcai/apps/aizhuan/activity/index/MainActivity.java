package com.jingcai.apps.aizhuan.activity.index;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragmentActivity;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMessageFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMineFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexMoneyFragment;
import com.jingcai.apps.aizhuan.activity.index.fragment.IndexCampusFragment;

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

    //TabIcon未获取焦点的情况下的DrawableId
    private final int[] mNormalTabIconDrawableIds = {R.drawable.icon_index_tab_campus_normal,R.drawable.icon_index_tab_message_normal,R.drawable.icon_index_tab_money_normal,R.drawable.icon_index_tab_mine_normal};
    //TabIcon获取焦点的情况下的DrawableId
    private final int[] mFocusedTabIconDrawableIds = {R.drawable.icon_index_tab_campus_focused,R.drawable.icon_index_tab_message_focused,R.drawable.icon_index_tab_money_focused,R.drawable.icon_index_tab_mine_focused};
    //Fragment的Class
    private final Class[] mTabFragments = {IndexCampusFragment.class, IndexMessageFragment.class, IndexMoneyFragment.class, IndexMineFragment.class};
    //TabIcon的ImageView对象
    private final ImageView[] mIconViews = new ImageView[mNormalTabIconDrawableIds.length];

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
    }

    private void initTabIconViews() {
        mIconViews[0] = (ImageView) findViewById(R.id.iv_campus);
        mIconViews[1] = (ImageView) findViewById(R.id.iv_message);
        mIconViews[2] = (ImageView) findViewById(R.id.iv_money);
        mIconViews[3] = (ImageView) findViewById(R.id.iv_mine);
    }

    private void changeFragment(int tabIndex) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        try {
            fragmentTransaction.replace(R.id.ll_fragment_container,(Fragment)mTabFragments[tabIndex].newInstance());
        } catch (Exception e) {
            Log.e(TAG,"MainActivity changeFragment get a new Fragment failed");
        }
        fragmentTransaction.commit();
    }

    /**
     * 将选中的TabIcon设置为获取焦点状态，其他设置为普通状态
     * @param tabIndex 选中的index
     */
    private void focusTapedIcon(int tabIndex) {
        for (int i = 0; i < mNormalTabIconDrawableIds.length; i++) {
            if(i != tabIndex){
                mIconViews[i].setImageResource(mNormalTabIconDrawableIds[i]);
            }else{
                mIconViews[i].setImageResource(mFocusedTabIconDrawableIds[i]);
            }
        }
    }
}
