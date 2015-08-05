package com.jingcai.apps.aizhuan.service.business.partjob.partjob36;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob36Request extends BaseRequest {

    public Partjob36Request(String answerid, String studentid) {
        this.parttimejob = new Parttimejob();
        this.parttimejob.setAnswerid(answerid);
        this.parttimejob.setStudentid(studentid);
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_36;
    }

    private Parttimejob parttimejob;

    public Parttimejob getParttimejob() {
        return parttimejob;
    }

    public void setParttimejob(Parttimejob parttimejob) {
        this.parttimejob = parttimejob;
    }

    class Parttimejob {
        private String answerid;
        private String studentid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getAnswerid() {
            return answerid;
        }

        public void setAnswerid(String answerid) {
            this.answerid = answerid;
        }
    }
}
