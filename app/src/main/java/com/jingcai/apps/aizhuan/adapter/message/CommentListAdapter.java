package com.jingcai.apps.aizhuan.adapter.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.entity.TestCommentsBean;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/7/14.
 */
public class CommentListAdapter extends BaseAdapter {
    public static final int ITEM_TYPE_COUNT = 2;   //类型总数
    public static final int ITEM_TYPE_NO_REPLY = 0;  //没有回复的类型
    public static final int ITEM_TYPE_HAS_REPLY = 1;  //有回复的类型

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<TestCommentsBean> mComments;

    public CommentListAdapter(Context ctx) {
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        mComments = new ArrayList<>();
    }

    public void setListData(List<TestCommentsBean> comments){
        if(comments != null){
            mComments = comments;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return StringUtil.isNotEmpty(mComments.get(position).getReply()) ? ITEM_TYPE_HAS_REPLY : ITEM_TYPE_NO_REPLY;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Object getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int itemType = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.message_comment_list_item, null);
            if (itemType == ITEM_TYPE_HAS_REPLY) {
                //选择有回复的布局，在此进行其特有View的初始化
                ((ViewStub) convertView.findViewById(R.id.stub_has_reply)).inflate();
                holder.mTvReply = (TextView) convertView.findViewById(R.id.tv_reply_content);
            } else if (itemType == ITEM_TYPE_NO_REPLY) {
                //选择没有回复的布局
                ((ViewStub) convertView.findViewById(R.id.stub_no_reply)).inflate();
            }
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mTvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder.mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.mIvLogo = (ImageView) convertView.findViewById(R.id.iv_logo);

            holder.mRefname = (TextView) convertView.findViewById(R.id.tv_reference_name);
            holder.mRefcontent = (TextView) convertView.findViewById(R.id.tv_reference_content);
            holder.mReflogo = (ImageView) convertView.findViewById(R.id.iv_reference_logo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TestCommentsBean comment = mComments.get(position);

        holder.mIvLogo.setImageResource(R.drawable.icon_index_message_list_item_comment);
        holder.mTvName.setText(comment.getName());
        holder.mTvTime.setText(comment.getTime());
        holder.mTvContent.setText(comment.getContent());

        if (null != holder.mTvReply) {
            holder.mTvReply.setText(comment.getReply());
        }

        holder.mRefname.setText(comment.getRefname());
        holder.mRefcontent.setText(comment.getRefcontent());
        holder.mReflogo.setImageResource(R.drawable.ic_launcher);

        return convertView;
    }

    /**
     * 两种类型共用一个ViewHolder
     */
    private class ViewHolder {

        private ImageView mIvLogo;
        private TextView mTvName;
        private TextView mTvContent;
        private TextView mTvTime;
        private TextView mTvReply;
        private TextView mRefname;
        private TextView mRefcontent;
        private ImageView mReflogo;
    }
}
