package com.jingcai.apps.aizhuan.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.stu.stu16.Stu16Rquest;
import com.jingcai.apps.aizhuan.service.upload.AzUploadService;
import com.jingcai.apps.aizhuan.util.AppUtil;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.CaptureCropUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class MineStudentCertificationActivity extends BaseActivity {
    private final String TAG = "MineStuCertification";

    private boolean mPickFront = true;//判断学生证内侧
    private String mFrontUrl;   //上传证件正面url
    private String mBehindUrl;  //上传证件背面url

    private MessageHandler messageHandler;
    private ImageView mIvFrontSide;
    private ImageView mIvBehindSide;
    private Button mBtnSubmit;

    private AzService azService;
    private PopupWin mMakePicWin;
    private CaptureCropUtil mCropUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_student_certification);

        messageHandler = new MessageHandler(this);
        azService = new AzService();
        mCropUtil = new CaptureCropUtil(this);

        initHeader();

        initView();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("学生认证֤");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {
        mIvFrontSide = (ImageView) findViewById(R.id.iv_mine_student_certification_inside);
        mIvBehindSide = (ImageView) findViewById(R.id.iv_mine_student_certification_back);
        mBtnSubmit = (Button) findViewById(R.id.btn_student_certification_submit);

        mIvFrontSide.setOnClickListener(mMakePicOnclickListener);
        mIvBehindSide.setOnClickListener(mMakePicOnclickListener);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCertification();
            }
        });
    }

    /**
     * 提交证书
     */
    private void submitCertification() {
        showProgressDialog("审核提交中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu16Rquest req = new Stu16Rquest();
                Stu16Rquest.Student student = req.new Student();
                student.setId(UserSubject.getStudentid());
                student.setSidingfront(mFrontUrl);
                student.setSidingback(mBehindUrl);
                req.setStudent(student);

                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
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
                        messageHandler.postException(e);
                    }
                });
            }
        });
    }

    private View.OnClickListener mMakePicOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPickFront = v.getId() == R.id.iv_mine_student_certification_inside;  //是否是点击正面

            if (null == mMakePicWin) {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("camera", "拍照");
                map.put("album", "从相册选择");
                View parentView = MineStudentCertificationActivity.this.getWindow().getDecorView();
                mMakePicWin = PopupWin.Builder.create(MineStudentCertificationActivity.this)
                        .setData(map, new PopupWin.Callback() {
                            @Override
                            public void select(String key, String val) {
                                if ("camera".equals(key)) {
                                    if (AppUtil.isSdcardExisting()) {
                                    mCropUtil.openCamera();
                                    } else {
                                        showToast("未找到SD卡");
                                    }
                                } else if ("album".equals(key)) {
                                    mCropUtil.openAlbum();
                                }
                            }
                        })
                        .setParentView(parentView)
                        .build();
            }
            mMakePicWin.show();
        }
    };


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    String url = msg.obj.toString();
                    if (StringUtil.isEmpty(url)) {
                        showToast("图片上传异常，请重试");
                    }
                    if (mPickFront) {
                        showToast("正面上传成功");
                        mFrontUrl = url;
                    } else {
                        showToast("反面上传成功");
                        mBehindUrl = url;
                    }
                    if (StringUtil.isNotEmpty(mFrontUrl) &&
                            StringUtil.isNotEmpty(mBehindUrl)) {
                        mBtnSubmit.setEnabled(true);
                        mBtnSubmit.setTextColor(getResources().getColor(R.color.important_dark));
                    }
                    break;
                }
                case 2:
                    showToast("提交成功，请耐心等待审核结果");
                    UserSubject.updateScAuthFlag("2");  //认证中状态
                    finish();
                    break;
                case 3:
                    showToast("提交失败，请稍后再试");
                    Log.w(TAG, "学生证提交失败:" + msg.obj.toString());
                    break;
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void setAndUploadLogo() {
        //将截取的照片 存入imageView中
        Bitmap bitmap1 = mCropUtil.decodeUriAsBitmap(CaptureCropUtil.getCropImageUri());
        if (mPickFront) {
            mIvFrontSide.setImageBitmap(bitmap1);
        }
        if (!mPickFront) {
            mIvBehindSide.setImageBitmap(bitmap1);
        }
        uploadLogo(bitmap1);
    }

    private synchronized void uploadLogo(final Bitmap bitmap) {
        if(null != bitmap) {
            showProgressDialog("图片上传中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    new AzUploadService().doTrans(UserSubject.getStudentid(), bitmap, new AzUploadService.Callback() {
                        @Override
                        public void success(String logopath) {
                            messageHandler.postMessage(0, logopath);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CaptureCropUtil.REQUEST_CODE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    mCropUtil.resizeImage(data.getData(), 3, 2, 300, 200);
                }
                break;
            // 照相机现拍
            case CaptureCropUtil.REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (AppUtil.isSdcardExisting()) {
                        mCropUtil.resizeImage(CaptureCropUtil.getCaptureImageUri(), 3, 2, 300, 200);
                    } else {
                        showToast("未找到存储卡，无法存储照片");
                    }
                }
                break;
            // 图片截取
            case CaptureCropUtil.REQUEST_CODE_RESIZE:
                if (resultCode == Activity.RESULT_OK) {
                    setAndUploadLogo();
                }
                break;
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
