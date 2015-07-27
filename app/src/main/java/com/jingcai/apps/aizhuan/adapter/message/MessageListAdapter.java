package com.jingcai.apps.aizhuan.adapter.message;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.entity.ConversationBean;
import com.jingcai.apps.aizhuan.entity.MessageCategoryBean;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.SmileUtils;
import com.jingcai.apps.aizhuan.util.StringUtil;

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
    public View getView(final int position,View convertView, ViewGroup parent) {
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
                default:
                    throw new IllegalArgumentException("unknown item view type :"+itemViewType);
            }
        }
        if (convertView != null) {
            convertView.setTranslationX(0);
        }
        if (null != holderCon) {   //对话
            final ConversationBean message = mMessages.get(position - CATEGORY_TYPE_COUNT);
            //如果姓名和头像的地址未获取到，则从服务端重新获取
//            Log.i(TAG,"position:"+position+"\nname:"+message.getName()+"\nlogo:"+message.getLogourl());
            if(StringUtil.isEmpty(message.getName()) || StringUtil.isEmpty(message.getLogourl())){
                getStudentInfo(message,holderCon.mTvName,holderCon.mIvLogo);
            }else{
                holderCon.mTvName.setText(message.getName());
                mBitmapUtil.getImage(holderCon.mIvLogo, message.getLogourl());
            }

            holderCon.mTvContent.setText(SmileUtils.getSmiledText(mContext,message.getContent()));
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

            holderCon.mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMessageListClickListener != null)
                        mOnMessageListClickListener.onItemClick(position,message);
                }
            });
            final View cView = convertView;
            holderCon.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMessageListClickListener != null) {
                        final ObjectAnimator animator = ObjectAnimator.ofFloat(cView, "translationX", -cView.getWidth());
                        animator.setInterpolator(new AnticipateOvershootInterpolator());
                        animator.setDuration(750);
                        animator.start();
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mOnMessageListClickListener.onDelete(position - CATEGORY_TYPE_COUNT, message.getStudentid());
                                mMessages.remove(position - CATEGORY_TYPE_COUNT);
                                notifyDataSetChanged();
                            }
                        });
                    }
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

    private void getStudentInfo(final ConversationBean conversation, final TextView mTvName,final  ImageView mIvLogo) {
        final AzService azService = new AzService(mContext);

        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(conversation.getStudentid());
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        final Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if("0".equals(result.getCode())) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //给传入的姓名TextView和头像ImageView赋值
                                    final String name = student.getName();
                                    final String logopath = student.getLogopath();
                                    mTvName.setText(name);
                                    mBitmapUtil.getImage(mIvLogo, logopath);
                                    conversation.setName(name);
                                    conversation.setLogourl(logopath);
                                }
                            });

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
         * @param username 对方用户名
         */
        void onDelete(int position,String username);
    }
}
