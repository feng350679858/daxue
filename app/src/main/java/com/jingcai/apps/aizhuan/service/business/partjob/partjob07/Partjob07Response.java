package com.jingcai.apps.aizhuan.service.business.partjob.partjob07;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class Partjob07Response extends BaseResponse<Partjob07Response.Partjob07Body> {

    public class Partjob07Body {
        private List<Merchant> merchant_list;

        public List<Merchant> getMerchant_list() {
            return merchant_list;
        }

        public void setMerchant_list(List<Merchant> merchant_list) {
            this.merchant_list = merchant_list;
        }

        public class Merchant{
            private String phone;
            private String name;
            private String logopath;
            private String jobtitle;
            private String worktype;
            private String workdays;
            private String joindate;

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLogopath() {
                return logopath;
            }

            public void setLogopath(String logopath) {
                this.logopath = logopath;
            }

            public String getJobtitle() {
                return jobtitle;
            }

            public void setJobtitle(String jobtitle) {
                this.jobtitle = jobtitle;
            }

            public String getWorktype() {
                return worktype;
            }

            public void setWorktype(String worktype) {
                this.worktype = worktype;
            }

            public String getWorkdays() {
                return workdays;
            }

            public void setWorkdays(String workdays) {
                this.workdays = workdays;
            }

            public String getJoindate() {
                return joindate;
            }

            public void setJoindate(String joindate) {
                this.joindate = joindate;
            }
        }
    }

}
