package com.jingcai.apps.aizhuan.service.business.busi.busi01;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

public class Busi01Response extends BaseResponse<Busi01Response.Body> {
    public class Body {
        private List<Label> label_list;

        public List<Label> getLabel_list() {
            return label_list;
        }

        public void setLabel_list(List<Label> label_list) {
            this.label_list = label_list;
        }

        public class Label {
            private String id;
            private String name;
            private String text;
            private String imgurl;

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
        }
    }

}
