package com.jingcai.apps.aizhuan.service.business.stu.std07;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by xiangqili on 2015/6/9.
 */
public class Stu07Response extends BaseResponse<Stu07Response.Stu07Body> {

    public class Stu07Body {
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