package com.jingcai.apps.aizhuan.adapter.partjob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob01.Partjob01Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PartjobListAdapter extends BaseAdapter{
    private AdapterType adapterType;
    private Context mContext;
    private List<Partjob01Response.Body.Parttimejob> dataList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public PartjobListAdapter(AdapterType adapterType, Context ctx){
        this.adapterType = adapterType;
        this.mContext = ctx;
        this.mInflater = LayoutInflater.from(ctx);
        this.bitmapUtil = new BitmapUtil(ctx);
        this.dataList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.mine_partjob_list_item,null);

            viewHolder = new ViewHolder();
            viewHolder.iv_logo = (ImageView) convertView.findViewById(R.id.pj_list_item_logo);
            viewHolder.tv_title = (TextView)convertView.findViewById(R.id.pj_list_item_title);
            viewHolder.tv_salary = (TextView)convertView.findViewById(R.id.pj_list_item_salary);
            viewHolder.tv_salary_unit = (TextView)convertView.findViewById(R.id.pj_list_item_salaryunit);
            viewHolder.tv_workdays = (TextView)convertView.findViewById(R.id.pj_list_item_workdays);
            viewHolder.iv_settlelength = (TextView)convertView.findViewById(R.id.pj_list_item_wage_settlelength);
            viewHolder.iv_label = (ImageView)convertView.findViewById(R.id.pj_list_item_label);
            viewHolder.tv_label=(TextView)convertView.findViewById(R.id.tv_pj_list_item_label);
            viewHolder.tv_distance_icon = convertView.findViewById(R.id.tv_distance_icon);
            viewHolder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            viewHolder.tv_distance_unit = convertView.findViewById(R.id.tv_distance_unit);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Partjob01Response.Body.Parttimejob parttimejob = dataList.get(position);
        viewHolder.partjob = parttimejob;

        String url = parttimejob.getLogopath();
        bitmapUtil.getImage(viewHolder.iv_logo, url, true, R.drawable.logo_merchant_default);

        //标题
        viewHolder.tv_title.setText(parttimejob.getTitle());
        setWorkdays(viewHolder.tv_workdays, parttimejob.getWorktimetype(), parttimejob.getWorkdays());
        setDistance(viewHolder.tv_distance_icon, viewHolder.tv_distance, viewHolder.tv_distance_unit, parttimejob.getDistance());
        setSalary(viewHolder.tv_salary, viewHolder.tv_salary_unit, parttimejob.getSalary(), parttimejob.getSalaryunit());
        if(AdapterType.MinePartjob == adapterType){
            setLabel(viewHolder.tv_label,parttimejob.getWorktimetype(), parttimejob.getLabel(), adapterType);
            viewHolder.tv_label.setVisibility(View.VISIBLE);
            viewHolder.iv_label.setVisibility(View.GONE);
        }else {
            setLabel(viewHolder.iv_label, parttimejob.getWorktimetype(), parttimejob.getLabel(), adapterType);
            viewHolder.tv_label.setVisibility(View.GONE);
            viewHolder.iv_label.setVisibility(View.VISIBLE);
        }
        setSettlelength(viewHolder.iv_settlelength, parttimejob.getSettlelength());

        return convertView;
    }

    public static void setSettlelength(TextView tv_settlelength, String settleLength) {
        if ("0".equals(settleLength)) {
            //日结
            tv_settlelength.setText(R.string.mine_partjob_detail_settle_length_day);
        } else if ("1".equals(settleLength)) {
            //周结
            tv_settlelength.setText(R.string.mine_partjob_detail_settle_length_week);
        } else if ("2".equals(settleLength)) {
            //半月结
            tv_settlelength.setText(R.string.mine_partjob_detail_settle_length_half_month);
        } else if ("3".equals(settleLength)) {
            //月结
            tv_settlelength.setText(R.string.mine_partjob_detail_settle_length_month);
        } else if ("4".equals(settleLength)) {
            //完工结
            tv_settlelength.setText(R.string.mine_partjob_detail_settle_length_finish);
        }
    }
    public static void setSettlelength(ImageView iv_settlelength, String settleLength) {
        if("0".equals(settleLength)){//日结
            iv_settlelength.setImageResource(R.drawable.mine_partjob_item_settlelength_day);
        }else if("1".equals(settleLength)){//周结
            iv_settlelength.setImageResource(R.drawable.mine_partjob_item_settlelength_week);
        }else if("2".equals(settleLength)){//半月结
            iv_settlelength.setImageResource(R.drawable.mine_partjob_item_settlelength_halfmonth);
        }else if("3".equals(settleLength)){//月结
            iv_settlelength.setImageResource(R.drawable.mine_partjob_item_settlelength_month);
        }else if("4".equals(settleLength)){//完工结
            iv_settlelength.setImageResource(R.drawable.mine_partjob_item_settlelength_finish);
        }
    }

    public static void setLabel(ImageView iv_label, String worktimetype, String label, AdapterType adapterType) {
        //填充本地图片
        //报名状态
        if(AdapterType.PartjobList == adapterType || AdapterType.IndexLabel == adapterType) {
            if ("1".equals(label)) {//推荐
                iv_label.setImageResource(R.drawable.partjob_list_item_label_recommend);
            } else if ("2".equals(label)) {//已报满
                iv_label.setImageResource(R.drawable.partjob_list_item_label_full);
            } else {
                iv_label.setImageResource(0);
            }
        }else{
            iv_label.setImageResource(0);
        }
    }
    public void setLabel(TextView iv_label, String worktimetype, String label, AdapterType adapterType) {
        if (AdapterType.MinePartjob == adapterType) {
            //长期兼职显示工作中
            if ("1".equals(worktimetype)) {
                if ("5".equals(label)) {//已取消
                    iv_label.setText("已取消");
                    iv_label.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_cancel));
                } else {  //其他状态
                    iv_label.setText("工作中");
                    iv_label.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_working));
                }
            } else {
                if ("1".equals(label)) {//已报名
                    iv_label.setText("已报名");
                    iv_label.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_joined));
                } else if ("2".equals(label)) {//工作中
                    iv_label.setText("工作中");
                    iv_label.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_working));
                } else if ("3".equals(label)) {//休息中
                    iv_label.setText("休息中");
                    iv_label.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_resting));
                } else if ("4".equals(label)) {//已完成
                    iv_label.setText("已完成");
                    iv_label.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_finish));
                } else if ("5".equals(label)) {//已取消
                    iv_label.setText("已取消");
                    iv_label.setTextColor(mContext.getResources().getColor(R.color.mine_partjob_item_status_cancel));
                } else {
                    iv_label.setText("");
                }
            }
        }
    }

    public static void setSalary(TextView tv_salary, TextView tv_salary_unit, String salary, String salaryunit) {
        //工资
        tv_salary.setText(StringUtil.money(salary));
        if ("0".equals(salaryunit)){
            tv_salary_unit.setText(R.string.mine_partjob_list_salary_unit_per_hour);
        }else if("1".equals(salaryunit)){
            tv_salary_unit.setText(R.string.mine_partjob_list_salary_unit_per_day);
        }else if("2".equals(salaryunit)){
            tv_salary_unit.setText(R.string.mine_partjob_list_salary_unit_per_month);
        }else{
            tv_salary_unit.setText("");
        }
    }

    public static void setDistance(View tv_distance_icon, TextView tv_distance, View tv_distance_unit, String distance) {
        //多地址
        if("-1".equals(distance) || StringUtil.isEmpty(distance)) {
            tv_distance_icon.setVisibility(View.GONE);
            tv_distance.setVisibility(View.GONE);
            tv_distance_unit.setVisibility(View.GONE);
        }else{
            tv_distance_icon.setVisibility(View.VISIBLE);
            tv_distance.setVisibility(View.VISIBLE);
            tv_distance_unit.setVisibility(View.VISIBLE);
            tv_distance.setText(StringUtil.distance(distance));
        }
    }

    public static void setWorkdays(TextView tv_workdays, String worktimetype, String workdays) {
        //长期兼职
        if("1".equals(worktimetype)){
            tv_workdays.setText("长期兼职");
        }else{
            //将工作时间由2014-12-31,2014-12-31,2014-12-31转换为12月31日 12月31日 12月31日
            StringBuffer sb = new StringBuffer();
            if(StringUtil.isNotEmpty(workdays)){
                String[] dates = workdays.split(",");
                for (int i = 0; i <dates.length ; i++) {
                    sb.append(DateUtil.formatDateString(dates[i], "yyyy-MM-dd", "M月d日"));
                    if(i!=dates.length-1){
                        sb.append(" ");
                    }
                }
            }
            tv_workdays.setText(sb.toString());
        }
    }

    public void addData(List<Partjob01Response.Body.Parttimejob> list) {
        dataList.addAll(list);
    }

    public void clearData() {
        dataList.clear();
    }

    public class ViewHolder{
        protected ImageView iv_logo;
        protected TextView tv_title;
        protected TextView tv_salary;
        protected TextView tv_salary_unit;
        protected TextView tv_workdays;
        protected TextView iv_settlelength;
        protected ImageView iv_label;
        protected TextView tv_label;
        protected View tv_distance_icon;
        protected TextView tv_distance;
        protected View tv_distance_unit;
        protected Partjob01Response.Body.Parttimejob partjob;

        public Partjob01Response.Body.Parttimejob getPartjob() {
            return partjob;
        }
    }

    public enum AdapterType{
        IndexLabel, PartjobList, MinePartjob
    }
}
