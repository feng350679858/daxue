package com.jingcai.apps.aizhuan.adapter.help;

import android.content.Context;
import android.os.Message;
import android.widget.CheckBox;

import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob12.Partjob12Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob12.Partjob12Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob30.Partjob30Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.InnerLock;

/**
 * Created by lejing on 15/8/4.
 */
public class LikeHandler {
    private AzExecutor azExecutor = new AzExecutor();;
    private AzService azService = new AzService();
    private InnerLock actionLock = new InnerLock();
    private MessageHandler messageHandler;

    private final Context context;
    private Callback callback;
    private int count;

    public LikeHandler(Context context){
        this.context = context;
        messageHandler = new MessageHandler(context);
    }

    public LikeHandler setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 点赞
     * @param targettype 1：求助  2：问题 3：答案 4：评论本身 5、求助公告
     */
    public void click(final String targettype, final String targetid, final String praiseid, final CheckBox checkBox){
        if(!actionLock.tryLock()){
            return ;
        }
        try {
            count = Integer.parseInt(checkBox.getText().toString());
        }catch (Exception e){count = 0;}
        boolean checked = !checkBox.isChecked();
        if (checked) {//点赞
            azExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Partjob12Request req = new Partjob12Request();
                    Partjob12Request.Parttimejob p = req.new Parttimejob();
                    p.setSourceid(UserSubject.getStudentid());
                    p.setTargettype(targettype);

                    p.setTargetid(targetid);
                    p.setOptype("2");//1：评论 2：点赞
                    req.setParttimejob(p);
                    azService.doTrans(req, Partjob12Response.class, new AzService.Callback<Partjob12Response>() {
                        @Override
                        public void success(Partjob12Response resp) {
                            if ("0".equals(resp.getResultCode())) {
                                Partjob12Response.Parttimejob parttimejob = resp.getBody().getParttimejob();
                                String praiseid = parttimejob.getCommentid();
                                String praisecount = parttimejob.getPraisecount();
                                Object[] objs = new Object[]{checkBox, true, praisecount, praiseid};
                                messageHandler.postMessage(3, objs);
                            } else {
                                messageHandler.postMessage(4, "点赞失败");
                            }
                        }

                        @Override
                        public void fail(AzException e) {
                            messageHandler.postException(e);
                        }
                    });
                }
            });
        }else {
            azExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Partjob30Request req = new Partjob30Request();
                    Partjob30Request.Parttimejob p = req.new Parttimejob();
                    p.setCommentid(praiseid);
                    p.setType("2");//1：评论 2：赞
                    req.setParttimejob(p);
                    azService.doTrans(req, Partjob12Response.class, new AzService.Callback<Partjob12Response>() {
                        @Override
                        public void success(Partjob12Response resp) {
                            if ("0".equals(resp.getResultCode())) {
                                String praisecount = resp.getBody().getParttimejob().getPraisecount();
                                Object[] objs = new Object[]{checkBox, false, praisecount, null};
                                messageHandler.postMessage(3, objs);
                            } else {
                                messageHandler.postMessage(4, "取消赞失败");
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
    }


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3: {
                    try {
                        Object[] objs = (Object[]) msg.obj;
                        CheckBox cb = (CheckBox) objs[0];
                        boolean flag = (boolean) objs[1];
                        String pariseCount = (String) objs[2];

                        cb.setChecked(flag);
                        cb.setText(pariseCount);
                        if(null != callback) {
                            if(flag) {
                                String praiseid = (String) objs[3];
                                callback.like(praiseid, cb);
                            }else{
                                callback.unlike(cb);
                            }
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 4: {
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

    public interface Callback{
        void like(String praiseid, CheckBox checkBox);

        void unlike(CheckBox checkBox);
    }
}
