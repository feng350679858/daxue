package com.jingcai.apps.aizhuan.adapter.mine.gold;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.business.account.account02.Account02Response;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/7/22.
 */
public class AccountStreamListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Account02Response.Account02Body.Account> accountList;
    private LayoutInflater mInflater;
    private BitmapUtil bitmapUtil;

    public AccountStreamListAdapter(Context ctx) {
        this.mContext = ctx;
        this.mInflater = LayoutInflater.from(ctx);
        this.bitmapUtil = new BitmapUtil(ctx);
        this.accountList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_gold_account_steam_detail_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_stream_detail_logo);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.iv_stream_detail_title);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.iv_stream_detail_time);
            viewHolder.tv_money = (TextView) convertView.findViewById(R.id.iv_stream_detail_money);
            viewHolder.tv_status = (TextView) convertView.findViewById(R.id.iv_stream_detail_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Account02Response.Account02Body.Account account = accountList.get(position);

        String url = account.getImgurl();
        bitmapUtil.getImage(viewHolder.iv_logo, url, true, R.drawable.default_image);

        //标题
        Date date = DateUtil.parseDate(account.getOptime(), "yyyy-MM-dd hh:mm:ss.S");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        viewHolder.tv_time.setText(time);
        viewHolder.tv_title.setText(account.getTitle());
        setMoney(viewHolder.tv_money, account.getOptype(), account.getOpmoney());
        setStatus(viewHolder.tv_status, account.getStatus());

        return convertView;
    }

    private void setStatus(TextView tv_status, String status) {
        if (status == null)
            return;
        switch (status) {
            case "1":
                tv_status.setText("进行中");
                tv_status.setTextColor(mContext.getResources().getColor(R.color.font_purple));
                break;
            case "2":
                tv_status.setText("冻结中");
                tv_status.setTextColor(mContext.getResources().getColor(R.color.font_blue));
                break;
            case "3":
                tv_status.setText("已完成");
                tv_status.setTextColor(mContext.getResources().getColor(R.color.font_red));
                break;
            case "4":
                tv_status.setText("已取消");
                tv_status.setTextColor(mContext.getResources().getColor(R.color.font_grey));
                break;
            default:
                Log.e(GlobalConstant.packageName, "account02->status has a wrong value.");
        }
    }

    private void setMoney(TextView tv_money, String optype, String opmoney) {
        String money = "";
        if ("credit".equals(optype)) {
            money += "+";
        } else {
            money += "-";
        }
        try {
            float fMoney = Float.parseFloat(opmoney);
            money += StringUtil.getFormatFloat(fMoney, "#");
            tv_money.setText(money);
        } catch (NumberFormatException e) {
            Log.e(GlobalConstant.packageName, "account02->opmoney has a wrong format.");
            tv_money.setText("0");
        }
    }

    public void addData(List<Account02Response.Account02Body.Account> list) {
        accountList.addAll(list);
    }

    public void clearData() {
        accountList.clear();
    }

    public class ViewHolder {
        protected ImageView iv_logo;
        protected TextView tv_title;
        protected TextView tv_time;
        protected TextView tv_money;
        protected TextView tv_status;
    }

}

