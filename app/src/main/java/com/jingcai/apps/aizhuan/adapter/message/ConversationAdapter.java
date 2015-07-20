package com.jingcai.apps.aizhuan.adapter.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.ImageUtils;
import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.common.ShowBigImage;
import com.jingcai.apps.aizhuan.util.AppUtil;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.ConversationImageCache;
import com.jingcai.apps.aizhuan.util.ImagePathUtils;
import com.jingcai.apps.aizhuan.util.SmileUtils;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.easemob.chat.EMMessage.Type.IMAGE;
import static com.easemob.chat.EMMessage.Type.TXT;
import static com.easemob.chat.EMMessage.Type.VOICE;

/**
 * Created by Json Ding on 2015/7/6.
 */
public class ConversationAdapter extends BaseAdapter {
    public static final String TAG = "ConversationAdapter";
    public static final int MESSAGE_TYPE_COUNT = 6;     //***注意***根据以下类型的总数设置本值
    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 3;
    private static final int MESSAGE_TYPE_SENT_VOICE = 4;
    private static final int MESSAGE_TYPE_RECV_VOICE = 5;
    private static final String IMAGE_DIR = "aizhuan/image";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<EMMessage> mMessages;   //所有的聊天
    private Hashtable<String, Timer> timers;
    private BitmapUtil mBitmapUtil;

    public ConversationAdapter(Context ctx){
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        mBitmapUtil = new BitmapUtil(ctx);
        timers = new Hashtable<>();
    }

