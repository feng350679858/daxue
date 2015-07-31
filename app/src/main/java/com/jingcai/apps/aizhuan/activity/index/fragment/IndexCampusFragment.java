package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob11.Partjob11Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob11.Partjob11Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class IndexCampusFragment extends BaseFragment {
    private View mBaseView;
    private MessageHandler messageHandler;
    private XListView groupListView;
    private CampusAdapter campusAdapter;
    private int mCurrentStart = 0;  //当前的开始

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

        final ImageView ivFunc = (ImageView) mBaseView.findViewById(R.id.iv_func);
        ivFunc.setImageResource(R.drawable.icon_index_campus_bird_online);
        ivFunc.setVisibility(View.VISIBLE);
        ivFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 上下线
                if (!"1".equals(ivFunc.getTag())) {
                    ivFunc.setImageResource(R.drawable.icon_index_campus_bird_online);
                    ivFunc.setTag("1");
                } else {
                    ivFunc.setImageResource(R.drawable.icon_index_campus_bird_offline);
                    ivFunc.setTag("0");
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

    }

    private void initGroupData() {
        //TODO 接入服务端接口
        if (actionLock.tryLock()) {
            showProgressDialog("获取圈子中...");
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
                            job.setMoney(String.valueOf(random.nextDouble()));
                            job.setContent("内容xxx的范德萨发到付浙江打发士大夫大学" + (i + mCurrentStart));
                            job.setSourceid(String.valueOf(random.nextInt(10000)));
                            job.setSourcename("花几支" + i);
                            job.setSourceschool("浙江理工大学");
                            job.setSourcecollege("计算机学院");
                            job.setOptime("2011-08-10 10:01:23");
                            job.setPraisecount("112");
                            job.setCommentcount("10");
                            job.setStatus("1");
                            jobList.add(job);
                        }
                        messageHandler.postMessage(0, jobList);
                    } else {
                        final AzService azService = new AzService(baseActivity);
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
                        showToast("获取商家失败:" + msg.obj);
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
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }
}
