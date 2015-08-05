package com.jingcai.apps.aizhuan.service.business.game.game10;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/8/4.
 */
public class Game10Request extends BaseRequest {

    private Student student;

    @Override
    public String getTranscode() {
        return BizConstant.BTZ_GAME_10;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public class Student{
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
