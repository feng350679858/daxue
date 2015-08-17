package com.jingcai.apps.aizhuan.service.business.partjob.partjob16;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Administrator on 2015/7/21.
 */
public class Partjob16Response extends BaseResponse<Partjob16Response.Body> {

    public static class Body {
        private Parttimejob parttimejob;

        public Parttimejob getParttimejob() {
            return parttimejob;
        }

        public void setParttimejob(Parttimejob parttimejob) {
            this.parttimejob = parttimejob;
        }
    }

    public static class Parttimejob {
        private String helpid;

        public String getHelpid() {
            return helpid;
        }

        public void setHelpid(String helpid) {
            this.helpid = helpid;
        }
    }

}
