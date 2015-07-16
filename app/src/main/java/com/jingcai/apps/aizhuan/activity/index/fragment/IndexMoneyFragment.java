package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseFragment;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.index.BannerDetailActivity;
import com.jingcai.apps.aizhuan.activity.index.IndexPartjobListByLabelActivity;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;
import com.jingcai.apps.aizhuan.activity.partjob.LocationCityActivity;
import com.jingcai.apps.aizhuan.activity.partjob.PartjobDetailActivity;
import com.jingcai.apps.aizhuan.activity.partjob.PartjobSearchActivity;
import com.jingcai.apps.aizhuan.adapter.partjob.PartjobSearchAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.business.advice.advice05.Advice05Request;
import com.jingcai.apps.aizhuan.service.business.advice.advice05.Advice05Response;
import com.jingcai.apps.aizhuan.service.business.base.base01.Base01Request;
import com.jingcai.apps.aizhuan.service.business.base.base01.Base01Response;
import com.jingcai.apps.aizhuan.service.business.busi.busi01.Busi01Request;
import com.jingcai.apps.aizhuan.service.business.busi.busi01.Busi01Response;
import com.jingcai.apps.aizhuan.service.business.busi.busi02.Busi02Request;
import com.jingcai.apps.aizhuan.service.business.busi.busi02.Busi02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.jingcai.apps.aizhuan.view.AutoMarqueeTextView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Json Ding on 2015/7/10.
 */
