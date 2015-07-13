package com.jingcai.apps.aizhuan.service.business.partjob.partjob08;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class Partjob08Response extends BaseResponse<Partjob08Response.Partjob04Body> {

    public class Partjob04Body {

        private Student student;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public class Student{
            private String count;
            private String leftcount;

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            public String getLeftcount() {
                return leftcount;
            }

            public void setLeftcount(String leftcount) {
                this.leftcount = leftcount;
            }
        }
    }
}
