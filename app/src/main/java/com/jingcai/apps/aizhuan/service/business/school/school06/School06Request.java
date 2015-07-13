package com.jingcai.apps.aizhuan.service.business.school.school06;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class School06Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_SCHOOL_06;
    }

    private Professional professional;

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public class Professional{
        private String name;
        private String start;
        private String pagesize;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPagesize() {
            return pagesize;
        }

        public void setPagesize(String pagesize) {
            this.pagesize = pagesize;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }
    }
}
