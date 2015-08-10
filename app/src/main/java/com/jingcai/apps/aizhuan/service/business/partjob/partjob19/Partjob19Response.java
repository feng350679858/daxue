package com.jingcai.apps.aizhuan.service.business.partjob.partjob19;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Administrator on 2015/7/21.
 */
public class Partjob19Response extends BaseResponse<Partjob19Response.Body> {

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
        private String publiccontent;
        private String privatecontent;
        private String money;
        private String sourceid;
        private String sourcename;
        private String sourceimgurl;
        private String sourcelevel;
        private String sourceschool;
        private String sourcecollege;
        private String optime;
        private String targetid;
        private String targetname;
        private String targetimgurl;
        private String commentcount;
        private String praisecount;
        private String praiseflag;
        private String praiseid;
        private String genderlimit;
        private String status;

        public String getGenderlimit() {
            return genderlimit;
        }

        public void setGenderlimit(String genderlimit) {
            this.genderlimit = genderlimit;
        }

        public String getPraiseflag() {
            return praiseflag;
        }

        public void setPraiseflag(String praiseflag) {
            this.praiseflag = praiseflag;
        }

        public String getPraiseid() {
            return praiseid;
        }

        public void setPraiseid(String praiseid) {
            this.praiseid = praiseid;
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

        public String getCommentcount() {
            return commentcount;
        }

        public void setCommentcount(String commentcount) {
            this.commentcount = commentcount;
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

        public String getPraisecount() {
            return praisecount;
        }

        public void setPraisecount(String praisecount) {
            this.praisecount = praisecount;
        }

        public String getPrivatecontent() {
            return privatecontent;
        }

        public void setPrivatecontent(String privatecontent) {
            this.privatecontent = privatecontent;
        }

        public String getPubliccontent() {
            return publiccontent;
        }

        public void setPubliccontent(String publiccontent) {
            this.publiccontent = publiccontent;
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

        public String getSourcelevel() {
            return sourcelevel;
        }

        public void setSourcelevel(String sourcelevel) {
            this.sourcelevel = sourcelevel;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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
    }
}
