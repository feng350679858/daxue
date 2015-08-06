package com.jingcai.apps.aizhuan.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
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

/**
 * Created by Administrator on 2015/7/18.
 */
public class MinePersonalDataActivity extends BaseActivity {
    private final String TAG="MinePersonalData";
    private static final int EMAIL_REQUEST_CODE = 1;//邮箱
    private static final int QQ_REQUEST_CODE = 2;//QQ
    private static final int IMAGE_REQUEST_CODE = 3;//头像的request_code
    private static final int CAMERA_REQUEST_CODE = 4;
    private static final int RESIZE_REQUEST_CODE = 5;

    private MessageHandler messageHandler;
    private View mFragmentLayout;
    private ImageView mImageLogopath;
    private TextView mTextName;
    private TextView mTextGender;
    private TextView mTextAreaname;
    private TextView mTextPhone;
    private TextView mTextEmail;
    private TextView mTextQq;
    private TextView mTextSchoolname;
    private TextView mTextJoindate;
    private TextView mTextCollegename;
    private TextView mTextProfessional;
    private AzService azService;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_personal_data);
        messageHandler = new MessageHandler(MinePersonalDataActivity.this);
        azService = new AzService(MinePersonalDataActivity.this);


        initHeader();
        initViews();  //初始化Views
        initData();  //填充数据
    }

    private void initHeader()
    {
        ((TextView)findViewById(R.id.tv_content)).setText("个人资料");
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化布局
     */
    private void initViews() {
        mImageLogopath = (ImageView) findViewById(R.id.iv_profile_logopath);
        mTextName = (TextView) findViewById(R.id.tv_profile_name);
        mTextGender = (TextView) findViewById(R.id.tv_profile_gender);
        mTextAreaname = (TextView)findViewById(R.id.tv_profile_areaname);
        mTextPhone = (TextView) findViewById(R.id.tv_profile_phone);
        mTextEmail = (TextView) findViewById(R.id.tv_profile_email);
        mTextQq = (TextView) findViewById(R.id.tv_profile_qq);
        mTextSchoolname = (TextView) findViewById(R.id.tv_profile_schoolname);
        mTextJoindate = (TextView) findViewById(R.id.tv_profile_joindate);
        mTextCollegename = (TextView) findViewById(R.id.tv_profile_collegename);
        mTextProfessional = (TextView)findViewById(R.id.tv_profile_professional);

        //修改邮箱
       findViewById(R.id.ll_mine_profile_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MinePersonalDataActivity.this, InproveProfileSimpleItemActivity.class);
                intent.putExtra("title", "邮箱地址");
                intent.putExtra("hint", "请填写您的邮箱地址");
                intent.putExtra("inputType", "email");
                startActivityForResult(intent, EMAIL_REQUEST_CODE);
            }
        });

        //修改QQ
       findViewById(R.id.ll_mine_profile_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MinePersonalDataActivity.this, InproveProfileSimpleItemActivity.class);
                intent.putExtra("title", "QQ");
                intent.putExtra("hint", "请填写您的QQ号码");
                intent.putExtra("inputType", "qq");
                startActivityForResult(intent, QQ_REQUEST_CODE);
            }
        });
       findViewById(R.id.ll_mine_profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupDialog dialog = new PopupDialog(MinePersonalDataActivity.this, R.layout.mine_photo_choose);
                dialog.setAction(R.id.ll_mine_photo_pai, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtil.isSdcardExisting()) {
                            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getImageUri());
                            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                            // dialog.dismiss();
                        } else {
                            showToast("未找到SD卡");
                            // dialog.dismiss();
                        }
                        dialog.dismiss();
                    }
                }).setAction(R.id.ll_choose_from_album, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");//相片类型
                        startActivityForResult(intent,IMAGE_REQUEST_CODE);
                        dialog.dismiss();
                    }
                }).setAction(R.id.btn_pick_photo_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });

    }

    private void initData() {
        showProgressDialog("数据加载中...");
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            messageHandler.postMessage(0, student);
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

    private void fillStudentInView(Stu02Response.Stu02Body.Student student) {
        mTextName.setText(student.getName());
        mTextGender.setText("1".equals(student.getGender())?"女":"男");
        mTextAreaname.setText(student.getAreaname());
        mTextPhone.setText(student.getPhone());
        mTextEmail.setText(student.getEmail());
        mTextQq.setText(student.getQq());
        mTextSchoolname.setText(student.getSchoolname());
        if(StringUtil.isNotEmpty(student.getJoindate())) {
            if(student.getJoindate().length()>4){
                mTextJoindate.setText(student.getJoindate().substring(0, 4));
            }else {
                mTextJoindate.setText(student.getJoindate());
            }
        }
        mTextCollegename.setText(student.getCollegename());
        mTextProfessional.setText(student.getProfessional());
        if (StringUtil.isNotEmpty(student.getLogopath())) {
            new BitmapUtil(MinePersonalDataActivity.this).getImage(mImageLogopath, student.getLogopath(), 0);
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
                case 0: {
                    Stu02Response.Stu02Body.Student student = (Stu02Response.Stu02Body.Student) msg.obj;
                    fillStudentInView(student);
                    break;
                }
                case 1: {
                    showToast("获取失败");
                    Log.i(TAG,"获取失败:" + msg.obj);
                    break;
                }
                case 2:
                    updateUIField((Stu03Request.Student) msg.obj);
                    showToast("更新成功");
                    break;
                case 3:
                    showToast("更新失败");
                    Log.i(TAG,"更新失败:" + msg.obj);
                    break;
                case 4: {
                    if(null != msg.obj ){
                        //将截取的照片 存入imageView中
                        Bitmap bitmap = (Bitmap)msg.obj;
                        mImageLogopath.setImageBitmap(bitmap);

                        uploadLogo(bitmap);

                    } else{
                        showToast("图片获取失败");
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void uploadLogo(Bitmap bitmap) {
        //将图片上传到服务器
        new AzUploadService().doTrans(UserSubject.getStudentid(), bitmap, new AzUploadService.Callback() {
            @Override
            public void success(String logopath) {
                updateLogopath(logopath);  //更新头像地址
            }

            @Override
            public void fail(AzException e) {
                messageHandler.postException(e);
            }
        });
    }

    //更新logo 发送到服务端
    private void updateLogopath(String logopath) {
        Stu03Request req = new Stu03Request();
        Stu03Request.Student student = req.new Student();
        req.setStudent(student);

        student.setLogopath(logopath);
        updateStudent(req);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                //邮箱
                case EMAIL_REQUEST_CODE:
                    if (data != null) {
                        Stu03Request req = new Stu03Request();
                        Stu03Request.Student student = req.new Student();
                        req.setStudent(student);

                        student.setEmail(data.getStringExtra("inputData"));
                        updateStudent(req);

                    }
                    break;
                //QQ
                case QQ_REQUEST_CODE:
                    if(data != null){
                        Stu03Request req = new Stu03Request();
                        Stu03Request.Student student = req.new Student();
                        req.setStudent(student);

                        student.setQq(data.getStringExtra("inputData"));
                        updateStudent(req);
                    }
                    break;

                // 从相册获取头像
                case IMAGE_REQUEST_CODE:
                    resizeImage(data.getData());
                    break;
                // 照相机现拍
                case CAMERA_REQUEST_CODE:
                    if (AppUtil.isSdcardExisting()) {
                        resizeImage(AppUtil.getImageUri());
                    } else {
                        showToast("未找到存储卡，无法存储照片");
                    }
                    break;
                // 图片截取
                case RESIZE_REQUEST_CODE:
                    if (data != null) {
                        Bitmap bitmap = null;
                        if(null != data.getExtras()){
                            bitmap = data.getExtras().getParcelable("data");
                        }
                        messageHandler.postMessage(4, bitmap);
                    }
                    break;
            }
        }

    }

    /**
     * 更新student，发送到服务端
     * @param req
     */
    private void updateStudent(final Stu03Request req) {
        req.getStudent().setStudentid(UserSubject.getStudentid());

        //更新指定的属性
        azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
            @Override
            public void success(BaseResponse resp) {
                ResponseResult result = resp.getResult();
                if (!"0".equals(result.getCode())) {
                    messageHandler.postMessage(3, result.getMessage());
                } else {
                    messageHandler.postMessage(2, req.getStudent());
                }
            }

            @Override
            public void fail(AzException e) {
                messageHandler.postException(e);
            }
        });
    }

    /**
     * 从服务端读取完，读取成功后更新UI与UserSubject
     * @param student 用户所输入的数据
     */
    private void updateUIField(Stu03Request.Student student) {
        if (StringUtil.isNotEmpty(student.getEmail())) {
            mTextEmail.setText(student.getEmail());
        }
        if( StringUtil.isNotEmpty(student.getQq())){
            mTextQq.setText(student.getQq());
        }
        if(StringUtil.isNotEmpty(student.getLogopath())){
           // UserSubject.setLogourl(student.getLogopath());
          //  Preferences.setLogopath(MinePersonalDataActivity.this, student.getLogopath());
         //   new BitmapUtil(MinePersonalDataActivity.this).getImage((MinePersonalDataActivity.this).getResideMenu().getIv_logopath(), UserSubject.getLogourl(), 0);
            //mImageLogopath.setImageDrawable(student.getLogopath());
            new BitmapUtil(MinePersonalDataActivity.this).getImage(mImageLogopath, student.getLogopath(), 0);
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
//        intent.putExtra("outputFormat", "JPEG");
//        intent.putExtra("noFaceDetection", true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }
}
