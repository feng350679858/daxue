package com.jingcai.apps.aizhuan.service.business.partjob.partjob37;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
