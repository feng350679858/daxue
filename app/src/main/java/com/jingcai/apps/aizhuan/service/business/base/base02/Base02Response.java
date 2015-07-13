package com.jingcai.apps.aizhuan.service.business.base.base02;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

public class Base02Response extends BaseResponse<Base02Response.Base02Body> {
    public class Base02Body {
        private Version version;

        public Version getVersion() {
            return version;
        }

        public void setVersion(Version version) {
            this.version = version;
        }

        public class Version {
            private String code;
            private String name;
            private String platform;
            private String forceupdate;
            private String url;
            private String remark;
            private String packagesize;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPlatform() {
                return platform;
            }

            public void setPlatform(String platform) {
                this.platform = platform;
            }

            public String getForceupdate() {
                return forceupdate;
            }

            public void setForceupdate(String forceupdate) {
                this.forceupdate = forceupdate;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getPackagesize() {
                return packagesize;
            }

            public void setPackagesize(String packagesize) {
                this.packagesize = packagesize;
            }
        }
    }

}
