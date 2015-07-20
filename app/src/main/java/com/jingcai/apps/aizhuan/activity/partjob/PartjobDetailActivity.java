package com.jingcai.apps.aizhuan.activity.partjob;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
//import com.jingcai.apps.aizhuan.activity.mine.activity.ProfileImproveActivity;
import com.jingcai.apps.aizhuan.adapter.partjob.PartjobListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob02.Partjob02Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob02.Partjob02Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob05.Partjob05Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob05.Partjob05Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.PopupDialog;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.UmengShareUtil;
import com.tencent.open.utils.Global;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenchao on 15/7/14.
 */
public class PartjobDetailActivity extends BaseActivity {
    private String partjobid, logopath;
    private UmengShareUtil umengShareUtil;
    private MessageHandler messageHandler;
    private BitmapUtil bitmapUtil;
    private LinearLayout linearLayout_label;
    /**
     * 接受到的数据
     */
    private Partjob02Response.Partjob02Body.Parttimejob mParttimejob;
    private String gisx,gisy;
    /**
     *
     * 控件
     */
    private ImageView partjob_content_top,partjob_content_logo;
    private TextView partjob_content_title,partjob_content_salary,partjob_content_salaryunit,partjob_content_address;
    private TextView partjob_content_settlelength,partjob_content_joinnum,partjob_content_workernum,partjob_content_workdays;
    private TextView partjob_content_worktime,partjob_content_endtime,partjob_content_workcontent;
    private TextView partjob_content_heightlimit,partjob_content_healthlimit,partjob_content_genderlimit,partjob_content_remarks;
    private Button partjob_isjoin;
    private String workernum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        umengShareUtil = new UmengShareUtil(PartjobDetailActivity.this);
        partjobid = getIntent().getStringExtra("partjobid");
        logopath = getIntent().getStringExtra("logopath");
        setContentView(R.layout.activity_partjob_detail);
        if(StringUtil.isEmpty(partjobid)){
            finish();
            return;
        }
        messageHandler = new MessageHandler(this);
        bitmapUtil = new BitmapUtil(this);
        initHeader();
        initView(); //初始化控件
        initData();  //填充数据
    }
    private void initHeader(){
        ((TextView)findViewById(R.id.tv_content)).setText("兼职详情");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView tv_info = (ImageView)findViewById(R.id.iv_func);
        tv_info.setVisibility(View.VISIBLE);
        tv_info.setImageDrawable(getResources().getDrawable(R.drawable.share));
        tv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num=Integer.parseInt(workernum)-Integer.parseInt(partjob_content_joinnum.getText().toString());
                String msg = "我发现了一个兼职："+partjob_content_title.getText().toString()+
                        ",居然"+partjob_content_salary.getText().toString()+
                        partjob_content_salaryunit.getText().toString()+
                        "的工资,地点就在："+partjob_content_address.getText().toString()+
                        "。现在还有"+num+"个名额，还不快来看看：";

                postShareMessage(msg);
            }
        });

        ((ImageView)findViewById(R.id.iv_bird_badge)).setVisibility(View.INVISIBLE);
    }
    private void initView(){
        partjob_content_top=(ImageView)findViewById(R.id.partjob_content_top);
        partjob_content_logo=(ImageView)findViewById(R.id.partjob_content_logo);
        partjob_content_address=(TextView)findViewById(R.id.partjob_content_address);
        partjob_content_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap(gisx,gisy);
            }
        });
        partjob_content_title=(TextView)findViewById(R.id.partjob_content_title);
        partjob_content_salary=(TextView)findViewById(R.id.partjob_content_salary);
        partjob_content_salaryunit=(TextView)findViewById(R.id.partjob_content_salaryunit);
        partjob_content_settlelength=(TextView)findViewById(R.id.partjob_content_settlelength);
        partjob_content_joinnum=(TextView)findViewById(R.id.partjob_content_joinnum);
        partjob_content_workernum=(TextView)findViewById(R.id.partjob_content_workernum);
        partjob_content_workdays=(TextView)findViewById(R.id.partjob_content_workdays);
        partjob_content_worktime=(TextView)findViewById(R.id.partjob_content_worktime);
        partjob_content_endtime=(TextView)findViewById(R.id.partjob_content_endtime);
        partjob_content_workcontent=(TextView)findViewById(R.id.partjob_content_workcontent);
        partjob_content_heightlimit=(TextView)findViewById(R.id.partjob_content_heightlimit);
        partjob_content_healthlimit=(TextView)findViewById(R.id.partjob_content_healthlimit);
        partjob_content_genderlimit=(TextView)findViewById(R.id.partjob_content_genderlimit);
        partjob_content_remarks=(TextView)findViewById(R.id.partjob_content_remarks);
        linearLayout_label=(LinearLayout)findViewById(R.id.linearLayout_label);
        partjob_isjoin=(Button)findViewById(R.id.partjob_isjoin);
    }
    private void initData(){
        showProgressDialog("数据加载中...");
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                final AzService azService = new AzService(PartjobDetailActivity.this);
                Partjob02Request req = new Partjob02Request();
                final Partjob02Request.Parttimejob parttimejob = req.new Parttimejob();
                parttimejob.setId(partjobid);
                parttimejob.setStudentid(UserSubject.getStudentid());
                req.setParttimejob(parttimejob);
                final Partjob02Request.Recommend recommend = req.new Recommend();
                req.setRecommend(recommend);
                azService.doTrans(req, Partjob02Response.class, new AzService.Callback<Partjob02Response>() {
                    @Override
                    public void success(Partjob02Response response) {
                        ResponseResult result = response.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            Partjob02Response.Partjob02Body.Parttimejob parttimejob1 = response.getBody().getParttimejob();
                            messageHandler.postMessage(0, parttimejob1);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        }));
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    mParttimejob = (Partjob02Response.Partjob02Body.Parttimejob) msg.obj;
                    fillDataInView();
                    break;
                }
                case 1: {
                    showToast("获取报名详情出错：" + msg.obj);
                    break;
                }
                case 2: {
                    showToast("报名出错：" + msg.obj);
                    break;
                }
                case 3: {
//                    showToast("获取用户信息失败");
//                    setResult(RESULT_CANCELED, null);
//                    finish();
                    break;
                }
                case 4: {
                    showToast("报名成功");
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    /**
     * 填充数据
     */
    private void fillDataInView() {
        /*
         * 兼职详情
         */
        switch (mParttimejob.getWorktype()){
            case "0": partjob_content_top.setImageDrawable(getResources().getDrawable(R.drawable.partjob_detail_type1));
                break;
            case "1": partjob_content_top.setImageDrawable(getResources().getDrawable(R.drawable.partjob_detail_type2));
                break;
            case "2": partjob_content_top.setImageDrawable(getResources().getDrawable(R.drawable.partjob_detail_type3));
                break;
            case "3": partjob_content_top.setImageDrawable(getResources().getDrawable(R.drawable.partjob_detail_type4));
                break;
            case "4": partjob_content_top.setImageDrawable(getResources().getDrawable(R.drawable.partjob_detail_type5));
                break;
            case "5": partjob_content_top.setImageDrawable(getResources().getDrawable(R.drawable.partjob_detail_type6));
                break;
            case "6": partjob_content_top.setImageDrawable(getResources().getDrawable(R.drawable.partjob_detail_type7));
                break;
            case "7": partjob_content_top.setImageDrawable(getResources().getDrawable(R.drawable.partjob_detail_type8));
                break;
            default:
        }

        bitmapUtil.getImage(partjob_content_logo, mParttimejob.getLogopath(), true, R.drawable.logo_merchant_default);
        partjob_content_title.setText(mParttimejob.getTitle());
        PartjobListAdapter.setSalary(partjob_content_salary, partjob_content_salaryunit, "￥" + mParttimejob.getSalary(), mParttimejob.getSalaryunit());
        PartjobListAdapter.setSettlelength(partjob_content_settlelength, mParttimejob.getSettlelength());
        partjob_content_joinnum.setText(mParttimejob.getJoinnum());
        workernum=mParttimejob.getWorkernum();
        partjob_content_workernum.setText("/"+mParttimejob.getWorkernum()+"人");
        PartjobListAdapter.setWorkdays(partjob_content_workdays, "0", mParttimejob.getWorkdays());
        partjob_content_worktime.setText(mParttimejob.getWorktime());
        partjob_content_endtime.setText(mParttimejob.getEndtime());
        partjob_content_address.setText(mParttimejob.getAddress());
        partjob_content_workcontent.setText(mParttimejob.getWorkcontent());
        if("0".equals(mParttimejob.getHeightupperlimit()))
            if("0".equals(mParttimejob.getHeightlowerlimit()))
                partjob_content_heightlimit.setText("1.身高不限");
            else
                partjob_content_heightlimit.setText("1.身高"+mParttimejob.getHeightlowerlimit()+"以上");
        else
            if("0".equals(mParttimejob.getHeightlowerlimit()))
                partjob_content_heightlimit.setText("1.身高"+mParttimejob.getHeightupperlimit()+"以下");
            else
                partjob_content_heightlimit.setText("1.身高"+mParttimejob.getHeightlowerlimit()+"与"+mParttimejob.getHeightupperlimit()+"之间");
        if("0".equals(mParttimejob.getHealthlimit()))
            partjob_content_healthlimit.setText("2.不需要健康证");
        else
            partjob_content_healthlimit.setText("2.需要健康证");
        if("0".equals(mParttimejob.getGenderlimit()))
            partjob_content_genderlimit.setText("3.男性");
        else if("1".equals(mParttimejob.getGenderlimit()))
            partjob_content_genderlimit.setText("3.女性");
        else
            partjob_content_genderlimit.setText("3.性别不限");
        partjob_content_remarks.setText(mParttimejob.getRemarks());
        if(null!=mParttimejob.getSchoolmate_list()) {
            for (Partjob02Response.Partjob02Body.Parttimejob.Schoolmate schoolmate : mParttimejob.getSchoolmate_list()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(35, 35);
                layout.setMargins(10, 0, 0, 0);
                imageView.setLayoutParams(layout);
                bitmapUtil.getImage(imageView, schoolmate.getLogopath(), R.drawable.logo_merchant_default);
                linearLayout_label.addView(imageView);
            }
        }
        else{
            TextView tv=(TextView)findViewById(R.id.schoolmate);
            tv.setText("暂无校友");
            tv.setVisibility(View.VISIBLE);
            linearLayout_label.setVisibility(View.GONE);
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=format.format(c1.getTime());
        if(time.compareTo(mParttimejob.getEndtime())>0){
            partjob_isjoin.setText("已截止");
            partjob_isjoin.setBackgroundResource(R.drawable.btn_red_disabled);
        }
        else if("1".equals(mParttimejob.getIsjoin())){
            partjob_isjoin.setText("已报名");
            partjob_isjoin.setBackgroundResource(R.drawable.btn_red_disabled);
        }
        else{
            partjob_isjoin.setText("我要报名");
            partjob_isjoin.setBackgroundResource(R.drawable.btn_yellow_boarded_bg_normal);
        }
        gisx=mParttimejob.getGisx();
        gisy=mParttimejob.getGisy();
        partjob_isjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    //            if("1".equals(UserSubject.getLevel()))
     //               fullfillProfile();
     //           else if(!UserSubject.getGender().equals(mParttimejob.getGenderlimit())) {
      //              Toast toast = Toast.makeText(PartjobDetailActivity.this, "对不起，您所报的兼职性别不符", Toast.LENGTH_LONG);
       //             toast.show();
       //         }
        //        else{
                    final PopupDialog dialog = new PopupDialog(PartjobDetailActivity.this, R.layout.partjob_detail_popupdialog);
                    View contentView = dialog.getContentView();
                    ((ImageView)contentView.findViewById(R.id.partjob_detail_popup_logo)).setImageDrawable(partjob_content_logo.getDrawable());
                    PartjobListAdapter.setSalary((TextView) contentView.findViewById(R.id.partjob_detail_popup_salary), (TextView) contentView.findViewById(R.id.partjob_detail_popup_salaryunit), "￥" + mParttimejob.getSalary(), mParttimejob.getSalaryunit());
                    PartjobListAdapter.setSettlelength((TextView) contentView.findViewById(R.id.partjob_detail_popup_settlelength), mParttimejob.getSettlelength());
                    ((TextView)contentView.findViewById(R.id.partjob_detail_popup_workdays)).setText(mParttimejob.getWorktime());
                    ((TextView) contentView.findViewById(R.id.partjob_detail_popup_address)).setText(mParttimejob.getAddress());
                    ((TextView)contentView.findViewById(R.id.partjob_detail_popup_tip)).setText("您即将报名\""+mParttimejob.getTitle()+"\"兼职。7天内，您仅有一次取消报名的权限，请确保你能按时前往并及时联系商家。");
                    ((Button)contentView.findViewById(R.id.btn_confirm_false)).setText("取消");
                    contentView.findViewById(R.id.btn_confirm_false).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    ((Button)contentView.findViewById(R.id.btn_confirm_true)).setText("确认");
                    contentView.findViewById(R.id.btn_confirm_true).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int num=Integer.parseInt(workernum)-Integer.parseInt(partjob_content_joinnum.getText().toString());
                            String msg = "求同行好友！我刚才在兼职兔报名了一个"+partjob_content_title.getText().toString()+
                                    ",薪资可是"+partjob_content_salary.getText().toString()+
                                    partjob_content_salaryunit.getText().toString()+
                                    "！有没有一起的，还有"+num+"个名额，，快点击：";
                            joinSuccess(msg,mParttimejob.getPhone());
                        }
                    });
                    dialog.show();
    //            }

            }
        });
    }
    private String getUrl() {
        String url = GlobalConstant.h5Url + "/partjob/detail?channel=" + GlobalConstant.TERMINAL_TYPE_ANDROID + "&id=" + partjobid;
        if(UserSubject.isLogin()){
            url += "&studentid=" + UserSubject.getStudentid();
        }
        if(GlobalConstant.gis.hasGis()) {
            url += "&gisx=" + GlobalConstant.gis.getGisx() + "&gisy=" + GlobalConstant.gis.getGisy();
        }
        Log.d("==url:", url);
        return url;
    }

    private String getShareUrl(){
        return GlobalConstant.weixinUrl + "/partjob/detail?id=" + partjobid;
    }

    /**
     * 分享
     * @param msg
     */
    public void postShareMessage(String msg) {
        Log.d("==msg", msg);

        umengShareUtil.setShareContent("兼职分享", msg + getShareUrl(), getShareUrl(), logopath);
        umengShareUtil.openShare();
    }
    /**
     * 显示地图
     * @param gisx
     * @param gisy
     */
    public void showMap(String gisx, String gisy) {
        Intent intent = new Intent(PartjobDetailActivity.this, PartjobDetailMapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("gisx", gisx);
        bundle.putString("gisy", gisy);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 报名成功回调方法
     * @param msg
     * @param tel
     */
    public void joinSuccess(String msg, String tel) {
        //报名
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                final AzService azService = new AzService(PartjobDetailActivity.this);
                Partjob05Request req = new Partjob05Request();
                final Partjob05Request.Joininfo joininfo= req.new Joininfo();
                joininfo.setStudentid(UserSubject.getStudentid());
                joininfo.setGisy(GlobalConstant.gis.getGisy());
                joininfo.setGisx(GlobalConstant.gis.getGisx());
                joininfo.setJoinchannel("2");
                joininfo.setJobid(partjobid);
                req.setJoininfo(joininfo);
                azService.doTrans(req, Partjob05Response.class, new AzService.Callback<Partjob05Response>() {
                    @Override
                    public void success(Partjob05Response response) {
                        ResponseResult result = response.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(2, result.getMessage());
                        } else {
                            messageHandler.postMessage(4);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        }));
        showToast("报名成功");
        Log.d("==", msg);
        Intent intent = new Intent(PartjobDetailActivity.this, PartjobJoinSuccessActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("partjobid", partjobid);
        bundle.putString("msg", msg);
        bundle.putString("tel", tel);
        bundle.putString("url", getShareUrl());
        bundle.putString("logopath", logopath);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * 完善资料
     */
    public void fullfillProfile(){
        //   Intent intent = new Intent(PartjobDetailActivity.this, ProfileImproveActivity.class);
        //  startActivity(intent);
    }
    /**
     * 打电话
     */
    public void callTel(String tel){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + tel));
        startActivity(intent);
    }


}
