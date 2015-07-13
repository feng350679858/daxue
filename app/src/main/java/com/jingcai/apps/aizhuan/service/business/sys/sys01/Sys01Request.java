package com.jingcai.apps.aizhuan.service.business.sys.sys01;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

public class Sys01Request extends BaseRequest {

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_SYS_01;
    }

    public class Student {
        private String phone;
        private String checkstr;
        private String password;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCheckstr() {
            return checkstr;
        }

        public void setCheckstr(String checkstr) {
            this.checkstr = checkstr;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}