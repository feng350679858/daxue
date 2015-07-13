package com.jingcai.apps.aizhuan.service.business.school.school07;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class School07Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_SCHOOL_07;
    }

    private Areainfo areainfo;

    public Areainfo getAreainfo() {
        return areainfo;
    }

    public void setAreainfo(Areainfo areainfo) {
        this.areainfo = areainfo;
    }

    public class Areainfo {
        private String provincename;
        private String cityname;
        private String districtname;

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
    }
}
