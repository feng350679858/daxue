package com.jingcai.apps.aizhuan.service.business.account.account01;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.ArrayList;

/**
 * Created by Json Ding on 2015/6/4.
 */
public class Account01Response extends BaseResponse<Account01Response.Account01Body> {

    public class Account01Body{
        private ArrayList<Wallet> wallet_list;

        public ArrayList<Wallet> getWallet_list() {
            return wallet_list;
        }

        public void setWallet_list(ArrayList<Wallet> wallet_list) {
            this.wallet_list = wallet_list;
        }

        public class Wallet{
            private String code;
            private String credit;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getCredit() {
                return credit;
            }

            public void setCredit(String credit) {
                this.credit = credit;
            }
        }
    }
}
