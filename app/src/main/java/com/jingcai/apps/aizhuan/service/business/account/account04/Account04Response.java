package com.jingcai.apps.aizhuan.service.business.account.account04;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Json Ding on 2015/6/9.
 */
public class Account04Response extends BaseResponse<Account04Response.Account04Body> {

    public class Account04Body{
        private List<Bank> bank_list;

        public List<Bank> getBank_list() {
            return bank_list;
        }

        public void setBank_list(List<Bank> bank_list) {
            this.bank_list = bank_list;
        }

        public class Bank implements Serializable{
            private String type;
            private String code;
            private String name;
            private String imgurl;
            private String cardno;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImgurl() {
                return imgurl;
            }

            public void setImgurl(String imgurl) {
                this.imgurl = imgurl;
            }

            public String getCardno() {
                return cardno;
            }

            public void setCardno(String cardno) {
                this.cardno = cardno;
            }
        }
    }
}
