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

import com.jingcai.apps.aizhuan.R;

/**
 * 弹出指定布局的dialog,布局的gravity确定其显示的位置
 * Created by Json Ding on 2015/5/6.
 */
public class PopupDialog  {
    private Context mContext;
    private View mContentView;
    private Dialog mDialog;

    public PopupDialog(Context context,@LayoutRes int layoutId){
        this(context, layoutId, true);
    }
    public PopupDialog(Context context,@LayoutRes int layoutId, boolean cancelable){
        this(context, layoutId, cancelable, true);
    }
    public PopupDialog(Context context,@LayoutRes int layoutId, boolean cancelable, boolean wrapContent){
        mContext = context;
        mContentView = LayoutInflater.from(context).inflate(layoutId, null);
        init(cancelable, wrapContent);
    }

    private void init(boolean cancelable, boolean wrapContent){
        mDialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
//        mDialog.setContentView(mContentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mDialog.setContentView(mContentView);
        Window window = mDialog.getWindow();
        // 设置显示动画;
        window.setWindowAnimations(R.style.main_menu_animstyle);
        Point point = new Point();
        ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = point.y;
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = wrapContent?ViewGroup.LayoutParams.WRAP_CONTENT:ViewGroup.LayoutParams.MATCH_PARENT;
        // 设置显示位置
        mDialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        mDialog.setCanceledOnTouchOutside(cancelable);
    }

    public void show(){
        mDialog.show();
    }

    public void dismiss(){
        mDialog.dismiss();
    }

    public View getContentView(){
        return mContentView;
    }

    public PopupDialog setAction(@IdRes int viewId, View.OnClickListener clickListener){
        View view = findViewById(viewId);
        if(null != view){
            view.setOnClickListener(clickListener);
        }
        return this;
    }

    public View findViewById(@IdRes int viewId) {
        return mContentView.findViewById(viewId);
    }
}
