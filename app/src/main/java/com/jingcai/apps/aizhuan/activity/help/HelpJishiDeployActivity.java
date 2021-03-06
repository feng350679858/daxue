package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.common.MoneyWatcher;
import com.jingcai.apps.aizhuan.activity.common.NumberWatcher;
import com.jingcai.apps.aizhuan.activity.common.WordCountWatcher;
import com.jingcai.apps.aizhuan.activity.util.PayInsufficientWin;
import com.jingcai.apps.aizhuan.activity.util.PayPwdWin;
import com.jingcai.apps.aizhuan.adapter.help.GroupAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Request;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob16.Partjob16Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob16.Partjob16Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lejing on 15/7/14.
 */
public class HelpJishiDeployActivity extends BaseActivity {
    public static final int REQUEST_CODE_FRIEND = 1101;
    private String genderlimit, money, publiccontent, privatecontent, validtime;

    private MessageHandler messageHandler;
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private PopupWin groupWin;
    private PopupWin end_timeWin;
    private EditText et_end_time;

    private PopupWin selfdeftimeWin;
    private PopupWin genderWin;
    private XListView groupListView;
    private GroupAdapter groupAdapter;
    private int mCurrentStart = 0;  //当前的开始

    private Button btn_jishi_help;
    private EditText et_content, et_secret, et_pay_money;
    private View iv_friend_clear, layout_friend_selected;
    private CircleImageView civ_friend_imgurl;
    private TextView tv_gender, tv_group, tv_friend, tv_end_time;
    private TextView tv_friend_name, tv_friend_school_college;
    private String oldfriendid;
    private TextView tv_secret_tip;
    private TextView tv_content_tip;
    private PayPwdWin payPwdWin;
    private static final Map<String, String> genderMap = new HashMap<String, String>(){{
        put("0", "男");
        put("1", "女");
        put("2", "不限");;
    }};
    private static final  Map<String, String> endTimeMap = new LinkedHashMap<String, String>(){{
        put("10", "10分钟");
        put("30", "30分钟");
        put("60", "1小时");
        put("180", "3小时");
        put("360", "6小时");
        put("720", "12小时");
        put("1440", "24小时");
        put("-1", "自定义");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        genderlimit = getIntent().getStringExtra("genderlimit");
        money = getIntent().getStringExtra("money");
        publiccontent = getIntent().getStringExtra("publiccontent");
        privatecontent = getIntent().getStringExtra("privatecontent");
        validtime = getIntent().getStringExtra("validtime");

        setContentView(R.layout.help_jishi_deploy);
        messageHandler = new MessageHandler(this);

        initHeader();

        initView();

        initData();
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("发布求助");

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        tv_content_tip = (TextView) findViewById(R.id.tv_content_tip);
        et_content = (EditText) findViewById(R.id.et_content);
        et_content.addTextChangedListener(new WordCountWatcher(tv_content_tip, 70));
        tv_secret_tip = (TextView) findViewById(R.id.tv_secret_tip);
        et_secret = (EditText) findViewById(R.id.et_secret);
        et_secret.addTextChangedListener(new WordCountWatcher(tv_secret_tip, 70));

        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_gender.setTag("2");//不限
        tv_group = (TextView) findViewById(R.id.tv_group);
        tv_group.setTag(UserSubject.getSchoolid());
        tv_group.setText(UserSubject.getSchoolname());
        iv_friend_clear = findViewById(R.id.iv_friend_clear);
        layout_friend_selected = findViewById(R.id.layout_friend_selected);
        tv_friend = (TextView) findViewById(R.id.tv_friend);
        civ_friend_imgurl = (CircleImageView) findViewById(R.id.civ_friend_imgurl);
        tv_friend_name = (TextView) findViewById(R.id.tv_friend_name);
        tv_friend_school_college = (TextView) findViewById(R.id.tv_friend_school_college);
        et_pay_money = (EditText) findViewById(R.id.et_pay_money);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_end_time.setTag("30");//30分钟

        et_pay_money.addTextChangedListener(new MoneyWatcher());

        btn_jishi_help = (Button) findViewById(R.id.btn_jishi_help);

        tv_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == genderWin) {
                    View parentView = HelpJishiDeployActivity.this.getWindow().getDecorView();
                    genderWin = PopupWin.Builder.create(HelpJishiDeployActivity.this)
                            .setData(genderMap, new PopupWin.Callback() {
                                @Override
                                public void select(String key, String val) {
                                    tv_gender.setTag(key);
                                    tv_gender.setText(val);
                                }
                            })
                            .setParentView(parentView)
                            .build();
                }
                genderWin.show();
            }
        });
        tv_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == groupWin) {
                    View parentView = HelpJishiDeployActivity.this.getWindow().getDecorView();
                    View contentView = LayoutInflater.from(HelpJishiDeployActivity.this).inflate(R.layout.help_jishi_deploy_group_pop, null);

                    groupWin = PopupWin.Builder.create(HelpJishiDeployActivity.this)
                            .setParentView(parentView)
                            .setContentView(contentView)
                            .build();

                    groupWin.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            groupWin.dismiss();
                        }
                    });

                    groupAdapter = new GroupAdapter(HelpJishiDeployActivity.this);
                    groupListView = (XListView) groupWin.findViewById(R.id.xlv_list);
                    groupListView.setAdapter(groupAdapter);

                    groupListView.setPullRefreshEnable(true);
                    groupListView.setPullLoadEnable(true);
                    groupListView.setAutoLoadEnable(true);

                    groupListView.setXListViewListener(new XListView.IXListViewListener() {
                        @Override
                        public void onRefresh() {
                            groupAdapter.clearData();
                            mCurrentStart = 0;
                            groupListView.setPullLoadEnable(true);
                            initGroupData();
                        }

                        @Override
                        public void onLoadMore() {
                            initGroupData();
                        }
                    });

                    groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Base04Response.Body.Region region = groupAdapter.getItem(position);
                            GroupAdapter.ViewHolder holder = (GroupAdapter.ViewHolder) view.getTag();
                            tv_group.setText(holder.region.getRegionname());
                            tv_group.setTag(holder.region.getRegionid());
                            groupWin.dismiss();
                        }
                    });

                    initGroupData();
                }
                groupWin.show();
            }
        });
        findViewById(R.id.layout_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpJishiDeployActivity.this, HelpFriendOnlineActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FRIEND);
            }
        });
        iv_friend_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldfriendid = null;
                showSelectWin(false);
            }
        });
        tv_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == end_timeWin) {
                    View parentView = HelpJishiDeployActivity.this.getWindow().getDecorView();
                    end_timeWin = PopupWin.Builder.create(HelpJishiDeployActivity.this)
                            .setData(endTimeMap, new PopupWin.Callback() {
                                @Override
                                public void select(String key, String val) {
                                    if ("-1".equals(key)) {
                                        messageHandler.postMessage(4);
                                    } else {
                                        tv_end_time.setTag(key);
                                        tv_end_time.setText(val);
                                    }
                                }
                            })
                            .setParentView(parentView)
                            .build();
                }
                end_timeWin.show();
            }
        });

        btn_jishi_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkDeploy()){
                    return;
                }
                if (null == payPwdWin) {
                    payPwdWin = new PayPwdWin(HelpJishiDeployActivity.this);
                    payPwdWin.setCallback(new PayPwdWin.Callback() {
                        @Override
                        public void call(String pwd) {
                            doDeploy(pwd);
                        }
                    });
                    payPwdWin.setTitle("确认支付");
                }
                payPwdWin.showPay(Double.parseDouble(et_pay_money.getText().toString()));
            }
        });
    }

    private void initData(){
        if(null != publiccontent){
            et_content.setText(publiccontent);
        }
        if(null != privatecontent){
            et_secret.setText(privatecontent);
        }
        if(null != genderlimit){
            String val = genderMap.get(genderlimit);
            if(null != val){
                tv_gender.setText(val);
                tv_gender.setTag(genderlimit);
            }
        }
        if(null != money){
            et_pay_money.setText(money);
        }
        if(null != validtime){
            String val = null;
            try {
                val = endTimeMap.get(validtime);
                if (null == val) {
                    val = String.format("%d小时", Integer.parseInt(validtime) / 60);
                }
            }catch (Exception e){}
            if(null != val){
                tv_end_time.setText(val);
                tv_end_time.setTag(validtime);
            }
        }

    }

    private void showSelectWin(boolean selectFlag) {
        if (selectFlag) {
            tv_friend.setVisibility(View.GONE);
            iv_friend_clear.setVisibility(View.VISIBLE);
            layout_friend_selected.setVisibility(View.VISIBLE);
        } else {
            tv_friend.setVisibility(View.VISIBLE);
            iv_friend_clear.setVisibility(View.GONE);
            layout_friend_selected.setVisibility(View.GONE);
        }
    }

    private void showSeldefEndtimeDialog() {
        if (null == selfdeftimeWin) {
            View parentView = this.getWindow().getDecorView();
            View contentView = LayoutInflater.from(this).inflate(R.layout.help_jishi_deploy_endtime_pop, null);
            et_end_time = (EditText) contentView.findViewById(R.id.et_end_time);
            et_end_time.addTextChangedListener(new NumberWatcher());

            selfdeftimeWin = PopupWin.Builder.create(this)
                    .setParentView(parentView)
                    .setContentView(contentView)
                    .build();
            selfdeftimeWin.setAction(R.id.iv_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selfdeftimeWin.dismiss();
                }
            }).setAction(R.id.iv_confirm, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (et_end_time.getText().toString().length() < 1) {
                        showToast("截止时间不能为空");
                        return;
                    }
                    int end_time = Integer.parseInt(et_end_time.getText().toString());
                    tv_end_time.setText(String.format("%s小时", end_time));
                    tv_end_time.setTag(String.valueOf(end_time * 60));
                    selfdeftimeWin.dismiss();
                }
            });
        }
        selfdeftimeWin.show();
        showInputMethodDialog(et_end_time);
    }

    private void onLoad() {
        groupListView.stopRefresh();
        groupListView.stopLoadMore();
        groupListView.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
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
                    try {
                        List<Base04Response.Body.Region> list = (List<Base04Response.Body.Region>) msg.obj;
                        groupAdapter.addData(list);
                        groupAdapter.notifyDataSetChanged();
                        mCurrentStart += list.size();
                        onLoad();
                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            groupListView.setPullLoadEnable(false);
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast("获取圈子失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        showToast("发布即时帮助成功", 0);
                        String helpid = String.valueOf(msg.obj);
                        Intent intent = new Intent(HelpJishiDeployActivity.this, HelpJishiDeployingActivity.class);
                        intent.putExtra("helpid", helpid);
                        startActivity(intent);
                        finish();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 3: {
                    try {
                        showToast("发布失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 4: {
                    showSeldefEndtimeDialog();
                    break;
                }
                case 5: {
                    try {
                        PayInsufficientWin win = new PayInsufficientWin(HelpJishiDeployActivity.this);
                        win.show();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void initGroupData() {
        if (actionLock.tryLock()) {
            final Context context = this;
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final AzService azService = new AzService(context);
                    final Base04Request req = new Base04Request();
                    final Base04Request.Region region = req.new Region();
                    region.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                    region.setAreacode(GlobalConstant.gis.getAreacode());
                    region.setStart(String.valueOf(mCurrentStart));
                    region.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    req.setRegion(region);
                    azService.doTrans(req, Base04Response.class, new AzService.Callback<Base04Response>() {
                        @Override
                        public void success(Base04Response response) {
                            ResponseResult result = response.getResult();
                            if ("0".equals(result.getCode())) {
                                Base04Response.Body partjob07Body = response.getBody();
                                List<Base04Response.Body.Region> regionList = partjob07Body.getRegion_list();
                                if (null == regionList) {
                                    regionList = new ArrayList<Base04Response.Body.Region>();
                                }
                                messageHandler.postMessage(0, regionList);
                            } else {
                                messageHandler.postMessage(1, result.getMessage());
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FRIEND: {
                if (resultCode == RESULT_OK) {
                    oldfriendid = data.getStringExtra("userid");
                    String username = data.getStringExtra("username");
                    String logoimgurl = data.getStringExtra("logoimgurl");
                    String schoolname = data.getStringExtra("schoolname");
                    String collegename = data.getStringExtra("collegename");
                    bitmapUtil.getImage(civ_friend_imgurl, logoimgurl, true, R.drawable.default_head_img);
                    tv_friend_name.setText(username);
                    tv_friend_school_college.setText(schoolname + "-" + collegename);
                    showSelectWin(true);
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private boolean checkDeploy(){
        if(StringUtil.isEmpty(et_content.getText().toString())){
            showToast("请输入求助内容");
            return false;
        }
        if(StringUtil.isEmpty(et_pay_money.getText().toString())){
            showToast("请输入酬谢金额");
            return false;
        }
        if(Double.parseDouble(et_pay_money.getText().toString())<2){
            showToast("酬谢金额不能低于2元");
            return false;
        }
        if(StringUtil.isEmpty(tv_end_time.getTag().toString())){
            showToast("请输入截止时间");
            return false;
        }
        return true;
    }

    private void doDeploy(final String pwd){
        if (!actionLock.tryLock()) {
            return;
        }
        showProgressDialog("发布中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob16Request req = new Partjob16Request();
                Partjob16Request.Parttimejob job = req.new Parttimejob();
                job.setStudentid(UserSubject.getStudentid());
                job.setType("2");
                job.setPaypassword(DES3Util.encrypt(pwd));
                job.setFriendid(oldfriendid);
                job.setGenderlimit(tv_gender.getTag().toString());
                job.setGisx(GlobalConstant.getGis().getGisx());
                job.setGisy(GlobalConstant.getGis().getGisy());
                job.setMoney(et_pay_money.getText().toString());
                job.setPubliccontent(et_content.getText().toString());
                job.setPrivatecontent(et_secret.getText().toString());
                job.setRegionid(tv_group.getTag().toString());
                job.setValidtime(tv_end_time.getTag().toString());
                req.setParttimejob(job);
                new AzService().doTrans(req, Partjob16Response.class, new AzService.Callback<Partjob16Response>() {
                    @Override
                    public void success(Partjob16Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(2, resp.getBody().getParttimejob().getHelpid());
                        } else if("S012".equals(resp.getResultCode())){//余额不足
                            messageHandler.postMessage(5);
                        } else {
                            messageHandler.postMessage(3, resp.getResultMessage());
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
}
