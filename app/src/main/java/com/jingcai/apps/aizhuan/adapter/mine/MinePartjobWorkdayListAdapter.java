package com.jingcai.apps.aizhuan.adapter.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.util.DateUtil;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class MinePartjobWorkdayListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mWorkdaylist;
    private LayoutInflater mInflater;
    private boolean mIsCancel;
    private String mWorktime;
    private String mEndtime;


    /**
     * 构建adapter
     *
     * @param ctx         上下文
     * @param workdayList 工作日列表
     * @param isCancel    是否被取消报名
     */
    public MinePartjobWorkdayListAdapter(Context ctx,
                                         List<String> workdayList,
                                         boolean isCancel) {
        mContext = ctx;
        mWorkdaylist = workdayList;
        mInflater = LayoutInflater.from(ctx);
        mIsCancel = isCancel;

    }

    @Override
    public int getCount() {
        return mWorkdaylist.size();
    }

    @Override
    public Object getItem(int position) {
        return mWorkdaylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_partjob_detail_workday_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_workday = (TextView) convertView.findViewById(R.id.tv_mine_partjob_detail_workdays_list_item_workday);
            viewHolder.tv_status=(TextView)convertView.findViewById(R.id.tv_mine_partjob_detail_workdays_list_status);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        //转换为
        viewHolder.tv_workday.setText(DateUtil.formatDateString(mWorkdaylist.get(position), "yyyy-MM-dd", "M月d日"));
        if (mIsCancel) {
            viewHolder.tv_status.setText("已取消");
            viewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_cancel));
        } else {
            long worktimeTimeMillis = DateUtil.parseDate(mWorkdaylist.get(position), "yyyy-MM-dd").getTime();
            long worktimeAfterDayTimeMillis = DateUtil.parseDate(mWorkdaylist.get(position), "yyyy-MM-dd").getTime() + 24 * 60 * 60 * 1000;

            if (worktimeTimeMillis > System.currentTimeMillis()) {
                //开没到工作时间
                viewHolder.tv_status.setText("待工作");
                viewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_resting));
            } else if (System.currentTimeMillis() > worktimeAfterDayTimeMillis) {
                //过了时间
                viewHolder.tv_status.setText("已完成");
                viewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_finish));
            } else {
                viewHolder.tv_status.setText("工作中");
                viewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_working));
            }
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tv_workday;
        public TextView tv_status;
    }
}
