package com.jingcai.apps.aizhuan.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.base.base02.Base02Request;
import com.jingcai.apps.aizhuan.service.business.base.base02.Base02Response;
import com.jingcai.apps.aizhuan.view.processbtn.ProcessButton;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2015/7/17.
 */
public class VersionUtil {
    private static final String APKNAME = "aizhuan.apk";
    private static final String PKG = "com.jingcai.apps.aizhuan";

    private PopupDialog popupDialog;
    private ProcessButton processBtn;

    private Activity activity;
    private MessageHandler messageHandler;
    private AzService azService;
    private final InnerLock actionLock = new InnerLock();

    private String apkUrl;
    private int totalSize;
    private volatile Integer downloadingSize = 0;//下载完成设置为-1
    private DownLoadApkTask downLoadApkTaskTask;

    public VersionUtil(Activity activity) {
        this.activity = activity;
        this.azService = new AzService(activity);
        this.messageHandler = new MessageHandler(activity);
    }

    /**
     * 更新App
     *
     * @param manualCheckFlag 手工检查更新
     */
    public void autoUpdateApp(final boolean manualCheckFlag) {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                checkNewVersion(manualCheckFlag);
            }
        });
    }

    private void checkNewVersion(final boolean manualCheckFlag) {
        Base02Request req = new Base02Request();
        Base02Request.Version version = req.new Version();
        version.setPlatform(GlobalConstant.TERMINAL_TYPE_ANDROID);
        req.setVersion(version);
        azService.doTrans(req, Base02Response.class, new AzService.Callback<Base02Response>() {
            @Override
            public void success(Base02Response resp) {
                ResponseResult result = resp.getResult();
                if (!"0".equals(result.getCode())) {
                    messageHandler.postMessage(3);//获取版本信息出错！
                    return;
                }
                Base02Response.Base02Body body = resp.getBody();
                Base02Response.Base02Body.Version version = body.getVersion();
                if (null == version || "-1".equals(version.getCode())) {//未找到最新版本记录，提示当前最新版本
                    if (manualCheckFlag) {
                        messageHandler.postMessage(4);
                    }
                    return;
                }
                if (getVersionCode(activity) < Integer.parseInt(version.getCode())) {//有更新
                    messageHandler.postMessage(1, version);
                } else if (manualCheckFlag) {//提示当前最新版本
                    messageHandler.postMessage(4);
                }
            }

            @Override
            public void fail(AzException e) {
                messageHandler.postException(e);
            }
        });
    }

    private void showDownloadingDialog() {
        if (null == popupDialog) {
            popupDialog = new PopupDialog(activity, R.layout.sys_setting_update, false, false);
            popupDialog.setAction(R.id.btn_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null != downLoadApkTaskTask){
                        downLoadApkTaskTask.cancel(true);
                    }
                    popupDialog.dismiss();
                }
            });
            processBtn = (ProcessButton) popupDialog.findViewById(R.id.btn_download);
            processBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (processBtn.isReady()) {
                        if (actionLock.tryLock()) {
                            processBtn.animation();
                            downLoadApk();
                        }
                    } else if (processBtn.isLoading()) {
                        downLoadApkTaskTask.cancel(true);
                        processBtn.reset();
                    } else if (processBtn.isFinished()) {
                        if (actionLock.tryLock()) {
                            messageHandler.postMessage(2);
                        }
                    }
                }
            });
        }
        popupDialog.show();
