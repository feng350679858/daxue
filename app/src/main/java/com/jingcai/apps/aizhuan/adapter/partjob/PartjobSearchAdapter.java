package com.jingcai.apps.aizhuan.adapter.partjob;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.business.school.school03.School03Request;
import com.jingcai.apps.aizhuan.service.business.school.school03.School03Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;

import java.util.List;

/**
 * Created by lejing on 15/5/6.
 */
public class PartjobSearchAdapter extends BaseAdapter {

    private Callback callback;
    private Context context;
    private MessageHandler messageHandler;
    private LayoutInflater mInflater;
    private String areaCode;
    private String wayFlag;
    private String[][] genderLimit = {{"", "0", "1"},{"性别不限", "限制男", "限制女"}};
    private String[][] worktypeLimit = {{"", "0", "1", "2", "3", "4", "5", "6", "7", "8"},
            {"类别不限", "工作人员", "派单", "促销", "服务员", "话务员", "调查问卷", "礼仪模特", "销售", "家教"}};
    private List<School03Response.Body.Areainfo> list = null;
    private ColorStateList font_normal, darker_yellow;
    public PartjobSearchAdapter(Context context){
        this.context = context;
        this.messageHandler = new MessageHandler(context);
        this.mInflater = LayoutInflater.from(context);

        font_normal = context.getResources().getColorStateList(R.color.normal_grey);
        darker_yellow = context.getResources().getColorStateList(R.color.darker_yellow);
    }

    @Override
    public int getCount() {
        if("0".equals(wayFlag)){
            return genderLimit[0].length;
        }else if("1".equals(wayFlag)){
            return worktypeLimit[0].length;
        }else if("2".equals(wayFlag)){
            return null != list ? list.size():0;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.partjob_list_normal_search_item,null);

            viewHolder = new ViewHolder();
            viewHolder.v_select_pre = convertView.findViewById(R.id.v_select_pre);
            viewHolder.v_item_text = (TextView)convertView.findViewById(R.id.v_item_text);
            viewHolder.v_select_suff = convertView.findViewById(R.id.v_select_suff);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.v_item_text.setText(getVal(position));
        //选中
        if(getKey(position).equals(callback.getSelectedKey(wayFlag))){
            //TODO 设置v_item_text 字体颜色 红色
            viewHolder.v_item_text.setTextColor(darker_yellow);
            viewHolder.v_select_pre.setVisibility(View.VISIBLE);
            viewHolder.v_select_suff.setVisibility(View.VISIBLE);
        }else{
            //TODO 设置v_item_text 字体颜色 黑色
            viewHolder.v_item_text.setTextColor(font_normal);
            viewHolder.v_select_pre.setVisibility(View.GONE);
            viewHolder.v_select_suff.setVisibility(View.GONE);
        }
        final int selectPosition = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder holder = (ViewHolder) v.getTag();
                holder.v_item_text.setTextColor(darker_yellow);
                holder.v_select_pre.setVisibility(View.VISIBLE);
                holder.v_select_suff.setVisibility(View.VISIBLE);
                if(null != callback){
                    callback.selectOption(wayFlag, getKey(selectPosition), getVal(selectPosition));
                }
            }
        });
        return convertView;
    }

    private void initAreaData(){
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AzService azService = new AzService(context);
                School03Request req = new School03Request();
                School03Request.Areainfo areainfo = req.new Areainfo();
                areainfo.setCode(areaCode);
                req.setAreainfo(areainfo);
                azService.doTrans(req, School03Response.class, new AzService.Callback<School03Response>() {
                    @Override
                    public void success(School03Response resp) {
                        if("0".equals(resp.getResultCode())){
                            School03Response.Body body = resp.getBody();
                            List<School03Response.Body.Areainfo> areainfo_list = body.getAreainfo_list();
                            School03Response.Body.Areainfo none = body.new Areainfo();
                            none.setCode("");
                            none.setName("区域不限");
                            areainfo_list.add(0, none);
                            messageHandler.postMessage(1, areainfo_list);
                        }else{
                            messageHandler.postMessage(2);
                        }
                    }
                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }
    private class ViewHolder{
        public View v_select_pre;
        public View v_select_suff;
        public TextView v_item_text;
    }

    private class MessageHandler extends BaseHandler{

        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{
                    list = (List<School03Response.Body.Areainfo>) msg.obj;
                    notifyDataSetChanged();
                    break;
                }
                case 2:{
                    Toast.makeText(context, "请求区域信息出错", Toast.LENGTH_SHORT).show();
                    break;
                }
                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }

    public void changeWayFlag(String wayFlag) {
        this.wayFlag = wayFlag;
        if("2".equals(wayFlag) && null == list){
            initAreaData();
        }else{
            notifyDataSetChanged();
        }
    }

    public void initAreaCode(String areaCode){
        if(null == this.areaCode){
            this.areaCode = areaCode;
        }else if(!this.areaCode.equals(areaCode)){
            this.areaCode = areaCode;
            this.list = null;
            initAreaData();
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void selectOption(String wayflag, String key, String value);

        String getSelectedKey(String wayflag);
    }

    private String getKey(int position){
        String key = null;
        if("0".equals(wayFlag)){
            key = genderLimit[0][position];
        }else if("1".equals(wayFlag)){
            key = worktypeLimit[0][position];
        }else if("2".equals(wayFlag)){
            School03Response.Body.Areainfo areainfo = list.get(position);
            key = areainfo.getCode();
        }
        return key;
    }

    private String getVal(int position){
        String val = null;
        if("0".equals(wayFlag)){
            val = genderLimit[1][position];
        }else if("1".equals(wayFlag)){
            val = worktypeLimit[1][position];
        }else if("2".equals(wayFlag)){
            School03Response.Body.Areainfo areainfo = list.get(position);
            val = areainfo.getName();
        }
        return val;
    }
}
