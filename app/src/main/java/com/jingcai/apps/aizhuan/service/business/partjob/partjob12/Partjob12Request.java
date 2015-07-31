package com.jingcai.apps.aizhuan.service.business.partjob.partjob12;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob12Request extends BaseRequest {

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_12;
    }

    private Parttimejob parttimejob;

    public Parttimejob getParttimejob() {
        return parttimejob;
    }

    public void setParttimejob(Parttimejob parttimejob) {
        this.parttimejob = parttimejob;
    }

    public class Parttimejob {
        private String sourceid;//发表者学生id
        private String targettype;//1：求助  2：问题 3：答案 4：评论本身 5、求助公告
        private String targetid;
        private String optype;//评论类型 1：评论 2：点赞
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getOptype() {
            return optype;
        }

        public void setOptype(String optype) {
            this.optype = optype;
        }

        public String getSourceid() {
            return sourceid;
        }

        public void setSourceid(String sourceid) {
            this.sourceid = sourceid;
        }

        public String getTargetid() {
            return targetid;
        }

        public void setTargetid(String targetid) {
            this.targetid = targetid;
        }

        public String getTargettype() {
            return targettype;
        }

        public void setTargettype(String targettype) {
            this.targettype = targettype;
        }
    }
}
