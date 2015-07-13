package com.jingcai.apps.aizhuan.service.business.sys.sys02;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

public class Sys02Response extends BaseResponse<Sys02Response.Sys02Body> {

    public class Sys02Body {
        private Student student;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public class Student {
            private String checkstr;

            public String getCheckstr() {
                return checkstr;
            }

            public void setCheckstr(String checkstr) {
                this.checkstr = checkstr;
            }
        }
    }

}
