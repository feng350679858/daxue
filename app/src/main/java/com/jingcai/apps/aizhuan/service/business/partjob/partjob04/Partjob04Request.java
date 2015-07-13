package com.jingcai.apps.aizhuan.service.business.partjob.partjob04;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class Partjob04Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_04;
    }

    private Joininfo joininfo;

    public Joininfo getJoininfo() {
        return joininfo;
    }

    public void setJoininfo(Joininfo joininfo) {
        this.joininfo = joininfo;
    }

    public class Joininfo {
        private String id;
        private String studentid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }


}
