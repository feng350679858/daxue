package com.jingcai.apps.aizhuan.service.business.school.school05;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class School05Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_SCHOOL_05;
    }

    private Schoolinfo schoolinfo;

    public Schoolinfo getSchoolinfo() {
        return schoolinfo;
    }

    public void setSchoolinfo(Schoolinfo schoolinfo) {
        this.schoolinfo = schoolinfo;
    }

    public class Schoolinfo{
        private String schoolid;

        public String getSchoolid() {
            return schoolid;
        }

        public void setSchoolid(String schoolid) {
            this.schoolid = schoolid;
        }
    }
}
