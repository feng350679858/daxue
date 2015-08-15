package com.jingcai.apps.aizhuan.service.business.partjob.partjob38;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob38Request extends BaseRequest {

    public Partjob38Request(String helpid, String studentid) {
        this.parttimejob = new Parttimejob();
        this.parttimejob.helpid = helpid;
        this.parttimejob.studentid = studentid;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_38;
    }

    private Parttimejob parttimejob;

    public Parttimejob getParttimejob() {
        return parttimejob;
    }

    public void setParttimejob(Parttimejob parttimejob) {
        this.parttimejob = parttimejob;
    }

    public class Parttimejob {
        private String studentid;
        private String helpid;

        public String getHelpid() {
            return helpid;
        }

        public void setHelpid(String helpid) {
            this.helpid = helpid;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }
}
