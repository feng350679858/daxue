package com.jingcai.apps.aizhuan.activity.mine.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.help.HelpEvaluateActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDeployActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDetailActivity;
import com.jingcai.apps.aizhuan.activity.util.PayInsufficientWin;
import com.jingcai.apps.aizhuan.activity.util.PayPwdWin;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob16.Partjob16Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob20.Partjob20Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob22.Partjob22Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob26.Partjob26Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob33.Partjob33Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lejing on 15/7/24.
 */
public class MineHelpJishiActivity extends BaseActivity {
    private String helpid;
    private boolean provideFlag = true;
    private AzExecutor azExecutor = new AzExecutor();
    private AzService azService = new AzService();
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private MineHelpProcessUtil mineHelpProcessUtil = new MineHelpProcessUtil(this);
    private MessageHandler messageHandler;
    private CheckBox cb_tip;
    private TextView tv_status;
    private TextView tv_time;
    private TextView tv_money;
    private TextView tv_private_content;
    private TextView tv_public_content;
    private Button btn_action;
    private CircleImageView civ_head_logo;
    private TextView tv_stu_name;
    private long timeout, receiveTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.mine_help_jishi);

        helpid = getIntent().getStringExtra("helpid");
        provideFlag = getIntent().getBooleanExtra("provideFlag", provideFlag);

        if (StringUtil.isEmpty(helpid)) {
            finish();
        } else {
            initHeader();

            initView();

            initData();
        }
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("求助详情");

        final ImageView iv_func = (ImageView) findViewById(R.id.iv_func);
        iv_func.setVisibility(View.VISIBLE);
        iv_func.setImageResource(R.drawable.icon__header_more);
        iv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dp10_px = MineHelpJishiActivity.this.getResources().getDimensionPixelSize(R.dimen.dp_10);
                Log.d("==", "---------" + dp10_px);
                View contentView = LayoutInflater.from(MineHelpJishiActivity.this).inflate(R.layout.help_wenda_answer_setting_pop, null);
                PopupWin groupWin = PopupWin.Builder.create(MineHelpJishiActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(iv_func)
                        .setContentView(contentView)
                        .build();
                {
                    View tv_pop_abuse_report = groupWin.findViewById(R.id.tv_pop_abuse_report);
                    View tv_pop_share = groupWin.findViewById(R.id.tv_pop_share);
                    if (provideFlag) {
                        tv_pop_abuse_report.setVisibility(View.GONE);
                    } else {
                        tv_pop_abuse_report.setVisibility(View.VISIBLE);
                        tv_pop_abuse_report.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {//举报
                                Log.d("==", "-----------tv_pop_abuse_report---");
                            }
                        });
                    }

                    tv_pop_share.setVisibility(View.VISIBLE);
                    tv_pop_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//分享
                            Log.d("==", "-----------tv_pop_share---");
                        }
                    });
                }
                groupWin.show(Gravity.TOP | Gravity.RIGHT, dp10_px, dp10_px * 6);
            }
        });

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mineHelpProcessUtil.initView();

        tv_status = (TextView) findViewById(R.id.tv_status);
        cb_tip = (CheckBox) findViewById(R.id.cb_tip);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_public_content = (TextView) findViewById(R.id.tv_public_content);
        tv_private_content = (TextView) findViewById(R.id.tv_private_content);

        civ_head_logo = (CircleImageView) findViewById(R.id.civ_head_logo);
        tv_stu_name = (TextView) findViewById(R.id.tv_stu_name);
//        cb_tip.setChecked(false);

        View btn_view_comment = findViewById(R.id.btn_view_comment);
        btn_view_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PayPwdWin payPwdWin = new PayPwdWin(MineHelpJishiActivity.this);
