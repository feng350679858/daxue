package com.jingcai.apps.aizhuan.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.util.AppUtil;
import com.jingcai.apps.aizhuan.util.AudioDialog;
import com.jingcai.apps.aizhuan.util.AudioManager;


/**
 * Created by Json Ding on 2015/7/3.
 */
public class AudioRecordButton extends Button {

    private static final String TAG = "AudioRecordButton";
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;
    public static final int MAX_LEVEL = 7;  //音量等级
    public static final float MIN_RECORD_TIME = 0.6f;   //最小录音时间
    public static final int DIALOG_DISMISS_DELAY_MILLIS = 1300;  //录音时间过短，dialog消失延迟时间
    public static final String DIR = Environment.getExternalStorageDirectory() + "/aizhuan/im";  //存储目录

    private int mCurrentState = STATE_NORMAL;
    private boolean mIsRecording;
    private float mTime;
    private boolean mReady;  //是否触发onLongClick

    private AudioDialog mAudioDialog;
    private AudioManager mAudioManager;
    private Context mContext;

    public AudioRecordButton(Context context) {
        this(context, null);
        init(context);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public interface AudioFinishRecordListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecordListener mAudioFinishRecordListener;

    public void setAudioFinishRecordListener(AudioFinishRecordListener audioFinishRecordListener){
        mAudioFinishRecordListener = audioFinishRecordListener;
    }

    private void init(Context context) {
        mContext = context;
        mAudioDialog = new AudioDialog(mContext);

        mAudioManager = AudioManager.getInstance(DIR);
        mAudioManager.setOnAudioPreparedListener(new AudioManager.OnAudioPreparedListener() {
            @Override
            public void prepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(!AppUtil.isSdcardExisting()){
                    Toast.makeText(mContext,"请挂载SDCard后重试",Toast.LENGTH_SHORT).show();
                    return false;
                }
                mAudioManager.prepareAudio();
                mReady = true;

                return true;
            }
        });
    }

    private static final int MSG_AUDIO_PREPARED = 0X1;
    private static final int MSG_AUDIO_CHANGED = 0X2;
    private static final int MSG_AUDIO_DISMISS = 0X3;

    /**
     * 获取音量大小的runnable
     */
    private Runnable mGetVolLevelRunnable = new Runnable() {
        @Override
        public void run() {
            if(!mIsRecording){
                Thread.interrupted();
            }
            while (mIsRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_AUDIO_CHANGED);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();

                }
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //在prepare之后进行显示
                    mIsRecording = true;
                    mAudioDialog.showRecodingDialog();
                    new Thread(mGetVolLevelRunnable).start();
                    break;
                case MSG_AUDIO_CHANGED:
                    int voiceLevel = mAudioManager.getVoiceLevel(MAX_LEVEL);
                    mAudioDialog.updateLevel(voiceLevel);
                    break;
                case MSG_AUDIO_DISMISS:
                    mAudioDialog.dismissDialog();
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //改变为开始录音的状态
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_UP:
                //松开的时候，如果正在录音
                if (!mReady) {  //没有触发LongClick
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!mIsRecording || mTime < MIN_RECORD_TIME) {  //AudioManager没有prepare,时间没有超过最低录制时间
                    mAudioDialog.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_AUDIO_DISMISS, DIALOG_DISMISS_DELAY_MILLIS);
                } else if (mCurrentState == STATE_RECORDING) {  //正常录制结束
                    mAudioDialog.dismissDialog();
                    mAudioManager.release(new AudioManager.AudioReleaseCallback() {
                        @Override
                        public void released(String path) {
                            if (mAudioFinishRecordListener != null)
                                mAudioFinishRecordListener.onFinish(mTime, path);
                        }
                    });
                } else if (mCurrentState == STATE_WANT_CANCEL) { //如果准备取消
                    mAudioManager.cancel();
                    mAudioDialog.dismissDialog();
                }

                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                //如果正在录音
                if (mIsRecording) {
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    //恢复状态及标志位
    private void reset() {
        mIsRecording = false;
        mReady = false;
        changeState(STATE_NORMAL);
        mTime = 0f;
    }

    private boolean wantToCancel(int x, int y) {
        return x < 0 || y < 0 || x > getWidth() || y > getHeight();
    }

    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.chat_voice_btn_bg_normal);
                    setText("按住 说话");
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.chat_voice_btn_bg_pressed);
                    setText("松开 结束");
                    if (mIsRecording) {
                        mAudioDialog.recording();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.chat_voice_btn_bg_pressed);
                    setText("松开手指，取消发送");
                    if (mIsRecording) {
                        mAudioDialog.wantToCancel();
                    }
                    break;
            }
        }
    }
}
