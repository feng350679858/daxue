package com.jingcai.apps.aizhuan.adapter.help;

import android.content.Context;
import android.os.Message;

import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob35.Partjob35Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.InnerLock;

/**
 * Created by lejing on 15/8/5.
 */
public class AnonHandler {
    private AzExecutor azExecutor = new AzExecutor();
    ;
    private AzService azService = new AzService();
    private InnerLock actionLock = new InnerLock();
    private MessageHandler messageHandler;

    private final Context context;
    private Callback callback;

    public AnonHandler(Context context) {
        this.context = context;
        messageHandler = new MessageHandler(context);
    }

    public AnonHandler setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * @param targettype 1问题  2答案
     */
    public void click(final String targettype, final String targetid) {
        if (!actionLock.tryLock()) {
            return;
        }
        Partjob35Request req = new Partjob35Request(UserSubject.getStudentid(), targettype, targetid);
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
