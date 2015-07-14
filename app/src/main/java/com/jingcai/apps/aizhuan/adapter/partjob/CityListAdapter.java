package com.jingcai.apps.aizhuan.adapter.partjob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by CFY on 2015/4/29.
 */
public class CityListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<HashMap<String, Object>> list;
    private ViewHolder viewHolder;
    private boolean showIndicator;

    public CityListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        showIndicator = true;
    }

    public void isShowIndicator(boolean flag){
        showIndicator = flag;
    }

    @Override
    public int getCount() {
        return 10;
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.location_item, null);
            viewHolder = new ViewHolder();
            viewHolder.item_tv_text = (TextView) convertView
                    .findViewById(R.id.tv_city);
            if(!showIndicator){
                viewHolder.item_tv_text.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView item_tv_text;

    }
}
