package com.jingcai.apps.aizhuan.activity.index.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
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
 * Created by cfy on 2015/4/17.
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
    private TextView tv_address;
    private View mainView;
    private ViewPager viewPager;
    private ViewGroup viewGroup;
    private AutoMarqueeTextView am_text;
    private ImageView[] pageViews, imageDots;
    private LinearLayout linearLayout_label, linearlout_left, linearlout_right;
    private final int REQUEST_CODE_ADDRESS = 11;//切换城市
    private int pixelOf1dp;
    String currentAreacode = null, currentAreaname = null;
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
    public  void onResume(){
        super.onResume();
      //  initHeader();
    }
    private void initHeader(){
        ((ImageView)mainView.findViewById(R.id.ib_back)).setVisibility(View.INVISIBLE);
        ((TextView)mainView.findViewById(R.id.tv_content)).setText("爱赚");
        ((TextView)mainView.findViewById(R.id.tv_content)).setVisibility(View.VISIBLE);
        ((ImageView)mainView.findViewById(R.id.iv_bird_badge)).setVisibility(View.INVISIBLE);
        ((ImageView)mainView.findViewById(R.id.iv_func)).setImageDrawable(getResources().getDrawable(R.drawable.search));
        ((ImageView)mainView.findViewById(R.id.iv_func)).setVisibility(View.VISIBLE);
        ((TextView)mainView.findViewById(R.id.tv_func)).setVisibility(View.INVISIBLE);
        tv_address=(TextView)mainView.findViewById(R.id.tv_back);
        tv_address.setVisibility(View.VISIBLE);

        if(StringUtil.isNotEmpty(GlobalConstant.getGis().getAreacode())){
            currentAreacode = GlobalConstant.getGis().getAreacode();
            currentAreaname = GlobalConstant.getGis().getAreaname();
        }else{
            currentAreacode = GlobalConstant.AREA_CODE_HANGZHOU;
            currentAreaname = GlobalConstant.AREA_NAME_HANGZHOU;
        }
        tv_address.setText(currentAreaname);
        tv_address.setTag(currentAreacode);

        searchAdapter = new PartjobSearchAdapter(baseActivity);
        searchAdapter.initAreaCode(currentAreacode);
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationCityActivity.class);
                intent.putExtra("address",currentAreacode);
                startActivityForResult(intent, REQUEST_CODE_ADDRESS);
            }
        });
        ((ImageView)mainView.findViewById(R.id.iv_func)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PartjobSearchActivity.class);
                intent.putExtra("address",currentAreacode);
                startActivity(intent);
            }
        });
    }
    private void initView() {


        viewPager = (ViewPager) mainView.findViewById(R.id.img_advert);
        viewGroup = (ViewGroup) mainView.findViewById(R.id.viewGroup);

        am_text = (AutoMarqueeTextView)mainView.findViewById(R.id.am_text);

        linearLayout_label = (LinearLayout) mainView.findViewById(R.id.linearLayout_label);
        linearlout_left = (LinearLayout) mainView.findViewById(R.id.linearlout_left);
        linearlout_right = (LinearLayout) mainView.findViewById(R.id.linearlout_right);


        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());


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
    private void initData() {
        //请求广告位信息
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Base01Request req = new Base01Request();
                Base01Request.Banner banner = req.new Banner();
                banner.setPlatform(GlobalConstant.TERMINAL_TYPE_ANDROID);
                req.setBanner(banner);
                azService.doTrans(req, Base01Response.class, new AzService.Callback<Base01Response>() {
                    @Override
                    public void success(Base01Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            messageHandler.postMessage(1, resp.getBody().getBanner_list());
                        } else {
                            messageHandler.postMessage(2, resp.getResultMessage());
                        }
                    }
                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        });

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
                case 1: {
                    List<Base01Response.Body.Banner> list = (List<Base01Response.Body.Banner>) msg.obj;
                    setAdverts(list);
                    break;
                }
                case 2: {
                    if (null != msg.obj) {
                        showToast("获取banner信息出错:" + msg.obj);
                    } else {
                        showToast("获取banner信息出错");
                    }
                    break;
                }
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
                case 9: {
                    // 切换当前显示的图片
                    viewPager.setCurrentItem((int) msg.obj);
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

            tv_name.setText(label.getName()+"：");
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

    /**
     * 指引页面Adapter
     */
    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return null == pageViews ? 0 : pageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
            arg0.removeView(pageViews[arg1]);
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            View view = pageViews[arg1];
            arg0.addView(view);
            Base01Response.Body.Banner banner = (Base01Response.Body.Banner) view.getTag();

            final String linkUrl = banner.getRedirecturl();
            final String title = banner.getTitle();
            pageViews[arg1].setOnClickListener(new View.OnClickListener() {
                public void onClick(final View arg0) {
                    if (StringUtil.isEmpty(linkUrl)) return;
                    Bundle bundle = new Bundle();
                    bundle.putString("title", title);
                    bundle.putString("url", linkUrl);

                    Intent intent = new Intent(arg0.getContext(), BannerDetailActivity.class);
                    intent.putExtras(bundle);
                    arg0.getContext().startActivity(intent);
                }
            });
            return view;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }


    /**
     * 指引页面改监听器
     */
    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < imageDots.length; i++) {
                imageDots[arg0].setBackgroundResource(R.drawable.page_indicator_focused);
                if (arg0 != i) {
                    imageDots[i].setBackgroundResource(R.drawable.page_indicator);
                }
            }
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


    public void setAdverts(List<Base01Response.Body.Banner> adverts) {
        {
            pageViews = new ImageView[adverts.size()];
            for (int i = 0; i < adverts.size(); i++) {
                Base01Response.Body.Banner banner = adverts.get(i);
                ImageView imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setTag(banner);

                pageViews[i] = imageView;

                //添加下载任务
                //new DownloadTask().execute(adverts.get(i).getImgUrl(), imageView);
                bitmapUtil.getImage(imageView, banner.getImgurl());
            }
        }
        {
            imageDots = new ImageView[adverts.size()];
            for (int i = 0; i < adverts.size(); i++) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
                imageView.setPadding(20, 0, 20, 0);
                imageView.setBackgroundResource(i == 0 ? R.drawable.page_indicator_focused : R.drawable.page_indicator);//默认选中第一张图片
                viewGroup.addView(imageView);
                imageDots[i] = imageView;
//			group.getChildAt(i)
            }
        }
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
