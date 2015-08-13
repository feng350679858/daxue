package com.jingcai.apps.aizhuan.service.business.partjob.partjob23;

import com.jingcai.apps.aizhuan.service.business.BizConstant;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob18.Partjob18Request;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob23Request extends Partjob18Request {
    public Partjob23Request(String studentid, String start, String pagesize) {
        super(studentid, start, pagesize);
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_23;
    }
}
