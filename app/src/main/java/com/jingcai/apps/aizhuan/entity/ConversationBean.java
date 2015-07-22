package com.jingcai.apps.aizhuan.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;

import java.io.Serializable;
import java.util.Date;

/**
 * 从消息会话中取得最后一条消息组装而成的联系人列表
 * 这个类对环信SDK依赖
 * Created by Json Ding on 2015/7/13.
 */
public class ConversationBean implements Serializable{
    public static final String TAG = "ConversationBean";
    private String studentid;
    private String logourl;
    private String name;
    private String content;
    private String time;
    private String unread;

    private AzService azService;
    private Context mContext;

    public ConversationBean(Context ctx,@NonNull EMConversation conversation){
        EMMessage message = conversation.getLastMessage();
        mContext = ctx;
        azService = new AzService(ctx);

        this.unread = String.valueOf(conversation.getUnreadMsgCount());  //未读消息
        this.studentid = conversation.getUserName();  //对方的用户名
        this.time = DateUtils.getTimestampString(new Date(message.getMsgTime()));
        switch (message.getType()){
            case TXT:
                this.content = ((TextMessageBody) message.getBody()).getMessage();
                break;
            case IMAGE:
                this.content = "[图片]";
                break;
            case VOICE:
                this.content = "[语音]";
                break;
        }
        if(message.getFrom().contains("admin")){
            this.name = "宿管阿姨";
            this.logourl = "http://img3.imgtn.bdimg.com/it/u=3681476745,3832605124&fm=21&gp=0.jpg";
            return;
        }
        getStudentInfo(azService,studentid);
    }

    private void getStudentInfo(final AzService azService, final String studentid) {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(studentid);
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        student.setStudentid(studentid);
                        if("0".equals(result.getCode())) {
                            ConversationBean.this.name = student.getName();
                            ConversationBean.this.logourl = student.getLogopath();
                        }
                    }
                    @Override
                    public void fail(AzException e) {
                        Log.e(TAG,"Transcode : stu02 failed.Code:"+e.getCode()+",Message:"+e.getMessage());
                    }
                });
            }
        });

    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }
}
