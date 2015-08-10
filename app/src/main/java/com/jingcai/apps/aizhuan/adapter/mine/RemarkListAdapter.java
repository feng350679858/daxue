package com.jingcai.apps.aizhuan.adapter.mine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.stu.stu12.Stu12Response.Body.Evaluate;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价列表适配器
 * Created by Json Ding on 2015/8/8.
 */
public class RemarkListAdapter extends BaseAdapter{

    private static final String TAG = "RemarkListAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Evaluate> mEvaluateList;
    private BitmapUtil mBitmapUtil;

    public RemarkListAdapter (Context ctx){
        mContext = ctx;
        mInflater= LayoutInflater.from(ctx);
        mBitmapUtil = new BitmapUtil(ctx);
    }

    public void setListData(List<Evaluate> list){
        if(null == list){
            Log.w(TAG,"a null list pass in adapter,check the data");
            mEvaluateList = new ArrayList<>();
        }else{
            mEvaluateList = list;
        }
    }

    @Override
    public int getCount() {
        return mEvaluateList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEvaluateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.mine_credit_remark_list_item,null);
            viewHolder.mLlStarContainer = (LinearLayout) convertView.findViewById(R.id.ll_remark_star_container);
            viewHolder.mTvTime = (TextView) convertView.findViewById(R.id.tv_remark_time);
            viewHolder.mTvContent = (TextView) convertView.findViewById(R.id.tv_remark_content);
            viewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_remark_source_name);
            viewHolder.mTvTitle = (TextView) convertView.findViewById(R.id.tv_remark_title);
            viewHolder.mIvSourceLogo = (ImageView) convertView.findViewById(R.id.iv_remark_source_logo);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Evaluate evaluate = mEvaluateList.get(position);

        //头像
        mBitmapUtil.getImage(viewHolder.mIvSourceLogo,evaluate.getSourceimgurl());
        //填充星星
        fillScoreStar(evaluate.getScore(), viewHolder.mLlStarContainer);
        //时间
        try {
            String optime = DateUtil.getHumanlityDateString(evaluate.getOptime());
            viewHolder.mTvTime.setText(optime);
        } catch (Exception e){
            Log.w(TAG,"optime is not format from server");
        }
        //内容
        viewHolder.mTvContent.setText(evaluate.getContent());
        //姓名
        viewHolder.mTvName.setText(evaluate.getSourcename());
        //标题
        viewHolder.mTvTitle.setText(evaluate.getTitle());
        return convertView;
    }

    /**
     * 根据传入的score向容器内加入star ImageView
     * @param score 分数
     * @param mLlStarContainer 星星容器
     */
    private void fillScoreStar(String score, LinearLayout mLlStarContainer) {
        // TODO: 2015/8/8 添加星星 
    }

    private class ViewHolder{
        LinearLayout mLlStarContainer;
        TextView mTvTime,mTvContent,mTvName,mTvTitle;
        ImageView mIvSourceLogo;
    }
}
