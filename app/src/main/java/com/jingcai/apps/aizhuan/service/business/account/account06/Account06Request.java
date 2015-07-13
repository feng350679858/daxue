package com.jingcai.apps.aizhuan.service.business.account.account06;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/6/9.
 */
public class Account06Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_ACCOUNT_06;
    }

    private Bank bank;
    private Student student;
    private User user;

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class Bank{
        private String banktype;
        private String cardtype;
        private String cardno;
        private String opflag;

        public String getBanktype() {
            return banktype;
        }

        public void setBanktype(String banktype) {
            this.banktype = banktype;
        }

        public String getCardtype() {
            return cardtype;
        }

        public void setCardtype(String cardtype) {
            this.cardtype = cardtype;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }

        public String getOpflag() {
            return opflag;
        }

        public void setOpflag(String opflag) {
            this.opflag = opflag;
        }
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

    public class User{
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
