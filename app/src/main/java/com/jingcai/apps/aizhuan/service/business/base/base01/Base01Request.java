package com.jingcai.apps.aizhuan.service.business.base.base01;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

public class Base01Request extends BaseRequest {
    private Banner banner;

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_BASE_01;
    }

    public class Banner {
        private String platform;
        private String type;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}