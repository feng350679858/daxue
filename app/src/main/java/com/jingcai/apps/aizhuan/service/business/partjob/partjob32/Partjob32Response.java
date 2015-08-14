package com.jingcai.apps.aizhuan.service.business.partjob.partjob32;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/8/14.
 */
public class Partjob32Response extends BaseResponse<Partjob32Response.Body> {
    public class Body {

        private Parttimejob parttimejob;

        public Parttimejob getParttimejob() {
            return parttimejob;
        }

        public void setParttimejob(Parttimejob parttimejob) {
            this.parttimejob = parttimejob;
        }

        public class  Parttimejob{
            private String seekcount;
            private String helpcount;

            public String getHelpcount() {
                return helpcount;
            }

            public void setHelpcount(String helpcount) {
                this.helpcount = helpcount;
            }

            public String getSeekcount() {
                return seekcount;
            }

            public void setSeekcount(String seekcount) {
                this.seekcount = seekcount;
            }
        }
    }
}
