package com.jingcai.apps.aizhuan.adapter.index;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.util.CheckCertificationUtil;
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob11.Partjob11Response;
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
public class CampusAdapter extends BaseAdapter {
    private static final int TYPE_JISHI = 0;
    private static final int TYPE_WENDA = 1;
    private Activity baseActivity;
    private List<Partjob11Response.Parttimejob> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;
    private Callback callback;
    private CheckCertificationUtil checkCertificationUtil;

    public CampusAdapter(Activity ctx) {
        baseActivity = ctx;
        regionList = new ArrayList<>();
        mInflater = LayoutInflater.from(ctx);
        bitmapUtil = new BitmapUtil(ctx);
        checkCertificationUtil = new CheckCertificationUtil(baseActivity);
        checkCertificationUtil.setCallback(new CheckCertificationUtil.Callback() {
            @Override
            public void online(boolean onlineFlag) {
                if(null != callback) {
                    callback.online(onlineFlag);
                }
            }
        });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Partjob11Response.Parttimejob job = regionList.get(position);
        boolean jishiFlag = "1".equals(job.getType()) || "3".equals(job.getType());
        return jishiFlag ? TYPE_JISHI : TYPE_WENDA;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final boolean jishiFlag = TYPE_JISHI == getItemViewType(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (jishiFlag) {
                convertView = mInflater.inflate(R.layout.index_campus_list_jishi, null);

                viewHolder.layout_jishi_like = convertView.findViewById(R.id.layout_jishi_like);
                viewHolder.cb_jishi_like = (CheckBox) convertView.findViewById(R.id.cb_jishi_like);
                viewHolder.layout_jishi_comment = convertView.findViewById(R.id.layout_jishi_comment);
                viewHolder.cb_jishi_comment = (CheckBox) convertView.findViewById(R.id.cb_jishi_comment);
                viewHolder.layout_jishi_help = convertView.findViewById(R.id.layout_jishi_help);
                viewHolder.cb_jishi_help = (CheckBox) convertView.findViewById(R.id.cb_jishi_help);

                viewHolder.tv_gender_limit = (TextView) convertView.findViewById(R.id.tv_gender_limit);
            } else {
                convertView = mInflater.inflate(R.layout.index_campus_list_wenda, null);

                viewHolder.layout_wenda_like = convertView.findViewById(R.id.layout_wenda_like);
                viewHolder.cb_wenda_like = (CheckBox) convertView.findViewById(R.id.cb_wenda_like);
                viewHolder.layout_wenda_comment = convertView.findViewById(R.id.layout_wenda_comment);
                viewHolder.cb_wenda_comment = (CheckBox) convertView.findViewById(R.id.cb_wenda_comment);
                viewHolder.layout_wenda_help = convertView.findViewById(R.id.layout_wenda_help);
                viewHolder.cb_wenda_help = (CheckBox) convertView.findViewById(R.id.cb_wenda_help);//撰写
                viewHolder.cb_wenda_help_my = (CheckBox) convertView.findViewById(R.id.cb_wenda_help_my);//我的答案
            }
            viewHolder.layout_help_content = convertView.findViewById(R.id.layout_help_content);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_main_tip);
            viewHolder.civ_head_logo = (CircleImageView) convertView.findViewById(R.id.civ_head_logo);
            viewHolder.ltv_level = (LevelTextView) convertView.findViewById(R.id.ltv_level);
            viewHolder.tv_stu_name = (TextView) convertView.findViewById(R.id.tv_stu_name);
            viewHolder.tv_stu_college = (TextView) convertView.findViewById(R.id.tv_stu_college);
            viewHolder.tv_deploy_time = (TextView) convertView.findViewById(R.id.tv_deploy_time);
            viewHolder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);

            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Partjob11Response.Parttimejob job = regionList.get(position);
        viewHolder.region = job;//将对象存入viewHolder

