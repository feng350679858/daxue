package com.jingcai.apps.aizhuan.service.business.partjob.partjob29;

import com.jingcai.apps.aizhuan.adapter.help.CommentItem;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Partjob29Response extends BaseResponse<Partjob29Response.Body> {

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
        private String readflag;
        private Refcomment refcomment;
        private Reftarget reftarget;

        public String getContentid() {
            return contentid;
        }

        public void setContentid(String contentid) {
            this.contentid = contentid;
        }

        public String getContent() {
            return content;
        }

        public String getOptime() {
            return optime;
        }

        public String getPraisecount() {
            return praisecount;
        }

        public String getPraiseflag() {
            return praiseflag;
        }

        public String getPraiseid() {
            return praiseid;
        }

        public String getReadflag() {
            return readflag;
        }

        public Refcomment getRefcomment() {
            return refcomment;
        }

        public Reftarget getReftarget() {
            return reftarget;
        }

        public String getSourcecollege() {
            return sourcecollege;
        }

        public String getSourceid() {
            return sourceid;
        }

        public String getSourceimgurl() {
            return sourceimgurl;
        }

        public String getSourcelevel() {
            return sourcelevel;
        }

        public String getSourcename() {
            return sourcename;
        }

        public String getSourceschool() {
            return sourceschool;
        }

        public String getRefname(){
            if(null != refcomment){
                return refcomment.getRefname();
            }
            return null;
        }

        public String getRefcontent(){
            if(null != refcomment){
                return refcomment.getRefcontent();
            }
            return null;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setOptime(String optime) {
            this.optime = optime;
        }

        public void setPraisecount(String praisecount) {
            this.praisecount = praisecount;
        }

        public void setPraiseflag(String praiseflag) {
            this.praiseflag = praiseflag;
        }

        public void setPraiseid(String praiseid) {
            this.praiseid = praiseid;
        }

        public void setReadflag(String readflag) {
            this.readflag = readflag;
        }

        public void setRefcomment(Refcomment refcomment) {
            this.refcomment = refcomment;
        }

        public void setReftarget(Reftarget reftarget) {
            this.reftarget = reftarget;
        }

        public void setSourcecollege(String sourcecollege) {
            this.sourcecollege = sourcecollege;
        }

        public void setSourceid(String sourceid) {
            this.sourceid = sourceid;
        }

        public void setSourceimgurl(String sourceimgurl) {
            this.sourceimgurl = sourceimgurl;
        }

        public void setSourcelevel(String sourcelevel) {
            this.sourcelevel = sourcelevel;
        }

        public void setSourcename(String sourcename) {
            this.sourcename = sourcename;
        }

        public void setSourceschool(String sourceschool) {
            this.sourceschool = sourceschool;
        }
    }

    public static class Refcomment{
        private String refid;
        private String refname;
        private String refcontent;

        public String getRefname() {
            return refname;
        }

        public void setRefname(String refname) {
            this.refname = refname;
        }

        public String getRefid() {
            return refid;
        }

        public void setRefid(String refid) {
            this.refid = refid;
        }

        public String getRefcontent() {
            return refcontent;
        }

        public void setRefcontent(String refcontent) {
            this.refcontent = refcontent;
        }
    }

    public static class Reftarget{
        private String targetid;
        private String targettype;
        private String imgurl;
        private String studentname;
        private String publiccontent;

        public String getTargetid() {
            return targetid;
        }

        public void setTargetid(String targetid) {
            this.targetid = targetid;
        }

        public String getTargettype() {
            return targettype;
        }

        public void setTargettype(String targettype) {
            this.targettype = targettype;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getStudentname() {
            return studentname;
        }

        public void setStudentname(String studentname) {
            this.studentname = studentname;
        }

        public String getPubliccontent() {
            return publiccontent;
        }

        public void setPubliccontent(String publiccontent) {
            this.publiccontent = publiccontent;
        }
    }
}
