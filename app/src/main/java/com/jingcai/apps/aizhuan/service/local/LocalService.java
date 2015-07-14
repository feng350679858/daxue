package com.jingcai.apps.aizhuan.service.local;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

public class LocalService extends Service {
    public static final int REQUEST_INTERVAL = 1 * 1000;

    public class SimpleBinder extends Binder {
        /**
         * 获取 Service 实例
         *
         * @return
         */
        public LocalService getService() {
            return LocalService.this;
        }

        public int add(int a, int b) {
            return a + b;
        }

        public void startCount() {
            unreadMsgTask.resume();
        }

        public void reset() {
            unreadMsgTask.reset();
        }

        public void showUnread() {

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
            boardCastCount(count = 0);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (!waitFlag) {
                        //获取远程数据
                        boardCastCount(count += 2);
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

    private void boardCastCount(int count) {
        Intent intent = new Intent(GlobalConstant.ACTION_UPDATE_BADGE);
        intent.putExtra("count", count);
        LocalService.this.sendBroadcast(intent);
    }
}