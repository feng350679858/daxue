package com.jingcai.apps.aizhuan.service.business.advice.advice05;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

public class Advice05Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_ADVICE_05;
    }

    private Notice notice;

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public class Notice {
        private String gisx;
        private String gisy;
        private String areacode;
        private String areacode2;
        private String provincename;
        private String cityname;

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

        public String getAreacode() {
            return areacode;
        }

        public void setAreacode(String areacode) {
            this.areacode = areacode;
        }

        public String getAreacode2() {
            return areacode2;
        }

        public void setAreacode2(String areacode2) {
            this.areacode2 = areacode2;
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
    }
}
