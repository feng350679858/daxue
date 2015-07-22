package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.message.MessageCommendActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageCommentActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageConversationActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageMerchantActivity;
import com.jingcai.apps.aizhuan.activity.message.MessageNotificationActivity;
import com.jingcai.apps.aizhuan.adapter.message.MessageListAdapter;
import com.jingcai.apps.aizhuan.entity.TestMessageBean;
import com.jingcai.apps.aizhuan.activity.util.PayPwdWin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMessageFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "IndexMessageFragment";
    private ListView mLvMessages;
    private MessageListAdapter mMessageListAdapter;

    private View mPopupPayPswContentView;
    private StringBuilder mPayPsw;
    private PayPwdWin payPwdWin;

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBaseView = inflater.inflate(R.layout.index_message_fragment, container, false);
        initView();
        initHeader();
        initPopupView();
        return mBaseView;
    }

    /**
     * 支付密码弹出框
     */
    private void initPopupView() {
        payPwdWin = new PayPwdWin(getActivity());
        payPwdWin.setTitle("设置支付密码");
        payPwdWin.setCallback(new PayPwdWin.Callback() {
            @Override
            public void finishInput(String pwd) {
                showToast("密码输入完毕");
            }
        });
    }

    private void initHeader() {
        TextView tvTitle = (TextView) mBaseView.findViewById(R.id.tv_content);
        tvTitle.setText("消息");
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPwdWin.show();
            }
        });

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

        List<TestMessageBean> messages = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            messages.add(new TestMessageBean("", "林" + i, "林三是" + i + "个哈哈哒", "昨天", String.valueOf(i)));
        }
        mMessageListAdapter.setListData(messages);
        mLvMessages.setAdapter(mMessageListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //评论、赞、兼职商家
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
        }
    }
}
