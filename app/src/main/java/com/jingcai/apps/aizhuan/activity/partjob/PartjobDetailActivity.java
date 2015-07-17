package com.jingcai.apps.aizhuan.activity.partjob;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.PopupDialog;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.UmengShareUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by chenchao on 15/7/14.
 */
public class PartjobDetailActivity extends BaseActivity {
    private String partjobid, logopath;
    private WebView wv_partjob_detail;
    private UmengShareUtil umengShareUtil;
    private MessageHandler messageHandler;
    private BitmapUtil bitmapUtil;
    /**
     * 接受到的数据
     */
    private Partjob02Response.Partjob02Body.Parttimejob mParttimejob;
    private String joinnum,workernum,gisx,gisy;
    /**
     *
     * 控件
     */
    private ImageView partjob_content_top,partjob_content_logo,partjob_content_location;
    private TextView partjob_content_title,partjob_content_salary,partjob_content_salaryunit,partjob_content_address;
    private TextView partjob_content_settlelength,partjob_content_peoplenum,partjob_content_workdays;
    private TextView partjob_content_worktime,partjob_content_endtime,partjob_content_workcontent;
    private TextView partjob_content_heightlimit,partjob_content_healthlimit,partjob_content_genderlimit,partjob_content_remarks;
    private Button partjob_isjoin;

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
        TextView tv_info = (TextView)findViewById(R.id.tv_func);
        tv_info.setVisibility(View.VISIBLE);
        tv_info.setText("分享");
        tv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num=Integer.parseInt(workernum)-Integer.parseInt(joinnum);
                String msg = "我发现了一个兼职："+partjob_content_title.getText().toString()+
                        ",居然"+partjob_content_salary.getText().toString()+
                        partjob_content_salaryunit.getText().toString()+
                        "的工资,地点就在："+partjob_content_address.getText().toString()+
                        "。现在还有"+num+"个名额，还不快来看看：";

