package com.jingcai.apps.aizhuan.util;

import android.app.Activity;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by Administrator on 2015/7/17.
 */
public class UmengShareUtil {
    public static final String DESCRIPTOR = "com.umeng.share";
    private final UMSocialService mController = UMServiceFactory.getUMSocialService(DESCRIPTOR);
    private final Activity activity;

    public UmengShareUtil(Activity activity) {
        this.activity = activity;
        initPlatform();

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                //SHARE_MEDIA.SINA,// SHARE_MEDIA.TENCENT,
                SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL);
    }

    private void initPlatform() {
        // ���QQ��QZoneƽ̨
        addQQQZonePlatform();
        // ���΢�š�΢������Ȧƽ̨
        addWXPlatform();
        // ��Ӷ���
        addSMS();
        // ���Emailƽ̨
        addEmail();
    }

    /**
     * ��Ӷ���ƽ̨</br>
     */
    private void addSMS() {
        // ��Ӷ���
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }

    /**
     * ���Emailƽ̨</br>
     */
    private void addEmail() {
        // ���email
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();
    }

    /**
     * @return
     * @�������� : ���΢��ƽ̨����
     */
    private void addWXPlatform() {
        // ע�⣺��΢����Ȩ��ʱ�򣬱��봫��appSecret
        // wx967daebe835fbeac������΢�ſ���ƽ̨ע��Ӧ�õ�AppID, ������Ҫ�滻����ע���AppID
        String appId = "wxc4b8ccbf51c73699";
        String appSecret = "3ad9669cac48abd6ae8f3e75b0b147e6";
        // ���΢��ƽ̨
        UMWXHandler wxHandler = new UMWXHandler(activity, appId, appSecret);
        wxHandler.addToSocialSDK();

        // ֧��΢������Ȧ
        UMWXHandler wxCircleHandler = new UMWXHandler(activity, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * @return
     * @�������� : ���QQƽ̨֧�� QQ��������ݣ� �����������ͣ� �����������֡�ͼƬ�����֡���Ƶ. ����˵�� : title, summary,
     * image url�б�����������һ��, targetUrl��������,��ҳ��ַ������"http://"��ͷ . title :
     * Ҫ������� summary : Ҫ��������ָ��� image url : ͼƬ��ַ [������������������дһ��] targetUrl
     * : �û�����÷���ʱ��ת����Ŀ���ַ [����] ( ������д��Ĭ������Ϊ������ҳ )
     */
    private void addQQQZonePlatform() {
        String appId = "1104549789";
        String appKey = "z8XOsQfLVVvuGh61";
        // ���QQ֧��, ��������QQ�������ݵ�target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // ���QZoneƽ̨
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, appId, appKey);
        qZoneSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qZoneSsoHandler.addToSocialSDK();
    }


    /**
     * ���ݲ�ͬ��ƽ̨���ò�ͬ�ķ�������</br>
     */
    public void setShareContent(String title, String msg, String targetUrl) {
        setShareContent(title, msg, targetUrl, 0);
    }

    /**
     * ���ݲ�ͬ��ƽ̨���ò�ͬ�ķ�������</br>
     */
    public void setShareContent(String title, String msg, String targetUrl, int logoDrawableId) {
        UMImage localImage = null;
        if(0 < logoDrawableId){
            localImage = new UMImage(activity, logoDrawableId);
        }
        setShareContent(title, msg, targetUrl, localImage);
    }

    public void setShareContent(String title, String msg, String targetUrl, String logoUrl) {
        UMImage urlImage = null;
        if(StringUtil.isNotEmpty(logoUrl)){
            if(!logoUrl.startsWith("http://")){
                logoUrl = GlobalConstant.imageUrl + logoUrl;
            }
            urlImage = new UMImage(activity, logoUrl);
        }
        setShareContent(title, msg, targetUrl, urlImage);
    }

    public void setShareContent(String title, String msg, String targetUrl, UMImage localImage) {
//        // ��Ƶ����
//        UMVideo video = new UMVideo("http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
//        //vedio.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
//        video.setTitle("������ữ�����Ƶ");
//        video.setThumb(urlImage);

//        UMusic uMusic = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
//        uMusic.setAuthor("umeng");
//        uMusic.setTitle("����֮��");
////        uMusic.setThumb(urlImage);
//        uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");

        if(null == localImage){
            localImage = new UMImage(activity, R.drawable.ic_launcher);
        }

        mController.setShareContent(msg);

        /**
         * ΢�ź���
         */
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(title);
        weixinContent.setShareContent(msg);
        weixinContent.setShareMedia(localImage);
        weixinContent.setTargetUrl(targetUrl);
        mController.setShareMedia(weixinContent);


        /**
         * ΢������Ȧ
         */
        CircleShareContent circleMedia = new CircleShareContent();
//        circleMedia.setTitle(title);
//        circleMedia.setShareContent(msg);
        circleMedia.setTitle(msg);
        circleMedia.setShareContent(msg);
        circleMedia.setShareMedia(localImage);
        circleMedia.setTargetUrl(targetUrl);
        mController.setShareMedia(circleMedia);


//        UMImage qzoneImage = new UMImage(getActivity(), "http://www.umeng.com/images/pic/social/integrated_3.png");
//        qzoneImage.setTargetUrl("http://www.umeng.com/images/pic/social/integrated_3.png");


        /**
         * QQ����
         */
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setTitle(title);
        qqShareContent.setShareContent(msg);
        qqShareContent.setShareMedia(localImage);
        qqShareContent.setTargetUrl(targetUrl);
        mController.setShareMedia(qqShareContent);


        UMusic uMusic = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
        uMusic.setAuthor("umeng");
        uMusic.setTitle("����֮��");
//        uMusic.setThumb(urlImage);
        uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");
        /**
         * QQ�ռ�
         */
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setTitle(title);
        qzone.setShareContent(msg);
        qzone.setShareMedia(localImage);
        qzone.setTargetUrl(targetUrl);
        mController.setShareMedia(qzone);

        /**
         * �ʼ�
         */
        MailShareContent mail = new MailShareContent(localImage);
        mail.setTitle(title);
        mail.setShareContent(msg);
        mail.setShareImage(localImage);
        mController.setShareMedia(mail);

        /**
         * ����
         */
        SmsShareContent sms = new SmsShareContent();
        sms.setShareContent(msg);
        sms.setShareImage(localImage);
        mController.setShareMedia(sms);


        /**
         * ΢��
         */
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setTitle(title);
        sinaContent.setShareContent(msg);
        sinaContent.setShareMedia(localImage);
        sinaContent.setTargetUrl(targetUrl);
        mController.setShareMedia(sinaContent);
    }

    public void openShare() {
        mController.openShare(activity, false);
    }
}
