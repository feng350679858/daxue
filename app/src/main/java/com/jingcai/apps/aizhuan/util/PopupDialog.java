package com.jingcai.apps.aizhuan.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lejing on 15/7/15.
 */
public class PopupDialog {
    private Context mctx;
    private View mParentView;
    private View mContentView;
    private Dialog popupWindow;
    private int mWidth, mHeight;
    private int mAnimstyle;
    private boolean mFocusable;

    private PopupDialog(View view1, View view2) {
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
        private int width = ViewGroup.LayoutParams.MATCH_PARENT;
        private int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        private int animstyle = 0;
        private boolean focusable = false;

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

        public Builder setContentViewLayout(int layoutId) {
            this.contentView = layoutInflater.inflate(layoutId, null);
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setAnimstyle(int animstyle) {
            this.animstyle = animstyle;
            return this;
        }

        public Builder setFocusable(boolean focusable) {
            this.focusable = focusable;
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

        public PopupDialog build() {
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

            final PopupDialog win = new PopupDialog(parentView, contentView);
            win.mctx = context;
            win.mWidth = width;
            win.mHeight = height;
            win.mAnimstyle = animstyle;
            win.mFocusable = focusable;

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
            private PopupDialog win;
            private int mResourceId;

            public PopItemAdapter(Context context, PopupDialog win, int textViewResourceId) {
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
                        if (null != win) {
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

    public static Rect getViewAbsoluteLocation(View view) {
        if (view == null) {
            return new Rect();
        }
        // 获取View相对于屏幕的坐标
        int[] location = new int[2];
        view.getLocationOnScreen(location);//这是获取相对于屏幕的绝对坐标，而view.getLocationInWindow(location); 是获取window上的相对坐标，本例中只有一个window，二者等价
        // 获取View的宽高
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        // 获取View的Rect
        Rect rect = new Rect();
        rect.left = location[0];
        rect.top = location[1];
        rect.right = rect.left + width;
        rect.bottom = rect.top + height;
        return rect;
    }

    //参考：http://blog.csdn.net/johnny901114/article/details/7839512
    private int[] calcPopupXY(View rootView, View anchor) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        rootView.measure(w, h);
        int popupWidth = rootView.getMeasuredWidth();
        int popupHeight = rootView.getMeasuredHeight();
        Rect anchorRect = getViewAbsoluteLocation(anchor);
        int x = anchorRect.left + (anchorRect.right - anchorRect.left) / 2 - popupWidth / 2;
        int y = anchorRect.top - popupHeight;
        return new int[]{x, y};
    }

    private void build() {
        mContentView.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
        popupWindow = new Dialog(mctx);
        if (!mFocusable) {
            popupWindow.setCanceledOnTouchOutside(false);// 设置点击外围解散
            popupWindow.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
        }

        Window window = popupWindow.getWindow();
        //去掉默认的背景,下面两个都可以
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //动画，从下往上
        if (0 != mAnimstyle) {
            window.setWindowAnimations(mAnimstyle);
        }
//        popupWindow.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        //popupWindow.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //http://stackoverflow.com/questions/12348405/dialog-is-bigger-than-expected-when-using-relativelayout
        popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);//dialog默认都是有title的，去掉标题，否则会影响高度计算，一定要在setContentView之前调用，终于明白有一个设置theme的构造函数的目的了
        popupWindow.setContentView(mContentView);

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = mWidth;//WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = mHeight;//WindowManager.LayoutParams.WRAP_CONTENT;
        if (null != mParentView) {
            //计算弹框位置
            int[] xy = calcPopupXY(mContentView, mParentView);
            //gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL.
            //参考: http://www.cnblogs.com/angeldevil/archive/2012/03/31/2426242.html
            window.setGravity(Gravity.LEFT | Gravity.TOP);
            params.x = xy[0];
            params.y = xy[1];
        }
    }

    public void show() {
        popupWindow.show();
    }

    public PopupDialog setAction(@IdRes int viewId, View.OnClickListener clickListener) {
        View view = findViewById(viewId);
        if (null != view) {
            view.setOnClickListener(clickListener);
        }
        return this;
    }

    public View findViewById(@IdRes int viewId) {
        return mContentView.findViewById(viewId);
    }

    public TextView findTextViewById(@IdRes int viewId) {
        return (TextView) mContentView.findViewById(viewId);
    }

    public Button findButtonById(@IdRes int viewId) {
        return (Button) mContentView.findViewById(viewId);
    }

    public interface Callback {
        void select(String key, String val);
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
}