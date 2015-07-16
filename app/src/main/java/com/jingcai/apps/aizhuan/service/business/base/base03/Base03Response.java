package com.jingcai.apps.aizhuan.service.business.base.base03;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

public class Base03Response extends BaseResponse<Base03Response.Base03Body> {
    public class Base03Body {
        private Loading loading;

        public Loading getLoading() {
            return loading;
        }

        public void setLoading(Loading loading) {
            this.loading = loading;
        }

        public class Loading {
            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }

}
