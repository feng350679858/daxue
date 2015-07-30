package com.jingcai.apps.aizhuan.adapter.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.partjob.Partjob24.Partjob24Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class HelpAdapter extends BaseAdapter {
    private Context mContext;
    private List<Partjob24Response.Partjob24Body.Parttimejob> parttimejobList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public HelpAdapter(Context ctx) {
        mContext = ctx;
        parttimejobList = new ArrayList<>();
        mInflater = LayoutInflater.from(ctx);
        bitmapUtil = new BitmapUtil(ctx);
    }

    @Override
    public int getCount() {
        return parttimejobList.size();
    }

    @Override
    public Partjob24Response.Partjob24Body.Parttimejob getItem(int position) {
        return parttimejobList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_help_detail_header, null);
            viewHolder = new ViewHolder();
            viewHolder.layout_help_content = convertView.findViewById(R.id.ll_mine_help_content);
            //viewHolder.layout_help_jishi = convertView.findViewById(R.id.layout_help_jishi);
           // viewHolder.layout_help_wenda = convertView.findViewById(R.id.layout_help_wenda);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_mine_help_title_gold);
            viewHolder.tv_help_time = (TextView) convertView.findViewById(R.id.tv_mine_help_title_time);
            viewHolder.tv_gender_limit = (TextView) convertView.findViewById(R.id.tv_mine_help_title_limit);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_mine_help_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Partjob24Response.Partjob24Body.Parttimejob parttimejob = parttimejobList.get(position);
        viewHolder.parttimejob = parttimejob;//���������viewHolder

       // if (0 == position % 2) {
           // viewHolder.layout_help_jishi.setVisibility(View.VISIBLE);
         //   viewHolder.layout_help_wenda.setVisibility(View.GONE);
            viewHolder.tv_gender_limit.setVisibility(View.VISIBLE);
        //    viewHolder.tv_title.setText("����");
            viewHolder.tv_title.setText(parttimejob.getCoin()+"金");
       // } else {
         //   viewHolder.layout_help_jishi.setVisibility(View.GONE);
       //     viewHolder.layout_help_wenda.setVisibility(View.VISIBLE);
         //   viewHolder.tv_gender_limit.setVisibility(View.GONE);
         //   viewHolder.tv_title.setText(region.getCoin());
            viewHolder.tv_help_time.setText(parttimejob.getOptime());
            viewHolder.tv_content.setText(parttimejob.getContent());

            String s= parttimejob.getStatus();
          if (s==null )
          {
              viewHolder.tv_gender_limit.setText("null");
          }
       else {
              if (s.equals("1")) {
                  viewHolder.tv_gender_limit.setText("求助中");
              }
              if (s.equals("2")) {
                  viewHolder.tv_gender_limit.setText("帮助中");
              }
              if (s.equals("3")) {
                  viewHolder.tv_gender_limit.setText("取消中");
              }
              if (s.equals("4")) {
                  viewHolder.tv_gender_limit.setText("已取消");
              }
              if (s.equals("5")) {
                  viewHolder.tv_gender_limit.setText("已帮助");
              }
              if (s.equals("6")) {
                  viewHolder.tv_gender_limit.setText("已结算");
              }
              if (s.equals("7")) {
                  viewHolder.tv_gender_limit.setText("已超时");
              }
              //  }
          }

        return convertView;
    }

    public void clearData() {
        parttimejobList.clear();
    }

    public void addData(List<Partjob24Response.Partjob24Body.Parttimejob> list) {
        parttimejobList.addAll(list);
    }

    public Partjob24Response.Partjob24Body.Parttimejob getMerchant(int position) {
        if (position >= parttimejobList.size()) {
            return null;
        }
        return parttimejobList.get(position);
    }

    public class ViewHolder {
        public Partjob24Response.Partjob24Body.Parttimejob parttimejob;
        public View layout_help_content;
       // public View layout_help_jishi;
      //  public View layout_help_wenda;
        public TextView tv_title;
        public TextView tv_help_time;
        public TextView tv_gender_limit;
        public TextView tv_content;
    }
}
