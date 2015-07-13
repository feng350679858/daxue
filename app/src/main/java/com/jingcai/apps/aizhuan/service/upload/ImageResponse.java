package com.jingcai.apps.aizhuan.service.upload;


import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/5/9.
 */
public class ImageResponse extends BaseResponse<ImageResponse.Student> {

        private Student student;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public class Student{
            private String logopath;

            public String getLogopath() {
                return logopath;
            }

            public void setLogopath(String logopath) {
                this.logopath = logopath;
            }
        }

}
