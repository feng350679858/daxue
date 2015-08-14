package com.jingcai.apps.aizhuan.adapter.help;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.util.LevelTextView;
import com.jingcai.apps.aizhuan.service.business.stu.stu10.Stu10Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class HelpFriendAdapter extends BaseAdapter {
    private BaseActivity baseActivity;
    private List<Stu10Response.Item> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;
    private Callback callback;

    public HelpFriendAdapter(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        regionList = new ArrayList<>();
        mInflater = LayoutInflater.from(baseActivity);
        bitmapUtil = new BitmapUtil(baseActivity);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return regionList.size();
    }

    @Override
    public Stu10Response.Item getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.help_friend_online_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.civ_head_logo = (CircleImageView)convertView.findViewById(R.id.civ_head_logo);
            viewHolder.ltv_level = (LevelTextView)convertView.findViewById(R.id.ltv_level);
            viewHolder.tv_stu_name = (TextView)convertView.findViewById(R.id.tv_stu_name);
            viewHolder.tv_stu_school = (TextView)convertView.findViewById(R.id.tv_stu_school);
            viewHolder.tv_stu_college = (TextView)convertView.findViewById(R.id.tv_stu_college);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //将对象存入viewHolder
        Stu10Response.Item region = regionList.get(position);
        final boolean online = "1".equals(region.getStatus());
        viewHolder.region = region;
        bitmapUtil.getImage(viewHolder.civ_head_logo, region.getTargetimgurl(), R.drawable.default_head_img,online);

        String targetlevel = region.getTargetlevel();
        if(StringUtil.isNotEmpty(targetlevel)) {
            viewHolder.ltv_level.setLevel(Integer.parseInt(targetlevel));
        }
        viewHolder.tv_stu_name.setText(region.getTargetname());
        viewHolder.tv_stu_school.setText(region.getTargetschool());
        viewHolder.tv_stu_college.setText(region.getTargetcollege());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (online) {
                    if (null != callback) {
                        callback.click(v, (ViewHolder) v.getTag());
                    }
                }else{
                    baseActivity.showToast("只能选择在线老友");
                }
            }
        });
        return convertView;
    }

    public void clearData() {
        regionList.clear();
    }

    public void addData(List<Stu10Response.Item> list) {
        regionList.addAll(list);
    }


    public class ViewHolder {
        public Stu10Response.Item region;
        public CircleImageView civ_head_logo;
        public LevelTextView ltv_level;
        public TextView tv_stu_name;
        public TextView tv_stu_school;
        public TextView tv_stu_college;
    }

    public interface Callback{
        void click(View view, ViewHolder holder);
    }
}
