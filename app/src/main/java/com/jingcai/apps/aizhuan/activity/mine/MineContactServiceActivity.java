package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.util.AppUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by Administrator on 2015/7/18.
 */
public class MineContactServiceActivity extends BaseActivity {
    private PopupWin popupWin;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_contact_service);


        initHeader();  //初始化头部

        initViews();

    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("联系客服");

        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        //电话客服
        findViewById(R.id.rl_mine_contact_service_tel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == popupWin) {
                    View parentView = MineContactServiceActivity.this.getWindow().getDecorView();
                    View contentView = LayoutInflater.from(MineContactServiceActivity.this).inflate(R.layout.comfirm_contact_merchant_dialog, null);
                    popupWin = PopupWin.Builder.create(MineContactServiceActivity.this)
                            .setParentView(parentView)
                            .setContentView(contentView)
                            .build();

                    //logo
                    ((ImageView) contentView.findViewById(R.id.iv_contact_merchant_dialog_logo)).setImageResource(R.drawable.ic_launcher);
                    //title
                    ((TextView) contentView.findViewById(R.id.tv_contact_merchant_dialog_title)).setText("客服赚赚");
                    //phone
                    ((TextView) contentView.findViewById(R.id.tv_contact_merchant_dialog_phone)).setText(R.string.mine_contact_service_tel_num);
                    //2 button
                    contentView.findViewById(R.id.btn_confirm_false).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWin.dismiss();
                        }
                    });
                    contentView.findViewById(R.id.btn_confirm_true).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + R.string.mine_contact_service_tel_num));
                            startActivity(intent);
                        }
                    });
                }
                popupWin.show();
            }
        });

        //QQ客服
        findViewById(R.id.rl_mine_contact_service_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String qqPackageName = "com.tencent.mobileqq";
                if (AppUtil.isApkInstalled(MineContactServiceActivity.this, qqPackageName)) {
                    String url = getString(R.string.support_staff_qq_url);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else {
                    showToast("您的手机未安装QQ");
                }
            }
        });

        //微信客服
        findViewById(R.id.rl_mine_contact_service_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MineContactServiceActivity.this, WeixinNumberActivity.class);
                startActivity(intent);
            }
        });
    }

}
