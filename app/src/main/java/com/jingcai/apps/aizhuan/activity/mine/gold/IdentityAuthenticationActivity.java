package com.jingcai.apps.aizhuan.activity.mine.gold;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.game.game09.Game09Request;
import com.jingcai.apps.aizhuan.service.upload.AzUploadService;
import com.jingcai.apps.aizhuan.util.AppUtil;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.CaptureCropUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Json Ding on 2015/8/5.
 */
public class IdentityAuthenticationActivity extends BaseActivity {
    private static final String TAG = "IdentityAuthentication";

    private static final int REQUEST_CODE_IMAGE = 0;
    private static final int REQUEST_CODE_CAMERA = 1;//相机
    private static final int REQUEST_CODE_RESIZE = 2;//截图

    private boolean mPickFront = true;//判断身份证内侧
    private String mFrontUrl;   //上传证件正面url
    private String mBehindUrl;  //上传证件背面url

    private MessageHandler messageHandler;
    private AzService azService;
    private CaptureCropUtil mCropUtil;

    private EditText mEtName;
    private EditText mEtIdNo;
    private TextView mTvIdFront;
    private TextView mTvIdBehind;
    private Button mBtnSubmit;

    private PopupWin mMakePicWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_gold_identity_authentication);

        messageHandler = new MessageHandler(this);
        azService = new AzService();
        mCropUtil = new CaptureCropUtil(this);
        initHeader();
        initViews();
    }

    private void initViews() {
        mEtName = (EditText) findViewById(R.id.et_authentication_name);
        mEtIdNo = (EditText) findViewById(R.id.et_authentication_idno);
        mTvIdFront = (TextView) findViewById(R.id.tv_identity_card_front);
        mTvIdBehind = (TextView) findViewById(R.id.tv_identity_card_behind);
        mBtnSubmit = (Button) findViewById(R.id.btn_authentication_submit);

        mEtIdNo.setTransformationMethod(new StringUtil.AllCapTransformationMethod());  //小写自动转大写

        initEvents();
    }

    private void initEvents() {
        mTvIdFront.setOnClickListener(mMakePicOnclickListener);
        mTvIdBehind.setOnClickListener(mMakePicOnclickListener);

        mEtIdNo.addTextChangedListener(mButtonStateMonitor);
        mEtName.addTextChangedListener(mButtonStateMonitor);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    submitAuthentication();
                }
            }
        });
    }

    private boolean checkInput() {
        if (!mEtIdNo.getText().toString()
                .matches("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}[\\d|X]$")) {
            showToast("请检查身份证号码");
            return false;
        }
        if (mEtName.length() < 2) {
            showToast("请输入正确的姓名");
            return false;
        }
        return true;
    }

    /**
     * 提交认证
     */
    private void submitAuthentication() {
        showProgressDialog("身份信息提交中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Game09Request req = new Game09Request();
                Game09Request.Student student = req.new Student();
                student.setId(UserSubject.getStudentid());
                student.setIdimg_front(mFrontUrl);
                student.setIdimg_back(mBehindUrl);
                student.setIdno(mEtIdNo.getText().toString());
                student.setName(mEtName.getText().toString());
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

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("身份验证");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private TextWatcher mButtonStateMonitor = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEtIdNo.length() == 18
                    && mEtName.length() > 1
                    && StringUtil.isNotEmpty(mFrontUrl)
                    && StringUtil.isNotEmpty(mBehindUrl)) {
                enableButton();
            } else {
                disableButton();
            }
        }
    };

    private View.OnClickListener mMakePicOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPickFront = v.getId() == R.id.tv_identity_card_front;  //是否是点击正面

            if (null == mMakePicWin) {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("camera", "拍照");
                map.put("album", "从相册选择");
                View parentView = IdentityAuthenticationActivity.this.getWindow().getDecorView();
                mMakePicWin = PopupWin.Builder.create(IdentityAuthenticationActivity.this)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case CaptureCropUtil.REQUEST_CODE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    mCropUtil.resizeImage(data.getData(), 856, 540, 428, 270);
                }
                break;
            // 照相机现拍
            case CaptureCropUtil.REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (AppUtil.isSdcardExisting()) {
                        mCropUtil.resizeImage(CaptureCropUtil.getCaptureImageUri(), 856, 540, 428, 270);
                    } else {
                        showToast("未找到存储卡，无法存储照片");
                    }
                }
                break;
            // 图片截取
            case CaptureCropUtil.REQUEST_CODE_RESIZE:
                if (resultCode == Activity.RESULT_OK) {
//                    Bitmap bitmap = null;
//                    if (null != data.getExtras()) {
//                        bitmap = data.getExtras().getParcelable("data");
//                    }
                    setAndUploadLogo();
//                    messageHandler.postMessage(1);
                }
                break;
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0:
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
                    if (StringUtil.isNotEmpty(mFrontUrl)
                            && StringUtil.isNotEmpty(mBehindUrl)
                            && mEtIdNo.length() == 18
                            && mEtName.length() > 1) {
                        enableButton();
                    }
                    break;

                //               case 1: {
//                    if (null != msg.obj) {
                //将截取的照片 存入imageView中
//                        Bitmap bitmap1 = (Bitmap) msg.obj;


//                    } else {
//                        showToast("图片获取失败");
//                    }
                //               break;
                //               }
                case 2:
                    showToast("提交成功，请耐心等待审核结果");
                    UserSubject.updateIdAuthFlag("2");
                    finish();
                    break;
                case 3:
                    showToast("提交失败，请稍后再试");
                    Log.w(TAG, "身份证提交失败:" + msg.obj.toString());
                    break;
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void enableButton() {
        mBtnSubmit.setEnabled(true);
        mBtnSubmit.setTextColor(getResources().getColor(R.color.important_dark));
    }

    private void disableButton() {
        mBtnSubmit.setEnabled(false);
        mBtnSubmit.setTextColor(getResources().getColor(R.color.assist_grey));
    }

    private void setAndUploadLogo() {
        //将截取的照片 存入imageView中
        Bitmap bitmap1 = mCropUtil.decodeUriAsBitmap(CaptureCropUtil.getCropImageUri());
        Drawable drawableBitmap = new BitmapDrawable(getResources(), bitmap1);
        if (mPickFront) {
            mTvIdFront.setCompoundDrawablesWithIntrinsicBounds(null, drawableBitmap, null, null);
        } else {
            mTvIdBehind.setCompoundDrawablesWithIntrinsicBounds(null, drawableBitmap, null, null);
        }
        uploadLogo(bitmap1);
    }

    private synchronized void uploadLogo(final Bitmap bitmap) {
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

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
//    public void resizeImage(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 856);  //身份证的比例
//        intent.putExtra("aspectY", 540);
//        intent.putExtra("outputX", 428);
//        intent.putExtra("outputY", 270);
//        intent.putExtra("return-data", true);
//        intent.putExtra("outputFormat", "JPEG");
//        intent.putExtra("noFaceDetection", true); //关闭人脸检测
////        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, REQUEST_CODE_RESIZE);
//    }
}
