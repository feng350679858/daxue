package com.jingcai.apps.aizhuan.service.business.busi.busi01;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

public class Busi01Request extends BaseRequest {
    private Label label;

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_BUSI_01;
    }

    public class Label {
        private String platform;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }
}