package com.jingcai.apps.aizhuan.service.business.partjob.partjob07;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class Partjob07Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_07;
    }

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public class Student{
        private String studentid;
        private String start;
        private String pagesize;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getPagesize() {
            return pagesize;
        }

        public void setPagesize(String pagesize) {
            this.pagesize = pagesize;
        }
    }
}
