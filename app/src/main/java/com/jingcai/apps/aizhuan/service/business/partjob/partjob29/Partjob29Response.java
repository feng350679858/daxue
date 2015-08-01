package com.jingcai.apps.aizhuan.service.business.partjob.partjob29;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Partjob29Response extends BaseResponse<Partjob29Response.Partjob29Body> {

    public static class Partjob29Body {

        private List<Parttimejob> parttimejob_list;

        public List<Parttimejob> getParttimejob_list() {
            return parttimejob_list;
        }

        public void setParttimejob_list(List<Parttimejob> parttimejob_list) {
            this.parttimejob_list = parttimejob_list;
        }

        public static class Parttimejob {
            private String sourceid;
            private String sourceimgurl;
            private String sourcelevel;
            private String sourcename;
            private String sourceschool;
            private String sourcecollege;
            private String optime;
            private String contentid;
            private String content;
            private String praisecount;
            private Refcomment refcomment;
            private Reftarget reftarget;

            public static class Refcomment{
                private String refid;
                private String refcontent;

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

            public String getContentid() {
                return contentid;
            }

            public void setContentid(String contentid) {
                this.contentid = contentid;
            }

            public Refcomment getRefcomment() {
                return refcomment;
            }

            public void setRefcomment(Refcomment refcomment) {
                this.refcomment = refcomment;
            }

            public Reftarget getReftarget() {
                return reftarget;
            }

            public void setReftarget(Reftarget reftarget) {
                this.reftarget = reftarget;
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

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPraisecount() {
                return praisecount;
            }

            public void setPraisecount(String praisecount) {
                this.praisecount = praisecount;
            }
        }
    }
}
