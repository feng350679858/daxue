package com.jingcai.apps.aizhuan.service.business.partjob.partjob23;


import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpListItem;
import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob23Response extends MineHelpResponse<Partjob23Response.Body> {

    @Override
    public List getList() {
        Body body = getBody();
        return null == body ? null : body.getParttimejob_list();
    }

    public static class Body {
        private List<Parttimejob> parttimejob_list;

        public List<Parttimejob> getParttimejob_list() {
            return parttimejob_list;
        }

        public void setParttimejob_list(List<Parttimejob> parttimejob_list) {
            this.parttimejob_list = parttimejob_list;
        }
    }

    public static class Parttimejob implements MineHelpListItem {
        private String helpid;
        private String optime;
        private String status;
        private String content;
        private String answerid;
        private String targetid;
        private String targetname;
        private String targetimgurl;
        private String targetschool;
        private String targetcollege;
        private String anonflag;

        public String getAnswerid() {
            return answerid;
        }

        public void setAnswerid(String answerid) {
            this.answerid = answerid;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTargetcollege() {
            return targetcollege;
        }

        public void setTargetcollege(String targetcollege) {
            this.targetcollege = targetcollege;
        }

        public String getTargetid() {
            return targetid;
        }

        public void setTargetid(String targetid) {
            this.targetid = targetid;
        }

        public String getTargetimgurl() {
            return targetimgurl;
        }

        public void setTargetimgurl(String targetimgurl) {
            this.targetimgurl = targetimgurl;
        }

        public String getTargetname() {
            return targetname;
        }

        public void setTargetname(String targetname) {
            this.targetname = targetname;
        }

        public String getTargetschool() {
            return targetschool;
        }

        public void setTargetschool(String targetschool) {
            this.targetschool = targetschool;
        }
    }
}
