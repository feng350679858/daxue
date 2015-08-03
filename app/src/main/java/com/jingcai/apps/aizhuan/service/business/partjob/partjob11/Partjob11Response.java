package com.jingcai.apps.aizhuan.service.business.partjob.partjob11;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob11Response extends BaseResponse<Partjob11Response.Body> {

    public class Body {
        private List<Parttimejob> parttimejob_list;

        public List<Parttimejob> getParttimejob_list() {
            return parttimejob_list;
        }

        public void setParttimejob_list(List<Parttimejob> parttimejob_list) {
            this.parttimejob_list = parttimejob_list;
        }

    }
    public static class Parttimejob {
        private String helpid;
        private String type;
        private String money;
        private String title;
        private String content;
        private String genderlimit;
        private String sourceid;
        private String sourcename;
        private String sourceimgurl;
        private String sourcelevel;
        private String sourceschool;
        private String sourcecollege;
        private String optime;
        private String praisecount;
        private String commentcount;
        private String status;
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

        public String getPraiseflag() {
            return praiseflag;
        }

        public void setPraiseflag(String praiseflag) {
            this.praiseflag = praiseflag;
        }

        public String getSourcelevel() {
            return sourcelevel;
        }

        public void setSourcelevel(String sourcelevel) {
            this.sourcelevel = sourcelevel;
        }

        public String getSourceimgurl() {
            return sourceimgurl;
        }

        public void setSourceimgurl(String sourceimgurl) {
            this.sourceimgurl = sourceimgurl;
        }

        public String getHelpid() {
            return helpid;
        }

        public void setHelpid(String helpid) {
            this.helpid = helpid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSourceid() {
            return sourceid;
        }

        public void setSourceid(String sourceid) {
            this.sourceid = sourceid;
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

        public String getSourcecollege() {
            return sourcecollege;
        }

        public void setSourcecollege(String sourcecollege) {
            this.sourcecollege = sourcecollege;
        }

        public String getOptime() {
            return optime;
        }

        public void setOptime(String optime) {
            this.optime = optime;
        }

        public String getCommentcount() {
            return commentcount;
        }

        public void setCommentcount(String commentcount) {
            this.commentcount = commentcount;
        }

        public String getGenderlimit() {
            return genderlimit;
        }

        public void setGenderlimit(String genderlimit) {
            this.genderlimit = genderlimit;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getPraisecount() {
            return praisecount;
        }

        public void setPraisecount(String praisecount) {
            this.praisecount = praisecount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
