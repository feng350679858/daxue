package com.jingcai.apps.aizhuan.service.business.base.base01;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

public class Base01Response extends BaseResponse<Base01Response.Body> {
    public class Body {
        private List<Banner> banner_list;

        public List<Banner> getBanner_list() {
            return banner_list;
        }

        public void setBanner_list(List<Banner> banner_list) {
            this.banner_list = banner_list;
        }

        public class Banner {
            private String title;
            private String imgurl;
            private String redirecturl;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImgurl() {
                return imgurl;
            }

            public void setImgurl(String imgurl) {
                this.imgurl = imgurl;
            }

            public String getRedirecturl() {
                return redirecturl;
            }

            public void setRedirecturl(String redirecturl) {
                this.redirecturl = redirecturl;
            }
        }
    }

}
