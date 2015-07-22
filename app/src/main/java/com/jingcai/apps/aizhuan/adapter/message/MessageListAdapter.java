package com.jingcai.apps.aizhuan.adapter.message;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.entity.ConversationBean;
import com.jingcai.apps.aizhuan.entity.MessageCategoryBean;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息首页 - 列表适配器，0-2项为固定类型，其他为对话
 * Created by Json Ding on 2015/7/13.
 */

public class MessageListAdapter extends BaseAdapter {

    public static final String TAG = "MessageListAdapter";

    public static final int VIEW_TYPE_COUNT         = 2;    //item 类型总数
    public static final int CATEGORY_TYPE_COUNT     = 3;    //大类型的总数
    public static final int VIEW_TYPE_CATEGORY      = 0;    //item类型 - 大类型(评论、赞、商家)
    public static final int VIEW_TYPE_CONVERSATION  = 1;    //item类型 - 对话
    public static final int ITEM_POSITION_COMMENT   = 0;    //位置 - 评论
    public static final int ITEM_POSITION_RECOMMEND = 1;    //位置 - 赞
    public static final int ITEM_POSITION_MERCHANT  = 2;    //位置 - 兼职商家

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<ConversationBean> mMessages;
    private List<MessageCategoryBean> mCategorys;
    private BitmapUtil mBitmapUtil;

    private OnMessageListClickListener mOnMessageListClickListener;

    public MessageListAdapter(Context ctx) {
        mContext = ctx;
        mLayoutInflater = LayoutInflater.from(ctx);
        mBitmapUtil = new BitmapUtil(ctx);
        initCategory();
    }

    public void setOnMessageListClickListener(OnMessageListClickListener listener){
        mOnMessageListClickListener = listener;
    }

    private void initCategory() {
        mCategorys = new ArrayList<>();
        mCategorys.add(new MessageCategoryBean(R.drawable.icon_index_message_list_item_comment,"评论",0));
        mCategorys.add(new MessageCategoryBean(R.drawable.icon_index_message_list_item_commend,"赞",0));
        mCategorys.add(new MessageCategoryBean(R.drawable.icon_index_message_list_item_merchant,"兼职商家",0));
    }

    public void setListData(List<ConversationBean> messages) {
        mMessages = messages;
        notifyDataSetChanged();
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        //0-2 大类型 3-* 对话类型
        return position >= 0 && position < 3 ? VIEW_TYPE_CATEGORY : VIEW_TYPE_CONVERSATION;
    }

    @Override
    public int getCount() {
        if (mMessages == null) {
            return 0;
        }
        return mMessages.size() + CATEGORY_TYPE_COUNT;        //还有三项为其他的
    }

    @Override
    public Object getItem(int position) {
        if (mMessages == null) {
            return null;
        }
        if(position > CATEGORY_TYPE_COUNT){
            return mMessages.get(position - CATEGORY_TYPE_COUNT);
        }else{
            return mCategorys.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderCategory holderCat = null;
        ViewHolderConversation holderCon = null;
        int itemViewType = getItemViewType(position);

        if (convertView == null) {
            switch (itemViewType) {
                case VIEW_TYPE_CATEGORY:
                    convertView = mLayoutInflater.inflate(R.layout.index_message_list_catagory_item, null);
                    holderCat = new ViewHolderCategory();
                    holderCat.mIvLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
                    holderCat.mTvBadge = (TextView) convertView.findViewById(R.id.tv_badge);
                    holderCat.mTvTitle = (TextView) convertView.findViewById(R.id.tv_content);
                    holderCat.mItem = convertView.findViewById(R.id.layout_line);
                    convertView.setTag(holderCat);
                    break;
                case VIEW_TYPE_CONVERSATION:
                    convertView = mLayoutInflater.inflate(R.layout.index_message_list_conversation_item, null);
                    holderCon = new ViewHolderConversation();
                    holderCon.mIvLevel = (ImageView) convertView.findViewById(R.id.iv_level);
                    holderCon.mIvLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
                    holderCon.mTvBadge = (TextView) convertView.findViewById(R.id.tv_badge);
                    holderCon.mTvContent = (TextView) convertView.findViewById(R.id.tv_content);
                    holderCon.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
                    holderCon.mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    holderCon.mItem = convertView.findViewById(R.id.layout_line);
                    holderCon.mDelete = convertView.findViewById(R.id.btn_account_list_unbind);
                    convertView.setTag(holderCon);
            }
        } else {  //convertView is not null
            switch (itemViewType) {
                case VIEW_TYPE_CATEGORY:
                    holderCat = (ViewHolderCategory) convertView.getTag();
                    break;
                case VIEW_TYPE_CONVERSATION:
                    holderCon = (ViewHolderConversation) convertView.getTag();
                    break;
            }
        }
        if (null != holderCon) {   //对话
            final ConversationBean message = mMessages.get(position - CATEGORY_TYPE_COUNT);
            holderCon.mTvName.setText(message.getName());
            mBitmapUtil.getImage(holderCon.mIvLogo,message.getLogourl());
            holderCon.mTvContent.setText(message.getContent());
            holderCon.mTvTime.setText(message.getTime());
            int unreadCount = 0;
            try {
                unreadCount = Integer.parseInt(message.getUnread());
            } catch (NumberFormatException e) {
                unreadCount = 0;
                Log.e(TAG,"unread count from messageList is not a number");
            }
            holderCon.mTvBadge.setVisibility(unreadCount <= 0 ? View.INVISIBLE : View.VISIBLE);
            holderCon.mTvBadge.setText(message.getUnread());
//            holderCon.mIvLogo.setImageResource(R.drawable.icon_index_message_list_item_comment);  //TODO logo_url不确定
            holderCon.mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMessageListClickListener != null)
                        mOnMessageListClickListener.onItemClick(position,message);
                }
            });
            holderCon.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMessageListClickListener != null)
                        mOnMessageListClickListener.onDelete(position - CATEGORY_TYPE_COUNT);
                }
            });
        } else if (null != holderCat) {  //大类型
            MessageCategoryBean category = mCategorys.get(position);
            holderCat.mIvLogo.setImageResource(category.getLogoResId());
            holderCat.mTvBadge.setVisibility(category.getBadgeCount() <= 0 ? View.INVISIBLE : View.VISIBLE);
            holderCat.mTvBadge.setText(String.valueOf(category.getBadgeCount()));
            holderCat.mTvTitle.setText(category.getTitle());
            holderCat.mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMessageListClickListener != null)
                        mOnMessageListClickListener.onItemClick(position,null);
                }
            });
        }
        return convertView;
    }

    private class ViewHolderCategory {
        public ImageView mIvLogo;
        public TextView mTvTitle;
        public TextView mTvBadge;
        public View mItem;
    }

    private class ViewHolderConversation {
        public ImageView mIvLogo;
        public TextView mTvName;
        public TextView mTvTime;
        public TextView mTvContent;
        public TextView mTvBadge;
        public ImageView mIvLevel;
        public View mItem;
        public View mDelete;
    }

    public interface OnMessageListClickListener{
        void onItemClick(int position,ConversationBean bean);

        /**
         * 滑动删除按钮,从对话开始计数
         * @param position 对话所处的位置
         */
        void onDelete(int position);
    }
}
