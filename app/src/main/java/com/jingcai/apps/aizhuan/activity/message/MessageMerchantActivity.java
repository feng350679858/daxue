package com.jingcai.apps.aizhuan.activity.message;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.message.MerchantListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob07.Partjob07Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob07.Partjob07Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupDialog;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.Date;
import java.util.List;

/**
 * Created by Json Ding on 2015/7/14.
 */
public class MessageMerchantActivity extends BaseActivity implements AdapterView.OnItemClickListener,XListView.IXListViewListener{
    private MessageHandler messageHandler;
    private XListView mListView ;
    private int mCurrentStart = 0;  //当前的开始
    private MerchantListAdapter mMerchantListAdapter;
    private MerchantListAdapter.ViewHolder mCurrentSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_merchant);
//        messageHandler = new MessageHandler(this);

        initHeader();
        initViews();
//
//        initData();
    }

    private void initHeader() {
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        //需要用到再findViewById，不要需则不调用，提高效率
//        ImageView ivFunc = (ImageView) findViewById(R.id.iv_func);
//        TextView tvFunc = (TextView) findViewById(R.id.tv_func);

        tvTitle.setText("兼职商家");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        mListView = (XListView) findViewById(R.id.lv_merchants);

        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setAutoLoadEnable(true);


        mMerchantListAdapter = new MerchantListAdapter(this);
        mListView.setAdapter(mMerchantListAdapter);

        mListView.setOnItemClickListener(this);
    }

    private void initData() {
        //TODO 接入服务端接口
        if(actionLock.tryLock()) {
            showProgressDialog("商家赶来中...");
            final Context context = this;
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final AzService azService = new AzService(context);
                    final Partjob07Request req = new Partjob07Request();
                    final Partjob07Request.Student student = req.new Student();
                    student.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                    student.setStart(String.valueOf(mCurrentStart));
                    student.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    req.setStudent(student);
                    azService.doTrans(req, Partjob07Response.class, new AzService.Callback<Partjob07Response>() {
                        @Override
                        public void success(Partjob07Response response) {
                            ResponseResult result = response.getResult();
                            if (!"0".equals(result.getCode())) {
                                messageHandler.postMessage(1, result.getMessage());
                            } else {
                                Partjob07Response.Partjob07Body partjob07Body = response.getBody();
                                List<Partjob07Response.Partjob07Body.Merchant> merchantList = partjob07Body.getMerchant_list();
                                if (merchantList.size() < 1 && 0 == mCurrentStart) {
                                    messageHandler.postMessage(2);
                                } else {
                                    messageHandler.postMessage(0, merchantList);
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

    }

    @Override
    public void onRefresh() {
        mMerchantListAdapter.clearData();
        mCurrentStart = 0;
        mListView.setPullLoadEnable(true);
        initData();
    }

    @Override
    public void onLoadMore() {
        initData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCurrentSelectedItem = (MerchantListAdapter.ViewHolder)view.getTag();
        final PopupDialog dialog = new PopupDialog(this,R.layout.dialog_comfirm_contact_merchant);
        View contentView = dialog.getContentView();
        //logo
        ((ImageView)contentView.findViewById(R.id.iv_contact_merchant_dialog_logo)).setImageDrawable(mCurrentSelectedItem.iv_logo.getDrawable());
        //title
        ((TextView)contentView.findViewById(R.id.tv_contact_merchant_dialog_title)).setText(mCurrentSelectedItem.merchant.getJobtitle());
        //phone
        ((TextView)contentView.findViewById(R.id.tv_contact_merchant_dialog_phone)).setText(mCurrentSelectedItem.merchant.getPhone());
        //2 button
        contentView.findViewById(R.id.btn_contact_merchant_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_contact_merchant_dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mCurrentSelectedItem.merchant.getPhone()));
                startActivity(intent);
            }
        });
        dialog.show();
    }
    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(DateUtil.formatDate(new Date(), "MM-dd HH:mm"));
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    try {
                        List<Partjob07Response.Partjob07Body.Merchant> list = (List<Partjob07Response.Partjob07Body.Merchant>) msg.obj;
                        mMerchantListAdapter.addData(list);
                        mMerchantListAdapter.notifyDataSetChanged();
                        mCurrentStart += list.size();
                        onLoad();
                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            mListView.setPullLoadEnable(false);
                        }
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast("获取商家失败:" + msg.obj);
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2:{
                    try {
                        mListView.setVisibility(View.GONE);
                        displayEmptyView();

                    }finally {
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

    /**
     * 显示空列表提示布局
     */
    private void displayEmptyView() {
        ((ViewStub) findViewById(R.id.stub_empty_view)).inflate();
        TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_text);
        tvEmpty.setText(getString(R.string.empty_merchant_list_tip));
        tvEmpty.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.empty_list_message_merchant,0,0);
    }

}
