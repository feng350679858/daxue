package com.jingcai.apps.aizhuan.service.business.partjob.partjob08;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class Partjob08Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_08;
    }

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public class Student{
        private String studentid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }
}
