package com.jingcai.apps.aizhuan.adapter.mine.help;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDetailActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpWendaDetailActivity;
import com.jingcai.apps.aizhuan.activity.mine.help.MineHelpJishiActivity;
import com.jingcai.apps.aizhuan.service.business.partjob.Partjob24.Partjob24Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob18.Partjob18Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob23.Partjob23Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob27.Partjob27Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.DictUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class MineHelpListAdapter extends BaseAdapter {
    private boolean provideFlag;
    private boolean jishiFlag;
    private BaseActivity baseActivity;
    private List<MineHelpListItem> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public MineHelpListAdapter(BaseActivity ctx) {
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
    public MineHelpListItem getItem(int position) {
        return regionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (provideFlag) {
            return jishiFlag ? 0 : 1;
        } else {
            return jishiFlag ? 2 : 3;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (provideFlag && jishiFlag) {
            return provideJishiView(position, convertView);
        } else if (provideFlag && !jishiFlag) {
            return provideWendaView(position, convertView);
        } else if (!provideFlag && jishiFlag) {
            return receiveJishiView(position, convertView);
        } else if (!provideFlag && !jishiFlag) {
            return receiveWendaView(position, convertView);
        }
        return null;
    }

    //我的求助
    private View provideJishiView(int position, View convertView) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_help_list_provide_jishi, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            {
                viewHolder.layout_content = convertView.findViewById(R.id.layout_content);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.tv_gender_limit = (TextView) convertView.findViewById(R.id.tv_gender_limit);

                viewHolder.civ_head_logo = (CircleImageView) convertView.findViewById(R.id.civ_head_logo);
                viewHolder.tv_stu_name = (TextView) convertView.findViewById(R.id.tv_stu_name);
                viewHolder.btn1 = (Button) convertView.findViewById(R.id.btn_comment);
                viewHolder.btn2 = (Button) convertView.findViewById(R.id.btn_reward);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Partjob18Response.Parttimejob job = (Partjob18Response.Parttimejob) getItem(position);
        viewHolder.tv_time.setText(DateUtil.getHumanlityDateString(job.getOptime()));
        if (StringUtil.isNotEmpty(job.getMoney())) {
            viewHolder.tv_money.setText(String.format("%.2f元", Double.parseDouble(job.getMoney())));
        } else {
            viewHolder.tv_money.setText("");
        }
        viewHolder.tv_status.setText(DictUtil.get(DictUtil.Item.help_jishi_status, job.getStatus()));
        viewHolder.tv_content.setText(job.getContent());
        viewHolder.tv_gender_limit.setText(DictUtil.get(DictUtil.Item.gender, job.getGender()));
        bitmapUtil.getImage(viewHolder.civ_head_logo, job.getTargetimgurl(), true, R.drawable.default_head_img);
        viewHolder.tv_stu_name.setText(job.getTargetname());
        viewHolder.layout_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示求助详情
                Intent intent = new Intent(baseActivity, MineHelpJishiActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                intent.putExtra("provideFlag", provideFlag);
                baseActivity.startActivity(intent);
            }
        });
        viewHolder.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看评论
                Intent intent = new Intent(baseActivity, HelpJishiDetailActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                intent.putExtra("type", "1");//1跑腿 还是 3公告
                baseActivity.startActivity(intent);
            }
        });
        viewHolder.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 确认打赏
                baseActivity.showToast("确认打赏");
            }
        });
        return convertView;
    }

    //我的提问
    private View provideWendaView(int position, View convertView) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_help_list_provide_wenda, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            {
                viewHolder.layout_content = convertView.findViewById(R.id.layout_content);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);

                viewHolder.civ_head_logo = (CircleImageView) convertView.findViewById(R.id.civ_head_logo);
                viewHolder.tv_stu_name = (TextView) convertView.findViewById(R.id.tv_stu_name);
                viewHolder.btn1 = (Button) convertView.findViewById(R.id.btn_view_answer);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Partjob23Response.Parttimejob job = (Partjob23Response.Parttimejob) getItem(position);
        viewHolder.tv_time.setText(DateUtil.getHumanlityDateString(job.getOptime()));
        viewHolder.tv_status.setText(DictUtil.get(DictUtil.Item.help_jishi_status, job.getStatus()));
        viewHolder.tv_content.setText(job.getContent());

        bitmapUtil.getImage(viewHolder.civ_head_logo, job.getTargetimgurl(), true, R.drawable.default_head_img);
        viewHolder.tv_stu_name.setText(job.getTargetname());
        viewHolder.layout_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 显示求助详情
                Intent intent = new Intent(baseActivity, MineHelpJishiActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                intent.putExtra("provideFlag", provideFlag);
                baseActivity.startActivity(intent);
            }
        });
        viewHolder.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看答案
                Intent intent = new Intent(baseActivity, HelpWendaDetailActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                baseActivity.startActivity(intent);
            }
        });
        return convertView;
    }

    //我的帮助
    private View receiveJishiView(int position, View convertView) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_help_list_receive_jishi, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            {
                viewHolder.layout_content = convertView.findViewById(R.id.layout_content);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.tv_gender_limit = (TextView) convertView.findViewById(R.id.tv_gender_limit);

                viewHolder.civ_head_logo = (CircleImageView) convertView.findViewById(R.id.civ_head_logo);
                viewHolder.tv_stu_name = (TextView) convertView.findViewById(R.id.tv_stu_name);
                viewHolder.btn1 = (Button) convertView.findViewById(R.id.btn_comment);
                viewHolder.btn2 = (Button) convertView.findViewById(R.id.btn_evaluate);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Partjob24Response.Parttimejob job = (Partjob24Response.Parttimejob) getItem(position);
        viewHolder.tv_time.setText(DateUtil.getHumanlityDateString(job.getOptime()));
        if (StringUtil.isNotEmpty(job.getMoney())) {
            viewHolder.tv_money.setText(String.format("%.2f元", Double.parseDouble(job.getMoney())));
        } else {
            viewHolder.tv_money.setText("");
        }
        viewHolder.tv_status.setText(DictUtil.get(DictUtil.Item.help_jishi_status, job.getStatus()));
        viewHolder.tv_content.setText(job.getContent());
        viewHolder.tv_gender_limit.setText(DictUtil.get(DictUtil.Item.gender, job.getGender()));
        bitmapUtil.getImage(viewHolder.civ_head_logo, job.getSourceimgurl(), true, R.drawable.default_head_img);
        viewHolder.tv_stu_name.setText(job.getSourcename());
        viewHolder.layout_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 显示求助详情
                Intent intent = new Intent(baseActivity, MineHelpJishiActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                intent.putExtra("provideFlag", provideFlag);
                baseActivity.startActivity(intent);
            }
        });
        viewHolder.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看评论
                Intent intent = new Intent(baseActivity, HelpJishiDetailActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                intent.putExtra("type", "1");//1跑腿 还是 3公告
                baseActivity.startActivity(intent);
            }
        });
        viewHolder.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 立即评价
                baseActivity.showToast("立即评价");
            }
        });
        return convertView;
    }

    //我的回答
    private View receiveWendaView(int position, View convertView) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_help_list_receive_wenda, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            {
                viewHolder.layout_content = convertView.findViewById(R.id.layout_content);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_money_label = (TextView) convertView.findViewById(R.id.tv_money_label);
                viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);

                viewHolder.civ_head_logo = (CircleImageView) convertView.findViewById(R.id.civ_head_logo);
                viewHolder.tv_stu_name = (TextView) convertView.findViewById(R.id.tv_stu_name);
                viewHolder.btn1 = (Button) convertView.findViewById(R.id.btn_view_question);
                viewHolder.btn2 = (Button) convertView.findViewById(R.id.btn_reedit);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Partjob27Response.Parttimejob job = (Partjob27Response.Parttimejob) getItem(position);
        viewHolder.tv_time.setText(DateUtil.getHumanlityDateString(job.getOptime()));
        if (StringUtil.isNotEmpty(job.getSalary())) {
            viewHolder.tv_money_label.setVisibility(View.VISIBLE);
            viewHolder.tv_money.setVisibility(View.VISIBLE);
            viewHolder.tv_money.setText(String.format("%.2f元", Double.parseDouble(job.getSalary())));
        } else {
            viewHolder.tv_money_label.setVisibility(View.GONE);
            viewHolder.tv_money.setVisibility(View.GONE);
        }
        viewHolder.tv_content.setText(job.getContent());
        bitmapUtil.getImage(viewHolder.civ_head_logo, job.getSourceimgurl(), true, R.drawable.default_head_img);
        viewHolder.tv_stu_name.setText(job.getSourcename());
        viewHolder.layout_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 显示详情
                Intent intent = new Intent(baseActivity, MineHelpJishiActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                intent.putExtra("provideFlag", provideFlag);
                baseActivity.startActivity(intent);
            }
        });
        viewHolder.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看问题
                Intent intent = new Intent(baseActivity, HelpWendaDetailActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                baseActivity.startActivity(intent);
            }
        });
        viewHolder.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 编辑
                baseActivity.showToast("编辑");
            }
        });
        return convertView;
    }

    public void clearData() {
        regionList.clear();
    }

    public void addData(List<MineHelpListItem> list) {
        regionList.addAll(list);
    }

    public MineHelpListItem getMerchant(int position) {
        if (position >= regionList.size()) {
            return null;
        }
        return regionList.get(position);
    }

    public void setProvideFlag(boolean provideFlag) {
        this.provideFlag = provideFlag;
    }

    public class ViewHolder {
        public MineHelpListItem region;
        public View layout_content;
        public TextView tv_time;
        public TextView tv_money_label;
        public TextView tv_money;
        public TextView tv_status;
        public TextView tv_content;
        public TextView tv_gender_limit;
        public CircleImageView civ_head_logo;
        public TextView tv_stu_name;
        public Button btn1;
        public Button btn2;
    }
}
