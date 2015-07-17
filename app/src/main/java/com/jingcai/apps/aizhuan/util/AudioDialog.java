package com.jingcai.apps.aizhuan.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;


/**
 * Created by Json Ding on 2015/7/3.
 */
public class AudioDialog {
    private Dialog mDialog;
    private ImageView mIvIcon;
    private ImageView mIvLevel;
    private TextView mTvText;

    private Context mContext;

    public AudioDialog(Context ctx){
        mContext = ctx;
    }

    public void showRecodingDialog(){
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.dialog_conversation_record,null);

        mDialog.setContentView(contentView);
        mIvIcon = (ImageView) contentView.findViewById(R.id.iv_dialog_icon);
        mIvLevel = (ImageView) contentView.findViewById(R.id.iv_dialog_level);
        mTvText = (TextView) contentView.findViewById(R.id.tv_dialog_text);

        mDialog.show();
    }

    public void recording(){
        if(null != mDialog && mDialog.isShowing()){
            mIvIcon.setVisibility(View.VISIBLE);
            mIvLevel.setVisibility(View.VISIBLE);
            mTvText.setVisibility(View.VISIBLE);
            mTvText.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            mIvIcon.setImageResource(R.drawable.recorder);
            mTvText.setText("手指上划，取消发送");
        }
    }

    public void wantToCancel(){
        if(null != mDialog && mDialog.isShowing()){
            mIvIcon.setVisibility(View.VISIBLE);
            mIvLevel.setVisibility(View.GONE);
            mTvText.setVisibility(View.VISIBLE);
            mTvText.setBackgroundColor(mContext.getResources().getColor(R.color.trans_red));
            mIvIcon.setImageResource(R.drawable.cancel);
            mTvText.setText("松开手指，取消发送");
        }
    }

    public void tooShort(){
        if(null != mDialog && mDialog.isShowing()){
            mIvIcon.setVisibility(View.VISIBLE);
            mIvLevel.setVisibility(View.GONE);
            mTvText.setVisibility(View.VISIBLE);
            mTvText.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            mIvIcon.setImageResource(R.drawable.voice_to_short);
            mTvText.setText("录音时间过短");
        }
    }

    /**
     * 根据level设置Dialog显示的音量
     * @param level 1-7
     */
    public void updateLevel(int level){
        int levelResId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
        mIvLevel.setImageResource(levelResId);

    }

    public void dismissDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
