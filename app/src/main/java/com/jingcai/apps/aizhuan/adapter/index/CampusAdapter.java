package com.jingcai.apps.aizhuan.adapter.index;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.help.HelpWenddaDetailActivity;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class CampusAdapter extends BaseAdapter {

    private Context mContext;
    private List<Base04Response.Body.Region> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public CampusAdapter(Context ctx) {
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
    public Base04Response.Body.Region getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.index_campus_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_banner = (ImageView) convertView.findViewById(R.id.iv_banner);
            viewHolder.layout_help = convertView.findViewById(R.id.layout_help);
            viewHolder.layout_help_content = convertView.findViewById(R.id.layout_help_content);
            viewHolder.layout_help_jishi = convertView.findViewById(R.id.layout_help_jishi);
            viewHolder.layout_help_wenda = convertView.findViewById(R.id.layout_help_wenda);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_help_college = (TextView) convertView.findViewById(R.id.tv_help_college);
            viewHolder.tv_gender_limit = (TextView) convertView.findViewById(R.id.tv_gender_limit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (0 == position) {
            viewHolder.iv_banner.setVisibility(View.VISIBLE);
            viewHolder.layout_help.setVisibility(View.GONE);
            viewHolder.iv_banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, HelpWenddaDetailActivity.class));
                }
            });
            return convertView;
        }
        Base04Response.Body.Region region = regionList.get(position);
        viewHolder.region = region;//将对象存入viewHolder

        viewHolder.iv_banner.setVisibility(View.GONE);
        viewHolder.layout_help.setVisibility(View.VISIBLE);
        if (0 == position % 2) {
            viewHolder.layout_help_jishi.setVisibility(View.VISIBLE);
            viewHolder.layout_help_wenda.setVisibility(View.GONE);
            viewHolder.tv_gender_limit.setVisibility(View.VISIBLE);
            viewHolder.tv_title.setText("跑腿");
        } else {
            viewHolder.layout_help_jishi.setVisibility(View.GONE);
            viewHolder.layout_help_wenda.setVisibility(View.VISIBLE);
            viewHolder.tv_gender_limit.setVisibility(View.GONE);
            viewHolder.tv_title.setText(region.getRegionname());
        }

        return convertView;
    }

    public void clearData() {
        regionList.clear();
    }

    public void addData(List<Base04Response.Body.Region> list) {
        regionList.addAll(list);
    }

    public Base04Response.Body.Region getMerchant(int position) {
        if (position >= regionList.size()) {
            return null;
        }
        return regionList.get(position);
    }

    public class ViewHolder {
        public Base04Response.Body.Region region;
        public ImageView iv_banner;
        public View layout_help;
        public View layout_help_content;
        public View layout_help_jishi;
        public View layout_help_wenda;
        public TextView tv_title;
        public TextView tv_help_college;
        public TextView tv_gender_limit;
    }
}
