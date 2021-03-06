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
    private Account04Response.Account04Body.Bank selectedBank;


    public AccountChoiceListAdapter(Context context, Account04Response.Account04Body.Bank bank) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        selectedBank = bank;
        mBitmapUtil = new BitmapUtil(context);
    }

    public void setData(List<Account04Response.Account04Body.Bank> list) {
        mBankList = list;
        if (mBankList == null) {
            mBankList = new ArrayList<>();
        }
    }

    public void setSelectednum(Account04Response.Account04Body.Bank bank) {
        selectedBank = bank;
    }

    @Override
    public int getCount() {
        if (mBankList == null)
            return 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.mine_gold_account_choose_list_item, null);
            viewHolder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_mine_account_choose_list_item_logo);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_mine_account_choose_list_item_title);
            viewHolder.tv_code = (TextView) convertView.findViewById(R.id.tv_mine_account_choose_list_item_code);
            viewHolder.iv_select = (ImageView) convertView.findViewById(R.id.iv_mine_account_choose_list_item_select);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Account04Response.Account04Body.Bank bank = mBankList.get(position);
        mBitmapUtil.getImage(viewHolder.iv_logo, bank.getImgurl(), R.drawable.default_image);
        viewHolder.tv_title.setText(bank.getName());
        String cardno = bank.getCardno();
        if (cardno != null) {
            cardno = StringUtil.hiddenPhone(cardno);  //隐藏字符串
            viewHolder.tv_code.setText("帐号" + cardno);
        } else {
            viewHolder.tv_code.setText("推荐有" + bank.getName() + "帐号的用户使用");
        }

        if (selectedBank.getType().equals(mBankList.get(position).getType())
                && selectedBank.getCode().equals(mBankList.get(position).getCode())
                && selectedBank.getName().equals(mBankList.get(position).getName())
                && (selectedBank.getCardno() == null
                || selectedBank.getCardno().equals(mBankList.get(position).getCardno()))) {

            viewHolder.iv_select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.help_wenda_hidden_checked));
        } else {
            viewHolder.iv_select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.help_wenda_hidden_uncheck));
        }
        return convertView;
    }


    protected class ViewHolder {
        protected ImageView iv_logo;
        protected TextView tv_title;
        protected TextView tv_code;
        protected ImageView iv_select;
    }
}
