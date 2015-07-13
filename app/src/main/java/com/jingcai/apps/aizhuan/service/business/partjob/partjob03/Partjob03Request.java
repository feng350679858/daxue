package com.jingcai.apps.aizhuan.service.business.partjob.partjob03;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Partjob03Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_03;
    }

    private Joininfo joininfo;

    public Joininfo getJoininfo() {
        return joininfo;
    }

    public void setJoininfo(Joininfo joininfo) {
        this.joininfo = joininfo;
    }

    public static class Joininfo{
        private String studentid;
        private String start;
        private String pagesize;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
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
