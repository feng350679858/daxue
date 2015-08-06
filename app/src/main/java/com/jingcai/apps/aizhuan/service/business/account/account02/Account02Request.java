package com.jingcai.apps.aizhuan.service.business.account.account02;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/6/4.
 */
public class Account02Request extends BaseRequest{
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_ACCOUNT_02;
    }

    private User user;
    private Student student;
    private Account account;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public class Student{
        private String studentid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }

    public class Account{
        private String begindate;
        private String enddate;
        private String walletcode;
        private String start;
        private String pagesize;
        private String optype;

        public String getOptype() {
            return optype;
        }

        public void setOptype(String optype) {
            this.optype = optype;
        }

        public String getBegindate() {
            return begindate;
        }

        public void setBegindate(String begindate) {
            this.begindate = begindate;
        }

        public String getEnddate() {
            return enddate;
        }

        public void setEnddate(String enddate) {
            this.enddate = enddate;
        }

        public String getWalletcode() {
            return walletcode;
        }

        public void setWalletcode(String walletcode) {
            this.walletcode = walletcode;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getPagesize() {
            return pagesize;
        }

        public void setPagesize(String pagesize) {
            this.pagesize = pagesize;
        }
    }

}
