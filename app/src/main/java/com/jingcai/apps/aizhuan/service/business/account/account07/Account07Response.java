package com.jingcai.apps.aizhuan.service.business.account.account07;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/6/9.
 */
public class Account07Response extends BaseResponse<Account07Response.Body> {

    public class Body{

        private Pay pay;

        public Pay getPay() {
            return pay;
        }

        public void setPay(Pay pay) {
            this.pay = pay;
        }

        public class Pay{
            private String tradeno;
            private String notifyurl;
            private String subject;
            private String paymenttype;
            private String sellerid;
            private String fee;
            private String des;
            private String partner;
            private String sign;


            public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }

            public String getFee() {
                return fee;
            }

            public void setFee(String fee) {
                this.fee = fee;
            }

            public String getNotifyurl() {
                return notifyurl;
            }

            public void setNotifyurl(String notifyurl) {
                this.notifyurl = notifyurl;
            }

            public String getPartner() {
                return partner;
            }

            public void setPartner(String partner) {
                this.partner = partner;
            }

            public String getPaymenttype() {
                return paymenttype;
            }

            public void setPaymenttype(String paymenttype) {
                this.paymenttype = paymenttype;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public String getSellerid() {
                return sellerid;
            }

            public void setSellerid(String sellerid) {
                this.sellerid = sellerid;
            }

            public String getSubject() {
                return subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }

            public String getTradeno() {
                return tradeno;
            }

            public void setTradeno(String tradeno) {
                this.tradeno = tradeno;
            }
        }
    }
}
