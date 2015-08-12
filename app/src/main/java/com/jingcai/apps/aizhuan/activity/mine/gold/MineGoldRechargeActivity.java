package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.gold.AccountChoiceListAdapter;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Request;
import com.jingcai.apps.aizhuan.service.business.account.account01.Account01Response;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Request;
import com.jingcai.apps.aizhuan.service.business.account.account04.Account04Response;
import com.jingcai.apps.aizhuan.service.business.account.account07.Account07Request;
import com.jingcai.apps.aizhuan.service.business.account.account07.Account07Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.jingcai.apps.aizhuan.util.alipay.PayResult;

import java.util.ArrayList;
import java.util.List;

public class MineGoldRechargeActivity extends BaseActivity {
    private final String TAG = "MineGoldRecharge";
    private MessageHandler messageHandler;

    private TextView mTvRechargeRMB;
    private EditText mEtInputCount;
    private Button mTvRechargeSubmit;

    private AzService azService;
    private ListView mListView;
    private Account04Response.Account04Body.Bank selectedBank;
    private AccountChoiceListAdapter mListAdapter;

    private float mEnableGoldCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_account_topup);
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);
        initHeader();
        initView();
        initData();
    }


    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("充值");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mEtInputCount = (EditText) findViewById(R.id.et_mine_gold_topup_count);
        mTvRechargeSubmit = (Button) findViewById(R.id.btn_mine_gold_topup_submit);
        mTvRechargeRMB = (TextView) findViewById(R.id.tv_mine_gold_topup_money);

        mEtInputCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputCount = s.toString();
                double count;
                try {
                    count = Double.parseDouble(inputCount);
                } catch (NumberFormatException e) {
                    count = 0.0;
                }
                if (count != 0.0) {
                    mTvRechargeRMB.setText(StringUtil.money(inputCount) + "元");
                    mTvRechargeSubmit.setEnabled(true);
                    mTvRechargeSubmit.setTextColor(getResources().getColor(R.color.important_dark));
                } else {
                    mTvRechargeRMB.setText("0");
                    mTvRechargeSubmit.setEnabled(false);
                    mTvRechargeSubmit.setTextColor(getResources().getColor(R.color.assist_grey));
                }
            }

        });

        mTvRechargeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCountStr = mEtInputCount.getText().toString();
                if (selectedBank == null) {
                    showToast("请选择账户类型");
                    return;
                }
