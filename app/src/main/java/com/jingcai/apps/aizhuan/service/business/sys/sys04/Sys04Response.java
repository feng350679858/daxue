package com.jingcai.apps.aizhuan.service.business.sys.sys04;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

public class Sys04Response extends BaseResponse<Sys04Response.Sys04Body> {

    public class Sys04Body {
        private Student student;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public class Student {
            private String studentid;
            private String initflag;

            public String getInitflag() {
                return initflag;
            }

            public void setInitflag(String initflag) {
                this.initflag = initflag;
            }

            public String getStudentid() {
                return studentid;
            }

            public void setStudentid(String studentid) {
                this.studentid = studentid;
            }
        }
    }

}
