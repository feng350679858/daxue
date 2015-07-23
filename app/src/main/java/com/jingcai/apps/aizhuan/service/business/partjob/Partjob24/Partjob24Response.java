package com.jingcai.apps.aizhuan.service.business.partjob.Partjob24;



import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob24Response extends BaseResponse<Partjob24Response.Partjob24Body> {

    public static class Partjob24Body {
        private List<Region> region_list;

        public List<Region> getRegion_list() {
            return region_list;
        }

        public void setRegion_list(List<Region> region_list) {
            this.region_list = region_list;
        }

        public static class Region {
           private String helpid;
            private String optime;
            private String coin;
            private String status;
            private String content;
            private String sourceid;
            private String Sourcename;
            private String Sourceschool;
            private String Sourcecollege;

            public String getHelpid() {
                return helpid;
            }

            public void setHelpid(String helpid) {
                this.helpid = helpid;
            }

            public String getOptime() {
                return optime;
            }

            public void setOptime(String optime) {
                this.optime = optime;
            }

            public String getCoin() {
                return coin;
            }

            public void setCoin(String coin) {
                this.coin = coin;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
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
                return Sourcename;
            }

            public void setSourcename(String sourcename) {
                Sourcename = sourcename;
            }


            public String getSourceschool() {
                return Sourceschool;
            }

            public void setSourceschool(String sourceschool) {
                Sourceschool = sourceschool;
            }

            public String getSourcecollege() {
                return Sourcecollege;
            }

            public void setSourcecollege(String sourcecollege) {
                Sourcecollege = sourcecollege;
            }
        }
    }
}