        if (jishiFlag) {
            viewHolder.tv_title.setText("1".equals(job.getType()) ? "跑腿" : "公告");
            viewHolder.tv_money.setText(StringUtil.getPrintMoney(job.getMoney()));
            viewHolder.tv_gender_limit.setText(DictUtil.get(DictUtil.Item.gender, job.getGenderlimit()));

            //点赞
            if ("0".equals(job.getPraisecount()) || StringUtil.isEmpty(job.getPraisecount())) {
                viewHolder.cb_jishi_like.setText("");
            } else {
                viewHolder.cb_jishi_like.setText(job.getPraisecount());
            }
            viewHolder.cb_jishi_like.setChecked("1".equals(job.getPraiseflag()));//本人是否已经点赞

            final CheckBox cb_jishi_like = viewHolder.cb_jishi_like;
            viewHolder.layout_jishi_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.jishi_like(cb_jishi_like, job);
                }
            });

            //评论
            if ("0".equals(job.getCommentcount()) || StringUtil.isEmpty(job.getCommentcount())) {
                viewHolder.cb_jishi_comment.setText("");
            } else {
                viewHolder.cb_jishi_comment.setText(job.getCommentcount());
            }

            final boolean selfFlag = UserSubject.getStudentid().equals(job.getSourceid());
            //即时帮助-求助中
            if ("1".equals(job.getType()) && "1".equals(job.getStatus()) && !selfFlag) {
                final CheckBox cb_jishi_help = viewHolder.cb_jishi_help;
                cb_jishi_help.setText("帮TA");
                viewHolder.layout_jishi_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //线下帮助，需要学生认证
                        if (!checkCertificationUtil.checkCertification()) {
                            return;
                        }
                        if (!checkCertificationUtil.checkOnline()) {
                            return;
                        }
                        callback.jishi_help(cb_jishi_help, job);
                    }
                });
            } else {//显示状态
                viewHolder.cb_jishi_help.setText(DictUtil.get(DictUtil.Item.help_jishi_status, job.getStatus()));
                viewHolder.layout_jishi_help.setOnClickListener(null);
            }
        } else {
            viewHolder.tv_title.setText(job.getTitle());
            viewHolder.tv_money.setVisibility(View.GONE);

            //点赞
            if ("0".equals(job.getPraisecount()) || StringUtil.isEmpty(job.getPraisecount())) {
                viewHolder.cb_wenda_like.setText("");
            } else {
                viewHolder.cb_wenda_like.setText(job.getPraisecount());
            }
            viewHolder.cb_wenda_like.setChecked("1".equals(job.getPraiseflag()));//本人是否已经点赞

            final CheckBox cb_wenda_like = viewHolder.cb_wenda_like;
            viewHolder.layout_wenda_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.wenda_like(cb_wenda_like, job);
                }
            });

            //评论
            if ("0".equals(job.getAnswercount()) || StringUtil.isEmpty(job.getAnswercount())) {
                viewHolder.cb_wenda_comment.setText("");
            } else {
                viewHolder.cb_wenda_comment.setText(job.getAnswercount());
            }

            //本人问答帮助
            final boolean selfHelpFlag = "1".equals(job.getHelpflag());
            if (selfHelpFlag) {
                viewHolder.cb_wenda_help.setVisibility(View.GONE);
                viewHolder.cb_wenda_help_my.setVisibility(View.VISIBLE);
            } else {
                viewHolder.cb_wenda_help.setVisibility(View.VISIBLE);
                viewHolder.cb_wenda_help_my.setVisibility(View.GONE);
            }
            final CheckBox cb_wenda_help = viewHolder.cb_wenda_help;
            final CheckBox cb_wenda_help_my = viewHolder.cb_wenda_help_my;
            viewHolder.layout_wenda_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selfHelpFlag) {
                        callback.wenda_help_my(cb_wenda_help_my, job);// 查看我的帮助
                    } else {
                        callback.wenda_help(cb_wenda_help, job);// 撰写
                    }
                }
            });
        }

        bitmapUtil.getImage(viewHolder.civ_head_logo, job.getSourceimgurl(), true, R.drawable.default_head_img);
        try {
            viewHolder.ltv_level.setLevel(Integer.parseInt(job.getSourcelevel()));
        } catch (Exception e) {
        }
        viewHolder.tv_stu_name.setText(job.getSourcename());
        if (UserSubject.getSchoolname().equals(job.getSourceschool())) {//同校的显示学院信息
            viewHolder.tv_stu_college.setText(job.getSourcecollege());
        } else {
            viewHolder.tv_stu_college.setText(job.getSourceschool());
        }
        viewHolder.tv_deploy_time.setText(DateUtil.getHumanlityDateString(job.getOptime()));
        viewHolder.tv_content.setText(job.getContent());

        viewHolder.layout_help_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.help_detail(jishiFlag, job);
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
        private CheckBox cb_jishi_like, cb_jishi_comment, cb_jishi_help;
        private View layout_help_wenda, layout_wenda_like, layout_wenda_comment, layout_wenda_help;
        private CheckBox cb_wenda_like, cb_wenda_comment, cb_wenda_help, cb_wenda_help_my;
    }

    public interface Callback {
        void online(boolean onlineFlag);

        void jishi_like(CheckBox cb_jishi_like, Partjob11Response.Parttimejob job);

        void wenda_like(CheckBox cb_wenda_like, Partjob11Response.Parttimejob job);

        void jishi_help(CheckBox cb_wenda_help, Partjob11Response.Parttimejob job);

        void wenda_help(CheckBox cb_wenda_help, Partjob11Response.Parttimejob job);

        void wenda_help_my(CheckBox cb_wenda_help_my, Partjob11Response.Parttimejob job);

        void help_detail(boolean jishiFlag, Partjob11Response.Parttimejob job);
    }
}
