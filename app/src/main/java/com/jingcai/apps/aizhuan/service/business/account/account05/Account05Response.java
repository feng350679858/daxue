package com.jingcai.apps.aizhuan.service.business.account.account05;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.ArrayList;

/**
 * Created by Json Ding on 2015/6/4.
 */
public class Account05Response extends BaseResponse<Account05Response.Account05Body> {

    public class Account05Body {
        private ArrayList<Account> account_list;

        public ArrayList<Account> getAccount_list() {
            return account_list;
        }

        public void setAccount_list(ArrayList<Account> account_list) {
            this.account_list = account_list;
        }

        public class Account{

            private String opmoney;
            private String opdate;

            public String getOpmoney() {
                return opmoney;
            }

            public void setOpmoney(String opmoney) {
                this.opmoney = opmoney;
            }

            public String getOpdate() {
                return opdate;
            }

            public void setOpdate(String opdate) {
                this.opdate = opdate;
            }
        }
    }
}
