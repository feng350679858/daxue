package com.jingcai.apps.aizhuan.service.business.partjob.partjob05;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/20.
 */
public class Partjob05Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_05;
    }

    private Joininfo joininfo;

    public Joininfo getJoininfo() {
        return joininfo;
    }

    public void setJoininfo(Joininfo joininfo) {
        this.joininfo = joininfo;
    }

    public class Joininfo {
        private String studentid;
        private String joinchannel;
        private String gisx;
        private String gisy;
        private String jobid;

        public String getJoinchannel() {
            return joinchannel;
        }

        public void setJoinchannel(String joinchannel) {
            this.joinchannel = joinchannel;
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

        public String getJobid() {
            return jobid;
        }

        public void setJobid(String jobid) {
            this.jobid = jobid;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }


}
