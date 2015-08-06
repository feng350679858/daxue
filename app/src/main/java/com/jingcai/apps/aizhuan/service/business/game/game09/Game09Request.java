package com.jingcai.apps.aizhuan.service.business.game.game09;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/16.
 */
public class Game09Request extends BaseRequest {
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BTZ_GAME_09;
    }

    public class Student{
        private String id;
        private String name;
        private String idno;
        private String idimg_front;
        private String idimg_back;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIdimg_back() {
            return idimg_back;
        }

        public void setIdimg_back(String idimg_back) {
            this.idimg_back = idimg_back;
        }

        public String getIdimg_front() {
            return idimg_front;
        }

        public void setIdimg_front(String idimg_front) {
            this.idimg_front = idimg_front;
        }

        public String getIdno() {
            return idno;
        }

        public void setIdno(String idno) {
            this.idno = idno;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
