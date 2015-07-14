package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.adapter.message.MessageListAdapter;
import com.jingcai.apps.aizhuan.entity.TestMessageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMessageFragment extends BaseFragment implements AdapterView.OnItemClickListener{
    private ListView mLvMessages;
    private MessageListAdapter mMessageListAdapter;

    private View mBaseView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_message_fragment,container,false);
        initView();
        return mBaseView;

    }

    private void initView() {
        mLvMessages = (ListView) mBaseView.findViewById(R.id.lv_messages);
        mLvMessages.setOnItemClickListener(this);
        mMessageListAdapter = new MessageListAdapter(baseActivity);

        List<TestMessageBean> messages = new ArrayList<>();
        for(int i = 0 ; i < 50; i++){
            messages.add(new TestMessageBean("","林"+i,"林三是"+i+"个哈哈哒","昨天",String.valueOf(i)));
        }
        mMessageListAdapter.setListData(messages);
        mLvMessages.setAdapter(mMessageListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //评论、赞、兼职商家
        Intent intent = null;
        if(position < MessageListAdapter.CATEGORY_TYPE_COUNT){
            switch (position){
                case MessageListAdapter.ITEM_POSITION_COMMENT:
                    intent = new Intent(baseActivity, MessageCommentActivity.class);
                    break;
                case MessageListAdapter.ITEM_POSITION_RECOMMEND:
                    break;
                case MessageListAdapter.ITEM_POSITION_MERCHANT:
                    break;
            }
        }else{//消息对话

        }
        if(null != intent){
            startActivity(intent);
        }
    }
}
