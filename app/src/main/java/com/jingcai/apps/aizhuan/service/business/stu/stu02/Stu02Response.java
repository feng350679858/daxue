package com.jingcai.apps.aizhuan.service.business.stu.stu02;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

public class Stu02Response extends BaseResponse<Stu02Response.Stu02Body> {

    public class Stu02Body {
        private Student student;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public class Student {
            private String name;
            private String gender;
            private String areaname;
            private String phone;
            private String email;
            private String qq;
            private String schoolname;
            private String collegename;
            private String joindate;
            private String college;
            private String professional;
            private String promotioncode;
            private String logopath;
            private String isvisiable;
            private String level;
            //adationnal
            private String studentid;
            private String password;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getAreaname() {
                return areaname;
            }

            public void setAreaname(String areaname) {
                this.areaname = areaname;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getQq() {
                return qq;
            }

            public void setQq(String qq) {
                this.qq = qq;
            }

            public String getSchoolname() {
                return schoolname;
            }

            public void setSchoolname(String schoolname) {
                this.schoolname = schoolname;
            }

            public String getCollegename() {
                return collegename;
            }

            public void setCollegename(String collegename) {
                this.collegename = collegename;
            }

            public String getJoindate() {
                return joindate;
            }

            public void setJoindate(String joindate) {
                this.joindate = joindate;
            }

            public String getCollege() {
                return college;
            }

            public void setCollege(String college) {
                this.college = college;
            }

            public String getProfessional() {
                return professional;
            }

            public void setProfessional(String professional) {
                this.professional = professional;
            }

            public String getPromotioncode() {
                return promotioncode;
            }

            public void setPromotioncode(String promotioncode) {
                this.promotioncode = promotioncode;
            }

            public String getLogopath() {
                return logopath;
            }

            public void setLogopath(String logopath) {
                this.logopath = logopath;
            }

            public String getIsvisiable() {
                return isvisiable;
            }

            public void setIsvisiable(String isvisiable) {
                this.isvisiable = isvisiable;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getStudentid() {
                return studentid;
            }

            public void setStudentid(String studentid) {
                this.studentid = studentid;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }
        }
    }

}
