package com.jingcai.apps.aizhuan.util;

import android.app.Application;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jingcai.apps.aizhuan.jpush.JpushUtil;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ApplicationEx extends Application {
    private static final String TAG = "ApplicationEx";
    private AzExecutor azExecutor = new AzExecutor();
    private CountDownLatch latch = new CountDownLatch(1);
    //	public static Database database;
    public LocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        VolleyQueueUtil.getInstance().init(getApplicationContext());

        AppUtil.loadProperties(this);//加载配置文件
        Preferences.initClass(this);
        UserSubject.init(Preferences.getInstance());
        HXHelper.getInstance().init(this);  //环信初始化

        //=============定位
        mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationClient.setLocOption(getLocationOption());
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start();
        mLocationClient.requestLocation();
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                }
                new LocateUtil(new LocateUtil.Callback() {
                    @Override
                    public void locateSuccess(LocateUtil.Area cityArea, List<LocateUtil.Area> areaList) {
                        GlobalConstant.getGis().setCityArea(cityArea.getCode(), cityArea.getName());
                        if(areaList.size()>2){
                            GlobalConstant.getGis().setAreacode2(areaList.get(2).getCode());
                        }
                    }
                }).locate();
            }
        });

//		database = new Database(this);
//		database.open();
//
//		Intent intent = new Intent(this, CpService.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startService(intent);

        //=============友盟==================
//		if(GlobalConstant.debugFlag) {
//			MobclickAgent.setDebugMode(GlobalConstant.debugFlag);
//			Log.d("==device", getDeviceInfo(getApplicationContext()));
//		}

        //=============JPush==================
        JpushUtil jpushUtil = new JpushUtil(this);
        jpushUtil.init();

        if (UserSubject.isLogin()) {
            jpushUtil.login(UserSubject.getStudentid());
            HXHelper.getInstance().loginOnEMChatServer(UserSubject.getStudentid());  //环信连接
        }
    }

    @Override
    public void onTerminate() {
//		database.close();
        super.onTerminate();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //61 ： GPS定位成功
            //161： 网络定位定位成功
            if (61 == location.getLocType() || 161 == location.getLocType()) {
                GlobalConstant.gis.resetGis(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()),
                        location.getProvince(), location.getCity(), location.getDistrict());
                if (StringUtil.isNotEmpty(location.getCity())) {
                    latch.countDown();
                    mLocationClient.stop();
                }
            }
            if (GlobalConstant.debugFlag) {
                //Receive Location
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ").append(location.getTime());
                sb.append("\nerror code : ").append(location.getLocType());
                sb.append("\nlatitude : ").append(location.getLatitude());
                sb.append("\nlontitude : ").append(location.getLongitude());
                sb.append("\nradius : ").append(location.getRadius());
                sb.append("\nprovince : ").append(location.getProvince());
                sb.append("\ncity : ").append(location.getCity());
                sb.append("\ncityCode : ").append(location.getCityCode());
                sb.append("\ndistrict : ").append(location.getDistrict());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    sb.append("\nspeed : ").append(location.getSpeed());
                    sb.append("\nsatellite : ").append(location.getSatelliteNumber());
                    sb.append("\ndirection : ").append(location.getDirection());
                    sb.append("\naddr : ").append(location.getAddrStr());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    sb.append("\naddr : ").append(location.getAddrStr());
                    sb.append("\noperationers : ").append(location.getOperators());
                }
                Log.i("==", sb.toString());
            }
        }
    }

    private LocationClientOption getLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        String tempcoor = "bd09ll";//baidu加密经纬度坐标
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(10000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        return option;
    }


//	public static String getDeviceInfo(Context context) {
//		try{
//			org.json.JSONObject json = new org.json.JSONObject();
//			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//			String device_id = tm.getDeviceId();
//
//			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//			String mac = wifi.getConnectionInfo().getMacAddress();
//			json.put("mac", mac);
//			if( TextUtils.isEmpty(device_id) ){
//				device_id = mac;
//			}
//			if( TextUtils.isEmpty(device_id) ){
//				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
//			}
//			json.put("device_id", device_id);
//			return json.toString();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;
//	}

}
