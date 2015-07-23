package com.jingcai.apps.aizhuan.adapter.mine.help;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class HelpAdapter extends BaseAdapter {
    private boolean jishiFlag = true;
    private Activity baseActivity;
    private List<Base04Response.Body.Region> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public HelpAdapter(Activity ctx) {
        baseActivity = ctx;
        regionList = new ArrayList<>();
        mInflater = LayoutInflater.from(ctx);
        bitmapUtil = new BitmapUtil(ctx);
    }

    public void setJishiFlag(boolean jishiFlag) {
        this.jishiFlag = jishiFlag;
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
            convertView = mInflater.inflate(R.layout.mine_help_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.layout_jishi = convertView.findViewById(R.id.layout_jishi);
            viewHolder.layout_help_content_jishi = convertView.findViewById(R.id.layout_help_content_jishi);
            viewHolder.tv_help_status_jishi = (TextView) convertView.findViewById(R.id.tv_help_status_jishi);
            viewHolder.btn_evaluate = (Button) convertView.findViewById(R.id.btn_evaluate);
            viewHolder.btn_reward = (Button) convertView.findViewById(R.id.btn_reward);


            viewHolder.layout_wenda = convertView.findViewById(R.id.layout_wenda);
            viewHolder.layout_help_content_wenda = convertView.findViewById(R.id.layout_help_content_wenda);
            viewHolder.tv_help_status_wenda = (TextView) convertView.findViewById(R.id.tv_help_status_wenda);
            viewHolder.tv_income_label = (TextView) convertView.findViewById(R.id.tv_income_label);
            viewHolder.tv_income_money = (TextView) convertView.findViewById(R.id.tv_income_money);
            viewHolder.btn_view_answer = (Button) convertView.findViewById(R.id.btn_view_answer);
            viewHolder.btn_view_question = (Button) convertView.findViewById(R.id.btn_view_question);
            viewHolder.btn_reedit = (Button) convertView.findViewById(R.id.btn_reedit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (jishiFlag) {
            viewHolder.layout_jishi.setVisibility(View.VISIBLE);
            viewHolder.layout_wenda.setVisibility(View.GONE);
            getJishiView(position, viewHolder);
        } else {
            viewHolder.layout_jishi.setVisibility(View.GONE);
            viewHolder.layout_wenda.setVisibility(View.VISIBLE);
            getWendaView(position, viewHolder);
        }
        return convertView;
    }

    private void getJishiView(int position, ViewHolder viewHolder) {
        Base04Response.Body.Region region = regionList.get(position);
        viewHolder.region = region;//将对象存入viewHolder

        if (1 == position % 3) {
            viewHolder.btn_evaluate.setVisibility(View.VISIBLE);
            viewHolder.btn_reward.setVisibility(View.GONE);
            viewHolder.tv_help_status_jishi.setText("已结算");
        } else if (2 == position % 3) {
            viewHolder.btn_evaluate.setVisibility(View.GONE);
            viewHolder.btn_reward.setVisibility(View.VISIBLE);
            viewHolder.tv_help_status_jishi.setText("帮助中");
        } else {
            viewHolder.btn_evaluate.setVisibility(View.GONE);
            viewHolder.btn_reward.setVisibility(View.GONE);
            viewHolder.tv_help_status_jishi.setText("已超时");
        }
        final boolean jishiFlag = 0 == position % 2;
        viewHolder.layout_help_content_jishi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (jishiFlag) {
//                    baseActivity.startActivity(new Intent(baseActivity, HelpJishiDetailActivity.class));
//                } else {
//                    baseActivity.startActivity(new Intent(baseActivity, HelpWendaDetailActivity.class));
//                }
            }
        });
    }

    private void getWendaView(int position, ViewHolder viewHolder) {
        Base04Response.Body.Region region = regionList.get(position);
        viewHolder.region = region;//将对象存入viewHolder

        if (1 == position % 3) {
            viewHolder.tv_income_label.setVisibility(View.GONE);
            viewHolder.tv_income_money.setVisibility(View.GONE);
            viewHolder.tv_help_status_wenda.setVisibility(View.VISIBLE);
            viewHolder.tv_help_status_wenda.setText("等待回答");

            viewHolder.btn_view_answer.setVisibility(View.VISIBLE);
            viewHolder.btn_view_question.setVisibility(View.GONE);
            viewHolder.btn_reedit.setVisibility(View.GONE);
        } else if (2 == position % 3) {
            viewHolder.tv_income_label.setVisibility(View.GONE);
            viewHolder.tv_income_money.setVisibility(View.GONE);
            viewHolder.tv_help_status_wenda.setVisibility(View.VISIBLE);
            viewHolder.tv_help_status_wenda.setText("已关闭");

            viewHolder.btn_view_answer.setVisibility(View.VISIBLE);
            viewHolder.btn_view_question.setVisibility(View.GONE);
            viewHolder.btn_reedit.setVisibility(View.GONE);
        } else {
            viewHolder.tv_income_label.setVisibility(View.VISIBLE);
            viewHolder.tv_income_money.setVisibility(View.VISIBLE);
            viewHolder.tv_help_status_wenda.setVisibility(View.GONE);

            viewHolder.btn_view_answer.setVisibility(View.GONE);
            viewHolder.btn_view_question.setVisibility(View.VISIBLE);
            viewHolder.btn_reedit.setVisibility(View.VISIBLE);
        }
        final boolean jishiFlag = 0 == position % 2;
        viewHolder.layout_help_content_wenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (jishiFlag) {
//                    baseActivity.startActivity(new Intent(baseActivity, HelpJishiDetailActivity.class));
//                } else {
//                    baseActivity.startActivity(new Intent(baseActivity, HelpWendaDetailActivity.class));
//                }
            }
        });
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
        public View layout_jishi;
        public View layout_help_content_jishi;
        public TextView tv_help_status_jishi;
        public Button btn_evaluate;
        public Button btn_reward;

        public View layout_wenda;
        public View layout_help_content_wenda;
        public TextView tv_help_status_wenda;
        public TextView tv_income_label;
        public TextView tv_income_money;
        public Button btn_view_answer;
        public Button btn_view_question;
        public Button btn_reedit;
    }
}
