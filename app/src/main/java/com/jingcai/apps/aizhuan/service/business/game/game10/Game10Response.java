package com.jingcai.apps.aizhuan.service.business.game.game10;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/8/4.
 */
public class Game10Response extends BaseResponse<Game10Response.Body> {

    public class Body {
        private Student student;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public class Student{
            private String id;
            private String name;
            private String idno;
            private String checkstatus;
            private String refusemsg;
            private String isbandalipay;
            private String logopath;
            private String goldbalance;
            private String freezebalance;
            private String noreadmsgcount;
            private String haspaypassword;
            private String minwithdraw;

            public String getCheckstatus() {
                return checkstatus;
            }

            public void setCheckstatus(String checkstatus) {
                this.checkstatus = checkstatus;
            }

            public String getFreezebalance() {
                return freezebalance;
            }

            public void setFreezebalance(String freezebalance) {
                this.freezebalance = freezebalance;
            }

            public String getGoldbalance() {
                return goldbalance;
            }

            public void setGoldbalance(String goldbalance) {
                this.goldbalance = goldbalance;
            }

            public String getHaspaypassword() {
                return haspaypassword;
            }

            public void setHaspaypassword(String haspaypassword) {
                this.haspaypassword = haspaypassword;
            }

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

            public String getIsbandalipay() {
                return isbandalipay;
            }

            public void setIsbandalipay(String isbandalipay) {
                this.isbandalipay = isbandalipay;
            }

            public String getLogopath() {
                return logopath;
            }

            public void setLogopath(String logopath) {
                this.logopath = logopath;
            }

            public String getMinwithdraw() {
                return minwithdraw;
            }

            public void setMinwithdraw(String minwithdraw) {
                this.minwithdraw = minwithdraw;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNoreadmsgcount() {
                return noreadmsgcount;
            }

            public void setNoreadmsgcount(String noreadmsgcount) {
                this.noreadmsgcount = noreadmsgcount;
            }

            public String getRefusemsg() {
                return refusemsg;
            }

            public void setRefusemsg(String refusemsg) {
                this.refusemsg = refusemsg;
            }
        }
    }
}
