package com.jingcai.apps.aizhuan.service.business.account.account07;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/6/9.
 */
public class Account07Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_ACCOUNT_07;
    }

    private Pay pay ;

    public Pay getPay() {
        return pay;
    }

    public void setPay(Pay pay) {
        this.pay = pay;
    }

    public class Pay{
        private String studentid;
        private String money;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }
    }
}