//                payPwdWin.setTitle("确认结算");
//                payPwdWin.setCallback(new PayPwdWin.Callback() {
//                    @Override
//                    public void call(String pwd) {
//                        showToast("----------");
//                    }
//                });
//                payPwdWin.showPay(Double.parseDouble("3.51"));
                //查看评论
                Intent intent = new Intent(MineHelpJishiActivity.this, HelpJishiDetailActivity.class);
                intent.putExtra("helpid", helpid);
                intent.putExtra("type", "1");//1跑腿 还是 3公告
                startActivity(intent);
            }
        });
        btn_action = (Button) findViewById(R.id.btn_action);
    }

    private void initData() {
        if (!actionLock.tryLock()) return;
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Partjob19Request req = new Partjob19Request();
                Partjob19Request.Parttimejob job = req.new Parttimejob();
                job.setType("1");//1：即时型求助   3：公告求助
                job.setStudentid(UserSubject.getStudentid());
                job.setHelpid(helpid);
                req.setParttimejob(job);

                azService.doTrans(req, Partjob19Response.class, new AzService.Callback<Partjob19Response>() {
                    @Override
                    public void success(Partjob19Response response) {
                        receiveTimestamp = System.currentTimeMillis();
                        ResponseResult result = response.getResult();
                        if ("0".equals(result.getCode())) {
                            Partjob19Response.Parttimejob job = response.getBody().getParttimejob();
                            messageHandler.postMessage(0, job);
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
                        Partjob19Response.Parttimejob job = (Partjob19Response.Parttimejob) msg.obj;
                        setValues(job);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 9: {
                    try {
                        showToast(String.valueOf(msg.obj));
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 11: {
                    long lefttime = timeout - (System.currentTimeMillis() - receiveTimestamp) / 1000;
                    cb_tip.setText(getLefttimeStr(lefttime));
                    break;
                }
                case 20: {
                    try {
                        showToast("取消求助成功，等待确认");
                        Intent data = new Intent();
                        data.putExtra("status", "3");//取消中
                        setResult(RESULT_OK, data);
                        finish();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 21: {
                    try {
                        showToast("成功完成帮助");
                        Intent data = new Intent();
                        //data.putExtra("status", "3");//取消中
                        setResult(RESULT_OK, data);
                        finish();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 22: {
                    try {
                        showToast("取消处理成功");
                        Intent data = new Intent();
                        //data.putExtra("status", "3");//取消中
                        setResult(RESULT_OK, data);
                        finish();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
//                case 23: {
//                    try {
//                        showToast("重新发布成功");
//                        Intent data = new Intent();
//                        //data.putExtra("status", "3");//取消中
//                        setResult(RESULT_OK, data);
//                        finish();
//                    } finally {
//                        actionLock.unlock();
//                    }
//                    break;
//                }
//                case 24: {
//                    try {
//                        PayInsufficientWin win = new PayInsufficientWin(MineHelpJishiActivity.this);
//                        win.show();
//                    } finally {
//                        actionLock.unlock();
//                    }
//                    break;
//                }
                case 25: {
                    try {
                        showToast("结算成功");
                        Intent data = new Intent();
                        //data.putExtra("status", "3");//取消中
                        setResult(RESULT_OK, data);
                        finish();
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

    private String getLefttimeStr(long lefttime) {
        long hours = lefttime / 3600;
        long minutes = (lefttime % 3600) / 60;
        long seconds = lefttime % 60;
        return String.format("剩余时间 %02d:%02d:%02d", hours, minutes, seconds);
    }

    private void setValues(Partjob19Response.Parttimejob job) {
        if (StringUtil.isNotEmpty(job.getMoney())) {
            tv_money.setText(String.format("%.2f元", Double.parseDouble(job.getMoney())));
        } else {
            tv_money.setText("");
        }
        tv_time.setText(DateUtil.getHumanlityDateString(job.getOptime()));
        tv_public_content.setText(job.getPubliccontent());
        tv_private_content.setText(job.getPrivatecontent());
        bitmapUtil.getImage(civ_head_logo, job.getSourceimgurl(), R.drawable.default_head_img);
        tv_stu_name.setText(job.getSourcename());
        tv_stu_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentView = MineHelpJishiActivity.this.getWindow().getDecorView();
                View contentView = LayoutInflater.from(MineHelpJishiActivity.this).inflate(R.layout.mine_help_jishi_connect, null);

                PopupWin groupWin = PopupWin.Builder.create(MineHelpJishiActivity.this)
                        .setParentView(parentView)
                        .setContentView(contentView)
                        .build();
                groupWin.show();
            }
        });

        setTipProcess(job);

        setAction(job);
    }

    private void setAction(final Partjob19Response.Parttimejob job) {
        final String status = job.getStatus();
        if ("1".equals(status)) {//求助中
            if (provideFlag) {
                btn_action.setText("取消求助");
            } else {
                btn_action.setVisibility(View.GONE);
            }
        } else if ("2".equals(status)) {//帮助中
            if (provideFlag) {
                btn_action.setText("取消求助");
            } else {
                btn_action.setText("完成帮助");
            }
        } else if ("3".equals(status)) {//取消中
            if (provideFlag) {
                btn_action.setVisibility(View.GONE);
            } else {
                btn_action.setText("取消处理");
            }
        } else if ("4".equals(status)) {//已取消
            if (provideFlag) {
                btn_action.setText("重新发布");
            } else {
                btn_action.setVisibility(View.GONE);
            }
        } else if ("5".equals(status)) {//已帮助
            if (provideFlag) {
                btn_action.setText("马上结算");
            } else {
                btn_action.setVisibility(View.GONE);
            }
        } else if ("6".equals(status)) {//已结算
            if (provideFlag) {
                btn_action.setText("立即评价");
            } else {
                btn_action.setText("立即评价");
            }
        } else if ("7".equals(status)) {//已超时
            if (provideFlag) {
                btn_action.setText("重新发布");
            } else {
                btn_action.setVisibility(View.GONE);
            }
        }
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(status)) {//求助中
                    if (provideFlag) {
                        //取消求助
                        doCancelHelp(job);
                    }
                } else if ("2".equals(status)) {//帮助中
                    if (provideFlag) {
                        doCancelHelp(job);
                    } else {
                        //完成帮助
                        doFinishHelp(job);
                    }
                } else if ("3".equals(status)) {//取消中
                    if (!provideFlag) {
                        //取消处理
                        dealwithCancel(job);
                    }
                } else if ("4".equals(status)) {//已取消
                    if (provideFlag) {
                        //重新发布
                        doRedeploy(job);
                    }
                } else if ("5".equals(status)) {//已帮助
                    if (provideFlag) {
                        //马上结算
                        doBalanceNow(job);
                    } else {
                        btn_action.setVisibility(View.GONE);
                    }
                } else if ("6".equals(status)) {//已结算
                    if (provideFlag) {
                        //立即评价
                        doEvaluate(job);
                    } else {
                        //立即评价
                        doEvaluate(job);
                    }
                } else if ("7".equals(status)) {//已超时
                    if (provideFlag) {
                        //重新发布
                        doRedeploy(job);
                    }
                }
            }
        });
    }

    //TODO 立即评价
    private void doEvaluate(Partjob19Response.Parttimejob job) {
        Intent intent = new Intent(this, HelpEvaluateActivity.class);

        startActivity(intent);
    }

    //TODO 马上结算
    private void doBalanceNow(final Partjob19Response.Parttimejob job) {
        PayPwdWin payPwdWin = new PayPwdWin(this);
        payPwdWin.setCallback(new PayPwdWin.Callback() {
            @Override
            public void call(String pwd) {
                doBalanceNowInner(job, pwd);
            }
        });
        payPwdWin.setTitle("马上结算");
        payPwdWin.showPay(Double.parseDouble(job.getMoney()));
    }

    private void doBalanceNowInner(Partjob19Response.Parttimejob job, final String pwd) {
        if (!actionLock.tryLock()) return;
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Partjob22Request req = new Partjob22Request();
                Partjob22Request.Parttimejob job = req.new Parttimejob();
                job.setStudentid(UserSubject.getStudentid());
                job.setHelpid(helpid);
                job.setPaypassword(DES3Util.encrypt(pwd));
                req.setParttimejob(job);
                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(25);
                        } else {
                            messageHandler.postMessage(9, "结算失败:" + resp.getResultMessage());
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

    //TODO 重新发布
    private void doRedeploy(final Partjob19Response.Parttimejob job) {
//        PayPwdWin payPwdWin = new PayPwdWin(this);
//        payPwdWin.setCallback(new PayPwdWin.Callback() {
//            @Override
//            public void call(String pwd) {
//                doDeploy(job, pwd);
//            }
//        });
//        payPwdWin.setTitle("确认支付");
//
//        payPwdWin.showPay(Double.parseDouble(job.getMoney()));
        Intent intent = new Intent(this, HelpJishiDeployActivity.class);
        //
        startActivity(intent);
    }

//    private void doDeploy(final Partjob19Response.Parttimejob job, final String pwd) {
//        if (!actionLock.tryLock()) {
//            return;
//        }
//        showProgressDialog("发布中...");
//        new AzExecutor().execute(new Runnable() {
//            @Override
//            public void run() {
//                Partjob16Request req = new Partjob16Request();
//                Partjob16Request.Parttimejob reqjob = req.new Parttimejob();
//                reqjob.setStudentid(UserSubject.getStudentid());
//                reqjob.setType("2");
//                reqjob.setPaypassword(DES3Util.encrypt(pwd));
//                reqjob.setFriendid(job.getFriendid());
//                reqjob.setGenderlimit(job.getGenderlimit());
//                reqjob.setGisx(GlobalConstant.getGis().getGisx());
//                reqjob.setGisy(GlobalConstant.getGis().getGisy());
//                reqjob.setMoney(job.getMoney());
//                reqjob.setPubliccontent(job.getPubliccontent());
//                reqjob.setPrivatecontent(job.getPrivatecontent());
//                reqjob.setRegionid(job.getRegionid());
//                reqjob.setValidtime(job.getValidtime());
//                //job.setPaypassword(UserSubject.get);
//                req.setParttimejob(reqjob);
//                new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
//                    @Override
//                    public void success(BaseResponse resp) {
//                        if ("0".equals(resp.getResultCode())) {
//                            messageHandler.postMessage(23);
//                        } else if ("S012".equals(resp.getResultCode())) {//余额不足
//                            messageHandler.postMessage(24);
//                        } else {
//                            messageHandler.postMessage(9, "重新发布失败:"+resp.getResultMessage());
//                        }
//                    }
//
//                    @Override
//                    public void fail(AzException e) {
//                        messageHandler.postException(e);
//                    }
//                });
//            }
//        });
//    }

    //TODO 取消处理
    private void dealwithCancel(Partjob19Response.Parttimejob job) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("1", "同意");
        map.put("2", "不同意");
        View parentView = this.getWindow().getDecorView();
        PopupWin genderWin = PopupWin.Builder.create(this)
                .setData(map, new PopupWin.Callback() {
                    @Override
                    public void select(final String key, String val) {
                        if (!actionLock.tryLock()) return;
                        azExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                Partjob26Request req = new Partjob26Request();
                                Partjob26Request.Parttimejob job = req.new Parttimejob();
                                job.setStudentid(UserSubject.getStudentid());
                                job.setHelpid(helpid);
                                job.setType(key);
                                req.setParttimejob(job);
                                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                                    @Override
                                    public void success(BaseResponse resp) {
                                        if ("0".equals(resp.getResultCode())) {
                                            messageHandler.postMessage(22);
                                        } else {
                                            messageHandler.postMessage(9, "处理失败:" + resp.getResultMessage());
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
                })
                .setParentView(parentView)
                .build();
        genderWin.show();
    }

    //TODO 完成帮助
    private void doFinishHelp(Partjob19Response.Parttimejob job) {
        if (!actionLock.tryLock()) return;
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Partjob33Request req = new Partjob33Request();
                Partjob33Request.Parttimejob job = req.new Parttimejob();
                job.setStudentid(UserSubject.getStudentid());
                job.setHelpid(helpid);
                req.setParttimejob(job);
                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(21);
                        } else {
                            messageHandler.postMessage(9, "完成帮助失败:" + resp.getResultMessage());
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

    //TODO 取消求助
    private void doCancelHelp(Partjob19Response.Parttimejob job) {
        if (!actionLock.tryLock()) return;
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Partjob20Request req = new Partjob20Request();
                Partjob20Request.Parttimejob job = req.new Parttimejob();
                job.setHelpid(helpid);
                job.setPeroid("1");//1发布成功后取消
                job.setStudentid(UserSubject.getStudentid());
                job.setHelpid(helpid);
                req.setParttimejob(job);
                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(20);
                        } else {
                            messageHandler.postMessage(9, "取消求助失败:" + resp.getResultMessage());
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

    private void setTipProcess(Partjob19Response.Parttimejob job) {
        if ("1".equals(job.getEvelflag())) {
            job.setStatus("99");
        }
        Tip tip = tipMap.get((provideFlag ? "provide" : "receive") + job.getStatus());
        tv_status.setText(tip.tip);
        if (null != tip.subtip) {
            cb_tip.setText(tip.subtip);
            cb_tip.setChecked(!tip.over);//显示灰色
        } else if (StringUtil.isNotEmpty(job.getTimeout())) {
            timeout = Long.parseLong(job.getTimeout());
            azExecutor.execute(timeTask);
        }
        mineHelpProcessUtil.setProcess(tip.nodeCount, tip.selectCount, tip.strs);
    }

    private boolean timerStopFlag = false;
    private Runnable timeTask = new Runnable() {
        @Override
        public void run() {
            while (!timerStopFlag) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messageHandler.postMessage(11);
            }
        }
    };

    @Override
    protected void onStop() {
        timerStopFlag = true;
        super.onStop();
    }

    private final static String[] processStr4_1 = new String[]{"已发布", "帮助中", "已帮助", "已结算"};
    private final static String[] processStr4_2 = new String[]{"已发布", "帮助中", "取消中", "已取消"};
    private final static String[] processStr2_1 = new String[]{"已发布", "已取消"};
    private final static String[] processStr2_2 = new String[]{"已发布", "已超时"};

    private final static String[] processStr3 = new String[]{"帮助中", "已帮助", "已结算"};
    private final static String[] processStr2_3 = new String[]{"帮助中", "取消中"};
    private final static String[] processStr2_4 = new String[]{"帮助中", "已取消"};

    private final static Map<String, Tip> tipMap = new HashMap<String, Tip>() {{
        put("provide1", new Tip("求助中，等待接单", null, false, 4, 1, processStr4_1));
        put("provide2", new Tip("帮助中，等待完成", "对方玩命帮助中", false, 4, 2, processStr4_1));
        put("provide3", new Tip("取消中，等待确认", null, false, 4, 3, processStr4_2));
        put("provide4", new Tip("已取消，求助结束", "谢谢使用", true, 2, 2, processStr2_1));
        put("provide5", new Tip("已帮助，等待结算", null, false, 4, 3, processStr4_1));
        put("provide6", new Tip("已结算，立即评价", "赶快评价一下吧", true, 4, 4, processStr4_1));
        put("provide7", new Tip("已超时，求助结束", "谢谢使用", true, 2, 2, processStr2_2));
        put("provide99", new Tip("已评价，帮助完成", "谢谢使用", true, 4, 4, processStr4_1));

        //put("receive1", new Tip("求助中，等待接单", null, false));
        put("receive2", new Tip("帮助中，等待完成", "对方玩命帮助中", false, 3, 1, processStr3));
        put("receive3", new Tip("取消中，等待确认", null, false, 2, 2, processStr2_3));
        put("receive4", new Tip("已取消，求助结束", "谢谢使用", true, 2, 2, processStr2_4));
        put("receive5", new Tip("已帮助，等待结算", null, false, 3, 2, processStr3));
        put("receive6", new Tip("已结算，立即评价", "赶快评价一下吧", true, 3, 3, processStr3));
        //put("receive7", new Tip("已超时，求助结束", "谢谢使用", true));
        put("receive99", new Tip("已评价，帮助完成", "谢谢使用", true, 3, 3, processStr3));
    }};

    private static class Tip {
        boolean over;
        String tip;
        String subtip;
        int nodeCount;
        int selectCount;
        String[] strs;

        public Tip(String a, String b, boolean over, int nodeCount, int selectCount, String[] strs) {
            this.tip = a;
            this.subtip = b;
            this.over = over;
            this.nodeCount = nodeCount;
            this.selectCount = selectCount;
            this.strs = strs;
        }
    }
}
