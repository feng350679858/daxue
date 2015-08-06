package com.jingcai.apps.aizhuan.service.business.stu.stu11;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Administrator on 2015/7/22.
 */
public class Stu11Response extends BaseResponse<Stu11Response.Body> {

    public class Body {
        private Student student;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public class Student{
         private String score;

          public String getScore() {
              return score;
          }

          public void setScore(String score) {
              this.score = score;
          }
      }

    }

}

