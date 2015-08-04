package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.mine.help.MineHelpListActivity;
import com.jingcai.apps.aizhuan.adapter.index.CampusAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob11.Partjob11Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob11.Partjob11Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob12.Partjob12Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob12.Partjob12Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob30.Partjob30Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu08.Stu08Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class IndexCampusFragment extends BaseFragment {
    private AzService azService = new AzService();
    private View mBaseView;
    private MessageHandler messageHandler;
    private XListView groupListView;
    private CampusAdapter campusAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private ImageView ivFunc;

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
        if (UserSubject.getOnlineFlag()) {
            ivFunc.setImageResource(R.drawable.icon_index_campus_bird_online);
        } else {
            ivFunc.setImageResource(R.drawable.icon_index_campus_bird_offline);
        }
        ivFunc.setVisibility(View.VISIBLE);
        ivFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上下线
                if (actionLock.tryLock()) {
                    new AzExecutor().execute(new Runnable() {
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
                                        messageHandler.postMessage(6);
                                    } else {
                                        messageHandler.postMessage(7, result.getMessage());
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
        mBaseView.findViewById(R.id.iv_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String title = "百度";
//                String linkUrl = "http://baidu.com";
//                if (StringUtil.isEmpty(linkUrl)) return;
//                Bundle bundle = new Bundle();
//                bundle.putString("title", title);
//                bundle.putString("url", linkUrl);
//
//                Intent intent = new Intent(baseActivity, IndexBannerDetailActivity.class);
//                intent.putExtras(bundle);
//                baseActivity.startActivity(intent);

//                baseActivity.startActivity(new Intent(baseActivity, HelpWendaRewardActivity.class));
                baseActivity.startActivity(new Intent(baseActivity, MineHelpListActivity.class));
            }
        });

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
            public void jishi_like(final CheckBox cb_jishi_like, final Partjob11Response.Parttimejob job) {
                if (actionLock.tryLock()) {
                    boolean checked = !cb_jishi_like.isChecked();
                    if (checked) {//点赞
                        new AzExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                Partjob12Request req = new Partjob12Request();
                                Partjob12Request.Parttimejob p = req.new Parttimejob();
                                p.setSourceid(UserSubject.getStudentid());
                                p.setTargettype("15");//1：求助  2：问题 3：答案 4：评论本身 5、求助公告
                                p.setTargetid(job.getHelpid());
                                p.setOptype("2");//1：评论 2：点赞
                                req.setParttimejob(p);
                                azService.doTrans(req, Partjob12Response.class, new AzService.Callback<Partjob12Response>() {
                                    @Override
                                    public void success(Partjob12Response resp) {
                                        ResponseResult result = resp.getResult();
                                        if ("0".equals(result.getCode())) {
                                            job.setPraiseflag("1");
                                            job.setPraiseid(resp.getBody().getParttimejob().getCommentid());
                                            job.setPraisecount(String.valueOf(Integer.parseInt(job.getPraisecount()) + 1));
//                                            Object[] objs = new Object[]{cb_jishi_like, true};
//                                            messageHandler.postMessage(3, objs);
                                            messageHandler.postMessage(3);
                                        } else {
                                            messageHandler.postMessage(4, result.getMessage());
                                        }
                                    }

                                    @Override
                                    public void fail(AzException e) {
                                        messageHandler.postException(e);
                                    }
                                });
                            }
                        });
                    } else {//取消赞
                        new AzExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                Partjob30Request req = new Partjob30Request();
                                Partjob30Request.Parttimejob p = req.new Parttimejob();
                                p.setCommentid(job.getPraiseid());
                                p.setType("2");//1：评论 2：点赞
                                req.setParttimejob(p);
                                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                                    @Override
                                    public void success(BaseResponse resp) {
                                        ResponseResult result = resp.getResult();
                                        if ("0".equals(result.getCode())) {
                                            job.setPraiseflag("0");
                                            job.setPraiseid(null);//TODO
                                            job.setPraisecount(String.valueOf(Integer.parseInt(job.getPraisecount()) - 1));
                                            //Object[] objs = new Object[]{cb_jishi_like, false};
                                            //messageHandler.postMessage(3, objs);
                                            messageHandler.postMessage(3);
                                        } else {
                                            messageHandler.postMessage(5, result.getMessage());
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
            }

            @Override
            public void wenda_like() {

            }

            @Override
            public void jishi_help() {

            }

            @Override
            public void wenda_help() {

            }

            @Override
            public void wenda_help_my() {

            }
        });
    }

    private void initGroupData() {
        //TODO 接入服务端接口
        if (actionLock.tryLock()) {
//            showProgressDialog("获取圈子中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
                        Random random = new Random();
                        List<Partjob11Response.Parttimejob> jobList = new ArrayList<Partjob11Response.Parttimejob>();
                        for (int i = 0; i < 10 && mCurrentStart < 24; i++) {
                            Partjob11Response.Parttimejob job = new Partjob11Response.Parttimejob();
                            job.setHelpid(String.valueOf(i));
                            job.setType(String.valueOf(random.nextInt(3) + 1));
                            job.setTitle("title------" + i);
                            job.setCommentcount(String.valueOf(i));
                            job.setCommentcount("22");
                            job.setGenderlimit(String.valueOf(random.nextInt(3)));
                            job.setMoney(String.valueOf(random.nextInt(100)) + ".23");
                            job.setContent("内容xxx的范德萨发到付浙江打发士大夫大学" + (i + mCurrentStart));
                            job.setSourceid(String.valueOf(random.nextInt(10000)));
                            job.setSourcelevel(String.valueOf(random.nextInt(20)));
                            job.setSourcename("花几支" + i);
                            job.setSourceschool("浙江理工大学");
                            job.setSourcecollege("计算机学院");
                            job.setOptime("20150731100123");
                            job.setPraisecount("112");
                            job.setCommentcount("10");
                            job.setStatus(String.valueOf(random.nextInt(6) + 1));
                            job.setPraiseflag(String.valueOf(random.nextInt(2)));
                            jobList.add(job);
                        }
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        messageHandler.postMessage(0, jobList);
                    } else {
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
                                    List<Partjob11Response.Parttimejob> regionList = body.getParttimejob_list();
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
                }
            });
        }

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
//                case 2:{
//                    try {
//                        groupListView.setVisibility(View.GONE);
//                    }finally {
//                        actionLock.unlock();
//                    }
//                    break;
//                }
                case 3: {
                    try {
//                        Object[] objs = (Object[]) msg.obj;
//                        CheckBox cb_jishi_like = (CheckBox) objs[0];
//                        boolean flag = (boolean) objs[1];
//                        cb_jishi_like.setChecked(flag);
                        campusAdapter.notifyDataSetChanged();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 4: {
                    try {
                        showToast("点赞失败");
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 5: {
                    try {
                        showToast("取消点赞失败");
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
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }
}
