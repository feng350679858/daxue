package com.jingcai.apps.aizhuan.service.business.busi.busi02;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

public class Busi02Response extends BaseResponse<Busi02Response.Body> {
    public class Body {
        private List<Recommend> recommend_list;

        public List<Recommend> getRecommend_list() {
            return recommend_list;
        }

        public void setRecommend_list(List<Recommend> recommend_list) {
            this.recommend_list = recommend_list;
        }

        public class Recommend {
            private String id;
            private String name;
            private String text;
            private String imgurl;
            private String type;
            private String logopath;
            private String areaname;
            private String width;
            private String height;
            private String salary;
            private String salaryunit;

            public String getSalary() {
                return salary;
            }

            public void setSalary(String salary) {
                this.salary = salary;
            }

            public String getSalaryunit() {
                return salaryunit;
            }

            public void setSalaryunit(String salaryunit) {
                this.salaryunit = salaryunit;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getImgurl() {
                return imgurl;
            }

            public void setImgurl(String imgurl) {
                this.imgurl = imgurl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getLogopath() {
                return logopath;
            }

            public void setLogopath(String logopath) {
                this.logopath = logopath;
            }

            public String getAreaname() {
                return areaname;
            }

            public void setAreaname(String areaname) {
                this.areaname = areaname;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }
        }
    }

}
