package com.jingcai.apps.aizhuan.service.business.stu.stu05;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

public class Stu05Request extends BaseRequest {

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_STU_05;
    }

    public class Student {
        private String studentid;
        private String isvisiable;

        public String getIsvisiable() {
            return isvisiable;
        }

        public void setIsvisiable(String isvisiable) {
            this.isvisiable = isvisiable;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }
}