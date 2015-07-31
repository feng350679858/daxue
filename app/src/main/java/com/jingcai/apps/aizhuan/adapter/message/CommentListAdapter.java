package com.jingcai.apps.aizhuan.adapter.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Response.Partjob29Body.Parttimejob;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Json Ding on 2015/7/14.
 */
public class CommentListAdapter extends BaseAdapter {
    private static final String TAG = "CommentListAdapter";
    public static final int ITEM_TYPE_COUNT = 2;   //类型总数
    public static final int ITEM_TYPE_NO_REPLY = 0;  //没有回复的类型
    public static final int ITEM_TYPE_HAS_REPLY = 1;  //有回复的类型

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<Parttimejob> mComments;
    private BitmapUtil mBitmapUtil;

    public CommentListAdapter(Context ctx) {
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        mComments = new ArrayList<>();
        mBitmapUtil = new BitmapUtil(ctx);
    }

    public void setListData(List<Parttimejob> comments){
        if(comments != null){
            mComments = comments;
        }
    }


    public void addData(List<Parttimejob> list) {
        if(mComments == null){
            mComments = new ArrayList<>();
        }
        if(list != null){
            mComments.addAll(list);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (mComments.get(position).getRefcomment() != null) ? ITEM_TYPE_HAS_REPLY : ITEM_TYPE_NO_REPLY;
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
            holder.mIvLogo = (CircleImageView) convertView.findViewById(R.id.iv_logo);

            holder.mRefname = (TextView) convertView.findViewById(R.id.tv_reference_name);
            holder.mRefcontent = (TextView) convertView.findViewById(R.id.tv_reference_content);
            holder.mReflogo = (ImageView) convertView.findViewById(R.id.iv_reference_logo);
            holder.mBtnReply = (TextView) convertView.findViewById(R.id.tv_reply);
            holder.mTvLevel = (LevelTextView) convertView.findViewById(R.id.tv_level);
            holder.mLlRefContainer = (LinearLayout) convertView.findViewById(R.id.ll_ref_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Parttimejob comment = mComments.get(position);

        mBitmapUtil.getImage(holder.mIvLogo, comment.getSourceimgurl());

        registerEvents(holder, position);  //注册各种事件

        holder.mTvName.setText(comment.getSourcename());
        holder.mTvTime.setText(DateUtil.getHumanlityDateString(DateUtil.parseDate(comment.getOptime(), "yyyyMMddHHmmss")));
        holder.mTvContent.setText(comment.getContent());

        if (null != holder.mTvReply) {
            holder.mTvReply.setText(comment.getRefcomment().getRefcontent());
        }

        try {
            holder.mTvLevel.setLevel(Integer.parseInt(comment.getSourcelevel()));
        }catch (NumberFormatException e){
            holder.mTvLevel.setLevel(0);
        }

        holder.mRefname.setText(comment.getReftarget().getStudentname());
        holder.mRefcontent.setText(comment.getReftarget().getPubliccontent());
        mBitmapUtil.getImage(holder.mReflogo, comment.getReftarget().getImgurl());

        return convertView;
    }

    /**
     * 注册事件
     * @param holder holder
     * @param position 点击的位置
     */
    private void registerEvents(ViewHolder holder, int position) {
        View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String str = "";
                switch (v.getId()){
                    case R.id.iv_logo:
                        str = "头像 click";
                        break;
                    case R.id.tv_reply:
                        str = "回复按钮 click";
                        break;
                    case R.id.tv_content:
                        str = "内容 click";
                        break;
                    case R.id.ll_ref_container:
                        str = "引用内容 click";
                        break;
                }
                Toast.makeText(mContext,str,Toast.LENGTH_SHORT).show();
            }
        };

        holder.mIvLogo.setOnClickListener(mClickListener);
        holder.mBtnReply.setOnClickListener(mClickListener);
        holder.mTvContent.setOnClickListener(mClickListener);
        holder.mLlRefContainer.setOnClickListener(mClickListener);
    }

    public void clearData() {
        if(mComments != null){
            mComments.clear();
        }
    }


    /**
     * 两种类型共用一个ViewHolder
     */
    private class ViewHolder {

        private CircleImageView mIvLogo;
        private TextView mTvName;
        private TextView mTvContent;
        private TextView mTvTime;
        private TextView mTvReply;
        private TextView mRefname;
        private TextView mRefcontent;
        private ImageView mReflogo;
        private TextView mBtnReply;
        private LevelTextView mTvLevel;
        private LinearLayout mLlRefContainer;
    }
}
