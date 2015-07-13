package com.jingcai.apps.aizhuan.persistence;

import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/4/28.
 */
public class GlobalConstant {
    public static final String TERMINAL_TYPE_ANDROID    = "2";//终端类型，2代表android
    public static final String BUSINESS_TYPE_PARTJOB    = "1";//1兼职
    public static final String AREA_CODE_HANGZHOU       = "330100";//杭州区域编码
    public static final String AREA_NAME_HANGZHOU       = "杭州市";

    public static final String ACTION_UPDATE_BADGE = "action_update_badge";  //更新badge的广播

    public static String SECRET_KEY;

    public static boolean debugFlag = true;
    public static String packageName;
    public static int versionCode = 1;
    public static String versionName = "1.0";
    public static String azserverUrl = null;//http://192.168.0.18:9082/azserver
    public static String weixinUrl = null;//http://weixin.izhuan365.com
    public static String imageUrl = null;//http://192.168.0.18:9080
    public static String h5Url = null;//http://192.168.0.18:9080

    public static int PAGE_SIZE = 10;
    public static Gis gis = new Gis();

    public static Gis getGis() {
        return gis;
    }

    public static class Gis{
        private String gisx, gisy;
        private String provincename;
        private String cityname;
        private String districtname;
        private String areacode;
        private String areaname;
        private String areacode2;

        public String getGisx() {
            return gisx;
        }

        public void setGisx(String gisx) {
            this.gisx = gisx;
        }

        public String getGisy() {
            return gisy;
        }

        public void setGisy(String gisy) {
            this.gisy = gisy;
        }

        public String getProvincename() {
            return provincename;
        }

        public void setProvincename(String provincename) {
            this.provincename = provincename;
        }

        public String getCityname() {
            return cityname;
        }

        public void setCityname(String cityname) {
            this.cityname = cityname;
        }

        public String getDistrictname() {
            return districtname;
        }

        public void setDistrictname(String districtname) {
            this.districtname = districtname;
        }

        public String getAreacode() {
            return areacode;
        }

        public void setAreacode(String areacode) {
            this.areacode = areacode;
        }

        public String getAreaname() {
            return areaname;
        }

        public void setAreaname(String areaname) {
            this.areaname = areaname;
        }

        public String getAreacode2() {
            return areacode2;
        }

        public void setAreacode2(String areacode2) {
            this.areacode2 = areacode2;
        }

        public void resetGis(String x, String y, String provincename, String cityname, String districtname) {
            this.gisx = x;
            this.gisy = y;
            this.provincename = provincename;
            this.cityname = cityname;
            this.districtname = districtname;
        }

        /**
         * 存在地理位置或者切换后的城市信息
         * @return
         */
        public boolean isAvail(){
            if(StringUtil.isNotEmpty(this.areacode)&&StringUtil.isNotEmpty(this.areacode2)){
                return true;
            }
            if(StringUtil.isNotEmpty(this.gisx)&&StringUtil.isNotEmpty(this.gisy)&&StringUtil.isNotEmpty(this.provincename)&&StringUtil.isNotEmpty(this.cityname)){
                return true;
            }
            return false;
        }

        public boolean hasGis(){
            if(StringUtil.isNotEmpty(this.gisx)&&StringUtil.isNotEmpty(this.gisy)&&StringUtil.isNotEmpty(this.provincename)&&StringUtil.isNotEmpty(this.cityname)){
                return true;
            }
            return false;
        }

        public void setAreacode(String areacode, String areacode2) {
            this.areacode = areacode;
            this.areacode2 = areacode2;
        }
    }
}
