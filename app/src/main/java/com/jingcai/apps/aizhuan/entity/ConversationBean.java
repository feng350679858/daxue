package com.jingcai.apps.aizhuan.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;

import java.util.Date;

/**
 * 从消息会话中取得最后一条消息组装而成的联系人列表
 * 这个类对环信SDK依赖
 * Created by Json Ding on 2015/7/13.
 */
public class ConversationBean implements Parcelable{
    public static final String TAG = "ConversationBean";
    private String studentid;
    private String logourl;
    private String name;
    private String content;
    private String time;
    private String unread;

    public static final Parcelable.Creator<ConversationBean> CREATOR = new Parcelable.Creator<ConversationBean>() {
        @Override
        public ConversationBean createFromParcel(Parcel source) {
            return new ConversationBean(source);
        }
        //重写createFromParcel方法，创建并返回一个获得了数据的ConversationBean对象
        @Override
        public ConversationBean[] newArray(int size) {
            return new ConversationBean[size];
        }
    };

    public ConversationBean(String studentid, String logourl, String name) {
        this.studentid = studentid;
        this.logourl = logourl;
        this.name = name;
    }
    private ConversationBean(Parcel source){
        studentid = source.readString();
        logourl = source.readString();
        name = source.readString();
        content = source.readString();
        time = source.readString();
        unread = source.readString();
    }

    public ConversationBean(@NonNull EMConversation conversation){
        EMMessage message = conversation.getLastMessage();
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
        if(studentid.contains("admin")){
            this.name = "宿管阿姨";
            this.logourl = "http://img3.imgtn.bdimg.com/it/u=3681476745,3832605124&fm=21&gp=0.jpg";
            return;
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(studentid);
        dest.writeString(logourl);
        dest.writeString(name);
        dest.writeString(content);
        dest.writeString(time);
        dest.writeString(unread);
    }
}
