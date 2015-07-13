package com.jingcai.apps.aizhuan.service.business.stu.stu01;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

public class Stu01Request extends BaseRequest {

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_STU_01;
    }

    public class Student {
        private String studentid;
        private String oldpassword;
        private String newpassword;

        public String getOldpassword() {
            return oldpassword;
        }

        public void setOldpassword(String oldpassword) {
            this.oldpassword = oldpassword;
        }

        public String getNewpassword() {
            return newpassword;
        }

        public void setNewpassword(String newpassword) {
            this.newpassword = newpassword;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }
}