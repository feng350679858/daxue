package com.jingcai.apps.aizhuan.activity.mine.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.jingcai.apps.aizhuan.activity.help.HelpWendaRewardActivity;
import com.jingcai.apps.aizhuan.adapter.help.AbuseReportHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob20.Partjob20Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob26.Partjob26Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob33.Partjob33Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
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
    public static final int REQUEST_CODE_PROVIDE_JISIH_REWARD = 1101;
    public static final int REQUEST_CODE_RECEIVE_JISIH_EVALUATE = 1201;
    private String helpid;
    private boolean provideFlag = true;
    private AzExecutor azExecutor = new AzExecutor();
    private AzService azService = new AzService();
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private MineHelpProcessUtil mineHelpProcessUtil = new MineHelpProcessUtil(this);
    private MessageHandler messageHandler;
    private CheckBox cb_tip;
    private TextView tv_status, tv_time, tv_money, tv_private_content, tv_public_content, tv_stu_name;
    private Button btn_action;
    private CircleImageView civ_head_logo;
    private long timeout, receiveTimestamp;

    private Partjob19Response.Parttimejob job;

    private boolean updateFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helpid = getIntent().getStringExtra("helpid");
        provideFlag = getIntent().getBooleanExtra("provideFlag", provideFlag);
        if (StringUtil.isEmpty(helpid)) {
            finishWithResult();
        } else {
            messageHandler = new MessageHandler(this);

            setContentView(R.layout.mine_help_jishi);

            initHeader();

            initView();

            initData();
        }
    }

    private void finishWithResult() {
        if (updateFlag) {
            Intent intent = new Intent();
            intent.putExtra("status", job.getStatus());
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
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
                                //举报求助
                                new AbuseReportHandler(MineHelpJishiActivity.this).setCallback(new AbuseReportHandler.Callback() {
                                    @Override
                                    public void call() {
                                        showToast("举报成功");
                                    }
                                }).click(job.getSourceid(), "1", helpid);
                            }
                        });
                    }

                    tv_pop_share.setVisibility(View.VISIBLE);
                    tv_pop_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//TODO 分享
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
                finishWithResult();
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

        View btn_view_comment = findViewById(R.id.btn_view_comment);
        btn_view_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            messageHandler.postMessage(9, result.getMessage());
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
                        job = (Partjob19Response.Parttimejob) msg.obj;
                        setValues();
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
                        finishWithResult();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 21: {
                    try {
                        showToast("成功完成帮助");
                        finishWithResult();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 22: {
                    try {
                        showToast("取消处理成功");
                        boolean agreeCancelFlag = (boolean) msg.obj;
                        if(agreeCancelFlag){//状态改为已取消4
                            job.setStatus("4");
                        }else{
                            job.setStatus("2");//状态改为帮助中
                        }
                        setStatusAndAction();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
//                case 25: {
//                    try {
//                        showToast("结算成功");
//                        Intent data = new Intent();
//                        //data.putExtra("status", "3");//取消中
//                        setResult(RESULT_OK, data);
//                        finishWithResult();
//                    } finally {
//                        actionLock.unlock();
//                    }
//                    break;
//                }
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

    private View.OnClickListener studentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO
            View parentView = MineHelpJishiActivity.this.getWindow().getDecorView();
            View contentView = LayoutInflater.from(MineHelpJishiActivity.this).inflate(R.layout.mine_help_jishi_connect, null);

            PopupWin win = PopupWin.Builder.create(MineHelpJishiActivity.this)
                    .setParentView(parentView)
                    .setContentView(contentView)
                    .build();
            win.findTextViewById(R.id.tv_stu_name).setText(provideFlag?job.getTargetname():job.getSourcename());
            win.findTextViewById(R.id.tv_stu_school).setText(provideFlag?job.getTargetschool():job.getSourceschool());
            win.findTextViewById(R.id.tv_stu_college).setText(provideFlag?job.getTargetcollege():job.getSourcecollege());

            win.show();
        }
    };

    private void setValues() {
        if (StringUtil.isNotEmpty(job.getMoney())) {
            tv_money.setText(String.format("%.2f元", Double.parseDouble(job.getMoney())));
        } else {
            tv_money.setText("");
        }
        tv_time.setText(DateUtil.getHumanlityDateString(job.getOptime()));
        tv_public_content.setText(job.getPubliccontent());
        tv_private_content.setText(job.getPrivatecontent());
        if(provideFlag) {
            bitmapUtil.getImage(civ_head_logo, job.getTargetimgurl(), R.drawable.default_head_img);
            tv_stu_name.setText(job.getTargetname());
        }else{
            bitmapUtil.getImage(civ_head_logo, job.getSourceimgurl(), R.drawable.default_head_img);
            tv_stu_name.setText(job.getSourcename());
        }
        civ_head_logo.setOnClickListener(studentClick);
        tv_stu_name.setOnClickListener(studentClick);
        setStatusAndAction();
    }


    private void setTipProcess() {
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

    private void setStatusAndAction() {

        setTipProcess();

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

    //立即评价
    private void doEvaluate(Partjob19Response.Parttimejob job) {
        // 立即评价
        Intent intent = new Intent(this, HelpEvaluateActivity.class);
        intent.putExtra("forceflag", false);
        intent.putExtra("content", job.getPubliccontent());
        intent.putExtra("targetreftype", "2");//1：兼职 2：求助
        intent.putExtra("targetrefid", helpid);
        intent.putExtra("targettype", "1");//1：学生 2：联系人 3：商家
        if(provideFlag){
            intent.putExtra("targetid", job.getTargetid());
            intent.putExtra("targetimgurl", job.getTargetimgurl());
            intent.putExtra("targetname", job.getTargetname());
            intent.putExtra("targetschool", job.getTargetschool());
            intent.putExtra("targetcollege", job.getTargetcollege());
        }else {
            intent.putExtra("targetid", job.getSourceid());
            intent.putExtra("targetimgurl", job.getSourceimgurl());
            intent.putExtra("targetname", job.getSourcename());
            intent.putExtra("targetschool", job.getSourceschool());
            intent.putExtra("targetcollege", job.getSourcecollege());
        }
        startActivityForResult(intent, REQUEST_CODE_RECEIVE_JISIH_EVALUATE);
    }

    // 马上结算
    private void doBalanceNow(final Partjob19Response.Parttimejob job) {
        Intent intent = new Intent(this, HelpWendaRewardActivity.class);
        intent.putExtra("answerflag", false);
        intent.putExtra("helpid", helpid);
        intent.putExtra("jishiHelpMoney", job.getMoney());
        startActivityForResult(intent, MineHelpListActivity.REQUEST_CODE_PROVIDE_JISIH_REWARD);
    }

    //重新发布
    private void doRedeploy(final Partjob19Response.Parttimejob job) {
        Intent intent = new Intent(this, HelpJishiDeployActivity.class);
        intent.putExtra("genderlimit", job.getGenderlimit());
        intent.putExtra("money", job.getMoney());
        intent.putExtra("publiccontent", job.getPubliccontent());
        intent.putExtra("privatecontent", job.getPrivatecontent());
        //intent.putExtra("validtime", null);
        startActivity(intent);
    }

    // 取消处理
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
                                            messageHandler.postMessage(22, "1".equals(key));
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

    // 完成帮助
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

    // 取消求助
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PROVIDE_JISIH_REWARD: {
                if(RESULT_OK == resultCode){
                    updateFlag = true;
                    job.setStatus("6");
                    setStatusAndAction();
                }
                break;
            }
            case REQUEST_CODE_RECEIVE_JISIH_EVALUATE: {
                if(RESULT_OK == resultCode){
                    updateFlag = true;
                    job.setEvelflag("1");
                    setStatusAndAction();
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishWithResult();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
