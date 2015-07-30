package com.jingcai.apps.aizhuan.activity.mine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.HelpAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.Partjob24.Partjob24Request;
import com.jingcai.apps.aizhuan.service.business.partjob.Partjob24.Partjob24Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class MineHelpFrament extends BaseFragment {
    private View  mBaseView;
    private MessageHandler messageHandler;
    private XListView groupListView;
    private HelpAdapter helpAdapter;
    private int mCurrentStart = 0;  //当前的开始
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (null ==  mBaseView)
        {
            messageHandler = new MessageHandler(baseActivity);
            mBaseView = inflater.inflate(R.layout.mine_frament_help, null);
            initView();
            initGroupData();
        }
        ViewGroup parent = (ViewGroup)  mBaseView.getParent();
        if (parent != null) {
            parent.removeView( mBaseView);
        }
        return  mBaseView;
    }



    private void initView() {
       /* mBaseView.findViewById(R.id.iv_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseActivity.startActivity(new Intent(baseActivity, HelpWendaDetailActivity.class));
//                baseActivity.startActivity(new Intent(baseActivity, HelpWendaAnswerActivity.class));
            }
        });*/

        groupListView = (XListView) mBaseView.findViewById(R.id.xlv_help_list);
        groupListView.setAdapter(helpAdapter = new HelpAdapter(baseActivity));
        groupListView.setPullRefreshEnable(true);
        groupListView.setPullLoadEnable(true);
        groupListView.setAutoLoadEnable(true);

        groupListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                helpAdapter.clearData();
                mCurrentStart = 0;
                groupListView.setPullLoadEnable(true);
                initGroupData();
            }

            @Override
            public void onLoadMore() {
                initGroupData();
            }
        });

//        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CampusAdapter.ViewHolder holder = (CampusAdapter.ViewHolder) view.getTag();
//                showToast(holder.region.getRegionname());
////                tv_group.setText(holder.region.getRegionname());
////                tv_group.setTag(holder.region.getRegionid());
//            }
//        });

    }

    private void initGroupData() {
        //TODO 接入服务端接口
        if (actionLock.tryLock()) {
            showProgressDialog("获取圈子中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
                        List<Partjob24Response.Partjob24Body.Parttimejob> parttimejobList = new ArrayList<Partjob24Response.Partjob24Body.Parttimejob>();
                        for (int i = 0; i < 10 && mCurrentStart < 24; i++) {
                            Partjob24Response.Partjob24Body.Parttimejob parttimejob = new Partjob24Response.Partjob24Body.Parttimejob();
                            parttimejob.setHelpid("" + (i + mCurrentStart));
                            parttimejob.setSourceschool("浙江大学" + (i + mCurrentStart));
                            parttimejobList.add(parttimejob);
                        }
                        messageHandler.postMessage(0, parttimejobList);
                    } else {
                        final AzService azService = new AzService(baseActivity);
                        final Partjob24Request req = new Partjob24Request();
                        final Partjob24Request.Parttimejob region = req.new Parttimejob();
                        region.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                        region.setStart(String.valueOf(mCurrentStart));
                        region.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                        req.setParttimejob(region);
                        azService.doTrans(req,  Partjob24Response.class, new AzService.Callback< Partjob24Response>() {
                            @Override
                            public void success( Partjob24Response response) {
                                ResponseResult result = response.getResult();
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(1, result.getMessage());
                                } else {
                                    Partjob24Response.Partjob24Body partjob07Body = response.getBody();
                                    List<Partjob24Response.Partjob24Body.Parttimejob> parttimejobList =  partjob07Body.getParttimejob_list();
                                    //if (regionList.size() < 1 && 0 == mCurrentStart) {
                                    //    messageHandler.postMessage(2);
                                    //} else {
                                    messageHandler.postMessage(0, parttimejobList);
                                    //}
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
                        List<Partjob24Response.Partjob24Body.Parttimejob> list = (List<Partjob24Response.Partjob24Body.Parttimejob>) msg.obj;
                        helpAdapter.addData(list);
                        helpAdapter.notifyDataSetChanged();
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
//               case 2:{
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
