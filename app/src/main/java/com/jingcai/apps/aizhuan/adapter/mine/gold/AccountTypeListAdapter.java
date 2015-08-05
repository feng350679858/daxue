package com.jingcai.apps.aizhuan.adapter.mine.gold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/6/9.
 */
public class AccountTypeListAdapter extends BaseAdapter {

    private List<Account04Response.Account04Body.Bank> mTypeList;
    private LayoutInflater mInflater;
    private Context mContext;
    private BitmapUtil mBitmapUtil;

    private boolean mIsFooterDividerEnable;

    public AccountTypeListAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mBitmapUtil = new BitmapUtil(context);
    }

    public void setData(List<Account04Response.Account04Body.Bank> list){
        mTypeList = list;
        if(mTypeList == null){
            mTypeList = new ArrayList<>();
        }
    }

    public void setFooterDividerEnabel(boolean enable){
        mIsFooterDividerEnable = enable;
    }

    @Override
    public int getCount() {
        return mTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.mine_gold_account_type_list_item,null);
            viewHolder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_account_type_list_item_logo);
            viewHolder.iv_divider = (ImageView) convertView.findViewById(R.id.iv_account_type_list_item_divider);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_account_type_list_item_title);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Account04Response.Account04Body.Bank bank = mTypeList.get(position);
        viewHolder.bank = bank;
        mBitmapUtil.getImage(viewHolder.iv_logo, bank.getImgurl(), R.drawable.logo_merchant_default);
        viewHolder.tv_title.setText(bank.getName());

        if(!mIsFooterDividerEnable){
            if(position == mTypeList.size()-1){
                viewHolder.iv_divider.setVisibility(View.GONE);
            }else{
                viewHolder.iv_divider.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    public class ViewHolder{
        protected ImageView iv_logo;
        protected TextView tv_title;
        protected ImageView iv_divider;

        protected Account04Response.Account04Body.Bank bank;

        public Account04Response.Account04Body.Bank getBank(){
            return bank;
        }
    }
}
