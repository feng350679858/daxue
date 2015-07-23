package com.jingcai.apps.aizhuan.activity.message;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.MessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.message.ConversationAdapter;
import com.jingcai.apps.aizhuan.adapter.message.EmotionPagerAdapter;
import com.jingcai.apps.aizhuan.entity.ConversationBean;
import com.jingcai.apps.aizhuan.util.AppUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.SmileUtils;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.jingcai.apps.aizhuan.view.AudioRecordButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by Json Ding on 2015/7/15.
 */
public class MessageConversationActivity extends BaseActivity{

    private static final String TAG = "MessageChatActivity";
    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_PICK_PICTURE = 1;
    private MessageHandler mHandler;
    private TextView mBtnSend;
    private String mReceiver = "18868831847";
    private EditText mEtMessage;
    private NewMessageBroadcastReceiver msgReceiver;
    private ViewPager mVpEmotion;
    private ImageButton mBtnEmotion;  //表情按钮
    private LinearLayout mLlEmotion; // 表情viewPager,indicator容器
    private ImageButton mBtnModeSwitch;   //切换语音和文字输入的按钮
    private ImageButton mBtnFuncPicker;  //照片、位置功能切换按钮
    private LinearLayout mLlFuncButtonContainer; //照片、位置按钮容器
    private ImageButton mBtnTakePicture;  //拍照
    private ImageButton mBtnPickPicture;  //选张照片
    private LinearLayout mLlPagerIndicator;
    private LinearLayout mLlInputTypeText;
    private AudioRecordButton mBtnRecord;
    private View mViewInputBottomLine;
    private ListView mListMessage;

    private ConversationAdapter mAdapter;

    private boolean mShowingEmotionPager;
    private boolean mIsInputTypeVoice;

