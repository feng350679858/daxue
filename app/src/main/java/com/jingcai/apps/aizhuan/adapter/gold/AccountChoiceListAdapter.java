package com.jingcai.apps.aizhuan.adapter.gold;

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
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/16.
 */
public class AccountChoiceListAdapter extends BaseAdapter {

    private List<Account04Response.Account04Body.Bank> mBankList;
    private LayoutInflater mInflater;
    private Context mContext;
    private BitmapUtil mBitmapUtil;
    private Account04Response.Account04Body.Bank mCurrentBank;

    private boolean mIsFooterDividerEnable;

    public AccountChoiceListAdapter(Context context,Account04Response.Account04Body.Bank selectedBank){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCurrentBank = selectedBank;
        mBitmapUtil = new BitmapUtil(context);
    }
    public void setFooterDividerEnabel(boolean enable){
        mIsFooterDividerEnable = enable;
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

            convertView = mInflater.inflate(R.layout.mine_gold_account_choose_list_item,null);
            viewHolder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_mine_account_choose_list_item_logo);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_mine_account_choose_list_item_title);
            viewHolder.tv_code = (TextView) convertView.findViewById(R.id.tv_mine_account_choose_list_item_code);
            viewHolder.iv_divider = (ImageView) convertView.findViewById(R.id.iv_mine_account_choose_list_item_divider);
            viewHolder.iv_select = (ImageView) convertView.findViewById(R.id.iv_mine_account_choose_list_item_select);

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
        Account04Response.Account04Body.Bank bank = mBankList.get(position);
        mBitmapUtil.getImage(viewHolder.iv_logo, bank.getImgurl(),R.drawable.ic_launcher);
        viewHolder.tv_title.setText(bank.getName());
        String cardno = bank.getCardno();
        cardno = StringUtil.hiddenPhone(cardno);
        viewHolder.tv_code.setText(cardno);

        if(mCurrentBank != null && mCurrentBank.getName().equals(bank.getName())
                &&mCurrentBank.getType().equals(bank.getType())
                &&mCurrentBank.getCode().equals(bank.getCode())
                &&mCurrentBank.getCardno().equals(bank.getCardno())){
            viewHolder.iv_select.setVisibility(View.VISIBLE);
        }else{
            viewHolder.iv_select.setVisibility(View.GONE);
        }
        return convertView;
    }


    protected class ViewHolder{
        protected ImageView iv_logo;
        protected TextView tv_title;
        protected TextView tv_code;
        protected ImageView iv_select;
        protected ImageView iv_divider;
    }
}
