package com.jingcai.apps.aizhuan.adapter.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.advice.advice01.Advice01Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class NotificationListAdapter extends BaseAdapter{

    private Context mContext;
    private List<Advice01Response.Advice01Body.Message> mMessageList;
    private LayoutInflater mInflater;

    public NotificationListAdapter(Context ctx){
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        mMessageList = new ArrayList<>();
    }

    public void addData(List<Advice01Response.Advice01Body.Message> list) {
        mMessageList.addAll(list);
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.message_notification_list_item,null);

            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView)convertView.findViewById(R.id.tv_message_center_list_item_title);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_message_center_list_item_date);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_message_center_list_item_content);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Advice01Response.Advice01Body.Message message = mMessageList.get(position);
        viewHolder.message = message;
        viewHolder.tv_title.setText(message.getTitle());
        viewHolder.tv_date.setText(message.getCreatetime());
        viewHolder.tv_content.setText(message.getContentsimple());
        boolean isRead = "1".equals(message.getIsread());

        changeTextColorByIsRead(viewHolder,isRead);
        return convertView;
    }

    public void  changeTextColorByIsRead(ViewHolder viewHolder , boolean isRead){
        viewHolder.message.setIsread(isRead?"1":"0");
        if(isRead){
            viewHolder.tv_content.setTextColor(mContext.getResources().getColor(R.color.assist_grey));
            viewHolder.tv_title.setTextColor(mContext.getResources().getColor(R.color.assist_grey));
            viewHolder.tv_date.setTextColor(mContext.getResources().getColor(R.color.assist_grey));
        }else{
            viewHolder.tv_content.setTextColor(mContext.getResources().getColor(R.color.important_dark));
            viewHolder.tv_title.setTextColor(mContext.getResources().getColor(R.color.important_dark));
            viewHolder.tv_date.setTextColor(mContext.getResources().getColor(R.color.important_dark));
        }
    }

    public void clearData() {
        mMessageList.clear();
    }

    public Advice01Response.Advice01Body.Message getMessage(int position) {
        if(position >= mMessageList.size()){
            return null;
        }
        return mMessageList.get(position);
    }

    public class ViewHolder{
        public Advice01Response.Advice01Body.Message message;
        public TextView tv_title;
        public TextView tv_date;
        public TextView tv_content;
    }
}
