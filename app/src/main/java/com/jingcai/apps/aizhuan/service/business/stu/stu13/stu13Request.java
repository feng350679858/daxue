package com.jingcai.apps.aizhuan.service.business.stu.stu13;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/22.
 */
public class stu13Request extends BaseRequest {
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BTZ_STU_13;
    }

    public class Student {
        private String sourceid;
        private String targetid;
        private String targettype;
        private String targetrefid;
        private String targetreftype;
        private String score;
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getSourceid() {
            return sourceid;
        }

        public void setSourceid(String sourceid) {
            this.sourceid = sourceid;
        }

        public String getTargetid() {
            return targetid;
        }

        public void setTargetid(String targetid) {
            this.targetid = targetid;
        }

        public String getTargetrefid() {
            return targetrefid;
        }

        public void setTargetrefid(String targetrefid) {
            this.targetrefid = targetrefid;
        }

        public String getTargetreftype() {
            return targetreftype;
        }

        public void setTargetreftype(String targetreftype) {
            this.targetreftype = targetreftype;
        }

        public String getTargettype() {
            return targettype;
        }

        public void setTargettype(String targettype) {
            this.targettype = targettype;
        }
    }
}