//        processBtn.reset();
        processBtn.animation();
    }

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    final Base02Response.Base02Body.Version version = (Base02Response.Base02Body.Version) msg.obj;
                    apkUrl = version.getUrl();
                    totalSize = -1;
                    if (StringUtil.isNotEmpty(version.getPackagesize())) {
                        try {
                            totalSize = 1024 * Integer.parseInt(version.getPackagesize());//单位k to byte
                        } catch (Exception e) {
                        }
                    }
                    //强制更新
                    if ("1".equals(version.getForceupdate())) {
                        new AlertDialog.Builder(activity).setTitle(R.string.message_title).setMessage(R.string.updapp_force)
                                .setPositiveButton(R.string.OK_text, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (actionLock.tryLock()) {
                                            showDownloadingDialog();
                                            downLoadApk();
                                        }
                                    }
                                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                                    return true;
                                }
                                return false;
                            }
                        }).show();
                    } else {
                        new AlertDialog.Builder(activity).setTitle(R.string.message_title).setMessage(R.string.updapp)
                                .setPositiveButton(R.string.OK_text, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (actionLock.tryLock()) {
                                            showDownloadingDialog();
                                            downLoadApk();
                                        }
                                    }
                                }).setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                    }
                    break;
                }
                case 2: {
                    try {
                        String fileName = getDownloadPath() + APKNAME;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                        activity.startActivity(intent);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        activity.finish();
                        killProcess();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 3: {
                    showStrToast("获取版本信息出错！");
                    break;
                }
                case 4: {
                    showStrToast("当前已经是最新版本！");
                    break;
                }
//				case 5: {
//                    new AlertDialog.Builder(activity).setTitle("提示").setMessage("您的手机时间有误，请检查！")
//                            .setPositiveButton(R.string.OK_text, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface popupDialog, int which) {
//									  WSUtil.setUser(null);
//                                    activity.finish();
//                                    ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
//                                    am.restartPackage(activity.getPackageName());
//                                    android.os.Process.killProcess(android.os.Process.myPid()); // 结束进程
//                                }
//                            }).show();
//                    break;
//                }
            }
        }
    }

    private void downLoadApk() {
        downLoadApkTaskTask = new DownLoadApkTask();
        downLoadApkTaskTask.execute(apkUrl);
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (totalSize > 0) {
                    for (; downloadingSize >= 0 && downloadingSize < totalSize; ) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        downLoadApkTaskTask.updateProgress(downloadingSize * 100 / totalSize);
                    }
                } else {
                    int i = 0;
                    while (downloadingSize >= 0) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        downLoadApkTaskTask.updateProgress(i++);
                        if (i >= 100) {
                            i = 0;
                        }
                    }
                }
            }
        });
    }


    class DownLoadApkTask extends AsyncTask<String, Integer, Integer> {
        public void updateProgress(Integer... values) {
            publishProgress(values);
        }

        @Override
        protected Integer doInBackground(String... params) {
            InputStream in = null;
            FileOutputStream out = null;
            try {
                String downloadPath = getDownloadPath();
                File p = new File(downloadPath);
                if (!p.exists()) {
                    p.mkdirs();
                }
                HttpURLConnection con = (HttpURLConnection) new URL(params[0]).openConnection();
                in = con.getInputStream();
                out = new FileOutputStream(new File(downloadPath + APKNAME));

                byte[] bytes = new byte[1024];
                int count;
                downloadingSize = 0;
                while ((count = in.read(bytes)) != -1) {
                    if (isCancelled()){
                        downloadingSize = -1;
                        break;
                    }
                    out.write(bytes, 0, count);
                    downloadingSize += count;
                }
                out.flush();
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                    if (null != out) {
                        out.close();
                    }
                } catch (IOException e) {
                }
            }
            return downloadingSize;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            processBtn.setupprogress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer s) {
            try {
                //取消下载
                if(downloadingSize < 0) {
                    return;
                }
                downloadingSize = -1;
                processBtn.finalAnimation();
                messageHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (actionLock.tryLock()) {
                            messageHandler.postMessage(2);
                        }
                    }
                }, 3 * 1000);
            } finally {
                actionLock.unlock();
            }
        }
    }

    private void killProcess() {
        ActivityManager activityMan = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> process = activityMan.getRunningAppProcesses();

        int len = process.size();
        for (int i = 0; i < len; i++) {
            if (process.get(i).processName.equals(PKG)) {
                MobclickAgent.onKillProcess(activity);
                android.os.Process.killProcess(process.get(i).pid);
            }
        }
    }

    private String getDownloadPath() {
        String sdDirPath = null;
        String status = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(status)) {
            sdDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/";
        } else {
            sdDirPath = activity.getFilesDir().getAbsolutePath() + "/data/";
        }
        return sdDirPath;
    }

    public void showStrToast(String str) {
        Toast.makeText(activity, str, Toast.LENGTH_LONG).show();
    }
}
