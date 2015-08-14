package com.jingcai.apps.aizhuan.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

import java.util.Properties;

/**
 * Created by lejing on 15/4/28.
 */
public class AppUtil {

    public static void loadProperties(Context context) {
        Properties props = new Properties();
        try {
            int id = context.getResources().getIdentifier("project", "raw", context.getPackageName());
            props.load(context.getResources().openRawResource(id));
        } catch (Exception e) {
            Log.e("-------", "找不到配置文件.", e);
        }
        GlobalConstant.debugFlag = "true".equalsIgnoreCase(props.getProperty("debugFlag"));

        GlobalConstant.azserverUrl = props.getProperty("azserver_url", "");
        GlobalConstant.imageUrl = props.getProperty("image_url", "");
        GlobalConstant.h5Url = props.getProperty("h5_url", "");
        GlobalConstant.weixinUrl = props.getProperty("weixin_url", "");

        GlobalConstant.packageName = context.getPackageName();
        PackageInfo packageInfo = getPackageInfo(context);
        if(null != packageInfo) {
            GlobalConstant.versionCode = packageInfo.versionCode;
            GlobalConstant.versionName = packageInfo.versionName;
        }
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            GlobalConstant.SECRET_KEY = String.valueOf(appInfo.metaData.get("SECRET_KEY"));
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否存在sd卡
     * @return
     */
    public static boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    public static boolean isApkInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
