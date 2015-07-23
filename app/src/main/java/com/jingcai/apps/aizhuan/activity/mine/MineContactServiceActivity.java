package com.jingcai.apps.aizhuan.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu03.Stu03Request;
import com.jingcai.apps.aizhuan.service.upload.AzUploadService;
import com.jingcai.apps.aizhuan.util.AppUtil;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.PopupDialog;
import com.jingcai.apps.aizhuan.util.StringUtil;

import org.bouncycastle.jce.provider.symmetric.ARC4;

/**
 * Created by Administrator on 2015/7/18.
 */
public class MineContactServiceActivity extends BaseActivity {


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_contact_service);


        initHeader();  //初始化头部

        initViews();

    }
    private void initHeader()
    {
        ((TextView)findViewById(R.id.tv_content)).setText("联系客服");

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
              final PopupDialog dialog = new PopupDialog(MineContactServiceActivity.this, R.layout.comfirm_contact_merchant_dialog);
              View contentView = dialog.getContentView();
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
                      dialog.dismiss();
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
              dialog.show();
          }
      });

        //QQ客服
        findViewById(R.id.rl_mine_contact_service_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PackageManager packageManager = MineContactServiceActivity.this.getPackageManager();
                    packageManager.getApplicationInfo("QQ",packageManager.GET_UNINSTALLED_PACKAGES);
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=2841329585";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                }
                catch (PackageManager.NameNotFoundException e){
                    showToast("请先安装QQ哦亲");
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
