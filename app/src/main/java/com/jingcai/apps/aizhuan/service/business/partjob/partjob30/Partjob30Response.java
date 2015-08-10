package com.jingcai.apps.aizhuan.service.business.partjob.partjob30;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob30Response extends BaseResponse<Partjob30Response.Body> {

    public class Body {
        private Parttimejob parttimejob;

        public Parttimejob getParttimejob() {
            return parttimejob;
        }

        public void setParttimejob(Parttimejob parttimejob) {
            this.parttimejob = parttimejob;
        }
    }

    public static class Parttimejob {
        private String praisecount;

        public String getPraisecount() {
            return praisecount;
        }

        public void setPraisecount(String praisecount) {
            this.praisecount = praisecount;
        }
    }
}
