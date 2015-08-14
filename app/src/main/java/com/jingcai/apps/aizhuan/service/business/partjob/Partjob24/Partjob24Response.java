package com.jingcai.apps.aizhuan.service.business.partjob.Partjob24;


import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpListItem;
import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob24Response extends MineHelpResponse<Partjob24Response.Body> {

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
        private String money;
        private String gender;
        private String status;
        private String content;
        private String sourceid;
        private String sourcename;
        private String sourceimgurl;
        private String sourceschool;
        private String sourcecollege;
        private String sourcegender;
        private String evelflag;

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getEvelflag() {
            return evelflag;
        }

        public void setEvelflag(String evelflag) {
            this.evelflag = evelflag;
        }

        public String getSourcegender() {
            return sourcegender;
        }

        public void setSourcegender(String sourcegender) {
            this.sourcegender = sourcegender;
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

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getOptime() {
            return optime;
        }

        public void setOptime(String optime) {
            this.optime = optime;
        }

        public String getSourcecollege() {
            return sourcecollege;
        }

        public void setSourcecollege(String sourcecollege) {
            this.sourcecollege = sourcecollege;
        }

        public String getSourceid() {
            return sourceid;
        }

        public void setSourceid(String sourceid) {
            this.sourceid = sourceid;
        }

        public String getSourceimgurl() {
            return sourceimgurl;
        }

        public void setSourceimgurl(String sourceimgurl) {
            this.sourceimgurl = sourceimgurl;
        }

        public String getSourcename() {
            return sourcename;
        }

        public void setSourcename(String sourcename) {
            this.sourcename = sourcename;
        }

        public String getSourceschool() {
            return sourceschool;
        }

        public void setSourceschool(String sourceschool) {
            this.sourceschool = sourceschool;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