                postShareMessage(msg);
            }
        });

        ((ImageView)findViewById(R.id.iv_func)).setVisibility(View.INVISIBLE);
        ((ImageView)findViewById(R.id.iv_bird_badge)).setVisibility(View.INVISIBLE);
    }
    private void initView(){
        partjob_content_top=(ImageView)findViewById(R.id.partjob_content_top);
        partjob_content_logo=(ImageView)findViewById(R.id.partjob_content_logo);
        partjob_content_location=(ImageView)findViewById(R.id.partjob_content_location);
        partjob_content_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap(gisx,gisy);
            }
        });
        partjob_content_address=(TextView)findViewById(R.id.partjob_content_address);
        partjob_content_title=(TextView)findViewById(R.id.partjob_content_title);
        partjob_content_salary=(TextView)findViewById(R.id.partjob_content_salary);
        partjob_content_salaryunit=(TextView)findViewById(R.id.partjob_content_salaryunit);
        partjob_content_settlelength=(TextView)findViewById(R.id.partjob_content_settlelength);
        partjob_content_peoplenum=(TextView)findViewById(R.id.partjob_content_peoplenum);
        partjob_content_workdays=(TextView)findViewById(R.id.partjob_content_workdays);
        partjob_content_worktime=(TextView)findViewById(R.id.partjob_content_worktime);
        partjob_content_endtime=(TextView)findViewById(R.id.partjob_content_endtime);
        partjob_content_workcontent=(TextView)findViewById(R.id.partjob_content_workcontent);
        partjob_content_heightlimit=(TextView)findViewById(R.id.partjob_content_heightlimit);
        partjob_content_healthlimit=(TextView)findViewById(R.id.partjob_content_healthlimit);
        partjob_content_genderlimit=(TextView)findViewById(R.id.partjob_content_genderlimit);
        partjob_content_remarks=(TextView)findViewById(R.id.partjob_content_remarks);
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
                case 3: {
//                    showToast("获取用户信息失败");
//                    setResult(RESULT_CANCELED, null);
//                    finish();
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
        bitmapUtil.getImage(partjob_content_logo, mParttimejob.getLogopath(), true, R.drawable.logo_merchant_default);
        partjob_content_title.setText(mParttimejob.getTitle());
        PartjobListAdapter.setSalary(partjob_content_salary, partjob_content_salaryunit, "￥"+mParttimejob.getSalary(), mParttimejob.getSalaryunit());
        PartjobListAdapter.setSettlelength(partjob_content_settlelength, mParttimejob.getSettlelength());
        joinnum=mParttimejob.getJoinnum();
        workernum=mParttimejob.getWorkernum();

        SpannableStringBuilder builder = new SpannableStringBuilder(mParttimejob.getJoinnum()+"/"+mParttimejob.getWorkernum()+"人");
        ForegroundColorSpan redSpan = new ForegroundColorSpan(R.color.red_normal);
        builder.setSpan(redSpan, 0, mParttimejob.getJoinnum().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        partjob_content_peoplenum.setText(builder);

        PartjobListAdapter.setWorkdays(partjob_content_workdays, "0", mParttimejob.getWorkdays());
        partjob_content_worktime.setText(mParttimejob.getWorktime());
        partjob_content_endtime.setText(mParttimejob.getEndtime());
        partjob_content_address.setText(mParttimejob.getAddress());
        partjob_content_workcontent.setText(mParttimejob.getWorkcontent());
        if("0".equals(mParttimejob.getHeightupperlimit())&& "0".equals(mParttimejob.getHeightlowerlimit()))
            partjob_content_heightlimit.setText("1.身高：不限");
        else
            partjob_content_heightlimit.setText("1.身高："+mParttimejob.getHeightlowerlimit()+"~"+mParttimejob.getHeightupperlimit());
        partjob_content_healthlimit.setText("2.是否需要健康证："+mParttimejob.getHealthlimit());
        partjob_content_genderlimit.setText("3.性别："+mParttimejob.getGenderlimit());
        partjob_content_remarks.setText(mParttimejob.getRemarks());
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
   //             if(!UserSubject.isLogin())
    //                requestLogin();
    //            else if("1".equals(UserSubject.getLevel()))
     //               fullfillProfile();
     //           else if(!UserSubject.getGender().equals(mParttimejob.getGenderlimit())) {
      //              Toast toast = Toast.makeText(PartjobDetailActivity.this, "对不起，您所报的兼职性别不符", Toast.LENGTH_LONG);
       //             toast.show();
       //         }
        //        else{
                    final PopupDialog dialog = new PopupDialog(PartjobDetailActivity.this, R.layout.partjob_detail_popupdialog);
                    View contentView = dialog.getContentView();
                    ((ImageView)contentView.findViewById(R.id.partjob_detail_popup_logo)).setImageDrawable(partjob_content_logo.getDrawable());
                   // PartjobListAdapter.setSalary((TextView) contentView.findViewById(R.id.partjob_detail_popup_salary), (TextView) contentView.findViewById(R.id.partjob_content_salaryunit), "￥"+mParttimejob.getSalary(), mParttimejob.getSalaryunit());
                    PartjobListAdapter.setSettlelength((TextView) contentView.findViewById(R.id.partjob_detail_popup_settlelength), mParttimejob.getSettlelength());
                    PartjobListAdapter.setWorkdays((TextView) contentView.findViewById(R.id.partjob_detail_popup_workdays), "0", mParttimejob.getWorkdays());
                    ((TextView)contentView.findViewById(R.id.partjob_detail_popup_address)).setText(mParttimejob.getAddress());
                    ((TextView)contentView.findViewById(R.id.partjob_detail_popup_tip)).setText("您即将报名\""+mParttimejob.getTitle()+"\"兼职。7天内，您仅有一次取消报名的权限，请确保你能按时前往并及时联系商家。");
                    contentView.findViewById(R.id.partjob_detail_popup_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    contentView.findViewById(R.id.partjob_detail_popup_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int num=Integer.parseInt(workernum)-Integer.parseInt(joinnum);
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
     * 请求重新登录
     */
    public void requestLogin() {
        Log.d("==", "requestLogin");
        startActivityForLogin();
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


    @Override
    protected void afterLoginSuccess() {
        wv_partjob_detail.loadUrl(getUrl());
    }
}
