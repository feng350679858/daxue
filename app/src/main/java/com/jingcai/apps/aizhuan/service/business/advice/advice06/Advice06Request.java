package com.jingcai.apps.aizhuan.service.business.advice.advice06;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

public class Advice06Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_ADVICE_06;
    }

    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public class Message{
        private String studentid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }
}
