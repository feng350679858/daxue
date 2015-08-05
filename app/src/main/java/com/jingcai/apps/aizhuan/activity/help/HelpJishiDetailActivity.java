package com.jingcai.apps.aizhuan.activity.help;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.help.CommentItem;
import com.jingcai.apps.aizhuan.adapter.help.HelpCommentAdapter;
import com.jingcai.apps.aizhuan.adapter.help.LikeHandler;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob19.Partjob19Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob29.Partjob29Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lejing on 15/7/21.
 */
public class HelpJishiDetailActivity extends BaseActivity {
    private String helpid, type;
    private AzExecutor azExecutor = new AzExecutor();
    private AzService azService = new AzService();
    private MessageHandler messageHandler;
    private XListView groupListView;
    private HelpCommentAdapter commentAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private CheckBox cb_jishi_help;
    private EditText et_reploy_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helpid = getIntent().getStringExtra("helpid");
        type = getIntent().getStringExtra("type");//1立即帮助 3公告
        if(StringUtil.isEmpty(helpid)){
            finish();
        } else {
            if(StringUtil.isEmpty(type)){
                type = "1";
            }
            messageHandler = new MessageHandler(this);
            setContentView(R.layout.help_jishi_detail);

            initHeader();

            initView();

            initGroupData();
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
                int dp10_px = HelpJishiDetailActivity.this.getResources().getDimensionPixelSize(R.dimen.dp_10);
                Log.d("==", "---------" + dp10_px);
                View contentView = LayoutInflater.from(HelpJishiDetailActivity.this).inflate(R.layout.help_wenda_answer_setting_pop, null);
                PopupWin groupWin = PopupWin.Builder.create(HelpJishiDetailActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(iv_func)
                        .setContentView(contentView)
                        .build();
                {
                    View tv_pop_abuse_report = groupWin.findViewById(R.id.tv_pop_abuse_report);
                    tv_pop_abuse_report.setVisibility(View.VISIBLE);
                    tv_pop_abuse_report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//举报
                            Log.d("==", "-----------tv_pop_abuse_report---");
                        }
                    });
                }
                {
                    View tv_pop_share = groupWin.findViewById(R.id.tv_pop_share);
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

//        Field field = null;
//        try {
//            int i = com.android.internal.R.style.Animation_Toast;
//            field = com.android.internal.R.style.class.getField("Animation_Toast");
//            Field modifiersField = Field.class.getDeclaredField("modifiers");
//            modifiersField.setAccessible(true);
//            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//            field.set(null, com.jingcai.apps.custometoast.R.style.toastInAndOutAnim);
//        } catch (NoSuchFieldException e ){
//        } catch (IllegalAccessException e) {
//        }

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initView() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("跑腿");

        et_reploy_comment = (EditText) findViewById(R.id.et_reploy_comment);

        cb_jishi_help = (CheckBox) findViewById(R.id.cb_jishi_help);
        cb_jishi_help.setVisibility(View.VISIBLE);//帮TA
        findViewById(R.id.layout_jishi_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("显示帮助确认弹窗");
            }
        });


        groupListView = (XListView) findViewById(R.id.xlv_list);
        groupListView.setAdapter(commentAdapter = new HelpCommentAdapter(this));
        groupListView.setPullRefreshEnable(true);
        groupListView.setPullLoadEnable(true);
        groupListView.setAutoLoadEnable(true);

        groupListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                commentAdapter.clearData();
                mCurrentStart = 0;
                groupListView.setPullLoadEnable(true);
                initGroupData();
            }

            @Override
            public void onLoadMore() {
                initGroupData();
            }
        });

        commentAdapter.setCallback(new HelpCommentAdapter.Callback() {
            @Override
            public void click(View view, CommentItem region) {
                boolean selected = region.isSelected();
                commentAdapter.clearSelected();
                if(!selected) {
                    region.setSelected(!selected);
                    et_reploy_comment.setHint("回复：" + region.getSourcename());
                }else{
                    et_reploy_comment.setHint("评论");
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void like(CheckBox checkBox, final CommentItem region) {
                new LikeHandler(HelpJishiDetailActivity.this).setCallback(new LikeHandler.Callback() {
                    @Override
                    public void like(String praiseid, CheckBox checkBox) {
                        region.setPraiseflag("1");
                        region.setPraiseid(praiseid);
                        region.setPraisecount(checkBox.getText().toString());
                    }

                    @Override
                    public void unlike(CheckBox checkBox) {
                        region.setPraiseflag("0");
                        region.setPraiseid(null);
                        region.setPraisecount(checkBox.getText().toString());
                    }
                }).click("1", helpid, region.getPraiseid(), checkBox);
            }

            @Override
            public void abuse(CommentItem region) {

            }
        });
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

                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast("获取详情失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        List<CommentItem> list = (List<CommentItem>) msg.obj;
                        commentAdapter.addData(list);
                        commentAdapter.notifyDataSetChanged();
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
                case 3: {
                    try {
                        showToast("获取评论失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
//                case 3:{
//                    try {
//                        groupListView.setVisibility(View.GONE);
//                    }finally {
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
    private void initGroupData() {
        //TODO 接入服务端接口
        if (actionLock.tryLock()) {
            //showProgressDialog("获取圈子中...");
            //获取详情
            azExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Partjob19Request req = new Partjob19Request();
                    Partjob19Request.Parttimejob job = req.new Parttimejob();
                    job.setStudentid(UserSubject.getStudentid());
                    job.setHelpid(helpid);
                    req.setParttimejob(job);

                    azService.doTrans(req, Partjob19Response.class, new AzService.Callback<Partjob19Response>() {
                        @Override
                        public void success(Partjob19Response response) {
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
            //获取答案列表
            azExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
                        List<CommentItem> regionList = new ArrayList<CommentItem>();
                        for (int i = 0; i < 10 && mCurrentStart < 24; i++) {
                            Partjob29Response.Parttimejob region = new Partjob29Response.Parttimejob();
                            region.setSourcename("学生" + (i + mCurrentStart));
                            region.setSourcelevel("19");
                            region.setSourceschool("浙江大学");
                            region.setSourcecollege("计算机学院");
                            region.setOptime("20150801233341");
                            if(i%4 == 0) {
                                region.setRefcomment(new Partjob29Response.Refcomment());
                                region.getRefcomment().setRefname("花仙子");
                                region.getRefcomment().setRefcontent("你是一个大SB");
                            }
                            if(i%2 == 0) {
                                region.setPraiseflag("1");
                                region.setPraiseid("23232332");
                            }
                            region.setPraisecount("23");
                            region.setContent("浙江大学浙江大学浙江大学浙江大学\n浙江大学" + (i + mCurrentStart));
                            regionList.add(region);
                        }
                        messageHandler.postMessage(2, regionList);
                    } else {
                        Partjob29Request req = new Partjob29Request();
                        Partjob29Request.Parttimejob job = req.new Parttimejob();
                        job.setSourceid(UserSubject.getStudentid());
                        job.setTargettype("1".equals(type) ? "1" : "5");//1求助 5求助公告
                        job.setTargetid(helpid);
                        job.setCommenttype("1");//1评论
                        job.setStart("" + mCurrentStart);
                        job.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                        req.setParttimejob(job);
                        azService.doTrans(req, Partjob29Response.class, new AzService.Callback<Partjob29Response>() {
                            @Override
                            public void success(Partjob29Response response) {
                                ResponseResult result = response.getResult();
                                if ("0".equals(result.getCode())) {
                                    List<Partjob29Response.Parttimejob> parttimejob_list = response.getBody().getParttimejob_list();
                                    messageHandler.postMessage(2, parttimejob_list);
                                } else {
                                    messageHandler.postMessage(3, result.getMessage());
                                }
                            }

                            @Override
                            public void fail(AzException e) {
                                messageHandler.postException(e);
                            }
                        });
                    }
                }
            });
        }
    }
}
