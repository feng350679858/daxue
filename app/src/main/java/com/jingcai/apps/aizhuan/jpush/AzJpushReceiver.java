package com.jingcai.apps.aizhuan.jpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDeploySuccessActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDeployingActivity;
import com.jingcai.apps.aizhuan.activity.help.HelpJishiDetailActivity;
import com.jingcai.apps.aizhuan.activity.message.NotificationDetailActivity;
import com.jingcai.apps.aizhuan.activity.mine.help.MineHelpJishiActivity;
import com.jingcai.apps.aizhuan.activity.partjob.PartjobDetailActivity;
import com.jingcai.apps.aizhuan.activity.util.BaseReceiver;
import com.jingcai.apps.aizhuan.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by lejingw on 2014/12/17.
 */
public class AzJpushReceiver extends BroadcastReceiver {
	private static final String TAG = AzJpushReceiver.class.getSimpleName();
	private static AtomicInteger atomicInteger = new AtomicInteger(100);
//	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private DecimalFormat moneyFormat = new DecimalFormat("##,###.00");

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			//send the Registration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			//processCustomMessage(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			receivingNotification(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
//			String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
//			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
//			Log.d(TAG, "Title : " + title + "  " + "Content : " + content);
//			//打开自定义的Activity
//			Intent i = new Intent(context, TestActivity.class);
//			i.putExtras(bundle);
//			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//			context.startActivity(i);
            openNotification(context,bundle);
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}
	private void receivingNotification(Context context, Bundle bundle){
		int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
		Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
		String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
		Log.d(TAG, " title : " + title);
		String message = bundle.getString(JPushInterface.EXTRA_ALERT);
		Log.d(TAG, "message : " + message);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Log.d(TAG, "extras : " + extras);

        String type = "";
        JSONObject extrasJson = null;
        try {
            extrasJson = new JSONObject(extras);
            type = extrasJson.optString("type");
        } catch (Exception e) {
            Log.d(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }
        if ("2".equals(type)) {//用户即时型求助发布成功-推送帮助者
            String helpid = extrasJson.optString("id");
            String studentid = extrasJson.optString("studentid");//求助者id
            if(StringUtil.isEmpty(helpid) || StringUtil.isEmpty(studentid)){
                return;
            }
            Intent intent = new Intent(BaseReceiver.ACTION_JISHI_DISPATCH_REQUEST);
            intent.putExtra("notiicationtype", type);
            intent.putExtra("notifactionid", notifactionId);
            intent.putExtra("helpid", helpid);
            intent.putExtra("studentid", studentid);
            context.sendBroadcast(intent);
        }else if ("3".equals(type)) {//用户即时型求助被人主动接单
            String helpid = extrasJson.optString("id");
            String studentid = extrasJson.optString("studentid");//帮助者id
            if(StringUtil.isEmpty(helpid) || StringUtil.isEmpty(studentid)){
                return;
            }
            Intent intent = new Intent(BaseReceiver.ACTION_JISHI_DISPATCH_REQUEST);
            intent.putExtra("notiicationtype", type);
            intent.putExtra("notifactionid", notifactionId);
            intent.putExtra("helpid", helpid);
            intent.putExtra("studentid", studentid);
            context.sendBroadcast(intent);
        }else if ("10".equals(type)) {//用户即时型求助发布成功-推送求助者
            String helpid = extrasJson.optString("id");
            String studentid = extrasJson.optString("studentid");
            if(StringUtil.isEmpty(helpid) || StringUtil.isEmpty(studentid)){
                return;
            }
            Intent intent = new Intent(HelpJishiDeployingActivity.ACTION_JISHI_DEPLOY_SUCCESS);
            intent.putExtra("notifactionid", notifactionId);
            intent.putExtra("helpid", helpid);
            intent.putExtra("studentid", studentid);
            context.sendBroadcast(intent);
        }
	}
	private void openNotification(Context context, Bundle bundle){
        JSONObject extrasJson = null;
		String type = "";
        try {
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            extrasJson = new JSONObject(extras);
			type = extrasJson.optString("type");
		} catch (Exception e) {
			Log.d(TAG, "Unexpected: extras is not a valid json", e);
			return;
		}
        if ("1".equals(type)) {//系统消息页
            String messageid = extrasJson.optString("id");
            if(StringUtil.isEmpty(messageid)){
                return;
            }
            Intent mIntent = new Intent(context, NotificationDetailActivity.class);
            mIntent.putExtra("id", messageid);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }else if ("2".equals(type)) {//用户即时型求助发布成功-推送帮助者
            String helpid = extrasJson.optString("id");
            String studentid = extrasJson.optString("studentid");//求助者id
            if(StringUtil.isEmpty(helpid) || StringUtil.isEmpty(studentid)){
                return;
            }
            Intent mIntent = new Intent(context, HelpJishiDetailActivity.class);
            mIntent.putExtra("notiicationtype", type);
            mIntent.putExtra("studentid", studentid);
            mIntent.putExtra("timestamp", System.currentTimeMillis());//超过30秒，直接超时
            mIntent.putExtra("helpid", helpid);
            mIntent.putExtra("type", "1");//1立即帮助 3公告
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }else if ("3".equals(type)) {//用户即时型求助被人主动接单
            String helpid = extrasJson.optString("id");
            String studentid = extrasJson.optString("studentid");//帮助者id
            if(StringUtil.isEmpty(helpid) || StringUtil.isEmpty(studentid)){
                return;
            }
            Intent mIntent = new Intent(context, HelpJishiDetailActivity.class);
            mIntent.putExtra("notiicationtype", type);
            mIntent.putExtra("studentid", studentid);
            mIntent.putExtra("helpid", helpid);
            mIntent.putExtra("type", "1");//1立即帮助 3公告
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }else if ("4".equals(type)) {//发起者取消求助或者确认付款，推送接收人
            String helpid = extrasJson.optString("id");
            if(StringUtil.isEmpty(helpid)){
                return;
            }
            //显示求助详情
            Intent mIntent = new Intent(context, MineHelpJishiActivity.class);
            mIntent.putExtra("helpid", helpid);
            mIntent.putExtra("provideFlag", false);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }else if ("5".equals(type)) {//求助超时、接收人处理取消申请或者完成帮助，推送发起者
            String helpid = extrasJson.optString("id");
            if(StringUtil.isEmpty(helpid)){
                return;
            }
            //显示求助详情
            Intent mIntent = new Intent(context, MineHelpJishiActivity.class);
            mIntent.putExtra("helpid", helpid);
            mIntent.putExtra("provideFlag", true);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }else if ("6".equals(type)) {//兼职详情
            String partjobid = extrasJson.optString("id");
            if(StringUtil.isEmpty(partjobid)){
                return;
            }
            Intent mIntent = new Intent(context, PartjobDetailActivity.class);
            mIntent.putExtra("partjobid", partjobid);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }else if ("10".equals(type)) {//用户即时型求助发布成功-推送求助者
            String helpid = extrasJson.optString("id");
            String studentid = extrasJson.optString("studentid");
            if(StringUtil.isEmpty(helpid) || StringUtil.isEmpty(studentid)){
                return;
            }
            Intent mIntent = new Intent(context, HelpJishiDeploySuccessActivity.class);
            mIntent.putExtra("helpid", helpid);
            mIntent.putExtra("studentid", studentid);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

//	private void processCustomMessage(Context context, Bundle bundle) {
////		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
//		String type = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//		String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
//		Log.d(TAG, "extra=" + message );
//		try {
//			RemoteViews myRemoteView = null;
//			PendingIntent launchIntent = null;
//			if("message".equalsIgnoreCase(type)) {
//				JSONObject extraJson = new JSONObject(message);
//				String id = extraJson.getString("id");
//				String title = extraJson.getString("title");
//				String content = extraJson.getString("content");
//				Log.d(TAG, "title=" + title + " content=" + content);
//
//				myRemoteView = new RemoteViews(context.getPackageName(), R.layout.sys_push_message);
//				myRemoteView.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher);
//				myRemoteView.setTextViewText(R.id.tv_main_tip, title);
//				//CharSequence cont = Html.fromHtml("<font color='blue'>" + content + "</font>");
//				myRemoteView.setTextViewText(R.id.tv_content, content);
//
//				Intent baseIntent = new Intent(context, NotificationDetailActivity.class);
//				baseIntent.putExtra("id", id);
//				launchIntent = PendingIntent.getActivity(context, 0, baseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			}
//			if (null != myRemoteView) {
//				showNotification(context, null, myRemoteView, launchIntent);
//			}
//		} catch (JSONException e) {
//		}
//
////		Notice notice = new Notice();
////		notice.setId(ApplicationEx.database.fetchMaxNoticeId("2") + 1);
////		notice.setTitle(title);
////		notice.setContent(content.toString());
////		notice.setTime(sdf.format(new Date()));
////		notice.setType("2");
////		ApplicationEx.database.createNotice(notice);
//	}
//
//	private void showNotification(final Context context, final String title, final RemoteViews myRemoteView, final PendingIntent launchIntent) {
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//				Notification newNotification = null;
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					Notification.Builder builder = new Notification.Builder(context)
//							.setSmallIcon(R.drawable.ic_launcher)
//							.setTicker(title)
//							.setWhen(System.currentTimeMillis())
//							.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
//							.setAutoCancel(true);
//
////					builder.setContentTitle(msgtitle)
////							.setContentText(msg)
////							//.setLargeIcon(R.drawable.icon_logo)
////							.setContentIntent(launchIntent);
//
//					builder.setContentTitle(title)
////							.setProgress(100, 30, false)
//							.setContent(myRemoteView)
//							.setContentIntent(launchIntent);
//
//					newNotification = builder.getNotification();
//				} else {
//					newNotification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
//					newNotification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
//					newNotification.flags = Notification.FLAG_AUTO_CANCEL;
//
////					newNotification.setLatestEventInfo(context, msgtitle, msg, launchIntent);
//
//					newNotification.contentView = myRemoteView;
//					newNotification.contentIntent = launchIntent;
//				}
//
//				mNotificationManager.notify(atomicInteger.incrementAndGet(), newNotification);
//				Looper.loop();
//			}
//		}.start();
//	}
}
