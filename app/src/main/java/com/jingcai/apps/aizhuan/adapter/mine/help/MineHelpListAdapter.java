package com.jingcai.apps.aizhuan.adapter.mine.help;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.mine.help.MineHelpJishiActivity;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class MineHelpListAdapter extends BaseAdapter {
    private boolean provideFlag;
    private boolean jishiFlag;
    private Activity baseActivity;
    private List<Base04Response.Body.Region> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public MineHelpListAdapter(Activity ctx) {
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
            convertView.setTag(viewHolder);
            {
                viewHolder.layout_jishi = convertView.findViewById(R.id.layout_jishi);
                viewHolder.layout_jishi_content = convertView.findViewById(R.id.layout_jishi_content);
                viewHolder.tv_jishi_status = (TextView) convertView.findViewById(R.id.tv_jishi_status);
                viewHolder.tv_jishi_content = (TextView) convertView.findViewById(R.id.tv_jishi_content);
                viewHolder.btn_jishi_comment = (Button) convertView.findViewById(R.id.btn_jishi_comment);
                viewHolder.btn_jishi_evaluate = (Button) convertView.findViewById(R.id.btn_jishi_evaluate);
                viewHolder.btn_jishi_reward = (Button) convertView.findViewById(R.id.btn_jishi_reward);
            }
            {
                viewHolder.layout_wenda = convertView.findViewById(R.id.layout_wenda);
                viewHolder.layout_wenda_content = convertView.findViewById(R.id.layout_wenda_content);
                viewHolder.layout_wenda_answer = convertView.findViewById(R.id.layout_wenda_answer);
                viewHolder.layout_wenda_question = convertView.findViewById(R.id.layout_wenda_question);
                viewHolder.tv_wenda_status = (TextView) convertView.findViewById(R.id.tv_wenda_status);
                viewHolder.tv_wenda_income_label = (TextView) convertView.findViewById(R.id.tv_wenda_income_label);
                viewHolder.tv_wenda_income_money = (TextView) convertView.findViewById(R.id.tv_wenda_income_money);
                viewHolder.tv_wenda_answer = (TextView) convertView.findViewById(R.id.tv_wenda_answer);
                viewHolder.tv_wenda_question = (TextView) convertView.findViewById(R.id.tv_wenda_question);
                viewHolder.btn_wenda_answer = (Button) convertView.findViewById(R.id.btn_wenda_answer);
                viewHolder.btn_wenda_question = (Button) convertView.findViewById(R.id.btn_wenda_question);
                viewHolder.btn_wenda_reedit = (Button) convertView.findViewById(R.id.btn_wenda_reedit);
            }
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

        viewHolder.btn_jishi_comment.setVisibility(View.VISIBLE);//查看评论
        viewHolder.btn_jishi_evaluate.setVisibility(View.GONE);//立即评价
        viewHolder.btn_jishi_reward.setVisibility(View.GONE);//确认打赏

        viewHolder.tv_jishi_content.setText(region.getRegionname());
        if(provideFlag){//我的帮助
            if (1 == position % 5) {
                viewHolder.btn_jishi_evaluate.setVisibility(View.VISIBLE);
                viewHolder.tv_jishi_status.setText("已结算");
            }else if (2 == position % 5) {
                viewHolder.tv_jishi_status.setText("帮助中");
            }else if (3 == position % 5) {
                viewHolder.tv_jishi_status.setText("求助中");
            }else if (4 == position % 5) {
                viewHolder.tv_jishi_status.setText("已取消");
            }else {
                viewHolder.tv_jishi_status.setText("已超时");
            }
        }else{//我的求助
            if (1 == position % 5) {
                viewHolder.btn_jishi_evaluate.setVisibility(View.VISIBLE);
                viewHolder.tv_jishi_status.setText("已结算");
            }else if (2 == position % 5) {
                viewHolder.btn_jishi_reward.setVisibility(View.VISIBLE);
                viewHolder.tv_jishi_status.setText("帮助中");
            }else if (3 == position % 5) {
                viewHolder.tv_jishi_status.setText("求助中");
            }else if (4 == position % 5) {
                viewHolder.tv_jishi_status.setText("已取消");
            }else {
                viewHolder.tv_jishi_status.setText("已超时");
            }
        }
        final boolean jishiFlag = 0 == position % 2;
        viewHolder.layout_jishi_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (provideFlag) {
                    baseActivity.startActivity(new Intent(baseActivity, MineHelpJishiActivity.class));
//                } else {
//                    baseActivity.startActivity(new Intent(baseActivity, HelpWendaDetailActivity.class));
                }
            }
        });
    }

    private void getWendaView(int position, ViewHolder viewHolder) {
        Base04Response.Body.Region region = regionList.get(position);
        viewHolder.region = region;//将对象存入viewHolder

        viewHolder.tv_wenda_status.setVisibility(View.GONE);
        viewHolder.tv_wenda_income_label.setVisibility(View.GONE);
        viewHolder.tv_wenda_income_money.setVisibility(View.GONE);

        viewHolder.layout_wenda_answer.setVisibility(View.GONE);//答案
        viewHolder.layout_wenda_question.setVisibility(View.GONE);//问题

        viewHolder.btn_wenda_answer.setVisibility(View.GONE);//查看答案
        viewHolder.btn_wenda_question.setVisibility(View.GONE);//查看问题
        viewHolder.btn_wenda_reedit.setVisibility(View.GONE);//

        if(provideFlag) {//我的回答
            viewHolder.tv_wenda_income_label.setVisibility(View.VISIBLE);
            viewHolder.tv_wenda_income_money.setVisibility(View.VISIBLE);

            viewHolder.layout_wenda_answer.setVisibility(View.VISIBLE);
            viewHolder.tv_wenda_answer.setText(region.getRegionname());

            viewHolder.btn_wenda_question.setVisibility(View.VISIBLE);
            if (1 == position % 2) {//等待回答
                viewHolder.btn_wenda_reedit.setVisibility(View.VISIBLE);
            } else {//已关闭
            }
        }else{//我的提问
            viewHolder.tv_wenda_status.setVisibility(View.VISIBLE);

            viewHolder.layout_wenda_question.setVisibility(View.VISIBLE);
            viewHolder.tv_wenda_question.setText(region.getRegionname());

            viewHolder.btn_wenda_answer.setVisibility(View.VISIBLE);
            if (1 == position % 2) {
                viewHolder.tv_wenda_status.setText("等待回答");
            } else {
                viewHolder.tv_wenda_status.setText("已关闭");
            }
        }

        final boolean jishiFlag = 0 == position % 2;
        viewHolder.layout_wenda_content.setOnClickListener(new View.OnClickListener() {
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

    public void setProvideFlag(boolean provideFlag) {
        this.provideFlag = provideFlag;
    }

    public class ViewHolder {
        public Base04Response.Body.Region region;
        public View layout_jishi;
        public View layout_jishi_content;
        public TextView tv_jishi_status;
        public TextView tv_jishi_content;
        public Button btn_jishi_comment;
        public Button btn_jishi_evaluate;
        public Button btn_jishi_reward;

        public View layout_wenda;
        public View layout_wenda_content;
        public View layout_wenda_answer;
        public View layout_wenda_question;
        public TextView tv_wenda_status;
        public TextView tv_wenda_income_label;
        public TextView tv_wenda_income_money;
        public TextView tv_wenda_answer;
        public TextView tv_wenda_question;
        public Button btn_wenda_answer;
        public Button btn_wenda_question;
        public Button btn_wenda_reedit;
    }
}
