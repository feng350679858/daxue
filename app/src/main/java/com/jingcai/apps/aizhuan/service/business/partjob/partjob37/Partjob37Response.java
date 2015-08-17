package com.jingcai.apps.aizhuan.service.business.partjob.partjob37;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Partjob37Response extends BaseResponse<Partjob37Response.Body> {

    public static class Body {

        private Parttimejob parttimejob;

        public Parttimejob getParttimejob() {
            return parttimejob;
        }

        public void setParttimejob(Parttimejob parttimejob) {
            this.parttimejob = parttimejob;
    }
    }

    public static class Parttimejob {
        private String status;
        private String code;
        private String description;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
