package com.jingcai.apps.aizhuan.adapter.index;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDetailActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpWendaDetailActivity;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class CampusAdapter extends BaseAdapter {

    private Activity baseActivity;
    private List<Base04Response.Body.Region> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public CampusAdapter(Activity ctx) {
        baseActivity = ctx;
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
            viewHolder.layout_help_content = convertView.findViewById(R.id.layout_help_content);
            viewHolder.layout_help_jishi = convertView.findViewById(R.id.layout_help_jishi);
            viewHolder.layout_help_wenda = convertView.findViewById(R.id.layout_help_wenda);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_help_college = (TextView) convertView.findViewById(R.id.tv_help_college);
            viewHolder.tv_gender_limit = (TextView) convertView.findViewById(R.id.tv_gender_limit);
            viewHolder.tv_help = (TextView) convertView.findViewById(R.id.tv_help);//撰写
            viewHolder.tv_my_help = (TextView) convertView.findViewById(R.id.tv_my_help);//我的答案
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Base04Response.Body.Region region = regionList.get(position);
        viewHolder.region = region;//将对象存入viewHolder

        if (0 == position % 2) {
            viewHolder.layout_help_jishi.setVisibility(View.VISIBLE);
            viewHolder.layout_help_wenda.setVisibility(View.GONE);
            viewHolder.tv_gender_limit.setVisibility(View.VISIBLE);
            viewHolder.tv_title.setText("跑腿");
        } else {
            viewHolder.layout_help_jishi.setVisibility(View.GONE);
            viewHolder.layout_help_wenda.setVisibility(View.VISIBLE);
            viewHolder.tv_help.setVisibility(View.VISIBLE);
            viewHolder.tv_my_help.setVisibility(View.GONE);
            viewHolder.tv_gender_limit.setVisibility(View.GONE);
            viewHolder.tv_title.setText(region.getRegionname());
        }
        final boolean jishiFlag = 0 == position % 2;
        viewHolder.layout_help_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jishiFlag) {
                    baseActivity.startActivity(new Intent(baseActivity, HelpJishiDetailActivity.class));
                } else {
                    baseActivity.startActivity(new Intent(baseActivity, HelpWendaDetailActivity.class));
                }
            }
        });

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
        public View layout_help_content;
        public View layout_help_jishi;
        public View layout_help_wenda;
        public TextView tv_title;
        public TextView tv_help_college;
        public TextView tv_gender_limit;
        public TextView tv_help;
        public TextView tv_my_help;
    }
}
