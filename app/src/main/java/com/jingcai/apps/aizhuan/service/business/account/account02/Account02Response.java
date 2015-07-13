package com.jingcai.apps.aizhuan.service.business.account.account02;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.ArrayList;

/**
 * Created by Json Ding on 2015/6/4.
 */
public class Account02Response extends BaseResponse<Account02Response.Account02Body> {

    public class Account02Body {
        private ArrayList<Account> account_list;

        public ArrayList<Account> getAccount_list() {
            return account_list;
        }

        public void setAccount_list(ArrayList<Account> account_list) {
            this.account_list = account_list;
        }

        public class Account{
            private String streamed;
            private String tradecode;
            private String optime;
            private String title;
            private String opmoney;
            private String optype;
            private String imgurl;
            private String status;

            public String getStreamed() {
                return streamed;
            }

            public void setStreamed(String streamed) {
                this.streamed = streamed;
            }

            public String getTradecode() {
                return tradecode;
            }

            public void setTradecode(String tradecode) {
                this.tradecode = tradecode;
            }

            public String getOptime() {
                return optime;
            }

            public void setOptime(String optime) {
                this.optime = optime;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getOpmoney() {
                return opmoney;
            }

            public void setOpmoney(String opmoney) {
                this.opmoney = opmoney;
            }

            public String getOptype() {
                return optype;
            }

            public void setOptype(String optype) {
                this.optype = optype;
            }

            public String getImgurl() {
                return imgurl;
            }

            public void setImgurl(String imgurl) {
                this.imgurl = imgurl;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}
