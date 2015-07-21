package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.jingcai.apps.aizhuan.util.PopupWin;

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
    PopupWin mPopWin;

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
        mPopupPayPswContentView = View.inflate(baseActivity, R.layout.pop_pay_psw, null);
        View parentView = baseActivity.getWindow().getDecorView();
        mPayPsw = new StringBuilder();

        mPopupPayPswContentView.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
            }
        });

        mPopWin = PopupWin.Builder.create(baseActivity)
                .setParentView(parentView)
                .setContentView(mPopupPayPswContentView)
                .build();

        View.OnClickListener padListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();

                if (id != R.id.iv_back) {
                    //数字
                    if (!(v instanceof TextView)) {
                        return;
                    }
                    TextView tvNum = (TextView) v;
                    if (mPayPsw.length() <= 6) {
                        mPayPsw.append(tvNum.getText());
                    }
                } else {
                    //后退键
                    if (mPayPsw.length() <= 0) return;
                    mPayPsw.deleteCharAt(mPayPsw.length() - 1);
                }

                for (int i = 1; i <= 6; i++) {
                    try {
                        int viewId = baseActivity.getResources().getIdentifier("tv_psw_length_" + i, "id", baseActivity.getPackageName());
                        mPopupPayPswContentView.findViewById(viewId).setVisibility(i <= mPayPsw.length() ? View.VISIBLE : View.INVISIBLE);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't find tv_psw_length_"+i+" ,Did you remove it?");
                    }
                }
                if(mPayPsw.length() == 6){
                    showToast("密码输入完毕");
                    mPopWin.dismiss();
                }

            }
        };

        mPopupPayPswContentView.findViewById(R.id.tv_num_0).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_1).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_2).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_3).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_4).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_5).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_6).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_7).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_8).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.tv_num_9).setOnClickListener(padListener);
        mPopupPayPswContentView.findViewById(R.id.iv_back).setOnClickListener(padListener);
    }

    private void initHeader() {
        TextView tvTitle = (TextView) mBaseView.findViewById(R.id.tv_content);
        //需要用到再findViewById，不要需则不调用，提高效率
//        TextView tvFunc = (TextView) findViewById(R.id.tv_func);
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


        tvTitle.setText("消息");

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.show();
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
