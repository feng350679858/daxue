package com.jingcai.apps.aizhuan.util;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 用于录音
 * Created by Json Ding on 2015/7/3.
 */
public class AudioManager {
    private static final String TAG = "AudioManager";
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;
    private boolean isPrepared;

    private static AudioManager mInstance;

    private AudioManager(String dir){mDir = dir;}

    public interface OnAudioPreparedListener{
        void prepared();
    }

    public OnAudioPreparedListener mOnAudioPreparedListener;

    public void setOnAudioPreparedListener(OnAudioPreparedListener OnAudioPreparedListener) {
        this.mOnAudioPreparedListener = OnAudioPreparedListener;
    }

    public static AudioManager getInstance(String dir){
        if(mInstance == null){
            synchronized (AudioManager.class){
                if(mInstance == null) {
                    mInstance = new AudioManager(dir);

                }
            }
        }
        return mInstance;
    }

    public void prepareAudio(){
        isPrepared = false;
        File dir = new File(mDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String fileName = generateFileName();
        File file = new File(dir,fileName);
        mCurrentFilePath = file.getAbsolutePath();

        mMediaRecorder = new MediaRecorder();
        //设置输出文件
        mMediaRecorder.setOutputFile(mCurrentFilePath);
        //设置mediaRecord的音频源为mic
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音频格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //设置音频编码
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared = true;
            if(null != mOnAudioPreparedListener) mOnAudioPreparedListener.prepared();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"AudioManager prepare() failed!");
        }

    }

    private String generateFileName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString()+".amr";
    }

    public int getVoiceLevel(int maxLevel){
        if(isPrepared && mMediaRecorder!=null){
            try {
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.e(TAG,"audio source hasn't been set");
            }
        }
        return 1;
    }

    public void release(AudioReleaseCallback audioReleaseCallback){
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        if(null != audioReleaseCallback) audioReleaseCallback.released(mCurrentFilePath);
        mMediaRecorder = null;
    }

    public void cancel(){
        release(null);
        if(mCurrentFilePath != null){
            File file = new File(mCurrentFilePath);
            file.delete();
        }
    }

    public interface AudioReleaseCallback{
        void released(String path);
    }

}
