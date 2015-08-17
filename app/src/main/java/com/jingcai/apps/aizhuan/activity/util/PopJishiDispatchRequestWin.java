package com.jingcai.apps.aizhuan.activity.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu11.Stu11Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lejing on 15/7/22.
 */
public class PopJishiDispatchRequestWin {
    private final Activity baseActivity;
    private MessageHandler messageHandler;
    private BitmapUtil bitmapUtil = new BitmapUtil();
    private PopupWin mPopWin;
    private TextView tv_title;
    private CircleImageView civ_head_logo;
    private TextView tv_stu_name;
    private TextView tv_stu_score;
    private TextView tv_go;

    public PopJishiDispatchRequestWin(Activity baseActivity) {
        this.baseActivity = baseActivity;
        this.messageHandler = new MessageHandler(baseActivity);
        init();
    }

    public void show(String studentid) {
        initData(studentid);
    }

    private void init() {
        View contentView = View.inflate(baseActivity, R.layout.pop_jishi_dispatch_request, null);
        View parentView = baseActivity.getWindow().getDecorView();

        int w = 0;
        if(baseActivity instanceof BaseActivity){
            w = ((BaseActivity)baseActivity).getScreenWidth() * 80 / 100;
        }else {
            Point point = new Point();
            baseActivity.getWindowManager().getDefaultDisplay().getSize(point);
            w = point.x * 80 / 100;
        }

        mPopWin = PopupWin.Builder.create(baseActivity)
                .setParentView(parentView)
                .setContentView(contentView)
                .setAnimstyle(0)
//                .setFocusable(false)
                .setWidth(w)
                .build();
        tv_title = mPopWin.findTextViewById(R.id.tv_title);
        civ_head_logo = (CircleImageView)mPopWin.findViewById(R.id.civ_head_logo);
        tv_stu_name = mPopWin.findTextViewById(R.id.tv_stu_name);
        tv_stu_score = mPopWin.findTextViewById(R.id.tv_stu_score);
        tv_go = mPopWin.findTextViewById(R.id.tv_go);
        tv_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
            }
        });
    }

    public void dismiss() {
        mPopWin.dismiss();
    }

    private void initData(String studentid) {
        getStudentInfo(studentid);
        getStudentScore(studentid);
    }

    private void getStudentInfo(final String studentid) {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(studentid);
                req.setStudent(stu);
                new AzService().doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        final Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if ("0".equals(result.getCode())) {
                            messageHandler.postMessage(11, student);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                    }
                });
            }
        });
    }

    private void getStudentScore(final String studentid) {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu11Request req = new Stu11Request();
                Stu11Request.Student student = req.new Student();
                student.setStudentid(studentid);
                req.setStudent(student);
                new AzService().doTrans(req, Stu11Response.class, new AzService.Callback<Stu11Response>() {
                    @Override
                    public void success(Stu11Response resp) {
                        ResponseResult result = resp.getResult();
                        if ("0".equals(result.getCode())) {
                            messageHandler.postMessage(21, resp.getBody().getStudent());
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                    }
                });
            }
        });
    }

    public void setTitle(String title){
        tv_title.setText(title);
    }

    public void setActionText(String txt){
        tv_go.setText(txt);
    }


    class MessageHandler extends BaseHandler {

        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 11: {
                    Stu02Response.Stu02Body.Student student = (Stu02Response.Stu02Body.Student) msg.obj;
                    bitmapUtil.getImage(civ_head_logo, student.getLogopath(), true, R.drawable.default_head_img);
                    tv_stu_name.setText(student.getName());
                    mPopWin.show(Gravity.CENTER, 0, 0);
                    break;
                }
                case 21: {
                    Stu11Response.Body.Student student = (Stu11Response.Body.Student) msg.obj;
                    tv_stu_score.setText(student.getScore());
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        tv_go.setOnClickListener(listener);
    }
}
