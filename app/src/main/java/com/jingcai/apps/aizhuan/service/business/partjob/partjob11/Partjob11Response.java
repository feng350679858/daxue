package com.jingcai.apps.aizhuan.service.business.partjob.partjob11;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob11Response extends BaseResponse<Partjob11Response.Partjob11Body> {

    public class Partjob11Body {
        private List<Merchant> merchant_list;

        public List<Merchant> getMerchant_list() {
            return merchant_list;
        }

        public void setMerchant_list(List<Merchant> merchant_list) {
            this.merchant_list = merchant_list;
        }

        public class Merchant{
            private String helpid;
            private String type;
            private String coin;
            private String content;
            private String sourceid;
            private String sourcename;
            private String sourceschool;
            private String sourcecollege;
            private String optime;

            public String getHelpid() {
                return helpid;
            }

            public void setHelpid(String helpid) {
                this.helpid = helpid;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCoin() {
                return coin;
            }

            public void setCoin(String coin) {
                this.coin = coin;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getSourceid() {
                return sourceid;
            }

            public void setSourceid(String sourceid) {
                this.sourceid = sourceid;
            }

            public String getSourcename() {
                return sourcename;
            }

            public void setSourcename(String sourcename) {
                this.sourcename = sourcename;
            }

            public String getSourceschool() {
                return sourceschool;
            }

            public void setSourceschool(String sourceschool) {
                this.sourceschool = sourceschool;
            }

            public String getSourcecollege() {
                return sourcecollege;
            }

            public void setSourcecollege(String sourcecollege) {
                this.sourcecollege = sourcecollege;
            }

            public String getOptime() {
                return optime;
            }

            public void setOptime(String optime) {
                this.optime = optime;
            }
        }
    }
}
