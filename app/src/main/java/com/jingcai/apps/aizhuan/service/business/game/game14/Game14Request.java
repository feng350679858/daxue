package com.jingcai.apps.aizhuan.service.business.game.game14;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/8/5.
 */
public class Game14Request extends BaseRequest {
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BTZ_GAME_14;
    }

    public class Student {
        private String id;
        private String paypassword;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPaypassword() {
            return paypassword;
        }

        public void setPaypassword(String paypassword) {
            this.paypassword = paypassword;
        }
    }
}