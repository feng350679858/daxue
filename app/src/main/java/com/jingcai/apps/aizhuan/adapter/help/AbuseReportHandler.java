package com.jingcai.apps.aizhuan.adapter.help;

import android.content.Context;
import android.os.Message;
import android.view.View;

import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob21.Partjob21Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.InnerLock;
import com.jingcai.apps.aizhuan.util.PopupWin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lejing on 15/8/5.
 */
public class AbuseReportHandler {
    private AzExecutor azExecutor = new AzExecutor();
    ;
    private AzService azService = new AzService();
    private InnerLock actionLock = new InnerLock();
    private MessageHandler messageHandler;

    private final BaseActivity activity;
    private Callback callback;

    public AbuseReportHandler(BaseActivity activity) {
        this.activity = activity;
        messageHandler = new MessageHandler(activity);
    }

    public AbuseReportHandler setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    /**
     *
     * @param targetid
     * @param contenttype 举报内容  1：求助 2：评论 3：答案 4：人
     * @param contentid
     */
    public void click(final String targetid, final String contenttype, final String contentid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("1", "非法广告");
        map.put("2", "色情淫秽");
        map.put("3", "虚假信息");
        map.put("4", "敏感信息");
        map.put("5", "恶意信息");
        map.put("6", "骚扰我");
        map.put("7", "其他问题");
        View parentView = activity.getWindow().getDecorView();
        final PopupWin win2 = PopupWin.Builder.create(activity)
                .setParentView(parentView)
                .setData(map, new PopupWin.Callback() {
                    @Override
                    public void select(String key, String val) {
                        if (!actionLock.tryLock()) {
                            return;
                        }
                        Partjob21Request req = new Partjob21Request();
                        Partjob21Request.Parttimejob job = req.new Parttimejob();
                        job.setSourceid(UserSubject.getStudentid());
                        job.setTargetid(targetid);
                        job.setType(key);
                        job.setContenttype(contenttype);
                        job.setContentid(contentid);
                        req.setParttimejob(job);
                        azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                            @Override
                            public void success(BaseResponse resp) {
                                if ("0".equals(resp.getResultCode())) {
                                    messageHandler.postMessage(0);
                                } else {
                                    messageHandler.postMessage(1);
                                }
                            }

                            @Override
                            public void fail(AzException e) {
                                messageHandler.postException(e);
                            }
                        });
                    }
                })
                .build();
        win2.show();

    }


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    try {
                        if (null != callback) {
                            callback.call();
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showInnerToast(String.valueOf(msg.obj));
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    public interface Callback {
        void call();
    }
}