//                if (Double.parseDouble(inputCountStr) < 10.0) {
//                    showToast("至少充值10元");
//                    return;
//                }
                generateOrder();
            }
        });


    }

    /**
     * 生成订单
     */
    private void generateOrder() {
        //支付宝
        if ("AliPay".equalsIgnoreCase(selectedBank.getCode())) {
            checkInstall();
            generateAlipayOrder();
        } else {
            showToast(selectedBank.getName() + "支付渠道暂未开通");
        }
    }


    /**
     * 查询终端设备是否存在支付宝认证账户
     */
    public void checkInstall() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(MineGoldRechargeActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                messageHandler.postMessage(7, isExist);
            }
        });
    }


    private void generateAlipayOrder() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Account07Request req = new Account07Request();
                Account07Request.Pay pay = req.new Pay();
                pay.setStudentid(UserSubject.getStudentid());
                pay.setMoney(mEtInputCount.getText().toString());
                req.setPay(pay);

                azService.doTrans(req, Account07Response.class, new AzService.Callback<Account07Response>() {
                    @Override
                    public void success(Account07Response resp) {
                        final String resultCode = resp.getResultCode();
                        if ("0".equals(resultCode)) {
                            messageHandler.postMessage(4, resp.getBody().getPay());
                        } else {
                            messageHandler.postMessage(5, resp.getResultMessage());
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

    private void initData() {
        showProgressDialog("数据加载中...");
        initBankData();
        initBalanceData();
    }

    private void initBalanceData() {
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
                            messageHandler.postMessage(3, result.getMessage());
                        } else {
                            messageHandler.postMessage(2, resp.getBody().getWallet_list());
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

    private void initBankData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(MineGoldRechargeActivity.this);

                Account04Request request = new Account04Request();

                azService.doTrans(request, Account04Response.class, new AzService.Callback<Account04Response>() {
                    @Override
                    public void success(Account04Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(0, resp.getBody().getBank_list());
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
                    fillBankInfo(((List<Account04Response.Account04Body.Bank>) msg.obj));
                    break;
                }
                case 1: {
                    showToast("第三方金融渠道列表失败");
                    Log.i(TAG, "第三方金融渠道列表失败：" + msg.obj);
                    break;
                }
                case 2: {
                    fillBalance((ArrayList<Account01Response.Account01Body.Wallet>) msg.obj);
                    break;
                }
                case 3: {
                    showToast("获取余额失败");
                    Log.i(TAG, "获取余额失败：" + msg.obj);
                    break;
                }
                case 4: {
                    postPayMessage(((Account07Response.Body.Pay) msg.obj));
                    break;
                }
                case 5: {
                    showToast("生成订单失败，请稍后重试");
                    Log.i(TAG, "生成订单失败：" + msg.obj);
                    break;
                }
                case 6: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showToast("支付成功");
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            showToast("支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            showToast("支付失败");
                        }
                    }
                    break;
                }
                case 7: {
                    boolean mInstall = (boolean) msg.obj;
                    if(!mInstall){
                        showToast("装了支付宝后要登录哟");
                    }
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    /**
     * 发送支付宝
     *
     * @param pay
     */
    private void postPayMessage(Account07Response.Body.Pay pay) {
        if (null == pay) {
            showToast("订单出现异常");
        } else {
            final String content = getOrderInfo(pay.getPartner(), pay.getSellerid(),
                    pay.getTradeno(), pay.getSubject(), pay.getDes(), pay.getFee(), pay.getNotifyurl(), pay.getPaymenttype());
            Log.i(TAG, content);
            String sign = pay.getSign();
            final String payInfo = content + "&sign=\"" + sign + "\"&"
                    + "sign_type=\"RSA\"";
            //调用支付宝
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    PayTask alipay = new PayTask(MineGoldRechargeActivity.this);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(payInfo);
                    messageHandler.postMessage(6, result);
                }
            });
        }
    }


    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String partner,
                                String sellerid,
                                String tradeno,
                                String subject,
                                String des,
                                String fee,
                                String notifyurl,
                                String paymenttype) {
        StringBuilder sb = new StringBuilder();
        // 签约合作者身份ID
        sb.append("partner=").append("\"").append(partner).append("\"");
        // 签约卖家支付宝账号
        sb.append("&seller_id=").append("\"").append(sellerid).append("\"");

        // 商户网站唯一订单号
        sb.append("&out_trade_no=").append("\"").append(tradeno).append("\"");

        // 商品名称
        sb.append("&subject=").append("\"").append(subject).append("\"");

        // 商品详情
        sb.append("&body=").append("\"").append(des).append("\"");

        // 商品金额
        sb.append("&total_fee=").append("\"").append(fee).append("\"");

        // 服务器异步通知页面路径
        sb.append("&notify_url=").append("\"").append(notifyurl).append("\"");

        // 服务接口名称， 固定值
        sb.append("&service=\"mobile.securitypay.pay\"");

        // 支付类型， 固定值
        sb.append("&payment_type=\"").append(paymenttype).append("\"");

        // 参数编码， 固定值
        sb.append("&_input_charset=\"utf-8\"");

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        sb.append("&it_b_pay=\"30m\"");

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // sb.append("&extern_token=").append("\"").append(extern_token).append("\"");

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        sb.append("&show_url=\"m.alipay.com\"");

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // sb.append("&paymethod=\"expressGateway\"");

        return sb.toString();
    }

    private void fillBankInfo(List<Account04Response.Account04Body.Bank> banks) {
        if (null == banks || 0 == banks.size()) {
            return;
        }
        selectedBank = banks.get(0);
        mListAdapter = new AccountChoiceListAdapter(this, selectedBank);
        mListView = (ListView) findViewById(R.id.lv_mine_account_topup_choice_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListAdapter.setSelectednum((Account04Response.Account04Body.Bank) mListAdapter.getItem(position));
                selectedBank = (Account04Response.Account04Body.Bank) mListAdapter.getItem(position);
                mListAdapter.notifyDataSetChanged();
            }
        });
        mListAdapter.setData(banks);
        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
    }

    private void fillBalance(ArrayList<Account01Response.Account01Body.Wallet> wallets) {
        for (int i = 0; i < wallets.size(); i++) {
            if ("gold".equals(wallets.get(i).getCode())) {
                mEnableGoldCount = Float.parseFloat(wallets.get(i).getCredit());
                String gold = StringUtil.getPrintMoney(mEnableGoldCount);
                ((TextView) findViewById(R.id.tv_mine_gold_topup_rest)).setText(gold + "元");
            }
        }
    }
}
