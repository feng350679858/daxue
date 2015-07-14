package com.jingcai.apps.aizhuan.service.local;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

public class UnreadMsgService extends Service {
    public static final int REQUEST_INTERVAL = 1 * 1000;

    public class SimpleBinder extends Binder {
        public UnreadMsgService getService() {
            return UnreadMsgService.this;
        }

        public void startCount() {
            unreadMsgTask.resume();
        }

        public void reset() {
            unreadMsgTask.reset();
            boardCastCount("0", 0);
        }

        public void showUnread() {
            boardCastCount("0", 1);
        }
    }

    public SimpleBinder sBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        sBinder = new SimpleBinder();
        new Thread(unreadMsgTask).start();
//        new Thread(task2).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private UnreadMsgTask unreadMsgTask = new UnreadMsgTask();

    private class UnreadMsgTask implements Runnable {
        private volatile boolean shutdownFlag = false;
        private volatile boolean waitFlag = true;
        private volatile int count = 0;

        public void resume() {
            waitFlag = false;
        }

        public void reset() {
            waitFlag = true;
            boardCastCount("1", count = 0);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (!waitFlag) {
                        //获取远程数据
                        boardCastCount("1", count ++);
                    }
                    Thread.sleep(REQUEST_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (shutdownFlag) {
                    break;
                }
            }
        }

    }

    private void boardCastCount(String type, int count) {
        Intent intent = new Intent(GlobalConstant.ACTION_UPDATE_BADGE);
        intent.putExtra("type", type);
        intent.putExtra("count", count);
        UnreadMsgService.this.sendBroadcast(intent);
    }
}