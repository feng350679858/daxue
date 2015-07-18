package com.jingcai.apps.aizhuan.activity.mine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.upload.AzUploadService;
import com.jingcai.apps.aizhuan.util.AppUtil;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.PopupDialog;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by Administrator on 2015/7/16.
 */
public class MineStudentCertificationActivity extends BaseActivity {
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;
    private MessageHandler messageHandler;
    private ImageView mphotoFront;
    private ImageView mphotoBack;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_student_certification);

        initHeader();

        initView();
    }

    private void initHeader(){
        ((TextView)findViewById(R.id.tv_content)).setText("我的֤");

    }

    private void initView(){
        mphotoFront = (ImageView)findViewById(R.id.iv_mine_student_certification_inside);
        mphotoBack  = (ImageView)findViewById(R.id.iv_mine_student_certification_back);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.iv_mine_student_certification_inside).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupDialog dialog = new PopupDialog(MineStudentCertificationActivity.this, R.layout.mine_photo_choose);
                dialog.setAction(R.id.ll_mine_photo_pai, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtil.isSdcardExisting()) {
                            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getImageUri());
                            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        } else {
                            showToast("δ�ҵ�SD��");
                        }
                        dialog.dismiss();
                    }
                }).setAction(R.id.ll_choose_from_album, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");//��Ƭ����
                        startActivityForResult(intent, IMAGE_REQUEST_CODE);
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
        findViewById(R.id.iv_mine_student_certification_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupDialog dialog = new PopupDialog(MineStudentCertificationActivity.this, R.layout.mine_photo_choose);
                dialog.setAction(R.id.ll_mine_photo_pai, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtil.isSdcardExisting()) {
                            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getImageUri());
                            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        } else {
                            showToast("δ�ҵ�SD��");
                        }
                        dialog.dismiss();
                    }
                }).setAction(R.id.ll_choose_from_album, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");//��Ƭ����
                        startActivityForResult(intent, IMAGE_REQUEST_CODE);
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
        //initCamer();
    }

    /*private void initCamer(){
        View dialogView = LayoutInflater.from(MineStudentCertificationActivity.this).inflate(R.layout.mine_photo_choose, null);
        final PopupWindow popupWindow = new PopupWindow(dialogView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        final View decorView =MineStudentCertificationActivity.this.getWindow().getDecorView();
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.main_menu_animstyle);
        popupWindow.showAtLocation(decorView, Gravity.CENTER_HORIZONTAL, 0, PixelUtil.px2dip(MineStudentCertificationActivity.this, 200f));
        dialogView.findViewById(R.id.ll_mine_photo_pai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();

            }
        });

    }*/


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
//                    Stu02Response.Stu02Body.Student student = (Stu02Response.Stu02Body.Student) msg.obj;
//                    fillStudentInView(student);
                    break;
                }
                case 1: {
                    showToast("��ȡʧ��:" + msg.obj);
                    break;
                }
                case 2: {
                    if (null != msg.obj) {
                        //����ȡ����Ƭ ����imageView��
                        Bitmap bitmap1 = (Bitmap) msg.obj;
                        mphotoFront.setImageBitmap(bitmap1);

                        uploadLogo(bitmap1);

                    } else {
                        showToast("ͼƬ��ȡʧ��");
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
        //��ͼƬ�ϴ���������
        new AzUploadService().doTrans(UserSubject.getStudentid(), bitmap, new AzUploadService.Callback() {
            @Override
            public void success(String logopath) {
                showToast("�ϴ�ͷ��ɹ�");
              //  mStudent.setLogopath(logopath);
            }

            @Override
            public void fail(AzException e) {
                messageHandler.postException(e);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // ������ȡͷ��
            case IMAGE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    resizeImage(data.getData());
                }
                break;
            // ���������
            case CAMERA_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (AppUtil.isSdcardExisting()) {
                        resizeImage(AppUtil.getImageUri());
                    } else {
                        showToast("δ�ҵ��洢�����޷��洢��Ƭ");
                    }
                }
                break;
            // ͼƬ��ȡ
            case RESIZE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap = null;
                    if (null != data.getExtras()) {
                        bitmap = data.getExtras().getParcelable("data");
                    }
                    messageHandler.postMessage(2, bitmap);
                }
                break;
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * �ü�ͼƬ����ʵ��
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
