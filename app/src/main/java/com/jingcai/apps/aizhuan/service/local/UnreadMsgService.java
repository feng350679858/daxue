package com.jingcai.apps.aizhuan.service.local;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jingcai.apps.aizhuan.activity.index.MainActivity;
import com.jingcai.apps.aizhuan.util.HXHelper;

public class UnreadMsgService extends Service {
    private final String TAG = UnreadMsgService.class.getSimpleName();
    public static final int REQUEST_INTERVAL = 30 * 1000;

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
        synchronized (this) {
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

            while (!shutdownFlag) {
                try {
                    if (waitFlag) {
                        synchronized (this) {
                            wait();
                        }
                    } else {
                        //获取远程数据
//                        Intent intent = new Intent("aizhuan.activity.help.HelpEvaluateActivity");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                        boardCastCount("1", HXHelper.getInstance().getAllUnreadMsgCount());
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
        synchronized (unreadMsgTask) {
            unreadMsgTask.waitFlag = false;
            unreadMsgTask.notify();
        }
    }

    public void freezeCount() {
        synchronized (unreadMsgTask) {
            boardCastCount("1", unreadMsgTask.count = 0);
            unreadMsgTask.waitFlag = true;
            unreadMsgTask.notify();
        }
    }

    /**
     * 显示一个小红点
     * 类型：0校园 1消息
     * @param type 类型
     */
    public void showUnread(String type) {
        boardCastCount(type, 1);
    }

    /**
     * 消失小红点
     * 类型：0校园 1消息
     * @param type 类型
     */
    public void markAsRead(String type) {
        boardCastCount(type, 0);
    }
}