package com.jingcai.apps.aizhuan.service.business.partjob.partjob37;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob37Request extends BaseRequest {

    public Partjob37Request(String helpid){
        this.parttimejob = new Parttimejob();
        this.parttimejob.setHelpid(helpid);
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_37;
    }

    private Parttimejob parttimejob;

    public Parttimejob getParttimejob() {
        return parttimejob;
    }

    public void setParttimejob(Parttimejob parttimejob) {
        this.parttimejob = parttimejob;
    }

    class Parttimejob {
        private String helpid;
        private String studentid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getHelpid() {
            return helpid;
        }

        public void setHelpid(String helpid) {
            this.helpid = helpid;
        }
    }
}
