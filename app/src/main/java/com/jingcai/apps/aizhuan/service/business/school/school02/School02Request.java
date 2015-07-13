package com.jingcai.apps.aizhuan.service.business.school.school02;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class School02Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_SCHOOL_02;
    }

    private Areainfo areainfo;

    public Areainfo getAreainfo() {
        return areainfo;
    }

    public void setAreainfo(Areainfo areainfo) {
        this.areainfo = areainfo;
    }

    public class Areainfo {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