    public String playMsgId = "";  //当前播放语音的id
    private File mCameraFile;  //照相机返回的file
    private ConversationBean mConversationBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_conversation);
        getIntentData();
        initHeader();
        initViews();
        showInputMethodDialog(mEtMessage);
        loadHistory();

        registerReceiver();
    }

    /**
     * 获取intent传入的数据
     */
    private void getIntentData() {
        final Intent intent = getIntent();
        if (intent != null) {
            mConversationBean = (ConversationBean) intent.getSerializableExtra("conversationBean");
        }
    }

    /**
     * 注册接收新信息的接收器
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        registerReceiver(msgReceiver, intentFilter);
    }


    private void loadHistory() {
        mAdapter = new ConversationAdapter(this);
        EMConversation conversation = EMChatManager.getInstance().getConversation(mReceiver);
        if (null == conversation || conversation.getAllMsgCount() <= 0) {
            return;
        }
        List<EMMessage> allMessages = conversation.getAllMessages();
        mAdapter.setListData(allMessages);   //***与conversation的message列表绑定，因此不需要手动进行更新
        mListMessage.setAdapter(mAdapter);
    }

    private void initViews() {
        mBtnSend = (TextView) findViewById(R.id.btn_send_msg);
        mEtMessage = (EditText) findViewById(R.id.et_message);

        mVpEmotion = (ViewPager) findViewById(R.id.vp_emotion);
        mBtnEmotion = (ImageButton) findViewById(R.id.btn_emotion);
        mBtnModeSwitch = (ImageButton) findViewById(R.id.btn_input_switch);
        mBtnRecord = (AudioRecordButton) findViewById(R.id.btn_record);
        mViewInputBottomLine = findViewById(R.id.view_input_bottom_line);
        mLlInputTypeText = (LinearLayout) findViewById(R.id.ll_input_type_text);
        mBtnFuncPicker = (ImageButton) findViewById(R.id.btn_func_picker);
        mLlFuncButtonContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        mBtnTakePicture = (ImageButton) findViewById(R.id.btn_take_picture);
        mBtnPickPicture = (ImageButton) findViewById(R.id.btn_picture);
        mLlPagerIndicator = (LinearLayout) findViewById(R.id.ll_pager_indicator);
        mLlEmotion = (LinearLayout) findViewById(R.id.ll_emotion);

        mBtnTakePicture.setOnClickListener(mFuncOnclickListener);
        mBtnPickPicture.setOnClickListener(mFuncOnclickListener);

        mListMessage = (ListView) findViewById(R.id.lv_message);
        mListMessage.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        //(+)号按钮
        mBtnFuncPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLlFuncButtonContainer.getVisibility() == View.GONE) {
                    mLlFuncButtonContainer.setVisibility(View.VISIBLE);
                    hideEmotionContainer();
                    hideInputMethodDialog(MessageConversationActivity.this);
                } else {
                    hideFuncBtnContainer();
                    showInputMethodDialog(mEtMessage);
                }
            }
        });

        //根据输入栏的焦点变化，改变底栏的颜色
        mEtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mViewInputBottomLine.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.main_yellow));
                    hideEmotionContainer();
                } else {
                    mViewInputBottomLine.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.assist_grey));
                }
            }
        });

        mEtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideEmotionContainer();
                hideFuncBtnContainer();
                return false;
            }
        });

        //录音按钮完成录音回调
        mBtnRecord.setAudioFinishRecordListener(new AudioRecordButton.AudioFinishRecordListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                //发送语音消息
                sendMessage(EMMessage.Type.VOICE, filePath, seconds);
            }
        });

        //表情添加
        EmotionPagerAdapter emotionPagerAdapter = new EmotionPagerAdapter(this);
        mVpEmotion.setAdapter(emotionPagerAdapter);
        initPagerIndicator(emotionPagerAdapter.getCount());
        emotionPagerAdapter.setOnEmotionClickListener(new EmotionPagerAdapter.OnEmotionClickListener() {
            @Override
            public void onEmotionClick(Spannable spannable) {
                mEtMessage.getText().append(spannable);
            }

            @Override
            public void onDeleteClick() {
                SmileUtils.backspace(mEtMessage);
            }
        });

        //发送文本
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEtMessage.getText().toString();
                mEtMessage.setText("");
                //发送文本消息
                sendMessage(EMMessage.Type.TXT, message);
            }
        });

        //表情
        mBtnEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlFuncButtonContainer.setVisibility(View.GONE);
                if (!mShowingEmotionPager) {
                    mLlEmotion.setVisibility(View.VISIBLE);
                    mBtnEmotion.setImageResource(R.drawable.icon_message_conversation_emotion_focused);
                    hideFuncBtnContainer();
                    hideInputMethodDialog(MessageConversationActivity.this);
                } else {
                    hideEmotionContainer();
                    showInputMethodDialog(mEtMessage);
                }
                mShowingEmotionPager = !mShowingEmotionPager;
            }
        });

        //输入转换
        mBtnModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsInputTypeVoice) {
                    mLlInputTypeText.setVisibility(View.VISIBLE);
                    mBtnRecord.setVisibility(View.GONE);
                    mBtnModeSwitch.setImageResource(R.drawable.chatting_setmode_voice_btn_selector);
                    //mEtMessage.performClick();
                    showInputMethodDialog(mEtMessage);
                } else {
                    //隐藏消息按钮
                    mLlInputTypeText.setVisibility(View.GONE);
                    //显示录音按钮
                    mBtnRecord.setVisibility(View.VISIBLE);
                    //改变转换按钮图标
                    mBtnModeSwitch.setImageResource(R.drawable.chatting_setmode_text_btn_selector);
                    hideEmotionContainer();

                    hideInputMethodDialog(MessageConversationActivity.this);  //隐藏软键盘
                }
                mIsInputTypeVoice = !mIsInputTypeVoice;
            }
        });

        //根据文本输入框的文字，判断显示按钮
        mEtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!StringUtil.isEmpty(s.toString()) && start == 0 && after == 0) {
                    //显示 +，隐藏发送消息
                    ObjectAnimator animatorAppearAlpha = ObjectAnimator.ofFloat(mBtnFuncPicker, "alpha", 0f, 1.00f);
                    ObjectAnimator animatorAppearX = ObjectAnimator.ofFloat(mBtnFuncPicker, "scaleX", 0.5f, 1.00f);
                    ObjectAnimator animatorAppearY = ObjectAnimator.ofFloat(mBtnFuncPicker, "scaleY", 0.5f, 1.00f);

                    ObjectAnimator animatorDismissAlpha = ObjectAnimator.ofFloat(mBtnSend, "alpha", 1.0f, 0f);
                    ObjectAnimator animatorDismissX = ObjectAnimator.ofFloat(mBtnSend, "scaleX", 1.0f, 0.5f);
                    ObjectAnimator animatorDismissY = ObjectAnimator.ofFloat(mBtnSend, "scaleY", 1.0f, 0.5f);

                    animatorDismissAlpha.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mBtnFuncPicker.setVisibility(View.VISIBLE);  //显示＋按钮
                            mBtnSend.setVisibility(View.GONE);  //隐藏发送按钮
                        }
                    });

                    AnimatorSet set = new AnimatorSet();
                    set.setDuration(50);
                    set.play(animatorAppearAlpha).with(animatorAppearX);
                    set.play(animatorAppearAlpha).with(animatorAppearY);
                    set.play(animatorDismissAlpha).before(animatorAppearAlpha);
                    set.play(animatorDismissAlpha).with(animatorDismissX);
                    set.play(animatorDismissAlpha).with(animatorDismissY);

                    set.start();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtil.isEmpty(s.toString()) && start == 0 && before == 0) {
                    //显示 发送消息，隐藏 +
                    ObjectAnimator animatorAppearAlpha = ObjectAnimator.ofFloat(mBtnSend, "alpha", 0f, 1.00f);
                    ObjectAnimator animatorAppearX = ObjectAnimator.ofFloat(mBtnSend, "scaleX", 0.5f, 1.0f);
                    ObjectAnimator animatorAppearY = ObjectAnimator.ofFloat(mBtnSend, "scaleY", 0.5f, 1.0f);

                    ObjectAnimator animatorDismissAlpha = ObjectAnimator.ofFloat(mBtnFuncPicker, "alpha", 1.0f, 0f);
                    ObjectAnimator animatorDismissX = ObjectAnimator.ofFloat(mBtnFuncPicker, "scaleX", 1.0f, 0.5f);
                    ObjectAnimator animatorDismissY = ObjectAnimator.ofFloat(mBtnFuncPicker, "scaleY", 1.0f, 0.5f);

                    animatorDismissAlpha.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mBtnFuncPicker.setVisibility(View.GONE);  //隐藏＋按钮
                            mBtnSend.setVisibility(View.VISIBLE);  //显示发送按钮
                        }
                    });

                    AnimatorSet set = new AnimatorSet();
                    set.setDuration(50);
                    set.play(animatorAppearAlpha).with(animatorAppearX);
                    set.play(animatorAppearAlpha).with(animatorAppearY);
                    set.play(animatorDismissAlpha).before(animatorAppearAlpha);
                    set.play(animatorDismissAlpha).with(animatorDismissX);
                    set.play(animatorDismissAlpha).with(animatorDismissY);
                    set.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void hideFuncBtnContainer() {
        mLlFuncButtonContainer.setVisibility(View.GONE);
    }

    /**
     * 初始化表情pager的indicator
     * @param count
     */
    private void initPagerIndicator(final int count) {
//        addIndicatorView(count,0);

        //改变indicator
        mVpEmotion.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i(TAG,"position:"+position+"\tpositionOffset:"+positionOffset+"\tpositionOffsetPixels:"+positionOffsetPixels);
                if(positionOffset == (int)positionOffset)
                    addIndicatorView(count,position);
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /**
     * 加入ImageView到Container中
     * @param count 总数
     * @param selectedPosition 选中的位置
     */
    private void addIndicatorView(int count,int selectedPosition) {
        mLlPagerIndicator.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            iv.setLayoutParams(params);
            iv.setImageResource(i==selectedPosition? R.drawable.icon_pager_indicator_yellow:R.drawable.icon_pager_indicator_white);
            iv.setPadding(5,5,5,5);
            mLlPagerIndicator.addView(iv);
        }
    }

    /**
     * 隐藏表情选择页
     */
    private void hideEmotionContainer() {
        mLlEmotion.setVisibility(View.GONE);
        mBtnEmotion.setImageResource(R.drawable.icon_message_conversation_emotion_normal);
    }

    private void initHeader() {
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        //需要用到再findViewById，不要需则不调用，提高效率
//        TextView tvFunc = (TextView) findViewById(R.id.tv_func);
        final ImageView ivFunc = (ImageView) findViewById(R.id.iv_func);
        ivFunc.setVisibility(View.VISIBLE);
        ivFunc.setImageResource(R.drawable.icon_message_conversation_3_point);
        ivFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dp10_px = MessageConversationActivity.this.getResources().getDimensionPixelSize(R.dimen.dp_10);
                Log.d(TAG, "---------" + dp10_px);
                View contentView = LayoutInflater.from(MessageConversationActivity.this).inflate(R.layout.popup_message_conversation_func, null);
                PopupWin groupWin = PopupWin.Builder.create(MessageConversationActivity.this)
                        .setWidth(dp10_px * 17)
                        .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                        .setAnimstyle(0)//取消动画
                        .setParentView(ivFunc)
                        .setContentView(contentView)
                        .build();
                groupWin.show(Gravity.TOP | Gravity.RIGHT, dp10_px, dp10_px * 6);
            }
        });

        tvTitle.setText("七爷");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendMessage(EMMessage.Type messageType, Object... args) {
        try {
            //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
            EMConversation conversation = EMChatManager.getInstance().getConversation(mReceiver);
            //创建一条文本消息
            EMMessage message = EMMessage.createSendMessage(messageType);
            //如果是群聊，设置chattype,默认是单聊
            //message.setChatType(EMMessage.ChatType.GroupChat);
            MessageBody messageBody = null;
            switch (messageType) {
                case TXT:
                    String msg = args[0].toString();
                    messageBody = new TextMessageBody(msg);
                    break;
                case VOICE:
                    String filePath = args[0].toString();
                    Float seconds = (Float) args[1];
                    messageBody = new VoiceMessageBody(new File(filePath), seconds.intValue());
                    break;
                case IMAGE:
                    File file = (File) args[0];
                    messageBody = new ImageMessageBody(file);
                    break;
            }
            //设置消息body
            message.addBody(messageBody);
            //设置接收人
            message.setReceipt(mReceiver);
            //把消息加入到此会话对象中
            conversation.addMessage(message);
            //发送消息 加入消息列表中，adapter会处理发送
//            mAdapter.addMessage(message);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            mHandler.postMessage(MESSAGE_WHAT_SHOW_TOAST,"发送失败:" + e.getMessage());
        }
    }

    //照片、相册、位置按钮相应监听器
    private View.OnClickListener mFuncOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                //照相按钮
                case R.id.btn_take_picture:
                    if (!AppUtil.isSdcardExisting()) {
                        String st = getResources().getString(R.string.sd_card_does_not_exist);
                        showToast(st);
                        return;
                    }

                    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aizhuan/image/");
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    mCameraFile = new File(path, UUID.randomUUID() + ".jpg");

                    startActivityForResult(
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile)),
                            REQUEST_CODE_CAMERA);
                    break;
                //相册选择照片
                case R.id.btn_picture:
                    if (!AppUtil.isSdcardExisting()) {
                        String st = getResources().getString(R.string.sd_card_does_not_exist);
                        showToast(st);
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent, REQUEST_CODE_PICK_PICTURE);
                    startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_PICK_PICTURE);
                    break;
            }
            mLlFuncButtonContainer.setVisibility(View.GONE);
        }
    };

    private static final int MESSAGE_WHAT_SHOW_TOAST = 0;
    private static final int MESSAGE_WHAT_EXIT = 2;
    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_WHAT_SHOW_TOAST:
                    showToast(msg.obj.toString());
                    break;
                case MESSAGE_WHAT_EXIT:
                    showToast("退出成功");
                    unregisterReceiver(msgReceiver);
                    finish();
                    break;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    sendMessage(EMMessage.Type.IMAGE, mCameraFile);
                }
                break;
            case REQUEST_CODE_PICK_PICTURE:
                ContentResolver resolver = getContentResolver();
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    InputStream inputStream = null;
                    FileOutputStream fileOutputStream = null;

                    try {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aizhuan/image");
                        if(!file.exists()){
                            file.mkdirs();
                        }
                        File path = new File(file,UUID.randomUUID()+".jpg");
                        Log.d(TAG,"New image file has been created,path is "+path.getAbsolutePath());
                        inputStream = resolver.openInputStream(uri);
                        fileOutputStream = new FileOutputStream(path);
                        byte[] buff = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(buff)) != -1) {
                            fileOutputStream.write(buff, 0, len);
                        }
                        fileOutputStream.flush();
                        sendMessage(EMMessage.Type.IMAGE, path);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "The stream of picked picture has a problem");
                    } finally {
                        try {
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG,"The stream of picked picture has a problem while closing");
                        }
                    }
                }

                break;
        }

    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();
            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
//            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation conversation = EMChatManager.getInstance().getConversation(username);
            EMMessage getMessage = conversation.getMessage(msgId);
            mAdapter.notifyDataSetChanged();

            // 如果是群聊消息，获取到group id
           /* if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                username = message.getTo();
            }
            if (!username.equals(username)) {
                // 消息不是发给当前会话，return
                return;
            }*/
        }
    }
}
