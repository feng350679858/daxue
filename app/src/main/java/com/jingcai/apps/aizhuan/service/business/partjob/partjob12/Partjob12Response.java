package com.jingcai.apps.aizhuan.service.business.partjob.partjob12;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob12Response extends BaseResponse<Partjob12Response.Body> {

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
        private String commentid;

        public String getCommentid() {
            return commentid;
        }

        public void setCommentid(String commentid) {
            this.commentid = commentid;
        }
    }
}
