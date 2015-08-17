package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDetailActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpWendaAnswerActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpWendaDetailActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpWendaEditActivity;
import com.jingcai.apps.aizhuan.activity.index.IndexBannerDetailActivity;
import com.jingcai.apps.aizhuan.activity.util.PopConfirmWin;
import com.jingcai.apps.aizhuan.adapter.help.LikeHandler;
import com.jingcai.apps.aizhuan.adapter.index.CampusAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.base.base01.Base01Request;
import com.jingcai.apps.aizhuan.service.business.base.base01.Base01Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob11.Partjob11Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob11.Partjob11Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob13.Partjob13Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob37.Partjob37Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu08.Stu08Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IndexCampusFragment extends BaseFragment {
    private static final int REQUEST_CODE_JISHI_DETAIL = 1101;
    private static final int REQUEST_CODE_WENDA_DETAIL = 1102;
    private static final int REQUEST_CODE_ANSWER_EDIT = 1103;
    private static final int REQUEST_CODE_ANSWER_VIEW = 1104;
    private AzExecutor azExecutor = new AzExecutor();
    private AzService azService = new AzService();
    private View mBaseView;
    private MessageHandler messageHandler;
    private XListView groupListView;
    private CampusAdapter campusAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private ImageView ivFunc;
    private PopConfirmWin helpConfirmWin;
    private ImageView iv_banner;
    private Partjob11Response.Parttimejob selectedJob;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

//        messageHandler = new MessageHandler(baseActivity);
//        mBaseView = inflater.inflate(R.layout.index_campus_fragment, container, false);
//        changeHeader();
//        initView();
//        initGroupData();
//        return mBaseView;

        if (null == mBaseView) {
            messageHandler = new MessageHandler(baseActivity);
            mBaseView = inflater.inflate(R.layout.index_campus_fragment, null);
            changeHeader();
            initView();
            initGroupData();
        }
        ViewGroup parent = (ViewGroup) mBaseView.getParent();
        if (parent != null) {
            parent.removeView(mBaseView);
        }
        return mBaseView;
    }

    private void changeHeader() {
        TextView tvTitle = (TextView) mBaseView.findViewById(R.id.tv_content);
        tvTitle.setText("校园");
        mBaseView.findViewById(R.id.ib_back).setVisibility(View.GONE);

        ivFunc = (ImageView) mBaseView.findViewById(R.id.iv_func);
        ivFunc.setVisibility(View.VISIBLE);
        if (UserSubject.getOnlineFlag()) {
            ivFunc.setImageResource(R.drawable.icon_index_campus_bird_online);
        } else {
            ivFunc.setImageResource(R.drawable.icon_index_campus_bird_offline);
        }
        ivFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上下线
                if (actionLock.tryLock()) {
                    azExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Stu08Request req = new Stu08Request();
                            Stu08Request.Student stu = req.new Student();
                            stu.setStudentid(UserSubject.getStudentid());
                            stu.setOptype(UserSubject.getOnlineFlag() ? "2" : "1");
                            stu.setGisx(GlobalConstant.gis.getGisx());
                            stu.setGisy(GlobalConstant.gis.getGisy());
                            req.setStudent(stu);
                            azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                                @Override
                                public void success(BaseResponse resp) {
                                    ResponseResult result = resp.getResult();
                                    if ("0".equals(result.getCode())) {
                                        messageHandler.postMessage(9);
                                    } else {
                                        messageHandler.postMessage(10, result.getMessage());
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
        });
    }

    private void initView() {
        iv_banner = (ImageView) mBaseView.findViewById(R.id.iv_banner);

        groupListView = (XListView) mBaseView.findViewById(R.id.xlv_list);
        groupListView.setAdapter(campusAdapter = new CampusAdapter(baseActivity));
        groupListView.setPullRefreshEnable(true);
        groupListView.setPullLoadEnable(true);
        groupListView.setAutoLoadEnable(true);

        groupListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                campusAdapter.clearData();
                mCurrentStart = 0;
                groupListView.setPullLoadEnable(true);
                initGroupData();
            }

            @Override
            public void onLoadMore() {
                initGroupData();
            }
        });

        campusAdapter.setCallback(new CampusAdapter.Callback() {
            @Override
            public void jishi_like(final CheckBox checkBox, final Partjob11Response.Parttimejob job) {
                selectedJob = job;
                onLickClick(checkBox);
            }

            @Override
            public void wenda_like(CheckBox checkBox, Partjob11Response.Parttimejob job) {
                selectedJob = job;
                onLickClick(checkBox);
            }

            @Override
            public void jishi_help(CheckBox checkBox, final Partjob11Response.Parttimejob job) {
                selectedJob = job;
                // 检查是否可以帮助，可以帮助显示确认对话框
                if (!actionLock.tryLock()) return;
                azExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Partjob37Request req = new Partjob37Request(job.getHelpid());
                        azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                            @Override
                            public void success(BaseResponse resp) {
                                ResponseResult result = resp.getResult();
                                if ("0".equals(result.getCode())) {
                                    messageHandler.postMessage(11);//检查通过，显示确认对话框
                                } else {
                                    messageHandler.postMessage(12, resp.getResultMessage());
                                }
                            }

                            @Override
                            public void fail(AzException e) {
                            }
                        });
                    }
                });
            }

            //撰写新答案
            @Override
            public void wenda_help(CheckBox checkBox, Partjob11Response.Parttimejob job) {
                selectedJob = job;
                Intent intent = new Intent(baseActivity, HelpWendaEditActivity.class);
                intent.putExtra("helpid", job.getHelpid());
                baseActivity.startActivityForResult(intent, REQUEST_CODE_ANSWER_EDIT);
            }

            //查看我的答案
            @Override
            public void wenda_help_my(CheckBox checkBox, Partjob11Response.Parttimejob job) {
                selectedJob = job;
                Intent intent = new Intent(baseActivity, HelpWendaAnswerActivity.class);
                intent.putExtra("answerid", job.getHelperid());
                baseActivity.startActivityForResult(intent, REQUEST_CODE_ANSWER_VIEW);
            }

            @Override
            public void help_detail(boolean jishiFlag, Partjob11Response.Parttimejob job) {
                selectedJob = job;
                if (jishiFlag) {
                    Intent intent = new Intent(baseActivity, HelpJishiDetailActivity.class);
                    intent.putExtra("helpid", job.getHelpid());
                    intent.putExtra("type", job.getType());//1跑腿 还是 3公告
                    baseActivity.startActivityForResult(intent, REQUEST_CODE_JISHI_DETAIL);
                } else {
                    Intent intent = new Intent(baseActivity, HelpWendaDetailActivity.class);
                    intent.putExtra("helpid", job.getHelpid());
                    baseActivity.startActivityForResult(intent, REQUEST_CODE_WENDA_DETAIL);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_JISHI_DETAIL: {
                if (Activity.RESULT_OK == resultCode) {
                    //TODO 更新点赞、评论数量
                }
                break;
            }
            case REQUEST_CODE_WENDA_DETAIL: {
                if (Activity.RESULT_OK == resultCode) {
                    //TODO 更新点赞、评论数量、我的答案
                }
                break;
            }
            case REQUEST_CODE_ANSWER_EDIT: {
                if (Activity.RESULT_OK == resultCode) {
                    //TODO 撰写变为我的答案
                    String answerid = data.getStringExtra("answerid");
//                    String helpContent = data.getStringExtra("helpContent");
//                    String anonFlag = data.getStringExtra("anonFlag");
                    selectedJob.setHelpflag("1");
                    selectedJob.setHelperid(answerid);
                    campusAdapter.notifyDataSetChanged();
                }
                break;
            }
            case REQUEST_CODE_ANSWER_VIEW: {
                if (Activity.RESULT_OK == resultCode) {
                    //TODO 更新点赞数量
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * 即时、问答 点赞
     *
     * @param checkBox
     */
    private void onLickClick(final CheckBox checkBox) {
        String targettype = null;//1：求助  2：问题 3：答案 4：评论本身 5、求助公告
        if ("1".equals(selectedJob.getType())) {
            targettype = "1";
        } else if ("2".equals(selectedJob.getType())) {
            targettype = "2";
        } else if ("3".equals(selectedJob.getType())) {
            targettype = "5";
        }
        new LikeHandler(baseActivity).setCallback(new LikeHandler.Callback() {
            @Override
            public void like(String praiseid, CheckBox checkBox) {
                selectedJob.setPraiseflag("1");
                selectedJob.setPraiseid(praiseid);
                selectedJob.setPraisecount(checkBox.getText().toString());
            }

            @Override
            public void unlike(CheckBox checkBox) {
                selectedJob.setPraiseflag("0");
                selectedJob.setPraiseid(null);
                selectedJob.setPraisecount(checkBox.getText().toString());
            }
        }).click(targettype, selectedJob.getHelpid(), selectedJob.getPraiseid(), checkBox);
    }

    private void initGroupData() {
        if (!actionLock.tryLock())
            return;
//            showProgressDialog("获取圈子中...");
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Base01Request req = new Base01Request();
                final Base01Request.Banner banner = req.new Banner();
                banner.setPlatform(GlobalConstant.TERMINAL_TYPE_ANDROID);
                banner.setType("1");//1 兼职 2任务
                req.setBanner(banner);

                azService.doTrans(req, Base01Response.class, new AzService.Callback<Base01Response>() {
                    @Override
                    public void success(Base01Response response) {
                        ResponseResult result = response.getResult();
                        if ("0".equals(result.getCode())) {
                            Base01Response.Body body = response.getBody();
                            List<Base01Response.Body.Banner> regionList = body.getBanner_list();
                            messageHandler.postMessage(15, regionList);
                        } else {
                            messageHandler.postMessage(15, null);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });

        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Partjob11Request req = new Partjob11Request();
                final Partjob11Request.Parttimejob job = req.new Parttimejob();
                job.setStudentid(UserSubject.getStudentid());
                job.setGisx(GlobalConstant.gis.getGisx());
                job.setGisy(GlobalConstant.gis.getGisy());
                job.setStart(String.valueOf(mCurrentStart));
                job.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                req.setParttimejob(job);

                azService.doTrans(req, Partjob11Response.class, new AzService.Callback<Partjob11Response>() {
                    @Override
                    public void success(Partjob11Response response) {
                        ResponseResult result = response.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            Partjob11Response.Body body = response.getBody();
                            List<Partjob11Response.Parttimejob> regionList = null == body ? null : body.getParttimejob_list();
                            if (null == regionList) {
                                regionList = new ArrayList<Partjob11Response.Parttimejob>();
                            }
                            if (regionList.size() < 1 && 0 == mCurrentStart) {
                                messageHandler.postMessage(2);
                            } else {
                                messageHandler.postMessage(0, regionList);
                            }
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
                        List<Partjob11Response.Parttimejob> list = (List<Partjob11Response.Parttimejob>) msg.obj;
                        campusAdapter.addData(list);
                        campusAdapter.notifyDataSetChanged();
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
                        showToast("获取列表失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        groupListView.setVisibility(View.GONE);
                        ((ViewStub) mBaseView.findViewById(R.id.stub_empty_view)).inflate();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 3: {
                    try {
                        Object[] objs = (Object[]) msg.obj;
                        CheckBox cb_jishi_like = (CheckBox) objs[0];
                        boolean flag = (boolean) objs[1];
                        String pariseCount = (String) objs[2];
                        cb_jishi_like.setChecked(flag);
                        cb_jishi_like.setText(pariseCount);
//                        campusAdapter.notifyDataSetChanged();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 4: {
                    try {
                        showToast(String.valueOf(msg.obj));
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 9: {
                    try {
                        UserSubject.setOnlineFlag(!UserSubject.getOnlineFlag());
                        if (UserSubject.getOnlineFlag()) {
                            ivFunc.setImageResource(R.drawable.icon_index_campus_bird_online);
                        } else {
                            ivFunc.setImageResource(R.drawable.icon_index_campus_bird_offline);
                        }
                        showToast((UserSubject.getOnlineFlag() ? "上线" : "下线") + "成功");
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 10: {
                    try {
                        showToast("上下线失败");
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 11: {
                    try {
                        if (null == helpConfirmWin) {
                            helpConfirmWin = new PopConfirmWin(baseActivity);
                            helpConfirmWin.setTitle("确认？").setContent("确认立即帮助？").setAction(R.id.tv_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    helpConfirmWin.dismiss();
                                }
                            }).setAction(R.id.tv_confirm, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doJishiHelp();
                                }
                            });
                        }
                        helpConfirmWin.show();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 12: {
                    try {
                        showToast("接单失败:" + String.valueOf(msg.obj));
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 13: {
                    try {
                        showToast("接单成功");
                        if (null != helpConfirmWin) {
                            helpConfirmWin.dismiss();
                        }
                        selectedJob.setStatus("2");//状态设置为帮助中
                        campusAdapter.notifyDataSetChanged();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 15: {
                    try {
                        List<Base01Response.Body.Banner> regionList = (List<Base01Response.Body.Banner>) msg.obj;
                        if (null != regionList && regionList.size() > 0) {
                            iv_banner.setVisibility(View.VISIBLE);
                            final Base01Response.Body.Banner banner = regionList.get(0);
                            new BitmapUtil().getImage(iv_banner, banner.getImgurl(), R.drawable.banner);
                            iv_banner.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String linkUrl = banner.getRedirecturl();
                                    if (StringUtil.isEmpty(linkUrl)) return;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("title", banner.getTitle());
                                    bundle.putString("url", linkUrl);

                                    Intent intent = new Intent(baseActivity, IndexBannerDetailActivity.class);
                                    intent.putExtras(bundle);
                                    baseActivity.startActivity(intent);
                                }
                            });
                        } else {
                            iv_banner.setVisibility(View.GONE);
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 16: {
                    try {

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

    private void doJishiHelp() {
        if (actionLock.tryLock()) {
            azExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Partjob13Request req = new Partjob13Request();
                    Partjob13Request.Parttimejob job2 = req.new Parttimejob();
                    job2.setHelpid(selectedJob.getHelpid());
                    job2.setSourceid(UserSubject.getStudentid());
                    req.setParttimejob(job2);

                    azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse response) {
                            ResponseResult result = response.getResult();
                            if ("0".equals(result.getCode())) {
                                messageHandler.postMessage(13);
                            } else {
                                messageHandler.postMessage(12, result.getMessage());
                            }
                        }

                        @Override
                        public void fail(AzException e) {
                        }
                    });
                }
            });
        }
    }
}
