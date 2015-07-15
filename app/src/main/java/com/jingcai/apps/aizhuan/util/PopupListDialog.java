package com.jingcai.apps.aizhuan.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
    private View mContentView;
    private Dialog mDialog;
    private LayoutInflater layoutInflater;

    public PopupListDialog(Context context, Map<String, String> data) {
        this(context, data, true);
    }

    public PopupListDialog(Context context, Map<String, String> data, boolean cancelable) {
        this(context, data, cancelable, true);
    }

    public PopupListDialog(Context context, Map<String, String> data, boolean cancelable, boolean wrapContent) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        mContentView = layoutInflater.inflate(R.layout.help_jishi_pop_gender, null);

        PopItemAdapter adapter = new PopItemAdapter(context, R.layout.help_jishi_pop_gender_item);
        adapter.addAll(convertToList(data));
        ListView listView = (ListView) mContentView.findViewById(R.id.lv_pop_list);
        listView.setAdapter(adapter);

        init(cancelable, wrapContent);
    }

    private List<PopItem> convertToList(Map<String, String> map){
        List<PopItem> list1 = new ArrayList<>();
        for(Map.Entry<String, String> entry:map.entrySet()){
            list1.add(new PopItem(entry.getKey(), entry.getValue()));
        }
        return list1;
    }

    private void init(boolean cancelable, boolean wrapContent) {
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

            return view;
        }
    }

    class PopItem{
        private String key;
        private String val;
        PopItem(String k, String v){
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
}
