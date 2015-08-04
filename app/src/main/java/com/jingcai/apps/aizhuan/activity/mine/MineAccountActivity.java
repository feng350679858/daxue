package com.jingcai.apps.aizhuan.activity.mine;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Request;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Response;
import com.jingcai.apps.aizhuan.service.business.account.account05.Account05Request;
import com.jingcai.apps.aizhuan.service.business.account.account05.Account05Response;
import com.jingcai.apps.aizhuan.service.business.game.game09.Game09Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PixelUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xiangqili on 2015/7/15.
 */
public class MineAccountActivity extends BaseActivity {

    private AzService azService;
    private MessageHandler messageHandler;
    private TextView mTvIncome;
    private TextView mTvBalance;
    private TextView mTvFreeze;
    private static final int IMAGE_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_index);
        azService = new AzService(this);
        messageHandler = new MessageHandler(this);
        initHeader();
        initView();
    }

    public void initHeader()
    {
        ((TextView)findViewById(R.id.tv_content)).setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }
    private void initView() {
        ((ImageView)findViewById(R.id.ib_back)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        findViewById(R.id.ll_gold_account_expense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineAccountActivity.this, MineGoldExpenseActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.ll_mine_gold_account_income).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineAccountActivity.this,MineGoldIncomeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.ll_account_reset_pay_psw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineAccountActivity.this,MineResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        initComponent();

        initEvents();
        initEvents();

    }

    private void initshenfen()
    {
                Game09Request rep = new Game09Request();
                Game09Request.Student student = rep.new Student();
                student.setStudentid(UserSubject.getStudentid());
                student.setStudentname(UserSubject.getSchoolname());

                if (null==student.getStudentname())
                {
                    View dialogView = LayoutInflater.from(MineAccountActivity.this).inflate(R.layout.mine_authentication, null);
                    final PopupWindow popupWindow = new PopupWindow(dialogView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                    final EditText txtPassword = (EditText) dialogView.findViewById(R.id.et_account_withdraw_pay_psw);
                    final View decorView =MineAccountActivity.this.getWindow().getDecorView();
                    popupWindow.setFocusable(true);
                    popupWindow.setAnimationStyle(R.style.main_menu_animstyle);
                    popupWindow.showAtLocation(decorView, Gravity.CENTER_HORIZONTAL, 0, PixelUtil.px2dip(MineAccountActivity.this, 200f));
                    dialogView.findViewById(R.id.btn_account_withdraw_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                    public void onClick(View v) {
                    popupWindow.dismiss();

                }
                });
                    ObjectAnimator.ofFloat(decorView, "alpha", 1.0f, 0.5f).setDuration(500).start();
                }

    }

    private void initEvents() {
            findViewById(R.id.btn_mine_gold_account_withdraw).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initshenfen();
                    Intent intent = new Intent(MineAccountActivity.this, MineGoldWithdrawActivity.class);
                    startActivity(intent);
                }
            });
        findViewById(R.id.btn_mine_gold_account_topup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // initshenfen();
                Intent intent = new Intent(MineAccountActivity.this, MineGoldTopupActivity.class);
                startActivity(intent);
            }
        });
        View.OnClickListener accountStreamListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(MineAccountActivity.this, AccountStreamDetailActivity.class);
               // startActivity(intent);
            }
        };

       // mTvIncome.setOnClickListener(accountStreamListener);
      //  mTvBalance.setOnClickListener(accountStreamListener);

    }

    private void initComponent() {
       mTvIncome = (TextView) findViewById(R.id.tv_mine_gold_index_income);
       mTvBalance = (TextView) findViewById(R.id.tv_mine_gold_index_balance);
      mTvFreeze = (TextView) findViewById(R.id.tv_mine_gold_index_freeze);
    }

    private void initData() {
        initBalanceData();

        initIncomeData();
    }


    private void fillIncome(ArrayList<Account05Response.Account05Body.Account> accounts) {
        ArrayList<String> xVals = DateUtil.getWeekList();
        ArrayList<Entry> entries = new ArrayList<>();

        float incomeThisWeek = 0f;
        float money = 0f;
        for (int i = 0; i < 7; i++) {
            money = Float.parseFloat(accounts.get(i).getOpmoney());
            entries.add(new Entry(money/100,i));
            incomeThisWeek +=money;
        }

        mTvIncome.setText(StringUtil.getFormatFloat(incomeThisWeek, "#,###"));
    }

    private void initBalanceData() {
        showProgressDialog("数据加载中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Account01Request req = new Account01Request();
                Account01Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);

                azService.doTrans(req, Account01Response.class, new AzService.Callback<Account01Response>() {
                    @Override
                    public void success(Account01Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(0, resp.getBody().getWallet_list());
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
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    fillBalance((ArrayList<Account01Response.Account01Body.Wallet>) msg.obj);
                    break;
                }
                case 1: {
                    showToast("账户余额获取失败："+msg.obj);
                    break;
                }
                case 2: {
                    fillIncome((ArrayList<Account05Response.Account05Body.Account>) msg.obj);
                    break;
                }
                case 3:{
                    showToast("账户收入获取失败："+msg.obj);
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }







    private void fillBalance(ArrayList<Account01Response.Account01Body.Wallet> wallets) {
        for(int i = 0 ; i < wallets.size(); i++){
            if("freeze".equals(wallets.get(i).getCode())){
                mTvFreeze.setText(StringUtil.getFormatFloat(Float.parseFloat(wallets.get(i).getCredit()), "#,###"));
            }else if("gold".equals(wallets.get(i).getCode())){
                mTvBalance.setText(StringUtil.getFormatFloat(Float.parseFloat(wallets.get(i).getCredit()), "#,###"));
            }
        }
    }

    /**
     *
     */
    private void initIncomeData() {

        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Account05Request req = new Account05Request();

                Account05Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);

                Account05Request.Account account = req.new Account();
                account.setBegindate(DateUtil.formatDate(new Date(System.currentTimeMillis() - 3600l*24l*6l*1000l),"yyyyMMdd"));
                account.setEnddate(DateUtil.formatDate(new Date(), "yyyyMMdd"));
                account.setWalletcode("gold");
                req.setAccount(account);

                azService.doTrans(req, Account05Response.class, new AzService.Callback<Account05Response>() {
                    @Override
                    public void success(Account05Response resp) {
                        ResponseResult result = resp.getResult();
                        if(!"0".equals(result.getCode())){
                            messageHandler.postMessage(3,result.getMessage());
                        }else{
                            messageHandler.postMessage(2,resp.getBody().getAccount_list());
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
