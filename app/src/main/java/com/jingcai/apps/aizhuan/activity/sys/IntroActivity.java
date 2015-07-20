package com.jingcai.apps.aizhuan.activity.sys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.jingcai.apps.aizhuan.R;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends Activity {
    private ViewPager viewPager;// viewpager
    private List<View> viewList = new ArrayList<View>();// 把需要滑动的页卡添加到这个list中
    //	private ViewGroup group;
//	private ImageView[] imageDots;
    private Button btn_start;
    private static final int[] pics = {R.drawable.sys_intro_1, R.drawable.sys_intro_2, R.drawable.sys_intro_3};// 引导图片资源

    private GestureDetector gestureDetector; // 用户滑动
    /**
     * 记录当前分页ID
     */
    private int flaggingWidth;// 互动翻页所需滚动的长度是当前屏幕宽度的1/3


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_intro);
        initView();
        btn_start = (Button) findViewById(R.id.start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator anim = ObjectAnimator.ofFloat(btn_start.getParent(), "alpha", 1.0F, 0F).setDuration(500);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        gotoMainActivity();
                    }
                });
                anim.start();
            }
        });
    }

    private void initView() {
        gestureDetector = new GestureDetector(this, new GuideViewTouch());

        // 获取分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        flaggingWidth = dm.widthPixels / 4;

        viewPager = (ViewPager) findViewById(R.id.img_advert);
        // 初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(IntroActivity.this);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(pics[i]);
            viewList.add(iv);
        }
        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    private class GuideViewTouch extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (currentItem == viewList.size() - 1) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())
                        && (e1.getX() - e2.getX() <= (-flaggingWidth) || e1
                        .getX() - e2.getX() >= flaggingWidth)) {
                    if (e1.getX() - e2.getX() >= flaggingWidth) {
                        gotoMainActivity();
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 指引页面Adapter
     */
    class GuidePageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
            arg0.removeView(viewList.get(arg1));
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            arg0.addView(viewList.get(arg1));
            return viewList.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup arg0) {
        }

        @Override
        public void finishUpdate(ViewGroup arg0) {
        }
    }

    /**
     * 指引页面改监听器
     */
    class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            currentItem = arg0;
            if (arg0 == (viewList.size() - 1)) {
                btn_start.setVisibility(View.VISIBLE);
            } else {
                btn_start.setVisibility(View.GONE);
            }
        }
    }

    int currentItem = 0;

    private void gotoMainActivity() {
        // 跳转界面
        overridePendingTransition(R.anim.right_2_left, 0);
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentItem > 0) {
                viewPager.setCurrentItem(--currentItem);
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
