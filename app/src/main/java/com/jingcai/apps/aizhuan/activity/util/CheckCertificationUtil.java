package com.jingcai.apps.aizhuan.activity.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;

import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.mine.MineStudentCertificationActivity;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu08.Stu08Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.InnerLock;

/**
 * Created by lejing on 15/8/17.
 */
public class CheckCertificationUtil {
    private MessageHandler messageHandler;
    private InnerLock actionLock = new InnerLock();
    private final Activity activity;
    private Callback callback;
    private PopConfirmWin onlineCheckWin;

    public CheckCertificationUtil(Activity activity) {
        this.activity = activity;
        this.messageHandler = new MessageHandler(activity);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 检查是否已认证
     *
     * @return
     */
    public boolean checkCertification() {
        if ("0".equals(UserSubject.getScnoauthflag())) {
            final PopConfirmWin win = new PopConfirmWin(activity);
            win.setTitle(null).setContent("为了促进平台的健康发展并保证用户双方的安全，我们需要对帮助者进行学生信息认证，还请见谅")
                    .setOkAction("前往认证", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            win.dismiss();
                            Intent intent = new Intent(activity, MineStudentCertificationActivity.class);
                            activity.startActivity(intent);
                        }
                    }).setCancelAction(null);
            win.show();
            return false;
        } else if ("2".equals(UserSubject.getScnoauthflag())) {
            final PopConfirmWin win = new PopConfirmWin(activity);
            win.setTitle(null).setContent("当前你的账号还处于学生信息认证中，请耐心等待，我们会尽快处理")
                    .setOkAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            win.dismiss();
                        }
                    }).setCancelAction(null);
            win.show();
            return false;
        }
        return true;
    }


    public void onoffline(final boolean onlineFlag) {
        if (!actionLock.tryLock()) return;
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu08Request req = new Stu08Request();
                Stu08Request.Student stu = req.new Student();
                stu.setStudentid(UserSubject.getStudentid());
                stu.setOptype(onlineFlag ? "1" : "2");//1上线 2下线
                stu.setGisx(GlobalConstant.gis.getGisx());
                stu.setGisy(GlobalConstant.gis.getGisy());
                req.setStudent(stu);
                new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        ResponseResult result = resp.getResult();
                        if ("0".equals(result.getCode())) {
                            messageHandler.postMessage(9, onlineFlag);
                        } else {
                            messageHandler.postMessage(10, result.getMessage());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }

    public boolean checkOnline() {
        if (UserSubject.getOnlineFlag()) return true;
        if (null == onlineCheckWin) {
            onlineCheckWin = new PopConfirmWin(activity);
            onlineCheckWin.setTitle(null).setContent("为了确保平台的正常进行，如果你想要帮助别人，需要上线才能接单")
                    .setOkAction("立即上线", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onoffline(true);
                        }
                    }).setCancelAction(null);
        }
        onlineCheckWin.show();
        return false;
    }


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 9: {
                    try {
                        boolean onlineFlag = (boolean) msg.obj;
                        UserSubject.setOnlineFlag(onlineFlag);
                        showInnerToast((onlineFlag ? "上线" : "下线") + "成功");
                        if (null != onlineCheckWin && onlineCheckWin.inShowing()) {
                            onlineCheckWin.dismiss();
                        }
                        if (null != callback) {
                            callback.online(onlineFlag);
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 10: {
                    try {
                        showInnerToast("上下线失败");
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
        void online(boolean onlineFlag);
    }
}
