package com.jingcai.apps.aizhuan.service.business.stu.stu16;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/8/6.
 */
public class Stu16Rquest extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_STU_16;
    }

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public class Student{
        private String id;
        private String studentcdno;
        private String sidingfront;
        private String sidingback;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSidingback() {
            return sidingback;
        }

        public void setSidingback(String sidingback) {
            this.sidingback = sidingback;
        }

        public String getSidingfront() {
            return sidingfront;
        }

        public void setSidingfront(String sidingfront) {
            this.sidingfront = sidingfront;
        }

        public String getStudentcdno() {
            return studentcdno;
        }

        public void setStudentcdno(String studentcdno) {
            this.studentcdno = studentcdno;
        }
    }
}
