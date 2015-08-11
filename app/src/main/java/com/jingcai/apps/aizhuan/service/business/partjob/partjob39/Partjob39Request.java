package com.jingcai.apps.aizhuan.service.business.partjob.partjob39;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob39Request extends BaseRequest {

    public Partjob39Request(String studentid, String answerid, String content){
        this.parttimejob = new Parttimejob();
        this.parttimejob.studentid = studentid;
        this.parttimejob.answerid = answerid;
        this.parttimejob.content = content;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_39;
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
        private String answerid;
        private String content;
        private String anonflag;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAnswerid() {
            return answerid;
        }

        public void setAnswerid(String answerid) {
            this.answerid = answerid;
        }

        public void setAnonflag(String anonflag) {
            this.anonflag = anonflag;
        }

        public String getAnonflag() {
            return anonflag;
        }
    }
}
