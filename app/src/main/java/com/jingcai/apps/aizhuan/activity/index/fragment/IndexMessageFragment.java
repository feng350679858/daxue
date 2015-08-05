package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageCommentActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageConversationActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageMerchantActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageNotificationActivity;
import com.jingcai.apps.aizhuan.adapter.message.MessageListAdapter;
import com.jingcai.apps.aizhuan.entity.ConversationBean;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.persistence.db.Database;
import com.jingcai.apps.aizhuan.persistence.vo.ContactInfo;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.HXHelper;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMessageFragment extends BaseFragment implements MessageListAdapter.OnMessageListClickListener {
    private static final String TAG = "IndexMessageFragment";
    private ListView mLvMessages;
    private MessageListAdapter mMessageListAdapter;
    private BroadcastReceiver mNewMessageReceiver;
    private AzService azService;
    private Database mDb;

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_message_fragment, container, false);
        azService = new AzService(baseActivity);
        mDb = Database.getInstance(baseActivity.getApplicationContext());
        mDb.open();

        initHeader();
        initView();
        initMessageReceiver();  //注册新消息广播接收

        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        HXHelper.getInstance().reConnect();
        loadConversations();  //加载历史消息
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDb.close();
    }

    /**
     * 新消息广播接收器
     */
    private void initMessageReceiver() {
        mNewMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "IndexMessageFragment receive a new Message from " + intent.getStringExtra("from"));
                loadConversations();  //加载所有的会话
            }
        };
        HXHelper.getInstance().regNewMessageReceiver(baseActivity, mNewMessageReceiver, 3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        baseActivity.unregisterReceiver(mNewMessageReceiver);  //注销广播
    }

    /**
     * 从环信加载会话列表，然后从本地数据库中加载用户的信息组装到Adapter需要的bean中
     */
    private void loadConversations() {
        List<ConversationBean> beans = new ArrayList<>();
        final Hashtable<String, EMConversation> allConversations = HXHelper.getInstance().getAllConversations();
        Set<String> keySet = allConversations.keySet();
        ConversationBean bean = null;
        for (String s : keySet) {
            EMConversation con = allConversations.get(s);
            bean = new ConversationBean(con);
            //环信管理端发过来的信息
            if (StringUtil.isEmpty(bean.getName())) {
                assembleBean(bean);
            }
            beans.add(bean);
        }

        mMessageListAdapter.setListData(beans);
        closeProcessDialog();
    }

    /**
     * 从数据库中将用户的姓名和头像取出
     * @param bean
     */
    private void assembleBean(final ConversationBean bean) {
        final List<ContactInfo> contactInfos
                = mDb.fetchContactsInfoByStudentId(UserSubject.getStudentid(), bean.getStudentid());
        ContactInfo c = null;

        if(contactInfos.size() > 0){
            //在数据库中存在，直接从本地获取
            c = contactInfos.get(0);
            bean.setName(c.getName());
            bean.setLogourl(c.getLogourl());
            //如果超时 或 头像为空
            if (StringUtil.isNotEmpty(c.getLogourl())
                    && (System.currentTimeMillis() - c.getLastUpdate())/1000 < GlobalConstant.CONTACT_INFO_UPDATE_TIME_OUT_SECOND) {
                return;
            }
        }
        getAndSaveStudentInfo(bean,c);
    }

    /**
     * 异步从远程获取学生信息，将其赋值给bean对象，并将其存储至数据库
     * @param bean
     */
    private void getAndSaveStudentInfo(final ConversationBean bean,final ContactInfo existContact) {

        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(bean.getStudentid());
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        final Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if("0".equals(result.getCode())) {
                            bean.setName(student.getName());
                            bean.setLogourl(student.getLogopath());

                            if(existContact != null){
                                existContact.setName(student.getName());
                                existContact.setLogourl(student.getLogopath());
                                mDb.updateContactInfo(UserSubject.getStudentid(),existContact);
                                Log.d(TAG,"update a contact info in database.");
                            }else{
                                ContactInfo newContact = new ContactInfo();
                                newContact.setStudentid(bean.getStudentid());
                                newContact.setLogourl(student.getLogopath());
                                newContact.setName(student.getName());
                                mDb.insertContactInfo(UserSubject.getStudentid(), newContact);
                                Log.d(TAG,"create a contact info in database.");
                            }
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

    private void initHeader() {
        TextView tvTitle = (TextView) mBaseView.findViewById(R.id.tv_content);
        tvTitle.setText("消息");

        if(GlobalConstant.debugFlag){
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String to = "7a82a05512bf411c9dd2f318f8798a3e";
                    EMConversation conversation = EMChatManager.getInstance().getConversation(to);
                    EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
                    message.setReceipt(to);
                    TextMessageBody body = new TextMessageBody("打开通道");
                    message.addBody(body);
                    try {
                        EMChatManager.getInstance().sendMessage(message);
                    } catch (EaseMobException e) {
                        Log.e(TAG,"send test message failed.");
                    }
                    conversation.addMessage(message);
                }
            });
        }

        mBaseView.findViewById(R.id.ib_back).setVisibility(View.INVISIBLE);
        ImageView ivFunc = (ImageView) mBaseView.findViewById(R.id.iv_func);
        ivFunc.setImageResource(R.drawable.icon_index_message_bird);
        ivFunc.setVisibility(View.VISIBLE);
        ivFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, MessageNotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mLvMessages = (ListView) mBaseView.findViewById(R.id.lv_messages);
        mMessageListAdapter = new MessageListAdapter(baseActivity);
        mMessageListAdapter.setOnMessageListClickListener(this);
        mLvMessages.setAdapter(mMessageListAdapter);
    }

    @Override
    public void onItemClick(int position,ConversationBean bean) {
        Intent intent = null;
        if (position < MessageListAdapter.CATEGORY_TYPE_COUNT) {
            switch (position) {
                case MessageListAdapter.ITEM_POSITION_COMMENT:
                    intent = new Intent(baseActivity, MessageCommentActivity.class);
                    intent.putExtra(MessageCommentActivity.INTENT_NAME_ACTIVITY_FLAG, MessageCommentActivity.INTENT_VALUE_ACTIVITY_FLAG_COMMENT);
                    break;
                case MessageListAdapter.ITEM_POSITION_RECOMMEND:
                    intent = new Intent(baseActivity, MessageCommentActivity.class);
                    intent.putExtra(MessageCommentActivity.INTENT_NAME_ACTIVITY_FLAG, MessageCommentActivity.INTENT_VALUE_ACTIVITY_FLAG_COMMEND);
                    break;
                case MessageListAdapter.ITEM_POSITION_MERCHANT:
                    intent = new Intent(baseActivity, MessageMerchantActivity.class);
                    break;
            }
        } else {//消息对话
            intent = new Intent(baseActivity, MessageConversationActivity.class);
            intent.putExtra("conversationBean",bean);
            if(null != bean){
                final HXHelper instance = HXHelper.getInstance();
                instance.resetUnreadMsgCountByUsername(bean.getStudentid()); //重置与该用户的未读消息数
                final int allUnreadMsgCount = instance.getAllUnreadMsgCount();
                if(allUnreadMsgCount == 0){
                    ((MainActivity)baseActivity).markAsRead("1");
                }
            }
        }
        if (null != intent) {
            startActivity(intent);
        }
    }


    @Override
    public void onDelete(int position,String username) {
        final HXHelper instance = HXHelper.getInstance();
        instance.deleteConversation(username);
        final int allUnreadMsgCount = instance.getAllUnreadMsgCount();
        if(allUnreadMsgCount == 0){
            ((MainActivity)baseActivity).markAsRead("1");
        }
//        loadConversations();
    }
}
