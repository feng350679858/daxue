package com.jingcai.apps.aizhuan.service.business.partjob.partjob11;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob11Request extends BaseRequest {

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_11;
    }
    private Partjob11Request partjob11Request;

    public Partjob11Request getPartjob11Request() {
        return partjob11Request;
    }

    public void setPartjob11Request(Partjob11Request partjob11Request) {
        this.partjob11Request = partjob11Request;
    }

    public class Parttimejob{
        private String studentid;
        private String gisx;
        private String gisy;
        private String start;
        private String pagesize;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

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
