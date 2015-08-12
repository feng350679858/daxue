package com.jingcai.apps.aizhuan.service.business.stu.stu10;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/22.
 */
public class Stu10Response extends BaseResponse<Stu10Response.Body> {

    public class Body {
        private List<Item> student_list;
        private Student student;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public List<Item> getStudent_list() {
            return student_list;
        }

        public void setStudent_list(List<Item> student_list) {
            this.student_list = student_list;
        }
    }

    public class Student {
        private String onlinenum;
        private String offlinenum;

        public String getOfflinenum() {
            return offlinenum;
        }

        public void setOfflinenum(String offlinenum) {
            this.offlinenum = offlinenum;
        }

        public String getOnlinenum() {
            return onlinenum;
        }

        public void setOnlinenum(String onlinenum) {
            this.onlinenum = onlinenum;
        }
    }

    public class Item {
        private String targetid;
        private String targetname;
        private String targetschool;
        private String targetcollege;
        private String targetimgurl;
        private String targetlevel;
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTargetcollege() {
            return targetcollege;
        }

        public void setTargetcollege(String targetcollege) {
            this.targetcollege = targetcollege;
        }

        public String getTargetid() {
            return targetid;
        }

        public void setTargetid(String targetid) {
            this.targetid = targetid;
        }

        public String getTargetimgurl() {
            return targetimgurl;
        }

        public void setTargetimgurl(String targetimgurl) {
            this.targetimgurl = targetimgurl;
        }

        public String getTargetlevel() {
            return targetlevel;
        }

        public void setTargetlevel(String targetlevel) {
            this.targetlevel = targetlevel;
        }

        public String getTargetname() {
            return targetname;
        }

        public void setTargetname(String targetname) {
            this.targetname = targetname;
        }

        public String getTargetschool() {
            return targetschool;
        }

        public void setTargetschool(String targetschool) {
            this.targetschool = targetschool;
        }
    }
}

