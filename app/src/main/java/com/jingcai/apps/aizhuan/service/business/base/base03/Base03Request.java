package com.jingcai.apps.aizhuan.service.business.base.base03;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

public class Base03Request extends BaseRequest {
    private Loading loading;

    public Loading getLoading() {
        return loading;
    }

    public void setLoading(Loading loading) {
        this.loading = loading;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_BASE_03;
    }

    public class Loading {
        private String platform;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }
}