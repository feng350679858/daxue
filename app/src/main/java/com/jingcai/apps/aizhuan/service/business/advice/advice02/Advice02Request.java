package com.jingcai.apps.aizhuan.service.business.advice.advice02;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/5/4.
 */
public class Advice02Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_ADVICE_02;
    }

    private Message message;
    private Student student;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public class Student{
        private String studentid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }
    public class Message{
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
