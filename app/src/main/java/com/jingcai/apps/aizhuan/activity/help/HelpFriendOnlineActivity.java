package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.help.HelpFriendAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.stu.stu10.Stu10Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu10.Stu10Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu15.stu15Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lejing on 15/7/29.
 */
public class HelpFriendOnlineActivity extends BaseActivity {
    public static final int REQUEST_CODE_FRIEND = 1100;
    private MessageHandler messageHandler;
    private XListView xlv_list;
    private HelpFriendAdapter helpFriendAdapter;
    private int mCurrentStart = 0;  //当前的开始
    private TextView tv_online_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        setContentView(R.layout.help_friend_online);

        initHeader();

        initView();

        initGroupData();
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("指定老友");

        final ImageView iv_func = (ImageView) findViewById(R.id.iv_func);
        iv_func.setVisibility(View.VISIBLE);
        iv_func.setImageResource(R.drawable.help_friend_add);
        iv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HelpFriendOnlineActivity.this, CaptureActivity.class), REQUEST_CODE_FRIEND);
            }
        });

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setImageResource(R.drawable.icon_cancel2);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initView() {
        tv_online_rate = (TextView) findViewById(R.id.tv_online_rate);

        xlv_list = (XListView) findViewById(R.id.xlv_list);
        xlv_list.setAdapter(helpFriendAdapter = new HelpFriendAdapter(this));
        xlv_list.setPullRefreshEnable(true);
        xlv_list.setPullLoadEnable(true);
        xlv_list.setAutoLoadEnable(true);

        xlv_list.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                initGroupData();
            }
        });

        helpFriendAdapter.setCallback(new HelpFriendAdapter.Callback() {
            @Override
            public void click(View view, HelpFriendAdapter.ViewHolder holder) {
                Intent intent = new Intent();
                Stu10Response.Item item = holder.region;
                intent.putExtra("userid", item.getTargetid());
                intent.putExtra("logoimgurl", item.getTargetimgurl());
                intent.putExtra("username", item.getTargetname());
                intent.putExtra("schoolname", item.getTargetschool());
                intent.putExtra("collegename", item.getTargetcollege());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void refresh() {
        helpFriendAdapter.clearData();
        mCurrentStart = 0;
        xlv_list.setPullLoadEnable(true);
        initGroupData();
    }

    private void onLoad() {
        xlv_list.stopRefresh();
        xlv_list.stopLoadMore();
        xlv_list.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
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
                        Stu10Response.Body body = (Stu10Response.Body) msg.obj;
                        List<Stu10Response.Item> list = null;
                        if(null != body) {
                            list = body.getStudent_list();
                            Stu10Response.Student stu = body.getStudent();
                            if(null != stu){
                                tv_online_rate.setText(String.format("%s/%s人", stu.getOnlinenum(), stu.getOfflinenum()));
                            }
                        }else{
                            tv_online_rate.setText(String.format("%s/%s人", "0", "0"));
                        }
                        if (null == list) {
                            list = new ArrayList<Stu10Response.Item>();
                        }
                        helpFriendAdapter.addData(list);
                        helpFriendAdapter.notifyDataSetChanged();
                        mCurrentStart += list.size();
                        onLoad();
                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            xlv_list.setPullLoadEnable(false);
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
                case 2:{
                    refresh();
                    break;
                }
                case 3:{
                    showToast("添加好友失败:" + msg.obj);
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void initGroupData() {
        if (!actionLock.tryLock()) {
            return;
        }
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu10Request req = new Stu10Request();
                Stu10Request.Student region = req.new Student();
                region.setStudentid(UserSubject.getStudentid());
                region.setStart(String.valueOf(mCurrentStart));
                region.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                req.setStudent(region);
                new AzService().doTrans(req, Stu10Response.class, new AzService.Callback<Stu10Response>() {
                    @Override
                    public void success(Stu10Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            Stu10Response.Body body = resp.getBody();
                            messageHandler.postMessage(0, body);
                        } else {
                            messageHandler.postMessage(1, resp.getResultMessage());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        actionLock.unlock();
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FRIEND: {
                if (RESULT_OK == resultCode) {
                    String qrcodeinfo = data.getStringExtra("qrcodeinfo");
                    String key = "60110f7b38dc3173=";
                    int index1 = qrcodeinfo.indexOf(key);
                    int index2 = qrcodeinfo.indexOf("&");
                    String studentid = qrcodeinfo.substring(index1 + key.length(), index2);
                    doAddFriend(studentid);
                }
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void doAddFriend(final String studentid){
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                stu15Request req = new stu15Request(UserSubject.getStudentid(), studentid);
                new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(2);
                        } else {
                            messageHandler.postMessage(3, resp.getResultMessage());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        actionLock.unlock();
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }
}
