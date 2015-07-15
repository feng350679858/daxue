package com.jingcai.apps.aizhuan.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lejing on 15/7/15.
 */
public class PopupWin {
    private View mParentView;
    private View mContentView;
    private PopupWindow popupWindow;

    private PopupWin(View view1, View view2) {
        this.mParentView = view1;
        this.mContentView = view2;
    }

    public static class Builder {
        private Context context;
        private LayoutInflater layoutInflater;
        private View parentView;
        private View contentView;
        private Map<String, String> data;
        private Callback callback;

        private Builder(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        public static Builder create(Context context) {
            return new Builder(context);
        }

        public Builder setParentView(View view) {
            this.parentView = view;
            return this;
        }

        public Builder setContentView(View view) {
            this.contentView = view;
            return this;
        }

        public Builder setData(Map<String, String> data, Callback callback) {
            if (null == data) {
                throw new RuntimeException("data can not be null");
            }
            this.data = data;
            this.callback = callback;
            return this;
        }

        public PopupWin build() {
            if (null == parentView) {
                throw new RuntimeException("you should set setParentView before build");
            }

            View mContentView = null;
            if (null != data) {
                if (data.size() > 3) {
                    mContentView = layoutInflater.inflate(R.layout.popup_list_content_scroll, null);
                } else {
                    mContentView = layoutInflater.inflate(R.layout.popup_list_content_wrap, null);
                }
                this.contentView = mContentView;
            } else if (null == this.contentView) {
                throw new RuntimeException("you should set setContentView or setData before build");
            }

            final PopupWin win = new PopupWin(parentView, contentView);

            if (null != mContentView) {
                PopItemAdapter adapter = new PopItemAdapter(context, win, R.layout.popup_list_item);
                adapter.addAll(convertToList(data));
                ListView listView = (ListView) mContentView.findViewById(R.id.lv_pop_list);
                listView.setAdapter(adapter);

                ImageButton btn_cancel = (ImageButton) mContentView.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        win.dismiss();
                    }
                });
            }
            win.build();
            return win;
        }

        class PopItemAdapter extends ArrayAdapter<PopItem> {
            private PopupWin win;
            private int mResourceId;

            public PopItemAdapter(Context context, PopupWin win, int textViewResourceId) {
                super(context, textViewResourceId);
                this.win = win;
                this.mResourceId = textViewResourceId;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                PopItem item = getItem(position);

                View view = layoutInflater.inflate(mResourceId, null);
                TextView nameText = (TextView) view.findViewById(R.id.tv_pop_item);

                nameText.setText(item.getVal());
                nameText.setTag(item.getKey());
                nameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != callback) {
                            TextView tv = (TextView) v;
                            callback.select((String) tv.getTag(), tv.getText().toString());
                        }
                        if(null != win) {
                            win.dismiss();
                        }
                    }
                });
                return view;
            }
        }

        class PopItem {
            private String key;
            private String val;

            PopItem(String k, String v) {
                this.key = k;
                this.val = v;
            }

            public String getKey() {
                return key;
            }

            public String getVal() {
                return val;
            }
        }

        private List<PopItem> convertToList(Map<String, String> map) {
            List<PopItem> list1 = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                list1.add(new PopItem(entry.getKey(), entry.getValue()));
            }
            return list1;
        }
    }

    private void build() {
        popupWindow = new PopupWindow(mContentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //final View decorView = this.getWindow().getDecorView();

        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.main_menu_animstyle);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x80000000));
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ObjectAnimator.ofFloat(mParentView, "alpha", 0.5f, 1.0f).setDuration(500).start();
            }
        });
    }

    public void show() {
        check();
        popupWindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
        ObjectAnimator.ofFloat(mParentView, "alpha", 1.0f, 0.5f).setDuration(500).start();
    }

    public void dismiss() {
        check();
        popupWindow.dismiss();
    }

    public PopupWin setAction(@IdRes int viewId, View.OnClickListener clickListener) {
        check();
        View view = findViewById(viewId);
        if (null != view) {
            view.setOnClickListener(clickListener);
        }
        return this;
    }

    public View findViewById(@IdRes int viewId) {
        check();
        return mContentView.findViewById(viewId);
    }

    private void check() {
        if (null == mContentView) {
            throw new RuntimeException("you should call build first");
        }
    }
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.help_jishi_deploy_endtime_pop, null);
//
//        final PopupWindow popupWindow = new PopupWindow(dialogView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        final View decorView = this.getWindow().getDecorView();
//
//        popupWindow.setFocusable(true);
//        popupWindow.setAnimationStyle(R.style.main_menu_animstyle);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(0x80000000));
//        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        popupWindow.showAtLocation(decorView, Gravity.BOTTOM, 0, 0);
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                ObjectAnimator.ofFloat(decorView, "alpha", 0.5f, 1.0f).setDuration(500).start();
//            }
//        });
//        ObjectAnimator.ofFloat(decorView, "alpha", 1.0f, 0.5f).setDuration(500).start();

    public interface Callback {
        void select(String key, String val);
    }
}