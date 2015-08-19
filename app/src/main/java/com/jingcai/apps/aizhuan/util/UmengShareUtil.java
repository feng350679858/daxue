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
 * Created by lejing on 15/5/11.
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
        // 添加QQ、QZone平台
        addQQQZonePlatform();
        // 添加微信、微信朋友圈平台
        addWXPlatform();
        // 添加短信
        addSMS();
        // 添加Email平台
        addEmail();
    }

    /**
     * 添加短信平台</br>
     */
    private void addSMS() {
        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }

    /**
     * 添加Email平台</br>
     */
    private void addEmail() {
        // 添加email
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wx9bc5c8000724d64f";
        String appSecret = "563d7c4de09e70b96001ae2192181eb0";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(activity, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     * image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     * 要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     * : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     */
    private void addQQQZonePlatform() {
        String appId = "1104549789";
        String appKey = "z8XOsQfLVVvuGh61";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, appId, appKey);
        qZoneSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qZoneSsoHandler.addToSocialSDK();
    }


    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    public void setShareContent(String title, String msg, String targetUrl) {
        setShareContent(title, msg, targetUrl, 0);
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
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
//        // 视频分享
//        UMVideo video = new UMVideo("http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
//        //vedio.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
//        video.setTitle("友盟社会化组件视频");
//        video.setThumb(urlImage);

//        UMusic uMusic = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
//        uMusic.setAuthor("umeng");
//        uMusic.setTitle("天籁之音");
////        uMusic.setThumb(urlImage);
//        uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");

        if(null == localImage){
            localImage = new UMImage(activity, R.drawable.ic_launcher);
        }

        mController.setShareContent(msg);

        /**
         * 微信好友
         */
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(title);
        weixinContent.setShareContent(msg);
        weixinContent.setShareMedia(localImage);
        weixinContent.setTargetUrl(targetUrl);
        mController.setShareMedia(weixinContent);


        /**
         * 微信朋友圈
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
         * QQ好友
         */
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setTitle(title);
        qqShareContent.setShareContent(msg);
        qqShareContent.setShareMedia(localImage);
        qqShareContent.setTargetUrl(targetUrl);
        mController.setShareMedia(qqShareContent);


        UMusic uMusic = new UMusic("http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
        uMusic.setAuthor("umeng");
        uMusic.setTitle("天籁之音");
//        uMusic.setThumb(urlImage);
        uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");
        /**
         * QQ空间
         */
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setTitle(title);
        qzone.setShareContent(msg);
        qzone.setShareMedia(localImage);
        qzone.setTargetUrl(targetUrl);
        mController.setShareMedia(qzone);

        /**
         * 邮件
         */
        MailShareContent mail = new MailShareContent(localImage);
        mail.setTitle(title);
        mail.setShareContent(msg);
        mail.setShareImage(localImage);
        mController.setShareMedia(mail);

        /**
         * 短信
         */
        SmsShareContent sms = new SmsShareContent();
        sms.setShareContent(msg);
        sms.setShareImage(localImage);
        mController.setShareMedia(sms);


        /**
         * 微博
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
