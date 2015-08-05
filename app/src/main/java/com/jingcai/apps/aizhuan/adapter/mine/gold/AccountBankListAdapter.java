package com.jingcai.apps.aizhuan.adapter.mine.gold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
public class AccountBankListAdapter extends BaseAdapter {

    private List<Account04Response.Account04Body.Bank> mBankList;
    private LayoutInflater mInflater;
    private Context mContext;
    private BitmapUtil mBitmapUtil;

    private boolean mIsFooterDividerEnable;
    private OnSwipeButtonClickListener clickListener;

    public AccountBankListAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mBitmapUtil = new BitmapUtil(context);
    }
    public void setFooterDividerEnabel(boolean enable){
        mIsFooterDividerEnable = enable;
    }

    public void setOnSwipeButtonClickListener(OnSwipeButtonClickListener listener){
        this.clickListener = listener;
    }

    public void setData(List<Account04Response.Account04Body.Bank> list){
        mBankList = list;
        if(mBankList == null){
            mBankList = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return mBankList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBankList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position,View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.mine_gold_account_financial_list_item,null);
            viewHolder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_account_list_logo);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_account_list_title);
            viewHolder.tv_code = (TextView) convertView.findViewById(R.id.tv_account_list_code);
            viewHolder.btn_unbind = (Button) convertView.findViewById(R.id.btn_account_list_unbind);
            viewHolder.iv_divider = (ImageView) convertView.findViewById(R.id.iv_account_list_divider);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(!mIsFooterDividerEnable){
            if(position == mBankList.size()-1){
                viewHolder.iv_divider.setVisibility(View.GONE);
            }else{
                viewHolder.iv_divider.setVisibility(View.VISIBLE);
            }
        }
        mBitmapUtil.getImage(viewHolder.iv_logo,mBankList.get(position).getImgurl(), R.drawable.logo_merchant_default);
        viewHolder.tv_title.setText(mBankList.get(position).getName());
        String cardno = mBankList.get(position).getCardno();

        char[] chars = cardno.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i>=3 && i<=6){
                chars[i] = '*';
            }
        }
        viewHolder.tv_code.setText(new String(chars));

        final View v = convertView;
        viewHolder.btn_unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnSwipeListButtonClick(position, v);
            }
        });

        return convertView;
    }

    public interface OnSwipeButtonClickListener{
        void OnSwipeListButtonClick(int position, View view);
    }

    protected class ViewHolder{
        protected ImageView iv_logo;
        protected TextView tv_title;
        protected TextView tv_code;
        protected Button btn_unbind;
        protected ImageView iv_divider;
    }
}
