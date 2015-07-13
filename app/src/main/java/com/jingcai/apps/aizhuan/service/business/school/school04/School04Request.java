package com.jingcai.apps.aizhuan.service.business.school.school04;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class School04Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_SCHOOL_04;
    }

    private Schoolinfo schoolinfo;

    public Schoolinfo getSchoolinfo() {
        return schoolinfo;
    }

    public void setSchoolinfo(Schoolinfo schoolinfo) {
        this.schoolinfo = schoolinfo;
    }

    public class Schoolinfo{
        private String name;
        private String start;
        private String pagesize;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getPagesize() {
            return pagesize;
        }

        public void setPagesize(String pagesize) {
            this.pagesize = pagesize;
        }
    }
}
