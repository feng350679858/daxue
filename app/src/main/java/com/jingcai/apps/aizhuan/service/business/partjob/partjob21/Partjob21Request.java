package com.jingcai.apps.aizhuan.service.business.partjob.partjob21;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob21Request extends BaseRequest{

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_21;
    }
    private Parttimejob parttimejob;

    public Parttimejob getParttimejob() {
        return parttimejob;
    }

    public void setParttimejob(Parttimejob parttimejob) {
        this.parttimejob = parttimejob;
    }

    public class Parttimejob{
        private String sourceid;
        private String targetid;
        private String type;
        private String contentid;
        private String contenttype;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentid() {
            return contentid;
        }

        public void setContentid(String contentid) {
            this.contentid = contentid;
        }

        public String getContenttype() {
            return contenttype;
        }

        public void setContenttype(String contenttype) {
            this.contenttype = contenttype;
        }
    }
}
