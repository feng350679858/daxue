package com.jingcai.apps.aizhuan.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.persistence.vo.ContactInfo;
import com.jingcai.apps.aizhuan.service.local.UpdateContactInfoService;

import java.util.Hashtable;
import java.util.List;

/**
 * 环信的工具类
 * Created by Json Ding on 2015/7/20.
 */
public class HXHelper {

    private static final String TAG = "HXHelper";
    private static HXHelper hxHelper;
    private Context mContext;

    private List<ContactInfo> mContactList;

    private HXHelper() {
    }

    public static HXHelper getInstance() {
        if (hxHelper == null) {
            synchronized (HXHelper.class) {
                if (hxHelper == null) {
                    hxHelper = new HXHelper();
                }
            }
        }
        return hxHelper;
    }

    /**
     * 设置消息联系人列表信息
     * @param contactList 联系人列表
     */
    public void setContactList(List<ContactInfo> contactList){
        mContactList = contactList;
    }

    /**
     * 获取消息联系人列表信息
     *
     * @return 联系人列表
     */
    public List<ContactInfo> getContactList(){
        return mContactList;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        try {
            mContext = context;
            //环信im
            EMChat.getInstance().init(context);
            /**
             * debugMode == true 时为打开，sdk 会在log里输入调试信息
             * @param debugMode
             * 在做代码混淆的时候需要设置成false
             */
            EMChat.getInstance().setDebugMode(GlobalConstant.debugFlag);
        } catch (Exception e) {
            Log.e(TAG, "Init EMChat failed,Please check.");
        }
    }

    /**
     * 登录到服务器中
     * 用户名为当前用户的用户名
     * 密码为也为用户名
     *
     * @param username 电话号码
     */
    public void loginOnEMChatServer(final String username) {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEmpty(username)) {
                    return;
                }
                EMChatManager.getInstance().login(username, username, new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        //加载所有的对话
                        final EMChatManager instance = EMChatManager.getInstance();
                        instance.loadAllConversations();
                        Log.d(TAG, "登陆聊天服务器成功！username:" + username);
                        //TODO 根据conversation 更新本地数据库
                        mContext.startService(new Intent(mContext, UpdateContactInfoService.class));
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d(TAG, "登陆聊天服务器失败：" + message);

                    }
                });
            }
        });

    }

    /**
     * 将当前用户从EMServer登出(注销)
     */
    public void logout() {
        EMChatManager.getInstance().logout(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "登出聊天服务器成功！");
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "登出聊天服务器失败！");
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


    /**
     * 获取所有的对话
     *
     * @return 所有对话(姓名, 对话对象)
     */
    public Hashtable<String, EMConversation> getAllConversations() {
        EMChatManager manager = EMChatManager.getInstance();
        Log.d(TAG, "获取所有对话成功，对话列表大小为:" + manager.getAllConversations().size());
        return manager.getAllConversations();
    }

    public void regNewMessageReceiver(Context ctx,BroadcastReceiver broadcastReceiver,int priority) {
        if(ctx == null){
            throw new NullPointerException("can't register receiver,context should not be null");
        }
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(priority);
        ctx.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 获取所有未读的消息的数量
     * @return 未读消息数量
     */
    public int getAllUnreadMsgCount(){
        return EMChatManager.getInstance().getUnreadMsgsCount();
    }

    public void resetUnreadMsgCountByUsername(String username){
        EMConversation con = EMChatManager.getInstance().getConversation(username);
        con.resetUnreadMsgCount();
    }

    public void deleteConversation(String username){
        EMChatManager.getInstance().deleteConversation(username);
    }

    /**
     * 如果未连接，重新进行登录
     */
    public void reConnect(){
        if(!EMChatManager.getInstance().isConnected()){
            loginOnEMChatServer(UserSubject.getStudentid());
        }

    }

}
