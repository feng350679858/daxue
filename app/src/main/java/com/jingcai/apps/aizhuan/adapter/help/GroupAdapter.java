package com.jingcai.apps.aizhuan.adapter.help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class GroupAdapter extends BaseAdapter{

    private Context mContext;
    private List<Base04Response.Body.Region> regionList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public GroupAdapter(Context ctx){
        mContext = ctx;
        regionList = new ArrayList<>();
        mInflater = LayoutInflater.from(ctx);
        bitmapUtil = new BitmapUtil(ctx);
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
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.popup_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_pop_item = (TextView)convertView.findViewById(R.id.tv_pop_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //将对象存入viewHolder
        Base04Response.Body.Region region = regionList.get(position);
        viewHolder.region=region;
        viewHolder.tv_pop_item.setText(region.getRegionname());

        return convertView;
    }

    public void clearData(){
        regionList.clear();
    }

    public void addData(List<Base04Response.Body.Region> list ){
        regionList.addAll(list);
    }

    public Base04Response.Body.Region getMerchant(int position){
        if(position >= regionList.size()){
            return null;
        }
        return regionList.get(position);
    }

    public class ViewHolder{
        public Base04Response.Body.Region region;
        public TextView tv_pop_item;
    }
}
