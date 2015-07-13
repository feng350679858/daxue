package com.jingcai.apps.aizhuan.service.business.account.account06;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/6/9.
 */
public class Account06Response extends BaseResponse<Account06Response.Account06Body> {

    public class Account06Body{

        private Account account;

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        private class Account{
            private String opdate;
            private String opmoney;

            public String getOpdate() {
                return opdate;
            }

            public void setOpdate(String opdate) {
                this.opdate = opdate;
            }

            public String getOpmoney() {
                return opmoney;
            }

            public void setOpmoney(String opmoney) {
                this.opmoney = opmoney;
            }
        }


    }
}
