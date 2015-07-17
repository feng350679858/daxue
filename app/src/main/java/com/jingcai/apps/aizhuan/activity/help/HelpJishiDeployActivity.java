package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
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
import com.jingcai.apps.aizhuan.activity.common.GoldWatcher;
import com.jingcai.apps.aizhuan.adapter.help.GroupAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Request;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lejing on 15/7/14.
 */
public class HelpJishiDeployActivity extends BaseActivity {
    private final static String TAG = "JishiHelpDeployActivity";
    private MessageHandler messageHandler;
    private PopupWin groupWin;
    private PopupWin end_timeWin;
    private EditText et_end_time;;
    private PopupWin selfdeftimeWin;
    private PopupWin genderWin;
    private XListView groupListView;
    private GroupAdapter groupAdapter;
    private int mCurrentStart = 0;  //当前的开始

    private Button btn_jishi_help;
    private EditText et_content, et_secret, et_pay_money;
    private View iv_friend_clear, layout_friend_selected;
    private TextView tv_gender, tv_group, tv_friend, tv_end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help_jishi_deploy);
        messageHandler = new MessageHandler(this);

        initHeader();
        initView();
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
        et_content = (EditText) findViewById(R.id.et_content);
        et_secret = (EditText) findViewById(R.id.et_secret);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_group = (TextView) findViewById(R.id.tv_group);
        iv_friend_clear = findViewById(R.id.iv_friend_clear);
        layout_friend_selected = findViewById(R.id.layout_friend_selected);
        tv_friend = (TextView) findViewById(R.id.tv_friend);
        et_pay_money = (EditText) findViewById(R.id.et_pay_money);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);

        et_pay_money.addTextChangedListener(new GoldWatcher(et_pay_money));

        btn_jishi_help = (Button) findViewById(R.id.btn_jishi_help);

        tv_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == genderWin) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("0", "男");
                    map.put("1", "女");
                    map.put("", "不限");
                    View parentView = HelpJishiDeployActivity.this.getWindow().getDecorView();
                    genderWin = PopupWin.Builder.create(HelpJishiDeployActivity.this)
                            .setData(map, new PopupWin.Callback() {
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
        tv_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectWin(true);
            }
        });
        iv_friend_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectWin(false);
            }
        });
        layout_friend_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectWin(false);
            }
        });
        tv_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == end_timeWin) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("10", "10分钟");
                    map.put("30", "30分钟");
                    map.put("60", "1小时");
                    map.put("180", "3小时");
                    map.put("360", "6小时");
                    map.put("720", "12小时");
                    map.put("1440", "24小时");
                    map.put("-1", "自定义");
                    View parentView = HelpJishiDeployActivity.this.getWindow().getDecorView();
                    end_timeWin = PopupWin.Builder.create(HelpJishiDeployActivity.this)
                            .setData(map, new PopupWin.Callback() {
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
                if (actionLock.tryLock()) {
                    messageHandler.postMessage(3);
                }
            }
        });
    }

    private void showSelectWin(boolean selectFlag){
        if(selectFlag){
            tv_friend.setVisibility(View.GONE);
            iv_friend_clear.setVisibility(View.VISIBLE);
            layout_friend_selected.setVisibility(View.VISIBLE);
        }else{
            tv_friend.setVisibility(View.VISIBLE);
            iv_friend_clear.setVisibility(View.GONE);
            layout_friend_selected.setVisibility(View.GONE);
        }
    }

    private void showSeldefEndtimeDialog() {
        if(null == selfdeftimeWin) {
            View parentView = this.getWindow().getDecorView();
            View contentView = LayoutInflater.from(this).inflate(R.layout.help_jishi_deploy_endtime_pop, null);
            et_end_time = (EditText) contentView.findViewById(R.id.et_end_time);
            et_end_time.addTextChangedListener(new GoldWatcher(et_end_time));

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
                case 3: {
                    showToast("发布即时帮助成功！");
                    finish();
                    actionLock.unlock();
                    break;
                }
                case 4: {
                    showSeldefEndtimeDialog();
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }


    private void initGroupData() {
        //TODO 接入服务端接口
        if (actionLock.tryLock()) {
//            showProgressDialog("获取圈子中...");
            final Context context = this;
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
                        List<Base04Response.Body.Region> regionList = new ArrayList<Base04Response.Body.Region>();
                        for (int i = 0; i < 10 && mCurrentStart < 24; i++) {
                            Base04Response.Body.Region region = new Base04Response.Body.Region();
                            region.setRegionid("" + (i + mCurrentStart));
                            region.setRegionname("浙江大学" + (i + mCurrentStart));
                            regionList.add(region);
                        }
                        messageHandler.postMessage(0, regionList);
                    } else {
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
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(1, result.getMessage());
                                } else {
                                    Base04Response.Body partjob07Body = response.getBody();
                                    List<Base04Response.Body.Region> regionList = partjob07Body.getRegion_list();
                                    //                                if (regionList.size() < 1 && 0 == mCurrentStart) {
                                    //                                    messageHandler.postMessage(2);
                                    //                                } else {
                                    messageHandler.postMessage(0, regionList);
                                    //                                }
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
