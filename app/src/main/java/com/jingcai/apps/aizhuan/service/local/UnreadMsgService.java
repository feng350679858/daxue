package com.jingcai.apps.aizhuan.service.local;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jingcai.apps.aizhuan.activity.index.MainActivity;

public class UnreadMsgService extends Service {
    private final String TAG = UnreadMsgService.class.getSimpleName();
    public static final int REQUEST_INTERVAL = 1 * 1000;

    public class SimpleBinder extends Binder {
        public UnreadMsgService getService() {
            return UnreadMsgService.this;
        }
    }

    public SimpleBinder sBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "---------create-----------");
        sBinder = new SimpleBinder();
        new Thread(unreadMsgTask).start();
//        new Thread(task2).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "---------onBind-----------");
        return sBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean b = super.onUnbind(intent);
        Log.d(TAG, "---------onUnbind-----------");
        return b;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "---------onDestroy-----------");
        synchronized (this){
            unreadMsgTask.shutdownFlag = true;
            notify();
        }
        super.onDestroy();
    }

    private UnreadMsgTask unreadMsgTask = new UnreadMsgTask();

    private class UnreadMsgTask implements Runnable {
        private volatile boolean shutdownFlag = false;
        private volatile boolean waitFlag = true;
        private volatile int count = 0;

        @Override
        public void run() {
//            while (true) {
//                try {
//                    if (!waitFlag) {
//                        //获取远程数据
//                        boardCastCount("1", count ++);
//                    }
//                    Thread.sleep(REQUEST_INTERVAL);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (shutdownFlag) {
//                    break;
//                }
//            }

            while(!shutdownFlag){
                try {
                    if (waitFlag) {
                        synchronized (this) {
                            wait();
                        }
                    }else{
                        //获取远程数据
                        boardCastCount("1", count ++);
                        Thread.sleep(REQUEST_INTERVAL);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void boardCastCount(String type, int count) {
        Intent intent = new Intent(MainActivity.ACTION_UPDATE_BADGE);
        intent.putExtra("type", type);
        intent.putExtra("count", count);
        sendBroadcast(intent);
    }

    public void startCount() {
        synchronized (unreadMsgTask){
            unreadMsgTask.waitFlag = false;
            unreadMsgTask.notify();
        }
    }

    public void reset() {
        synchronized (unreadMsgTask){
            boardCastCount("1", unreadMsgTask.count = 0);
            unreadMsgTask.waitFlag = true;
            unreadMsgTask.notify();
        }
    }

    public void showUnread() {
        boardCastCount("0", 1);
    }
    public void markAsRead() {
        boardCastCount("0", 0);
    }
}