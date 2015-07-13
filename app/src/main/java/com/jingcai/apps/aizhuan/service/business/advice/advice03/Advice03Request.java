package com.jingcai.apps.aizhuan.service.business.advice.advice03;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Advice03Request extends BaseRequest {
    @Override
    public String getTranscode() {
        return BizConstant.BIZ_ADVICE_03;
    }

   private Suggestion suggestion;

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public class Suggestion {
        private String studentid;
        private String jobid;
        private String channel;
        private String type;
        private String content;
        private String joinid;

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getJobid() {
            return jobid;
        }

        public void setJobid(String jobid) {
            this.jobid = jobid;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setJoinid(String joinid) {
            this.joinid = joinid;
        }

        public String getJoinid() {
            return joinid;
        }
    }
}
