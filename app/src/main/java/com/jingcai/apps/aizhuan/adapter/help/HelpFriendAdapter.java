package com.jingcai.apps.aizhuan.adapter.help;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class HelpFriendAdapter extends BaseAdapter {
    private BaseActivity baseActivity;
    private List<Base04Response.Body.Region> regionList;
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
            convertView = mInflater.inflate(R.layout.help_friend_online_list_item, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //将对象存入viewHolder
        Base04Response.Body.Region region = regionList.get(position);
        viewHolder.region = region;
        if(null != callback) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != callback) {
                        callback.click(v, (ViewHolder) v.getTag());
                    }
                }
            });
        }
        return convertView;
    }

    public void clearData() {
        regionList.clear();
    }

    public void addData(List<Base04Response.Body.Region> list) {
        regionList.addAll(list);
    }


    public class ViewHolder {
        public Base04Response.Body.Region region;
    }

    public interface Callback{
        void click(View view, ViewHolder holder);
    }
}
