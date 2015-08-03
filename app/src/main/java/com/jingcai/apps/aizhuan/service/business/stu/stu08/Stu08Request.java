package com.jingcai.apps.aizhuan.service.business.stu.stu08;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by xiangqili on 2015/6/9.
 */
public class Stu08Request extends BaseRequest {

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BTZ_STU_08;
    }

    public class Student {
        private String studentid;
        private String optype;//1上线 2下线
        private String gisx;
        private String gisy;

        public String getGisx() {
            return gisx;
        }

        public void setGisx(String gisx) {
            this.gisx = gisx;
        }

        public String getGisy() {
            return gisy;
        }

        public void setGisy(String gisy) {
            this.gisy = gisy;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getOptype() {
            return optype;
        }

        public void setOptype(String optype) {
            this.optype = optype;
        }
    }
}
