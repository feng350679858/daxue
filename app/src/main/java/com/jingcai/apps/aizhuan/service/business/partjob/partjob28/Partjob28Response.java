package com.jingcai.apps.aizhuan.service.business.partjob.partjob28;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/21.
 */
public class Partjob28Response extends BaseResponse<Partjob28Response.Body> {

    public static class Body {
        private List<Parttimejob> parttimejob_list;

        public List<Parttimejob> getParttimejob_list() {
            return parttimejob_list;
        }

        public void setParttimejob_list(List<Parttimejob> parttimejob_list) {
            this.parttimejob_list = parttimejob_list;
        }
    }

    public static class Parttimejob {
        private String topicid;
        private String topicname;

        public String getTopicid() {
            return topicid;
        }

        public void setTopicid(String topicid) {
            this.topicid = topicid;
        }

        public String getTopicname() {
            return topicname;
        }

        public void setTopicname(String topicname) {
            this.topicname = topicname;
        }
    }
}
