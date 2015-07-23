package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.partjob.PartjobDetailMapActivity;
import com.jingcai.apps.aizhuan.adapter.mine.MinePartjobWorkdayListAdapter;
import com.jingcai.apps.aizhuan.adapter.partjob.PartjobListAdapter;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob04.Partjob04Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob04.Partjob04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.PopupDialog;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class MyPartjobDetailActivity extends BaseActivity {
    private MessageHandler messageHandler;
    private BitmapUtil bitmapUtil;
    private String mId;  //列表传过来的id

    //时间表
    private ListView mLvWorkDays;
    private TextView mTvWorkDaysEmptyTip;
    private View layout_worktimetype_long;
    private TextView tv_worktimetype_long;

    //按钮
    private Button mBtnCancle;  //取消报名
    private Button mBtnContactMerchant;  //联系商家
    private Button mBtnComplain;

    //工作信息
    private TextView mTvWorkWorktime;
    private TextView mTvWorkSalary;
    private TextView mTvWorkSalaryUnit;
    private TextView mTvWorkSettlelength;
    private TextView mTvWorkAddress;

    //兼职信息
    private ImageView mIvPartjobLogo;
    private TextView mTvPartjobTitle;
    private TextView mTvPartjobSalary;
    private TextView mTvPartjobSalaryUnit;
    private TextView mTvPartjobWorkdays;
    private ImageView mIvPartjobSettlelength;
    private ImageView mIvPartjobLabel;
    private View mTvDistanceIcon;
    private TextView mTvDistance;
    private View mTvDistanceUnit;

    /**
     * 接受到的数据
     */
    private Partjob04Response.Partjob04Body.Joininfo mJoininfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partjob_detail);

        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        messageHandler = new MessageHandler(this);
        bitmapUtil = new BitmapUtil(this);
        initHeader();
        initView();
    }

    private void initHeader(){
        ((TextView) findViewById(R.id.tv_content)).setText("报名详情");
        findViewById(R.id.iv_bird_badge).setVisibility(View.GONE);
        findViewById(R.id.iv_func).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回主界面
                finish();
            }
        });
    }
    /**
     * 初始化控件
     */
    private void initView() {

        //兼职信息
        mIvPartjobLogo = (ImageView) findViewById(R.id.pj_list_item_logo);
        mTvPartjobTitle = (TextView) findViewById(R.id.pj_list_item_title);
        mTvPartjobSalary = (TextView) findViewById(R.id.pj_list_item_salary);
        mTvPartjobSalaryUnit = (TextView) findViewById(R.id.pj_list_item_salaryunit);
        mTvPartjobWorkdays = (TextView) findViewById(R.id.pj_list_item_workdays);
        mIvPartjobSettlelength = (ImageView) findViewById(R.id.pj_list_item_wage_settlelength);
        mIvPartjobLabel = (ImageView) findViewById(R.id.pj_list_item_label);
        mTvDistanceIcon = findViewById(R.id.tv_distance_icon);
        mTvDistance = (TextView) findViewById(R.id.tv_distance);
        mTvDistanceUnit = findViewById(R.id.tv_distance_unit);

        //工作信息
        mTvWorkWorktime = (TextView) findViewById(R.id.tv_mine_partjob_detail_worktime);
        mTvWorkSalary = (TextView) findViewById(R.id.tv_mine_partjob_detail_salary);
        mTvWorkSalaryUnit = (TextView) findViewById(R.id.tv_mine_partjob_detail_salaryunit);
        mTvWorkSettlelength = (TextView) findViewById(R.id.tv_mine_partjob_detail_settlelength);
        mTvWorkAddress = (TextView) findViewById(R.id.tv_mine_partjob_detail_address);
        findViewById(R.id.layout_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isNotEmpty(mJoininfo.getGisx()) && StringUtil.isNotEmpty(mJoininfo.getGisy())) {
                    Intent intent = new Intent(MyPartjobDetailActivity.this, PartjobDetailMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("gisx", mJoininfo.getGisx());
                    bundle.putString("gisy", mJoininfo.getGisy());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        //时间表
        mLvWorkDays = (ListView) findViewById(R.id.lv_mine_partjob_detail_workday_list);
        mTvWorkDaysEmptyTip = (TextView) findViewById(R.id.tv_mine_partjob_detail_workdays_list_empty);

        layout_worktimetype_long = findViewById(R.id.layout_workday);
        tv_worktimetype_long = (TextView) findViewById(R.id.tv_mine_partjob_detail_workdays_list_item_workday);

        //按钮
        mBtnCancle = (Button) findViewById(R.id.btn_confirm_false);
        mBtnContactMerchant = (Button) findViewById(R.id.btn_confirm_true);
        mBtnComplain = (Button) findViewById(R.id.btn_mine_partjob_detail_complain);

        mBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPartjobDetailActivity.this, PartjobCancelActivity.class);
                intent.putExtra("endtime", mJoininfo.getEndtime());
                intent.putExtra("jobid", mId);
                intent.putExtra("phone", mJoininfo.getPhone());
                startActivity(intent);
            }
        });
        //联系商家 按钮
        mBtnContactMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupDialog dialog = new PopupDialog(MyPartjobDetailActivity.this, R.layout.comfirm_contact_merchant_dialog);
                View contentView = dialog.getContentView();
                //logo
                ((ImageView) contentView.findViewById(R.id.iv_contact_merchant_dialog_logo)).setImageDrawable(mIvPartjobLogo.getDrawable());
                //title
                ((TextView) contentView.findViewById(R.id.tv_contact_merchant_dialog_title)).setText(mJoininfo.getName());
                //phone
                ((TextView) contentView.findViewById(R.id.tv_contact_merchant_dialog_phone)).setText(mJoininfo.getPhone());
                //2 button
                contentView.findViewById(R.id.btn_confirm_false).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                contentView.findViewById(R.id.btn_confirm_true).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + mJoininfo.getPhone()));
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });
   /*     mBtnComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mJoininfo != null) {
                    Intent intent = new Intent(MyPartjobDetailActivity.this, ComplainActivity.class);
                    intent.putExtra("jobid", mJoininfo.getJobid());
                    intent.putExtra("joinid", mJoininfo.getId());
                    intent.putExtra("logopath", mJoininfo.getLogopath());
                    intent.putExtra("title", mJoininfo.getTitle());
                    startActivity(intent);
                } else {
                    showToast("兼职信息努力加载中，请稍后~");
                }
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();  //填充数据
    }

    /**
     * 初始化数据
     */
    private void initData() {
        showProgressDialog("数据加载中...");
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                final AzService azService = new AzService(MyPartjobDetailActivity.this);
                Partjob04Request req = new Partjob04Request();
                final Partjob04Request.Joininfo joininfo = req.new Joininfo();
                joininfo.setId(mId);
                joininfo.setStudentid(UserSubject.getStudentid());
                req.setJoininfo(joininfo);
                azService.doTrans(req, Partjob04Response.class, new AzService.Callback<Partjob04Response>() {
                    @Override
                    public void success(Partjob04Response response) {
                        ResponseResult result = response.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            Partjob04Response.Partjob04Body.Joininfo joininfo1 = response.getBody().getJoininfo();
                            messageHandler.postMessage(0, joininfo1);
                        }
                    }
                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        }));
    }

    /**
     * 填充数据
     */
    private void fillDataInView() {
        /*
         * 兼职信息
         */
        bitmapUtil.getImage(mIvPartjobLogo, mJoininfo.getLogopath(), true, R.drawable.logo_merchant_default);
        mTvPartjobTitle.setText(mJoininfo.getTitle());

        PartjobListAdapter.setWorkdays(mTvPartjobWorkdays, mJoininfo.getWorktimetype(), mJoininfo.getWorkdays());
        PartjobListAdapter.setDistance(mTvDistanceIcon, mTvDistance, mTvDistanceUnit, mJoininfo.getDistance());
        PartjobListAdapter.setSalary(mTvPartjobSalary, mTvPartjobSalaryUnit, mJoininfo.getSalary(), mJoininfo.getSalaryunit());
        PartjobListAdapter.setLabel(mIvPartjobLabel, mJoininfo.getWorktimetype(), mJoininfo.getLabel(), PartjobListAdapter.AdapterType.MinePartjob);
        PartjobListAdapter.setSettlelength(mIvPartjobSettlelength, mJoininfo.getSettlelength());

        PartjobListAdapter.setSalary(mTvWorkSalary, mTvWorkSalaryUnit, mJoininfo.getSalary(), mJoininfo.getSalaryunit());
        PartjobListAdapter.setSettlelength(mTvWorkSettlelength, mJoininfo.getSettlelength());

        //工作时间
        mTvWorkWorktime.setText(mJoininfo.getWorktime());
        //工作地点
        mTvWorkAddress.setText(mJoininfo.getAddress());

        //1为取消状态
        boolean isCancel = "1".equals(mJoininfo.getIscancel());
        if (isCancel) {
            mBtnCancle.setTextColor(getResources().getColor(R.color.font_assistant));
            mBtnCancle.setEnabled(false);
        }

        //长期兼职
        if("1".equals(mJoininfo.getWorktimetype())){
            layout_worktimetype_long.setVisibility(View.VISIBLE);
            mLvWorkDays.setVisibility(View.GONE);
            mTvWorkDaysEmptyTip.setVisibility(View.GONE);

            tv_worktimetype_long.setText("长期兼职");
            if(isCancel){
                tv_worktimetype_long.setCompoundDrawablesWithIntrinsicBounds(R.drawable.partjob_detail_popup_time, 0, R.drawable.mine_partjob_detail_workday_list_cancel, 0);
            }else{
                tv_worktimetype_long.setCompoundDrawablesWithIntrinsicBounds(R.drawable.partjob_detail_popup_time, 0, R.drawable.mine_partjob_detail_workday_list_working, 0);
            }
        }else {
            layout_worktimetype_long.setVisibility(View.GONE);
            //时间表
            String workDays = mJoininfo.getWorkdays();
            if (StringUtil.isEmpty(workDays)) {
                mLvWorkDays.setVisibility(View.GONE);
                mTvWorkDaysEmptyTip.setVisibility(View.VISIBLE);
            } else {
                mLvWorkDays.setVisibility(View.VISIBLE);
                mTvWorkDaysEmptyTip.setVisibility(View.GONE);

                List<String> dateList = new ArrayList<>();
                if (StringUtil.isNotEmpty(workDays)) {
                    String[] dates = workDays.split(",");
                    for (int i = 0; i < dates.length; i++) {
                        dateList.add(dates[i]);
                    }
                }
                mLvWorkDays.setAdapter(new MinePartjobWorkdayListAdapter(this, dateList, isCancel));
            }
        }
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
                    mJoininfo = (Partjob04Response.Partjob04Body.Joininfo) msg.obj;
                    fillDataInView();
                    break;
                }
                case 1: {
                    showToast("获取报名详情出错：" + msg.obj);
                    break;
                }
                case 3: {
//                    showToast("获取用户信息失败");
//                    setResult(RESULT_CANCELED, null);
//                    finish();
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }
}
