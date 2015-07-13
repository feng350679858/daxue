package com.jingcai.apps.aizhuan.service.business.stu.stu07;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by xiangqili on 2015/6/9.
 */
public class Stu07Request extends BaseRequest{

    private Student student;

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }
    @Override
    public String getTranscode() {
        return BizConstant.BTZ_STU_07;
    }

    public class Student
    {
        private String checkstr;
        private String phone;
        private String password;
        private String studentid;

        public String getCheckstr() {
            return checkstr;
        }

        public void setCheckstr(String checkstr) {
            this.checkstr = checkstr;
        }

        public String getPhone()
        {
            return phone;
        }

        public void setPhone(String phone)
        {
            this.phone = phone;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public String getStudentid()
        {
            return studentid;
        }

        public void setStudentid(String studentid)
        {
            this.studentid = studentid;
        }
    }
}
