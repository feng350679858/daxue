package com.jingcai.apps.aizhuan.adapter.mine.help;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by lejing on 15/8/13.
 */
public abstract class MineHelpResponse<T> extends BaseResponse<T> {
    public abstract List getList();
}
