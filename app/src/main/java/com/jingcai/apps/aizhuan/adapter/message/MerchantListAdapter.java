package com.jingcai.apps.aizhuan.adapter.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob07.Partjob07Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class MerchantListAdapter extends BaseAdapter{

    private Context mContext;
    private List<Partjob07Response.Partjob07Body.Merchant> mMerchantList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public MerchantListAdapter(Context ctx){
        mContext = ctx;
        mMerchantList = new ArrayList<>();
        mInflater = LayoutInflater.from(ctx);
        bitmapUtil = new BitmapUtil(ctx);
    }

    @Override
    public int getCount() {
        return mMerchantList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMerchantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.message_merchant_list_item,null);

            viewHolder = new ViewHolder();

            viewHolder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_contact_merchant_list_item_logo);
            viewHolder.tv_title = (TextView)convertView.findViewById(R.id.tv_contact_merchant_list_item_title);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_contact_merchant_list_item_date);
            viewHolder.tv_work = (TextView) convertView.findViewById(R.id.tv_contact_merchant_list_item_work);
            viewHolder.btn_call = (ImageView) convertView.findViewById(R.id.iv_contact_merchant_list_item_call);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //将对象存入viewHolder
        Partjob07Response.Partjob07Body.Merchant merchant = mMerchantList.get(position);
        viewHolder.merchant = merchant;
        /*
            根据logopath 下载 商家的logo
            listener传入三个参数为：iamgeView,默认图片，错误图片
            再发送请求
         */

        String url = merchant.getLogopath();
        bitmapUtil.getImage(viewHolder.iv_logo, url, false, R.drawable.ic_launcher);  //TODO 更换默认图片
        //工作类型
        String workType = merchant.getWorktype();
        if("0".equals(workType)){
            viewHolder.tv_work.setText("工作人员");
        }else if("1".equals(workType)){
            viewHolder.tv_work.setText("派单");
        }else if("2".equals(workType)){
            viewHolder.tv_work.setText("促销");
        }else if("3".equals(workType)){
            viewHolder.tv_work.setText("服务员");
        }else if("4".equals(workType)){
            viewHolder.tv_work.setText("话务员");
        }else if("5".equals(workType)){
            viewHolder.tv_work.setText("问卷调查");
        }else if("6".equals(workType)){
            viewHolder.tv_work.setText("礼仪模特");
        }else if("7".equals(workType)){
            viewHolder.tv_work.setText("销售");
        }else if("8".equals(workType)){
            viewHolder.tv_work.setText("家教");
        }
        viewHolder.tv_date.setText(merchant.getJoindate());

        //标题
        viewHolder.tv_title.setText(merchant.getName() + " " + merchant.getJobtitle());

        return convertView;
    }

    public void clearData(){
        mMerchantList.clear();
    }

    public void addData(List<Partjob07Response.Partjob07Body.Merchant> list ){
        mMerchantList.addAll(list);
    }

    public Partjob07Response.Partjob07Body.Merchant getMerchant(int position){
        if(position >= mMerchantList.size()){
            return null;
        }
        return mMerchantList.get(position);
    }

    public class ViewHolder{
        public Partjob07Response.Partjob07Body.Merchant merchant;
        public ImageView iv_logo;
        public TextView tv_title;
        public TextView tv_date;
        public TextView tv_work;
        public ImageView btn_call;
    }
}
