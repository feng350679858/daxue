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
        private String checkstr;
        private String optype;

        public String getCheckstr() {
            return checkstr;
        }

        public void setCheckstr(String checkstr) {
            this.checkstr = checkstr;
        }

        public String getOptype() {
            return optype;
        }

        public void setOptype(String optype) {
            this.optype = optype;
        }
    }
}
