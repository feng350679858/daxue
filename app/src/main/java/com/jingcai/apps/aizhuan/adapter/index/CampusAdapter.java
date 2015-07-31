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
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob11.Partjob11Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.GenderUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class CampusAdapter extends BaseAdapter {

    private Activity baseActivity;
    private List<Partjob11Response.Parttimejob> regionList;
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
    public Partjob11Response.Parttimejob getItem(int position) {
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
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.civ_head_logo = (CircleImageView) convertView.findViewById(R.id.civ_head_logo);
            viewHolder.ltv_level = (LevelTextView) convertView.findViewById(R.id.ltv_level);
            viewHolder.tv_stu_name = (TextView) convertView.findViewById(R.id.tv_stu_name);
            viewHolder.tv_stu_college = (TextView) convertView.findViewById(R.id.tv_stu_college);
            viewHolder.tv_deploy_time = (TextView) convertView.findViewById(R.id.tv_deploy_time);
            viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);

            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tv_gender_limit = (TextView) convertView.findViewById(R.id.tv_gender_limit);

            viewHolder.layout_help_jishi = convertView.findViewById(R.id.layout_help_jishi);
            viewHolder.layout_jishi_like = convertView.findViewById(R.id.layout_jishi_like);
            viewHolder.tv_jishi_like = (TextView) convertView.findViewById(R.id.tv_jishi_like);
            viewHolder.layout_jishi_comment = convertView.findViewById(R.id.layout_jishi_comment);
            viewHolder.tv_jishi_comment = (TextView) convertView.findViewById(R.id.tv_jishi_comment);
            viewHolder.layout_jishi_help = convertView.findViewById(R.id.layout_jishi_help);
            viewHolder.tv_jishi_help = (TextView) convertView.findViewById(R.id.tv_jishi_help);

            viewHolder.layout_help_wenda = convertView.findViewById(R.id.layout_help_wenda);
            viewHolder.layout_wenda_like = convertView.findViewById(R.id.layout_wenda_like);
            viewHolder.tv_wenda_like = (TextView) convertView.findViewById(R.id.tv_wenda_like);
            viewHolder.layout_wenda_comment = convertView.findViewById(R.id.layout_wenda_comment);
            viewHolder.tv_wenda_comment = (TextView) convertView.findViewById(R.id.tv_wenda_comment);
            viewHolder.layout_wenda_help = convertView.findViewById(R.id.layout_wenda_help);
            viewHolder.tv_wenda_help = (TextView) convertView.findViewById(R.id.tv_wenda_help);//撰写
            viewHolder.tv_wenda_help_my = (TextView) convertView.findViewById(R.id.tv_wenda_help_my);//我的答案
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Partjob11Response.Parttimejob region = regionList.get(position);
        viewHolder.region = region;//将对象存入viewHolder

        final boolean jishiFlag = "1".equals(region.getType()) || "3".equals(region.getType());
        if (jishiFlag) {
            viewHolder.layout_help_jishi.setVisibility(View.VISIBLE);
            viewHolder.layout_help_wenda.setVisibility(View.GONE);
            viewHolder.tv_gender_limit.setVisibility(View.VISIBLE);
            viewHolder.tv_gender_limit.setText(GenderUtil.get(region.getGenderlimit()));

            viewHolder.tv_jishi_like.setText(region.getPraisecount());
            viewHolder.tv_jishi_comment.setText(region.getCommentcount());

            viewHolder.layout_jishi_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            viewHolder.layout_jishi_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            viewHolder.layout_help_jishi.setVisibility(View.GONE);
            viewHolder.layout_help_wenda.setVisibility(View.VISIBLE);
            viewHolder.tv_gender_limit.setVisibility(View.GONE);
            //TODO
            viewHolder.tv_wenda_help.setVisibility(View.VISIBLE);
            viewHolder.tv_wenda_help_my.setVisibility(View.GONE);

            viewHolder.tv_wenda_like.setText(region.getPraisecount());
            viewHolder.tv_wenda_comment.setText(region.getCommentcount());

            viewHolder.layout_wenda_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            viewHolder.layout_wenda_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        viewHolder.tv_title.setText(region.getTitle());
        bitmapUtil.getImage(viewHolder.civ_head_logo, region.getSourceimgurl(), true, R.drawable.default_image);
        viewHolder.ltv_level.setLevel(10);
        viewHolder.tv_stu_name.setText(region.getSourcename());
        viewHolder.tv_stu_college.setText(region.getSourcecollege());
        viewHolder.tv_deploy_time.setText(DateUtil.getHumanlityDateString(region.getOptime()));
        viewHolder.tv_money.setText(region.getMoney());

        viewHolder.tv_content.setText(region.getContent());

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

    public void addData(List<Partjob11Response.Parttimejob> list) {
        regionList.addAll(list);
    }

    public Partjob11Response.Parttimejob getMerchant(int position) {
        if (position >= regionList.size()) {
            return null;
        }
        return regionList.get(position);
    }

    public class ViewHolder {
        public Partjob11Response.Parttimejob region;
        public View layout_help_content;
        //头
        public TextView tv_title;
        public CircleImageView civ_head_logo;
        public LevelTextView ltv_level;
        public TextView tv_stu_name, tv_stu_college, tv_deploy_time, tv_money;
        //正文
        public TextView tv_content, tv_gender_limit;
        //操作栏
        private View layout_help_jishi, layout_jishi_like, layout_jishi_comment, layout_jishi_help;
        public TextView tv_jishi_like, tv_jishi_comment, tv_jishi_help;
        private View layout_help_wenda, layout_wenda_like, layout_wenda_comment, layout_wenda_help;
        public TextView tv_wenda_like, tv_wenda_comment, tv_wenda_help, tv_wenda_help_my;
    }
}