    /**
     * 给Adapter设置数据
     * @param messages EMMessage的列表
     */
    public void setListData(List<EMMessage> messages){
        mMessages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    /**
     * item的id设置为消息的时间
     * @param position 消息处在列表的位置
     * @return 消息的时间（毫秒）
     */
    @Override
    public long getItemId(int position) {
        return mMessages.get(position).getMsgTime();
    }


    private View createViewByMessage(EMMessage message) {
        switch (message.getType()) {
            case IMAGE:
                return message.direct == EMMessage.Direct.RECEIVE ? mInflater.inflate(R.layout.row_received_picture, null) : mInflater.inflate(
                        R.layout.row_sent_picture, null);
            case VOICE:
                return message.direct == EMMessage.Direct.RECEIVE ? mInflater.inflate(R.layout.row_received_voice, null) : mInflater.inflate(
                        R.layout.row_sent_voice, null);
            case TXT:
                return message.direct == EMMessage.Direct.RECEIVE ? mInflater.inflate(R.layout.row_received_message, null) : mInflater.inflate(
                        R.layout.row_sent_message, null);
            default:return null;
        }
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EMMessage message = mMessages.get(position);    //获取消息
        int itemViewType = getItemViewType(position);  //消息类型
        EMMessage.ChatType chatType = message.getChatType();  //获取消息类型
        final ViewHolder holder;  //ViewHolder 所有类型共用一个ViewHolder
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message);
            if (message.getType() == IMAGE) {
                try {
                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {
                    Log.e(TAG,"can't find view in image type,check if you choose a wrong type.\n"+e.getMessage());
                }
            } else if (message.getType() == TXT) {
                try {
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    // 这里是文字内容
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {
                    Log.e(TAG, "can't find view in txt type,check if you choose a wrong type.\n" + e.getMessage());
                }
            } else if (message.getType() == VOICE) {
                try {
                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
                    holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
                } catch (Exception e) {
                    Log.e(TAG, "can't find view in voice type,check if you choose a wrong type.\n" + e.getMessage());
                }
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 如果是发送的消息并且不是群聊消息，显示已读textview
        if (!(chatType == EMMessage.ChatType.GroupChat || chatType == EMMessage.ChatType.ChatRoom)
                && message.direct == EMMessage.Direct.SEND) {
            holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
            holder.tv_delivered = (TextView) convertView.findViewById(R.id.tv_delivered);
            if (holder.tv_ack != null) {
                if (message.isAcked) {
                    if (holder.tv_delivered != null) {
                        holder.tv_delivered.setVisibility(View.INVISIBLE);
                    }
                    holder.tv_ack.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_ack.setVisibility(View.INVISIBLE);
                    // check and display msg delivered ack status
                    if (holder.tv_delivered != null) {
                        if (message.isDelivered) {
                            holder.tv_delivered.setVisibility(View.VISIBLE);
                        } else {
                            holder.tv_delivered.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        } else {
            // 如果是文本或者地图消息并且不是group messgae,chatroom message，显示的时候给对方发送已读回执
            if ((MESSAGE_TYPE_RECV_TXT == itemViewType ||MESSAGE_TYPE_RECV_IMAGE==itemViewType)
                    && !message.isAcked && chatType != EMMessage.ChatType.GroupChat && chatType != EMMessage.ChatType.ChatRoom) {
                // 不是语音通话记录
                if (!message.getBooleanAttribute(MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    try {
                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                        // 发送已读回执
                        message.isAcked = true;
                    } catch (Exception e) {
                        Log.e(TAG, "ack message failed,check the message you send.\n" + e.getMessage());
                    }
                }
            }
        }

        //设置用户头像  todo 根据UserSubject中的头像url设置用户头像
//        setUserAvatar(message, holder.iv_avatar);
        switch (message.getType()) {
            // 根据消息type显示item
            case IMAGE: // 图片
                handleImageMessage(message, holder, position);
                break;
            case TXT: // 文本
                  handleTextMessage(message, holder, position);
                break;
            case VOICE: // 语音
                handleVoiceMessage(message, holder);
            default:
                // not supported
        }

        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

        if (position == 0) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            EMMessage prevMessage = mMessages.get(position - 1);
            if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                timestamp.setVisibility(View.GONE);
            } else {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return MESSAGE_TYPE_COUNT;
    }

    /**
     * 根据消息类型（枚举）返回消息类型（int）
     * @param position 消息的位置
     * @return 消息类型(int),如果类型不定为-1
     */
    @Override
    public int getItemViewType(int position) {
        EMMessage message = mMessages.get(position);
        if (message == null) {
            return -1;
        }
        if (message.getType() == EMMessage.Type.TXT) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        return -1;// invalid
    }

    /**
     * 图片消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position) {
        holder.pb.setTag(position);
        holder.iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ((Activity) mContext).startActivityForResult(
//                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
//                                EMMessage.Type.IMAGE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        // 接收方向的消息
        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.status == EMMessage.Status.INPROGRESS) {
                holder.iv.setImageResource(R.drawable.default_image);
                showDownloadImageProgress(message, holder);
            } else {
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.iv.setImageResource(R.drawable.default_image);
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                if (imgBody.getLocalUrl() != null) {
                    String remotePath = imgBody.getRemoteUrl();
                    String filePath = ImagePathUtils.getImagePath(remotePath);
                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
                    if(TextUtils.isEmpty(thumbRemoteUrl)&&!TextUtils.isEmpty(remotePath)){
                        thumbRemoteUrl = remotePath;
                    }
                    String thumbnailPath = ImagePathUtils.getThumbnailImagePath(thumbRemoteUrl);
                    showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message);
                }
            }
            return;
        }

        // 发送的消息
        // process send message
        // send pic, show the pic directly
        ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
        String filePath = imgBody.getLocalUrl();
        if (filePath != null && new File(filePath).exists()) {
            showImageView(ImagePathUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
        } else {
            showImageView(ImagePathUtils.getThumbnailImagePath(filePath), holder.iv, filePath, IMAGE_DIR, message);
        }

        switch (message.status) {
            case SUCCESS:
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.staus_iv.setVisibility(View.INVISIBLE);
                        holder.pb.setVisibility(View.VISIBLE);
                        handleImageMessage(message,holder,position);
                    }
                });

                break;
            case INPROGRESS:
                holder.staus_iv.setVisibility(View.GONE);
                holder.pb.setVisibility(View.VISIBLE);
                holder.tv.setVisibility(View.VISIBLE);
                if (timers.containsKey(message.getMsgId()))
                    return;
                // set a timer
                final Timer timer = new Timer();
                timers.put(message.getMsgId(), timer);
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            public void run() {
                                holder.tv.setText(message.progress + "%");
                                if (message.status == EMMessage.Status.SUCCESS) {
                                    holder.pb.setVisibility(View.GONE);
                                    holder.tv.setVisibility(View.GONE);
                                    timer.cancel();
                                } else if (message.status == EMMessage.Status.FAIL) {
                                    holder.pb.setVisibility(View.GONE);
                                    holder.tv.setVisibility(View.GONE);
                                    holder.staus_iv.setVisibility(View.VISIBLE);
                                    Toast.makeText(mContext,
                                            mContext.getString(R.string.send_fail) + mContext.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
                                            .show();
                                    timer.cancel();
                                }
                            }
                        });
                    }
                }, 0, 500);
                break;
            default:
                sendPictureMessage(message, holder);
        }
    }

 /*
 * send message with new sdk
 */
    private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
        try {
            // before send, update ui
            holder.staus_iv.setVisibility(View.GONE);
            holder.pb.setVisibility(View.VISIBLE);
            holder.tv.setVisibility(View.VISIBLE);
            holder.tv.setText("0%");

            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

                @Override
                public void onSuccess() {
                    Log.d(TAG, "send image message successfully");
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        public void run() {
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        public void run() {
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                            // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                            holder.staus_iv.setVisibility(View.VISIBLE);
                            holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.staus_iv.setVisibility(View.GONE);
                                    holder.pb.setVisibility(View.VISIBLE);
                                    sendPictureMessage(message,holder);
                                }
                            });
                            Toast.makeText(mContext,
                                    mContext.getString(R.string.send_fail) + mContext.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onProgress(final int progress, String status) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        public void run() {
                            holder.tv.setText(progress + "%");
                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"send picture error,check the network.");
        }
    }

    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath,final String remoteDir,
                                  final EMMessage message) {
        Bitmap bitmap = ConversationImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EMLog.d(TAG, "image view on click");
                    Intent intent = new Intent( mContext, ShowBigImage.class);
                    File file = new File(localFullSizePath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        intent.putExtra("uri", uri);
                        EMLog.d(TAG, "here need to check why download everytime");
                    } else {
                        // The local full size pic does not exist yet.
                        // ShowBigImage needs to download it from the server
                        // first
                        // intent.putExtra("", message.get);
                        ImageMessageBody body = (ImageMessageBody) message.getBody();
                        intent.putExtra("secret", body.getSecret());
                        intent.putExtra("remotepath", remoteDir);
                    }
                    if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
                            && message.getChatType() != EMMessage.ChatType.GroupChat && message.getChatType() != EMMessage.ChatType.ChatRoom) {
                        try {
                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mContext.startActivity(intent);
                }
            });
            return true;
        } else {
            new LoadImageTask().execute(thumbernailPath, localFullSizePath, remoteDir, message.getChatType(), iv, ((Activity) mContext), message);
            return true;
        }
    }

    /*
     * chat sdk will automatic download thumbnail image for the image message we
     * need to register callback show the download progress
     */
    private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
        final FileMessageBody msgbody = (FileMessageBody) message.getBody();
        if(holder.pb!=null)
            holder.pb.setVisibility(View.VISIBLE);
        if(holder.tv!=null)
            holder.tv.setVisibility(View.VISIBLE);

        msgbody.setDownloadCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.getType() == EMMessage.Type.IMAGE) {
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onProgress(final int progress, String status) {
                if (message.getType() == EMMessage.Type.IMAGE) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tv.setText(progress + "%");

                        }
                    });
                }
            }

        });
    }


        /**
         * 语音消息
         *
         * @param message
         * @param holder
         */
    private void handleVoiceMessage(final EMMessage message, final ViewHolder holder) {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        holder.tv.setText(voiceBody.getLength() + "\"");
        holder.iv.setOnClickListener(new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, ((Activity) mContext)));
        holder.iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                activity.startActivityForResult(
//                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
//                                EMMessage.Type.VOICE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.isListened()) {
                // 隐藏语音未听标志
                holder.iv_read_status.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_read_status.setVisibility(View.VISIBLE);
            }
            if (message.status == EMMessage.Status.INPROGRESS) {
                holder.pb.setVisibility(View.VISIBLE);
                ((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                                notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
            } else {
                holder.pb.setVisibility(View.INVISIBLE);
            }
            return;
        }

        // until here, deal with send voice msg
        switch (message.status) {
            case SUCCESS:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.staus_iv.setVisibility(View.GONE);
                        handleVoiceMessage(message, holder);
                    }
                });
                break;
            case INPROGRESS:
                holder.pb.setVisibility(View.VISIBLE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            default:
                sendMsgInBackground(message, holder);
        }
    }

    /**
     * 文本消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleTextMessage(final EMMessage message, final ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        Spannable span = SmileUtils.getSmiledText(mContext, txtBody.getMessage());
        // 设置内容
        holder.tv.setText(span, TextView.BufferType.SPANNABLE);
        // 设置长按事件监听
        holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ((Activity) mContext).startActivityForResult(
//                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
//                                EMMessage.Type.TXT.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // 发送成功
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.staus_iv.setVisibility(View.GONE);
                            handleTextMessage(message,holder,position);
                        }
                    });
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(message, holder);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param message
     * @param holder
     */
    public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
        holder.staus_iv.setVisibility(View.GONE);
        holder.pb.setVisibility(View.VISIBLE);

        // 调用sdk发送异步发送方法
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                updateSendedView(message, holder);
            }

            @Override
            public void onError(int code, String error) {
                updateSendedView(message, holder);
            }

            @Override
            public void onProgress(int progress, String status) {
            }

        });
    }

    /**
     * 更新ui上消息发送状态
     *
     * @param message
     * @param holder
     */
    private void updateSendedView(final EMMessage message, final ViewHolder holder) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // send success
                if (message.getType() == EMMessage.Type.VIDEO) {
                    holder.tv.setVisibility(View.GONE);
                }
                EMLog.d(TAG, "message status : " + message.status);
                if (message.status == EMMessage.Status.SUCCESS) {

                } else if (message.status == EMMessage.Status.FAIL) {

                    Log.e(TAG,"message send error,error code:"+message.getError());
                }

                notifyDataSetChanged();
            }
        });
    }

    class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
        private ImageView iv = null;
        String localFullSizePath = null;
        String thumbnailPath = null;
        String remotePath = null;
        EMMessage message = null;
        EMMessage.ChatType chatType;
        Activity activity;

        @Override
        protected Bitmap doInBackground(Object... args) {
            thumbnailPath = (String) args[0];
            localFullSizePath = (String) args[1];
            remotePath = (String) args[2];
            chatType = (EMMessage.ChatType) args[3];
            iv = (ImageView) args[4];
            // if(args[2] != null) {
            activity = (Activity) args[5];
            // }
            message = (EMMessage) args[6];
            File file = new File(thumbnailPath);
            if (file.exists()) {
                return ImageUtils.decodeScaleImage(thumbnailPath, 160, 160);
            } else {
                if (message.direct == EMMessage.Direct.SEND) {
                    return ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
                } else {
                    return null;
                }
            }


        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                iv.setImageBitmap(image);
                ConversationImageCache.getInstance().put(thumbnailPath, image);
                iv.setClickable(true);
                iv.setTag(thumbnailPath);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (thumbnailPath != null) {

						Intent intent = new Intent(activity, ShowBigImage.class);
						File file = new File(localFullSizePath);
						if (file.exists()) {
							Uri uri = Uri.fromFile(file);
							intent.putExtra("uri", uri);
						} else {
							// The local full size pic does not exist yet.
							// ShowBigImage needs to download it from the server
							// first
							intent.putExtra("remotepath", remotePath);
						}
						if (message.getChatType() != EMMessage.ChatType.Chat) {
							// delete the image from server after download
						}
						if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked && message.getChatType() != EMMessage.ChatType.GroupChat && message.getChatType() != EMMessage.ChatType.ChatRoom) {
							message.isAcked = true;
							try {
								// 看了大图后发个已读回执给对方
								EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						activity.startActivity(intent);
					}
				}
			});
            } else {
                if (message.status == EMMessage.Status.FAIL) {
                    if (AppUtil.isNetWorkConnected(activity)) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                EMChatManager.getInstance().asyncFetchMessage(message);
                            }
                        }).start();
                    }
                }

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    public static class ViewHolder {
        ImageView iv;
        TextView tv;
        ProgressBar pb;
        ImageView staus_iv;
        ImageView iv_avatar;
        TextView tv_usernick;
        ImageView playBtn;
        TextView timeLength;
        TextView size;
        LinearLayout container_status_btn;
        LinearLayout ll_container;
        ImageView iv_read_status;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;

        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_file_download_state;
    }

}
