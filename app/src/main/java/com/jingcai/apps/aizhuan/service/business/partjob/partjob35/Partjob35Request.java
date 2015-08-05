package com.jingcai.apps.aizhuan.service.business.partjob.partjob35;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob35Request extends BaseRequest {

    public Partjob35Request(String studentid, String targettype, String targetid){
        this.parttimejob = new Parttimejob();
        this.parttimejob.setStudentid(studentid);
        this.parttimejob.setTargettype(targettype);
        this.parttimejob.setTargetid(targetid);
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_35;
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
        private String targettype;
        private String targetid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getTargetid() {
            return targetid;
        }

        public void setTargetid(String targetid) {
            this.targetid = targetid;
        }

        public String getTargettype() {
            return targettype;
        }

        public void setTargettype(String targettype) {
            this.targettype = targettype;
        }
    }
}
