package com.jingcai.apps.aizhuan.service.business.stu.stu12;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/8/8.
 */
public class Stu12Response extends BaseResponse<Stu12Response.Body> {
    public static class Body {
        private List<Evaluate> evaluate_list;

        public List<Evaluate> getEvaluate_list() {
            return evaluate_list;
        }

        public void setEvaluate_list(List<Evaluate> evaluate_list) {
            this.evaluate_list = evaluate_list;
        }

        public static class Evaluate{
            private String evaluateid;
            private String sourceid;
            private String sourcename;
            private String sourceimgurl;
            private String optime;
            private String score;
            private String title;
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getEvaluateid() {
                return evaluateid;
            }

            public void setEvaluateid(String evaluateid) {
                this.evaluateid = evaluateid;
            }

            public String getOptime() {
                return optime;
            }

            public void setOptime(String optime) {
                this.optime = optime;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
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

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
