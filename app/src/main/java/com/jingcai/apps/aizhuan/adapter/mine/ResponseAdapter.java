package com.jingcai.apps.aizhuan.adapter.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.partjob.Partjob24.Partjob24Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob27.Partjob27Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/21.
 */
public class ResponseAdapter extends BaseAdapter {
    private Context mContext;
    private List<Partjob27Response.Partjob27Body.Region> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public ResponseAdapter(Context ctx) {
        mContext = ctx;
        regionList = new ArrayList<>();
        mInflater = LayoutInflater.from(ctx);
        bitmapUtil = new BitmapUtil(ctx);
    }

    @Override
    public int getCount() {
        return regionList.size();
    }

    @Override
    public Partjob27Response.Partjob27Body.Region getItem(int position) {
        return regionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_response_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.layout_help_content = convertView.findViewById(R.id.ll_mine_response);
            //viewHolder.layout_help_jishi = convertView.findViewById(R.id.layout_help_jishi);
            // viewHolder.layout_help_wenda = convertView.findViewById(R.id.layout_help_wenda);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_mine_response_gold);
            viewHolder.tv_help_time = (TextView) convertView.findViewById(R.id.tv_mine_response_time);
           // viewHolder.tv_gender_limit = (TextView) convertView.findViewById(R.id.tv_mine_help_title_limit);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_mine_response_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Partjob27Response.Partjob27Body.Region region = regionList.get(position);
        viewHolder.region = region;//?????????viewHolder

        // if (0 == position % 2) {
        // viewHolder.layout_help_jishi.setVisibility(View.VISIBLE);
        //   viewHolder.layout_help_wenda.setVisibility(View.GONE);
      //  viewHolder.tv_gender_limit.setVisibility(View.VISIBLE);
        //    viewHolder.tv_title.setText("????");
        viewHolder.tv_title.setText("元");
        // } else {
        //   viewHolder.layout_help_jishi.setVisibility(View.GONE);
        //     viewHolder.layout_help_wenda.setVisibility(View.VISIBLE);
        //   viewHolder.tv_gender_limit.setVisibility(View.GONE);
      // viewHolder.tv_title.setText(region.getCoin());
        viewHolder.tv_help_time.setText(region.getOptime());
        viewHolder.tv_content.setText(region.getContent());

        return convertView;
    }

    public void clearData() {
        regionList.clear();
    }

    public void addData(List< Partjob27Response.Partjob27Body.Region> list) {
        regionList.addAll(list);
    }

    public Partjob27Response.Partjob27Body.Region getMerchant(int position) {
        if (position >= regionList.size()) {
            return null;
        }
        return regionList.get(position);
    }

    public class ViewHolder {
        public Partjob27Response.Partjob27Body.Region region;
        public View layout_help_content;
        // public View layout_help_jishi;
        //  public View layout_help_wenda;
        public TextView tv_title;
        public TextView tv_help_time;
        public TextView tv_gender_limit;
        public TextView tv_content;
    }
}