package com.jingcai.apps.aizhuan.adapter.mine;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/29.
 */
public class SchoolListAdapter  extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Map<String, String>> list;
    private boolean showIndicator;
    private int mTextGravity;

    public SchoolListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        showIndicator = true;
        mTextGravity = Gravity.LEFT;
        list= new ArrayList<>();
    }

    public Map<String,String> getListData(int position){
        return list.get(position);
    }

    public void setListData(List<Map<String, String>> locationList){
        list = locationList;
    }

    public void addListData(List<Map<String, String>> locationList){
        list.addAll(locationList);
    }

    public void clearListData(){
        list.clear();

    }

    public void isShowIndicator(boolean flag){
        showIndicator = flag;
    }

    public void setTextGravity(int gravity){
        mTextGravity = gravity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.school_item, null);
            viewHolder = new ViewHolder();
            viewHolder.item_tv_text = (TextView) convertView.findViewById(R.id.tv_city);
            if(!showIndicator){
                viewHolder.item_tv_text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            viewHolder.item_tv_text.setGravity(mTextGravity);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.item_tv_text.setText(list.get(position).get("name"));
        return convertView;
    }

    public class ViewHolder {
        private TextView item_tv_text;

    }
}