public class IndexMoneyFragment extends BaseFragment {
    private static final String TAG = IndexMoneyFragment.class.getName();
    private ScheduledExecutorService scheduledExecutorService;
    private LayoutInflater layoutInflator;
    private BitmapUtil bitmapUtil;
    private AzExecutor azExecutor;
    private AzService azService;
    private MessageHandler messageHandler;
    private PartjobSearchAdapter searchAdapter;
    private View mainView;
    private AutoMarqueeTextView am_text;
    private ImageView[] pageViews, imageDots;
    private LinearLayout linearLayout_label, linearlout_left, linearlout_right;
    private TextView tv_address;
    private int pixelOf1dp;
    private final int REQUEST_CODE_ADDRESS = 11;//切换城市

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if(null == mainView){
            layoutInflator = inflater;//LayoutInflater.from(parentActivity);
            bitmapUtil = new BitmapUtil(baseActivity);
            azExecutor = new AzExecutor();
            azService = new AzService();
            messageHandler = new MessageHandler(baseActivity);

            pixelOf1dp = getResources().getDimensionPixelSize(R.dimen.margin_1dp);

            mainView = layoutInflator.inflate(R.layout.index_money_fragment, null);
            initHeader();
            initView();

            initData();
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeView(mainView);
        }
        return mainView;
    }

    private void initHeader(){
        //将activity中的header.xml隐藏
        (getActivity().findViewById(R.id.layout_header)).setVisibility(View.GONE);

        searchAdapter = new PartjobSearchAdapter(baseActivity);
        tv_address=(TextView)mainView.findViewById(R.id.tv_address);
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationCityActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADDRESS);
            }
        });
        ((TextView)mainView.findViewById(R.id.tv_search)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), PartjobSearchActivity.class);
                startActivity(intent);
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ADDRESS: {
                if(Activity.RESULT_OK == resultCode) {
                    // 返回城市编码
                    String code = data.getStringExtra("code");
                    String name = data.getStringExtra("name");
                    Log.i(TAG, "code=" + code + " name=" + name);
                    tv_address.setTag(code);
                    tv_address.setText(name);
                    // 刷新区域数据
                    searchAdapter.initAreaCode(code);
                    // 检索
                    initData();
                }
            }

            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    private void initView() {

        am_text = (AutoMarqueeTextView)mainView.findViewById(R.id.am_text);

        linearLayout_label = (LinearLayout) mainView.findViewById(R.id.linearLayout_label);
        linearlout_left = (LinearLayout) mainView.findViewById(R.id.linearlout_left);
        linearlout_right = (LinearLayout) mainView.findViewById(R.id.linearlout_right);

    }

    private void initData() {
        //请求世界消息列表
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Advice05Request req = new Advice05Request();
                Advice05Request.Notice notice = req.new Notice();
                if (GlobalConstant.gis.hasGis()) {
                    notice.setGisx(GlobalConstant.gis.getGisx());
                    notice.setGisy(GlobalConstant.gis.getGisy());
                    notice.setProvincename(GlobalConstant.gis.getProvincename());
                    notice.setCityname(GlobalConstant.gis.getCityname());
                }
                String areacode = null, areacode2 = null;
                if(StringUtil.isNotEmpty(GlobalConstant.getGis().getAreacode())){
                    areacode = GlobalConstant.getGis().getAreacode();
                    areacode2 = GlobalConstant.getGis().getAreacode2();
                }else{
                    areacode = GlobalConstant.AREA_CODE_HANGZHOU;
                }
                notice.setAreacode(areacode);
                notice.setAreacode2(areacode2);
                req.setNotice(notice);
                azService.doTrans(req, Advice05Response.class, new AzService.Callback<Advice05Response>() {
                    @Override
                    public void success(Advice05Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(7, resp.getBody().getNotice_list());
                        } else {
                            messageHandler.postMessage(8, resp.getResultMessage());
                        }
                    }
                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });

        //请求标签列表信息
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Busi01Request req = new Busi01Request();
                Busi01Request.Label label = req.new Label();
                label.setPlatform(GlobalConstant.TERMINAL_TYPE_ANDROID);
                req.setLabel(label);
                azService.doTrans(req, Busi01Response.class, new AzService.Callback<Busi01Response>() {
                    @Override
                    public void success(Busi01Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(3, resp.getBody().getLabel_list());
                        } else {
                            messageHandler.postMessage(4, resp.getResultMessage());
                        }
                    }
                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });

        //请求推荐位信息
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Busi02Request req = new Busi02Request();
                Busi02Request.Recommend reco = req.new Recommend();
                reco.setPlatform(GlobalConstant.TERMINAL_TYPE_ANDROID);
                reco.setType(GlobalConstant.BUSINESS_TYPE_PARTJOB);
                req.setRecommend(reco);
                Busi02Request.Parttimejob partjob = req.new Parttimejob();
                if(null != GlobalConstant.gis) {
                    partjob.setGisx(GlobalConstant.gis.getGisx());
                    partjob.setGisy(GlobalConstant.gis.getGisy());
                    partjob.setAreacode(GlobalConstant.gis.getAreacode());
                    partjob.setAreacode2(GlobalConstant.gis.getAreacode2());
                    partjob.setProvincename(GlobalConstant.gis.getProvincename());
                    partjob.setCityname(GlobalConstant.gis.getCityname());
                }
                req.setParttimejob(partjob);
                azService.doTrans(req, Busi02Response.class, new AzService.Callback<Busi02Response>() {
                    @Override
                    public void success(Busi02Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(5, resp.getBody().getRecommend_list());
                        } else {
                            messageHandler.postMessage(6, resp.getResultMessage());
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

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3: {
                    List<Busi01Response.Body.Label> list = (List<Busi01Response.Body.Label>) msg.obj;
                    setLabel(list);
                    break;
                }
                case 4: {
                    if (null != msg.obj) {
                        showToast("获取标签信息出错:" + msg.obj);
                    } else {
                        showToast("获取标签信息出错");
                    }
                    break;
                }
                case 5: {
                    List<Busi02Response.Body.Recommend> list = (List<Busi02Response.Body.Recommend>) msg.obj;
                    setRecommand(list);
                    break;
                }
                case 6: {
                    if (null != msg.obj) {
                        showToast("获取推荐位信息出错:" + msg.obj);
                    } else {
                        showToast("获取推荐位信息出错");
                    }
                    break;
                }
                case 7: {
                    List<Advice05Response.Body.Notice> list = (List<Advice05Response.Body.Notice>) msg.obj;
                    setNotice(list);
                    break;
                }
                case 8: {
                    if (null != msg.obj) {
                        showToast("获取推荐位信息出错:" + msg.obj);
                    } else {
                        showToast("获取推荐位信息出错");
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                    break;
                }
            }
        }
    }

    private void setNotice(List<Advice05Response.Body.Notice> list) {
        StringBuffer buff = new StringBuffer();
        for(Advice05Response.Body.Notice notice:list){
            buff.append(notice.getContent()).append(" ");
        }
        am_text.setText(buff.toString());
    }
    private void setLabel(List<Busi01Response.Body.Label> list) {
        for (Busi01Response.Body.Label label : list) {
            View convertView = layoutInflator.inflate(R.layout.index_index_label_item, linearLayout_label, false);
            ImageView iv_logo = (ImageView) convertView.findViewById(R.id.iv_logo);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_text = (TextView) convertView.findViewById(R.id.tv_text);

            tv_name.setText(label.getName());
            tv_text.setText(label.getText());
            bitmapUtil.getImage(iv_logo, label.getImgurl(), R.drawable.logo_merchant_default);

//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            convertView.setLayoutParams(layoutParams);
            linearLayout_label.addView(convertView);

            final String labelid = label.getId();
            final String labelname = label.getName();
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(baseActivity, IndexPartjobListByLabelActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("labelid", labelid);
                    bundle.putString("labelname", labelname);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    private volatile int leftHeight = 0, rightHeight = 0;
    private void setRecommand(List<Busi02Response.Body.Recommend> list) {
        leftHeight = rightHeight = 0;
        int displayWidth = linearlout_right.getWidth();
        for (Busi02Response.Body.Recommend recommend : list) {
//        for (int i = 0; i < list.size(); i++) {
//            Busi02Response.Body.Recommend recommend = list.get(i);
            boolean leftFlag = leftHeight <= rightHeight;
            LinearLayout linearLayout = leftFlag ? linearlout_left : linearlout_right;

            int height = displayWidth;
            if(StringUtil.isNotEmpty(recommend.getWidth()) && !"0".equals(recommend.getWidth())
                    && StringUtil.isNotEmpty(recommend.getHeight()) && !"0".equals(recommend.getHeight())) {
                try {
                    height = displayWidth * Integer.parseInt(recommend.getHeight()) / Integer.parseInt(recommend.getWidth());
                }catch (Exception e){
                }
            }
            //Log.d("==", "leftHeight=" + leftHeight + " -- rightHeight=" + rightHeight + " -- leftFlag=" + leftFlag + " -- height=" + height);
            if(leftFlag){
                leftHeight += height;
            }else{
                rightHeight += height;
            }

            View convertView = layoutInflator.inflate(R.layout.index_index_recommend_item, linearLayout, false);
            ImageView iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            ImageView iv_logo = (ImageView) convertView.findViewById(R.id.iv_logo);
            TextView tv_text = (TextView) convertView.findViewById(R.id.tv_text);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_area = (TextView) convertView.findViewById(R.id.tv_area);

            tv_text.setText(recommend.getName());
            LinearLayout.LayoutParams ivImageLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            iv_image.setLayoutParams(ivImageLayoutParam);
            bitmapUtil.getImage(iv_image, recommend.getImgurl(), R.drawable.logo_merchant_default);
            bitmapUtil.getImage(iv_logo, recommend.getLogopath(), R.drawable.logo_merchant_default);
            tv_text.setText(recommend.getText());
            tv_name.setText(recommend.getName());
            tv_area.setText(recommend.getAreaname());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5 * pixelOf1dp, 0, 0);
            convertView.setLayoutParams(layoutParams);
            linearLayout.addView(convertView);

            final String partjobid = recommend.getId();
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(baseActivity, PartjobDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("partjobid", partjobid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }




    @Override
    public void onStart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每3秒钟切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            private int currentItem = 0;

            public void run() {
                currentItem = (currentItem + 1) % pageViews.length;
                messageHandler.postMessage(9, currentItem);
            }
        }, 3, 3, TimeUnit.SECONDS);
        super.onStop();
    }

    @Override
    public void onStop() {
        // 当Activity不可见的时候停止切换
        scheduledExecutorService.shutdown();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
