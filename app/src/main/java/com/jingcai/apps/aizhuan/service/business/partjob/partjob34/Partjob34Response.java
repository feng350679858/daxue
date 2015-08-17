package com.jingcai.apps.aizhuan.service.business.partjob.partjob34;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Partjob34Response extends BaseResponse<Partjob34Response.Body> {

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
        private String questionid;
        private String topiccontent;
        private String content;
        private String regionname;
        private String sourceid;
        private String sourcename;
        private String sourceimgurl;
        private String sourcelevel;
        private String sourceschool;
        private String sourcecollege;
        private String optime;
        private String praisecount;
        private String answercount;
        private String anonflag;
        private String helpflag;
        private String helperid;
        private String praiseflag;
        private String praiseid;

        public String getPraiseid() {
            return praiseid;
        }

        public void setPraiseid(String praiseid) {
            this.praiseid = praiseid;
        }

        public String getPraiseflag() {
            return praiseflag;
        }

        public void setPraiseflag(String praiseflag) {
            this.praiseflag = praiseflag;
        }

        public String getRegionname() {
            return regionname;
        }

        public void setRegionname(String regionname) {
            this.regionname = regionname;
        }

        public String getTopiccontent() {
            return topiccontent;
        }

        public void setTopiccontent(String topiccontent) {
            this.topiccontent = topiccontent;
        }

        public String getQuestionid() {
            return questionid;
        }

        public void setQuestionid(String questionid) {
            this.questionid = questionid;
        }

        public String getAnonflag() {
            return anonflag;
        }

        public void setAnonflag(String anonflag) {
            this.anonflag = anonflag;
        }

        public String getAnswercount() {
            return answercount;
        }

        public void setAnswercount(String answercount) {
            this.answercount = answercount;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getOptime() {
            return optime;
        }

        public void setOptime(String optime) {
            this.optime = optime;
        }

        public String getPraisecount() {
            return praisecount;
        }

        public void setPraisecount(String praisecount) {
            this.praisecount = praisecount;
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

        public String getSourcelevel() {
            return sourcelevel;
        }

        public void setSourcelevel(String sourcelevel) {
            this.sourcelevel = sourcelevel;
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

        public String getHelperid() {
            return helperid;
        }

        public void setHelperid(String helperid) {
            this.helperid = helperid;
        }

        public String getHelpflag() {
            return helpflag;
        }

        public void setHelpflag(String helpflag) {
            this.helpflag = helpflag;
        }
    }
}
