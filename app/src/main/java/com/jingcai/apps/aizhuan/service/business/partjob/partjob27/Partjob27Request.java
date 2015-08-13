package com.jingcai.apps.aizhuan.service.business.partjob.partjob27;

import com.jingcai.apps.aizhuan.service.business.BizConstant;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob18.Partjob18Request;

/**
 * Created by Administrator on 2015/7/21.
 */
public class Partjob27Request extends Partjob18Request {
    public Partjob27Request(String studentid, String start, String pagesize) {
        super(studentid, start, pagesize);
    }

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_27;
    }
}
