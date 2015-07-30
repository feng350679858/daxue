package com.jingcai.apps.aizhuan.service.business.stu.stu10;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by xiangqili on 2015/6/9.
 */
public class Stu10Request extends BaseRequest {

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BTZ_STU_10;
    }

    public class Student {
        private String studentid;
        private String start;
        private String pagesize;

        public String getPagesize() {
            return pagesize;
        }

        public void setPagesize(String pagesize) {
            this.pagesize = pagesize;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }
}
