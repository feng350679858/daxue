package com.jingcai.apps.aizhuan.service.business.partjob.partjob17;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob17Request extends BaseRequest {

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_17;
    }

    private Parttimejob parttimejob;

    public Parttimejob getParttimejob() {
        return parttimejob;
    }

    public void setParttimejob(Parttimejob parttimejob) {
        this.parttimejob = parttimejob;
    }

    public class Parttimejob {
        private String studentid;
        private String topicid;
        private String topiccontent;
        private String content;
        private String regionid;
        private String anonflag;
        private String gisx;
        private String gisy;

        public String getGisx() {
            return gisx;
        }

        public void setGisx(String gisx) {
            this.gisx = gisx;
        }

        public String getGisy() {
            return gisy;
        }

        public void setGisy(String gisy) {
            this.gisy = gisy;
        }

        public String getAnonflag() {
            return anonflag;
        }

        public void setAnonflag(String anonflag) {
            this.anonflag = anonflag;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRegionid() {
            return regionid;
        }

        public void setRegionid(String regionid) {
            this.regionid = regionid;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getTopiccontent() {
            return topiccontent;
        }

        public void setTopiccontent(String topiccontent) {
            this.topiccontent = topiccontent;
        }

        public String getTopicid() {
            return topicid;
        }

        public void setTopicid(String topicid) {
            this.topicid = topicid;
        }
    }
}
