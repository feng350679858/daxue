package com.jingcai.apps.aizhuan.service.business.game.game09;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/16.
 */
public class Game09Request extends BaseRequest {
    private Student studnet;

    public Student getStudnet() {
        return studnet;
    }

    public void setStudnet(Student studnet) {
        this.studnet = studnet;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BTZ_GAME_09;
    }

    public class Student
    {
        private String studentid;
        private String studentname;
        private String studentidno;
        private String studentidimg_front;
        private String studnetidimg_back;


        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getStudentname() {
            return studentname;
        }

        public void setStudentname(String studentname) {
            this.studentname = studentname;
        }

        public String getStudentidno() {
            return studentidno;
        }

        public void setStudentidno(String studentidno) {
            this.studentidno = studentidno;
        }

        public String getStudentidimg_front() {
            return studentidimg_front;
        }

        public void setStudentidimg_front(String studentidimg_front) {
            this.studentidimg_front = studentidimg_front;
        }

        public String getStudnetidimg_back() {
            return studnetidimg_back;
        }

        public void setStudnetidimg_back(String studnetidimg_back) {
            this.studnetidimg_back = studnetidimg_back;
        }
    }
}
