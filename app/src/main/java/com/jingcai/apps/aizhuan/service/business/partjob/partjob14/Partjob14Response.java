package com.jingcai.apps.aizhuan.service.business.partjob.partjob14;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob13.Partjob13Request;

import java.util.List;

/**
 * Created by Administrator on 2015/7/21.
 */
public class Partjob14Response extends BaseResponse<Partjob14Response.Body> {

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
        private String helperid;

        public String getHelperid() {
            return helperid;
        }

        public void setHelperid(String helperid) {
            this.helperid = helperid;
        }
    }
}
