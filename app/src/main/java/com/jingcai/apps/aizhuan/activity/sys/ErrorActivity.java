package com.jingcai.apps.aizhuan.activity.sys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ErrorActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_error);

        final String errmsg = this.getIntent().getExtras().getString("msg");

        TextView err_text = (TextView) findViewById(R.id.err_text);
        err_text.setText(errmsg);
        Button btn_send = (Button) findViewById(R.id.btn_send);
        Button btn_close = (Button) findViewById(R.id.btn_close);

        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//				String requestUrl = WSUtil.host + "/recError";
//				try {
//					byte[] data = errmsg.getBytes();
//					URL realUrl = new URL(requestUrl);
//					HttpURLConnection conn = (HttpURLConnection) realUrl
//							.openConnection();
//					conn.setDoOutput(true);// 发送POST请求必须设置允许输出
//					conn.setUseCaches(false);// 不使用Cache
//					conn.setRequestMethod("POST");
//					conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//					conn.setRequestProperty("Charset", "UTF-8");
//					conn.setRequestProperty("Content-Length",
//							String.valueOf(data.length));
//					conn.setRequestProperty("Content-Type",
//							"application/x-www-form-urlencoded");
//					DataOutputStream outStream = new DataOutputStream(conn
//							.getOutputStream());
//					outStream.write(data);
//					outStream.flush();
//					if (conn.getResponseCode() == 200) {
//						String result = readData(conn.getInputStream(), "UTF-8");
//						outStream.close();
//						System.out.println("-------" + result);
//						showStrToast("发送成功!", 1);
//						finish();
//					} else {
//						showStrToast("发送失败!", 1);
//					}
//				} catch (Exception e) {
//					showStrToast("发送失败!", 1);
//				}
                finish();
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // 第一个参数为输入流,第二个参数为字符集编码
    public static String readData(InputStream inSream, String charsetName) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inSream.close();
        return new String(data, charsetName);
    }

}
