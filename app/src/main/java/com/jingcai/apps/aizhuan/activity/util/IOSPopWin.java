package com.jingcai.apps.aizhuan.activity.util;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by Json Ding on 2015/8/4.
 */
public class IOSPopWin {
    private static final String TAG = "IOSPopWin";
    private PopupWin mIOSWin;
    private Activity mContext;
    private ConfirmButtonClickListener mListener;

    private TextView mTvTitle;
    private TextView mTvContent;
    private Button mCancelButton;
    private Button mConfirmButton;

    public IOSPopWin(Activity context){
        mContext = context;
    }

    public void setConfirmButtonClickListener(ConfirmButtonClickListener listener){
        mListener = listener;
    }

    /**
     * 参数必须大于3个，否则不会起作用.
     * 3个参数时，唯一的一个按钮为dismiss
     * 4个参数时，callBack才起作用
     *
     * args[0]  标题
     * args[1]  内容
     * args[2]  左按钮文字
     * args[3]  右按钮文字
     *
     * @param args
     */
    public void showWindow(String... args) {
        if(args.length < 3){
            Log.w(TAG, "Length can't below 3 In ios dialog");
            return;
        }
        if(null == mIOSWin) {
            final View decorView = mContext.getWindow().getDecorView();
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_withdraw_validate_tip, null);
            mIOSWin = PopupWin.Builder.create(mContext)
                    .setParentView(decorView)
                    .setContentView(contentView)
                    .build();
            mTvContent = (TextView) contentView.findViewById(R.id.tv_ios_dialog_content);
            mTvTitle = (TextView) contentView.findViewById(R.id.tv_ios_dialog_title);
            mCancelButton = (Button) contentView.findViewById(R.id.btn_ios_dialog_cancel);
            mConfirmButton = (Button) contentView.findViewById(R.id.btn_ios_dialog_confirm);
            if(args.length >= 3){

                mTvTitle.setText(args[0]);
                mTvContent.setText(args[1]);
                mCancelButton.setText(args[2]);
                if(args.length ==3){
                    mConfirmButton.setVisibility(View.GONE);
                    mCancelButton.setBackgroundResource(R.drawable.btn_white_bottom_radius_bg);
                }
                if(args.length == 4){
                    mConfirmButton.setText(args[3]);
                }
            }

            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIOSWin.dismiss();
                }
            });
            mConfirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   mListener.onConfirmButtonClick();
                }
            });
        }

        mIOSWin.show(Gravity.CENTER, 0, 0);
    }

    public void dismiss(){
        mIOSWin.dismiss();
    }

    public interface ConfirmButtonClickListener{
        void onConfirmButtonClick();
    }
}
