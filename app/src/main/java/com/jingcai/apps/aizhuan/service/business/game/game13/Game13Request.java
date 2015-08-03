package com.jingcai.apps.aizhuan.service.business.game.game13;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/31.
 */
public class Game13Request extends BaseRequest {
    private Student studnet;

    public Student getStudnet() {
        return studnet;
    }

    public void setStudnet(Student studnet) {
        this.studnet = studnet;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BTZ_GAME_13;
    }

    public class Student
    {
        private String id;
        private String idno;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIdno() {
            return idno;
        }

        public void setIdno(String idno) {
            this.idno = idno;
        }
    }
}