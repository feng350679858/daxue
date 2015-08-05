package com.jingcai.apps.aizhuan.adapter.help;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class HelpCommentAdapter extends BaseAdapter {
    private BaseActivity baseActivity;
    private List<CommentItem> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;
    private Callback callback;

    public HelpCommentAdapter(BaseActivity baseActivity) {
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
    public CommentItem getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.help_wenda_comment_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.civ_head_logo = (ImageView) convertView.findViewById(R.id.civ_head_logo);
            viewHolder.tv_stu_name = (TextView) convertView.findViewById(R.id.tv_stu_name);
            viewHolder.tv_stu_school = (TextView) convertView.findViewById(R.id.tv_stu_school);
            viewHolder.tv_stu_college = (TextView) convertView.findViewById(R.id.tv_stu_college);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.layout_comment_like = convertView.findViewById(R.id.layout_comment_like);
            viewHolder.cb_comment_like = (CheckBox) convertView.findViewById(R.id.cb_comment_like);
            viewHolder.tv_comment_origin = (TextView) convertView.findViewById(R.id.tv_comment_origin);
            viewHolder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //将对象存入viewHolder
        final CommentItem region = regionList.get(position);
        viewHolder.region = region;
        if (region.isSelected()) {
            convertView.setBackgroundResource(R.drawable.list_item_bg_selected);
        } else {
            convertView.setBackgroundResource(R.drawable.list_item_bg);
        }
        bitmapUtil.getImage(viewHolder.civ_head_logo, region.getSourceimgurl(), R.drawable.default_head_img);
        viewHolder.tv_stu_name.setText(region.getSourcename());
        viewHolder.tv_stu_school.setText(region.getSourceschool());
        viewHolder.tv_stu_college.setText(region.getSourcecollege());

        final boolean likeFlag = "1".equals(region.getPraiseflag());
        viewHolder.cb_comment_like.setText(region.getPraisecount());
        viewHolder.cb_comment_like.setChecked(likeFlag);

        if (StringUtil.isEmpty(region.getRefname())) {
            viewHolder.tv_comment_origin.setVisibility(View.GONE);
        } else {
            viewHolder.tv_comment_origin.setVisibility(View.VISIBLE);
            viewHolder.tv_comment_origin.setText(String.format("回复 %s:%s", region.getRefname(), region.getRefcontent()));
        }
        viewHolder.tv_comment.setText(region.getContent());
        if (null != callback) {
            final CheckBox cb_comment_like = viewHolder.cb_comment_like;
            viewHolder.layout_comment_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//赞
                    callback.like(cb_comment_like, region);
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.click(v, region);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("0", "复制");
                    map.put("1", "举报");
                    View parentView = baseActivity.getWindow().getDecorView();
                    final PopupWin genderWin = PopupWin.Builder.create(baseActivity)
                            .setData(map, new PopupWin.Callback() {
                                @Override
                                public void select(String key, String val) {
                                    if ("0".equals(key)) {
                                        // 得到剪贴板管理器
                                        ClipboardManager cmb = (ClipboardManager)baseActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                        cmb.setPrimaryClip(ClipData.newPlainText(null, region.getContent()));
                                    } else if ("1".equals(key)) {
                                        callback.abuse(region);
                                    }
                                }
                            })
                            .setParentView(parentView)
                            .build();
                    genderWin.show();
                    return true;
                }
            });
        }
        return convertView;
    }

    public void clearData() {
        regionList.clear();
    }

    public void addData(List<CommentItem> list) {
        regionList.addAll(list);
    }

    public CommentItem getMerchant(int position) {
        if (position >= regionList.size()) {
            return null;
        }
        return regionList.get(position);
    }

    public class ViewHolder {
        public CommentItem region;
        public CheckBox cb_comment_like;
        public ImageView civ_head_logo;
        public TextView tv_stu_name;
        public TextView tv_stu_school;
        public TextView tv_stu_college;
        public TextView tv_time;
        public TextView tv_comment_origin;
        public TextView tv_comment;
        public View layout_comment_like;
    }

    public interface Callback {
        void click(View view, CommentItem region);

        void like(CheckBox checkBox, CommentItem region);

        void abuse(CommentItem region);
    }

    public void clearSelected() {
        if (null != regionList) {
            for (CommentItem region : regionList) {
                region.setSelected(false);
            }
        }
    }
}
