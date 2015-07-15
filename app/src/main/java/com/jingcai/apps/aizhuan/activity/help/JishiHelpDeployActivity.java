package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.common.GoldWatcher;
import com.jingcai.apps.aizhuan.util.PopupWin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lejing on 15/7/14.
 */
public class JishiHelpDeployActivity extends BaseActivity {
    private final static String TAG = "JishiHelpDeployActivity";
    private MessageHandler messageHandler;
    private Button btn_jishi_help;
    private EditText et_content, et_secret, et_pay_money;
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
        tv_gender = (TextView) findViewById(R.id.et_gender);
        tv_group = (TextView) findViewById(R.id.et_group);
        tv_friend = (TextView) findViewById(R.id.et_friend);
        et_pay_money = (EditText) findViewById(R.id.et_pay_money);
        tv_end_time = (TextView) findViewById(R.id.et_end_time);

        et_pay_money.addTextChangedListener(new GoldWatcher(et_pay_money));

        btn_jishi_help = (Button) findViewById(R.id.btn_jishi_help);

        tv_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("0", "男");
                map.put("1", "女");
                map.put("", "不限");
                View parentView = JishiHelpDeployActivity.this.getWindow().getDecorView();
                PopupWin.Builder.create(JishiHelpDeployActivity.this)
                        .setData(map, new PopupWin.Callback() {
                            @Override
                            public void select(String key, String val) {
                                tv_gender.setTag(key);
                                tv_gender.setText(val);
                            }
                        })
                        .setParentView(parentView)
                        .build()
                        .show();
            }
        });
        tv_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeldefEndtimeDialog();
            }
        });

        tv_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("10", "10分钟");
                map.put("30", "30分钟");
                map.put("60", "1小时");
                map.put("180", "3小时");
                map.put("360", "6小时");
                map.put("720", "12小时");
                map.put("1440", "24小时");
                map.put("-1", "自定义");
                View parentView = JishiHelpDeployActivity.this.getWindow().getDecorView();
                PopupWin.Builder.create(JishiHelpDeployActivity.this)
                        .setData(map, new PopupWin.Callback() {
                            @Override
                            public void select(String key, String val) {
                                if ("-1".equals(key)) {
                                    showSeldefEndtimeDialog();
                                } else {
                                    tv_end_time.setTag(key);
                                    tv_end_time.setText(val);
                                }
                            }
                        })
                        .setParentView(parentView)
                        .build()
                        .show();
            }
        });

        btn_jishi_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionLock.tryLock()) {
                    messageHandler.postMessage(1);
                }
            }
        });
    }

    private void showSeldefEndtimeDialog() {
        View parentView = this.getWindow().getDecorView();
        View contentView = LayoutInflater.from(this).inflate(R.layout.help_jishi_deploy_endtime_pop, null);
        final EditText et_end_time = (EditText) contentView.findViewById(R.id.et_end_time);
        et_end_time.addTextChangedListener(new GoldWatcher(et_end_time));

        final PopupWin win = PopupWin.Builder.create(this)
                .setParentView(parentView)
                .setContentView(contentView)
                .build();
        win.setAction(R.id.iv_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                win.dismiss();
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
                win.dismiss();
            }
        });
        win.show();
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    showToast("发布即时帮助成功！");
                    finish();
                    actionLock.unlock();
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }
}
