package com.jingcai.apps.aizhuan.service.business.partjob.partjob15;

import com.jingcai.apps.aizhuan.adapter.help.CommentItem;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/21.
 */
public class Partjob15Response extends BaseResponse<Partjob15Response.Body> {

    public static class Body {
        private List<Parttimejob> parttimejob_list;

        public List<Parttimejob> getParttimejob_list() {
            return parttimejob_list;
        }

        public void setParttimejob_list(List<Parttimejob> parttimejob_list) {
            this.parttimejob_list = parttimejob_list;
        }
    }

    public static class Parttimejob extends CommentItem {
        private String contentid;
        private String sourceid;
        private String sourceimgurl;
        private String sourcelevel;
        private String sourcename;
        private String sourceschool;
        private String sourcecollege;
        private String optime;
        private String content;
        private String praisecount;
        private String praiseflag;
        private String praiseid;
        private String anonflag;

        @Override
        public String getContentid() {
            return contentid;
        }

        public void setContentid(String contentid) {
            this.contentid = contentid;
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

        @Override
        public String getPraiseflag() {
            return praiseflag;
        }

        public void setPraiseflag(String praiseflag) {
            this.praiseflag = praiseflag;
        }

        @Override
        public String getPraiseid() {
            return praiseid;
        }

        public void setPraiseid(String praiseid) {
            this.praiseid = praiseid;
        }

        @Override
        public String getRefname() {
            return null;
        }

        @Override
        public String getRefcontent() {
            return null;
        }
    }
}
