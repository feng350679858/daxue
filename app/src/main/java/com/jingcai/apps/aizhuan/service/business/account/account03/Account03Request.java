package com.jingcai.apps.aizhuan.service.business.account.account03;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/6/10.
 */
public class Account03Request extends BaseRequest {
    private User user;
    private Student student;
    private Wallet wallet;
    private Bank bank;

    public User getUser(){return user;}
    public void setUser(User user){this.user = user;}

    public Student getStudent(){return student;}
    public void setStudent(Student student){this.student =student;}

    public Wallet getWallet(){return  wallet;}
    public void setWallet(Wallet wallet){this.wallet = wallet;}

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_ACCOUNT_03;
    }

    public class User{
        private String id;
        public String getId(){return id;}
        public void setId(String id){this.id = id;}
    }

    public class Student{
        private  String studentid;
        private String paypassword;

        public String getStudentid(){return studentid;}
        public void setStudentid(String studentid){this.studentid = studentid;}


        public String getPaypassword() {
            return paypassword;
        }

        public void setPaypassword(String paypassword){
            this.paypassword = paypassword;
        }
    }

    public class Bank {
        private String banktype;
        private String cardtype;
        private String cardno;

        public String getBanktype(){return banktype;}
        public void setBanktype(String banktype){this.banktype = banktype;}

        public String getCardtype(){return cardtype;}
        public void setCardtype(String cardtype){this.cardtype = cardtype;}

        public String getCardno(){return cardno;}
        public void setCardno(String cardno){this.cardno = cardno;}
    }

    public class Wallet{
        private String code;
        private String opmoney;

        public String getCode(){return code;}
        public void setCode(String code){this.code = code;}

        public String getOpmoney(){return opmoney;}
        public void setOpmoney(String opmoney){this.opmoney = opmoney;}
    }
}
