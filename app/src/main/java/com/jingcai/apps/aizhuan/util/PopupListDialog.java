package com.jingcai.apps.aizhuan.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 弹出指定布局的dialog,布局的gravity确定其显示的位置
 * Created by Json Ding on 2015/5/6.
 */
public class PopupListDialog {
    private Context mContext;
    private Map<String, String> data;
    private boolean cancelable = true;
    private boolean wrapContent = true;
    private Callback callback;


    private View mContentView;
    private Dialog mDialog;
    private LayoutInflater layoutInflater;

    public PopupListDialog(Context context) {
        this.mContext = context;
    }

    public PopupListDialog setData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    public PopupListDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public PopupListDialog setWrapContent(boolean wrapContent) {
        this.wrapContent = wrapContent;
        return this;
    }

    public PopupListDialog setCallback(Callback cb) {
        this.callback = cb;
        return this;
    }

    public PopupListDialog build() {
        if (null == data) {
            throw new RuntimeException("please call setData before build");
        }
        if (null == callback) {
            throw new RuntimeException("please call setCallback before build");
        }

        layoutInflater = LayoutInflater.from(mContext);
        mContentView = layoutInflater.inflate(R.layout.help_jishi_pop_gender, null);

        PopItemAdapter adapter = new PopItemAdapter(mContext, R.layout.help_jishi_pop_gender_item);
        adapter.addAll(convertToList(data));
        ListView listView = (ListView) mContentView.findViewById(R.id.lv_pop_list);
        listView.setAdapter(adapter);
        ImageButton btn_cancel = (ImageButton) mContentView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        init();
        return this;
    }

    private List<PopItem> convertToList(Map<String, String> map) {
        List<PopItem> list1 = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            list1.add(new PopItem(entry.getKey(), entry.getValue()));
        }
        return list1;
    }

    private void init() {
        mDialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
//        mDialog.setContentView(mContentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mDialog.setContentView(mContentView);
        Window window = mDialog.getWindow();
        // 设置显示动画;
        window.setWindowAnimations(R.style.main_menu_animstyle);
        Point point = new Point();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = point.y;
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = wrapContent ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT;
        // 设置显示位置
        mDialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        mDialog.setCanceledOnTouchOutside(cancelable);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public View getContentView() {
        return mContentView;
    }

    public PopupListDialog setAction(@IdRes int viewId, View.OnClickListener clickListener) {
        View view = findViewById(viewId);
        if (null != view) {
            view.setOnClickListener(clickListener);
        }
        return this;
    }

    public View findViewById(@IdRes int viewId) {
        return mContentView.findViewById(viewId);
    }


    class PopItemAdapter extends ArrayAdapter<PopItem> {
        private int mResourceId;

        public PopItemAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PopItem user = getItem(position);

            View view = layoutInflater.inflate(mResourceId, null);
            TextView nameText = (TextView) view.findViewById(R.id.tv_pop_item);

            nameText.setText(user.getVal());
            nameText.setTag(user.getKey());
            nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;
                    callback.select((String) tv.getTag(), tv.getText().toString());
                    dismiss();
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

        public void setKey(String key) {
            this.key = key;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }
    }

    public interface Callback {
        void select(String key, String val);
    }
}
