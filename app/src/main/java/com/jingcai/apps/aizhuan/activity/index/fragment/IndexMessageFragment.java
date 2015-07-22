package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMConversation;
import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.message.MessageCommendActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageCommentActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageConversationActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageMerchantActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageNotificationActivity;
import com.jingcai.apps.aizhuan.adapter.message.MessageListAdapter;
import com.jingcai.apps.aizhuan.entity.ConversationBean;
import com.jingcai.apps.aizhuan.util.HXHelper;

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

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_message_fragment, container, false);
        initHeader();
        initView();
        initMessageReceiver();  //注册新消息广播接收

        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversations();  //加载历史消息
    }

    /**
     * 新消息广播接收器
     */
    private void initMessageReceiver() {
        mNewMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadConversations();  //加载所有的会话
            }
        };
        HXHelper.getInstance().regNewMessageReceiver(baseActivity, mNewMessageReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        baseActivity.unregisterReceiver(mNewMessageReceiver);  //注销广播
    }

    private void loadConversations() {
        List<ConversationBean> beans = new ArrayList<>();
        final Hashtable<String, EMConversation> allConversations = HXHelper.getInstance().getAllConversations();
        Set<String> keySet = allConversations.keySet();
        ConversationBean bean = null;
        for(String s : keySet){
            EMConversation con = allConversations.get(s);
            bean = new ConversationBean(baseActivity,con);
            beans.add(bean);
        }
        mMessageListAdapter.setListData(beans);
    }

    private void initHeader() {
        TextView tvTitle = (TextView) mBaseView.findViewById(R.id.tv_content);
        tvTitle.setText("消息");
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
//        mLvMessages.setOnItemClickListener(this);
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
                    break;
                case MessageListAdapter.ITEM_POSITION_RECOMMEND:
                    intent = new Intent(baseActivity, MessageCommendActivity.class);
                    break;
                case MessageListAdapter.ITEM_POSITION_MERCHANT:
                    intent = new Intent(baseActivity, MessageMerchantActivity.class);
                    break;
            }
        } else {//消息对话
            intent = new Intent(baseActivity, MessageConversationActivity.class);
        }
        if (null != intent) {
            startActivity(intent);
            if(null != bean){
                HXHelper.getInstance().resetUnreadMsgCountByUsername(bean.getStudentid()); //重置与该用户的未读消息数
            }
        }
    }

    @Override
    public void onDelete(int position) {
        showToast("delete button :"+position+"click.");
    }
}
