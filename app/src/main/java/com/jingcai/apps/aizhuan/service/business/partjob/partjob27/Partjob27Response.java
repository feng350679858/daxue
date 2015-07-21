package com.jingcai.apps.aizhuan.service.business.partjob.partjob27;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/21.
 */
public class Partjob27Response extends BaseResponse<Partjob27Response.Partjob27Body> {

    public static class Partjob27Body {
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
