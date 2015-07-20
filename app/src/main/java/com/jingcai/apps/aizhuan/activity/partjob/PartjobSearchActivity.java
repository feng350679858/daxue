package com.jingcai.apps.aizhuan.activity.partjob;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.partjob.PartjobListAdapter;
import com.jingcai.apps.aizhuan.adapter.partjob.PartjobSearchAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob09.Partjob09Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob09.Partjob09Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.jingcai.apps.aizhuan.view.MultiDirectionSlidingDrawer;
import com.markmao.pulltorefresh.widget.XListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PartjobSearchActivity extends BaseActivity {
    private static final String TAG = PartjobSearchActivity.class.getName();
    private MessageHandler messageHandler;
    private AzService azService;
    private PartjobSearchAdapter searchAdapter;
    private ListView listview_search_normal;//普通搜索使用
    private View linear_search_special;//特殊搜索使用
    private int mCurrentStart = 0;
    private XListView partjobListView;
    private View layout_empty;
    private PartjobListAdapter partjobListAdapter;
    private MultiDirectionSlidingDrawer mDrawer;
    private LinearLayout layout_gender_limit, layout_worktype_limit, layout_area_limit, layout_search_all;
    private ImageView iv_address, iv_gender_limit, iv_worktype_limit, iv_area_limit, iv_search_all;
    private TextView tv_gender_limit, tv_worktype_limit, tv_area_limit, tv_search_all;
    private String lastSelectedWayFlag = null;
    private String currentAreacode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);
        setContentView(R.layout.activity_partjob_search);
        initHeader();
        initView();
        initDate();


    }
    private EditText mTxtSearchKey;
    private void initHeader(){
        ((ImageButton)findViewById(R.id.partjob_search_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtSearchKey.setText("");
                doReSearch();
            }
        });
    }
    private void initView() {

        //将搜索关键字输入editText中
        mTxtSearchKey = (EditText)findViewById(R.id.index_pj_search_content);
        mTxtSearchKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH/* && null!=event && event.getKeyCode()==KeyEvent.KEYCODE_ENTER*/) {
                    String mSearchKey = mTxtSearchKey.getText().toString();
                    if (StringUtil.isNotEmpty(mSearchKey)) {
                        Preferences pref = Preferences.getInstance(Preferences.TYPE.partjob);
                        int index = pref.getInt(Preferences.Partjob.SEARCH_INDEX, 0);
                        pref.update(Preferences.Partjob.SEARCH_KEY_PREFIX + index, mSearchKey);
                        pref.update(Preferences.Partjob.SEARCH_INDEX, ++index);

                        hideInputMethodDialog(PartjobSearchActivity.this);
                    }
                    if (mDrawer.isOpened()) {
                        mDrawer.animateClose();
                        changeArrowDirection(getSearchViewArrow(lastSelectedWayFlag), false);
                    }
                    doReSearch();
                }
                return false;
            }
        });

        mDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.drawer);
        partjobListView = (XListView)findViewById(R.id.index_pj_list_lv2);
        layout_empty = findViewById(R.id.layout_empty);

        //检索部分
        listview_search_normal = (ListView) findViewById(R.id.listview_search_normal);
        linear_search_special = findViewById(R.id.linear_search_special);

        layout_gender_limit = (LinearLayout) findViewById(R.id.layout_gender_limit);
        layout_worktype_limit = (LinearLayout) findViewById(R.id.layout_worktype_limit);
        layout_area_limit = (LinearLayout) findViewById(R.id.layout_area_limit);
        layout_search_all = (LinearLayout) findViewById(R.id.layout_search_all);//特殊搜索


        layout_gender_limit.setOnClickListener(new SearchNormalClickListener("0"));
        layout_worktype_limit.setOnClickListener(new SearchNormalClickListener("1"));
        layout_area_limit.setOnClickListener(new SearchNormalClickListener("2"));
        layout_search_all.setOnClickListener(new SearchNormalClickListener("-1"));//特殊搜索

        iv_gender_limit = (ImageView) findViewById(R.id.iv_gender_limit);
        iv_worktype_limit = (ImageView) findViewById(R.id.iv_worktype_limit);
        iv_area_limit = (ImageView) findViewById(R.id.iv_area_limit);
        iv_search_all = (ImageView) findViewById(R.id.iv_search_all);//特殊搜索

        tv_gender_limit = (TextView) findViewById(R.id.tv_gender_limit);
        tv_worktype_limit = (TextView) findViewById(R.id.tv_worktype_limit);
        tv_area_limit = (TextView) findViewById(R.id.tv_area_limit);
        tv_search_all = (TextView) findViewById(R.id.tv_search_all);//特殊搜索

        SearchSpecialClickListener searchSpecialClickListener = new SearchSpecialClickListener();
        View search_special_0 = findViewById(R.id.search_special_0);//特殊搜索-全部
        View search_special_1 = findViewById(R.id.search_special_1);//特殊搜索-老东家
        View search_special_2 = findViewById(R.id.search_special_2);//特殊搜索-周末汇
        View search_special_3 = findViewById(R.id.search_special_3);//特殊搜索-紧急令

        search_special_0.setOnClickListener(searchSpecialClickListener);
        search_special_1.setOnClickListener(searchSpecialClickListener);
        search_special_2.setOnClickListener(searchSpecialClickListener);
        search_special_3.setOnClickListener(searchSpecialClickListener);

//        mDrawer.setOnClickListener();
        mDrawer.setOnDrawerCloseListener(new MultiDirectionSlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                partjobListView.setEnabled(true);
//                Drawable d = tv_gender_limit.getCompoundDrawables()[2];
//                float h2 = 180f;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    imgShowAward.setRotation(h2);
//                } else {
//                    AnimatorProxy.wrap(imgShowAward).setRotation(h2);
//                }
//                alphaBackground2(imgShowAward, false);
                alphaBackground(false);
            }
        });
        mDrawer.setOnDrawerOpenListener(new MultiDirectionSlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                partjobListView.setEnabled(false);
                alphaBackground(true);
            }
        });

        searchAdapter = new PartjobSearchAdapter(this);
        searchAdapter.setCallback(normalSearchCallback);
        listview_search_normal.setAdapter(searchAdapter);

        partjobListAdapter = new PartjobListAdapter(PartjobListAdapter.AdapterType.PartjobList, this);
        partjobListView.setAdapter(partjobListAdapter);
        partjobListView.setPullRefreshEnable(true);
        partjobListView.setPullLoadEnable(true);
        partjobListView.setXListViewListener(listViewListener);
        partjobListView.setAutoLoadEnable(true);
        partjobListView.setRefreshTime(getTime());
        partjobListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PartjobListAdapter.ViewHolder holder = (PartjobListAdapter.ViewHolder) view.getTag();
                if (null != holder && null != holder.getPartjob()) {
                    Partjob09Response.Body.Parttimejob partjob = holder.getPartjob();
                    Intent intent = new Intent(PartjobSearchActivity.this, PartjobDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("partjobid", partjob.getId());
                    bundle.putString("logopath", partjob.getLogopath());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        searchFlag = false;
        //changeSearchStatus();
    }

    private final int REQUEST_CODE_ADDRESS = 11;//切换城市
    private final int REQUEST_CODE_SEARCH = 12;//检索

    private void initDate(){
        currentAreacode = getIntent().getStringExtra("address");
        searchAdapter.initAreaCode(currentAreacode);
        initListData();
    }

    private void alphaBackground(boolean openFlag){
        AnimatorSet alphaAnimation = new AnimatorSet();
        if(openFlag) {
            alphaAnimation.playTogether(ObjectAnimator.ofFloat(partjobListView, "alpha", 0.25f));
            alphaAnimation.playTogether(ObjectAnimator.ofFloat(layout_empty, "alpha", 0.25f));
        }else{
            alphaAnimation.playTogether(ObjectAnimator.ofFloat(partjobListView, "alpha", 1.0f));
            alphaAnimation.playTogether(ObjectAnimator.ofFloat(layout_empty, "alpha", 1.0f));
        }
        //alphaAnimation.playTogether(alpha_menu);
        alphaAnimation.setDuration(250);
        alphaAnimation.start();
    }
    private void changeArrowDirection(View view, boolean openFlag){
        if(null == view){
            return;
        }
        AnimatorSet alphaAnimation = new AnimatorSet();
        if(openFlag) {
            alphaAnimation.playTogether(ObjectAnimator.ofFloat(view, "rotation", 180f));
        }else{
            alphaAnimation.playTogether(ObjectAnimator.ofFloat(view, "rotation", 0f));
        }
        //alphaAnimation.playTogether(alpha_menu);
        alphaAnimation.setDuration(250);
        alphaAnimation.start();
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    private XListView.IXListViewListener listViewListener = new XListView.IXListViewListener(){
        @Override
        public void onRefresh() {
            messageHandler.post(new Runnable() {
                @Override
                public void run() {
                    doReSearch();
                }
            });
        }
        @Override
        public void onLoadMore() {
            messageHandler.post(new Runnable() {
                @Override
                public void run() {
                    initListData();
                }
            });
        }
    };

    private void onLoad() {
        partjobListView.stopRefresh();
        partjobListView.stopLoadMore();
        partjobListView.setRefreshTime(getTime());
    }



    private void doReSearch() {
        partjobListAdapter.clearData();
        mCurrentStart = 0;
        partjobListView.setPullLoadEnable(true);
        initListData();
    }
    private void initListData() {
        if(actionLock.tryLock()) {
            showProgressDialog("列表加载中...");
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final Partjob09Request req = new Partjob09Request();
                    final Partjob09Request.Parttimejob job = req.new Parttimejob();
                    if (GlobalConstant.gis.hasGis()) {
                        job.setGisx(GlobalConstant.gis.getGisx());
                        job.setGisy(GlobalConstant.gis.getGisy());
                        job.setProvincename(GlobalConstant.gis.getProvincename());
                        job.setCityname(GlobalConstant.gis.getCityname());
                        job.setDistrictname(GlobalConstant.gis.getDistrictname());
                    }
                    job.setAreacode(currentAreacode);
                    job.setAreacode2((String) tv_area_limit.getTag());
                    job.setGender((String) tv_gender_limit.getTag());
                    job.setWorktype((String) tv_worktype_limit.getTag());
                    job.setKeys(mTxtSearchKey.getText().toString());
                    job.setType((String) tv_search_all.getTag());
                    job.setStudentid(UserSubject.getStudentid());

                    job.setStart(String.valueOf(mCurrentStart));
                    job.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    req.setParttimejob(job);
                    azService.doTrans(req, Partjob09Response.class, new AzService.Callback<Partjob09Response>() {
                        @Override
                        public void success(Partjob09Response response) {
                            ResponseResult result = response.getResult();
                            if ("0".equals(result.getCode())) {
                                Partjob09Response.Body body = response.getBody();
                                List<Partjob09Response.Body.Parttimejob> messageList = body.getParttimejob_list();
                                if(0 == mCurrentStart && messageList.size()<1){
                                    messageHandler.postMessage(2);
                                }else {
                                    messageHandler.postMessage(0, messageList);
                                }
                            } else {
                                messageHandler.postMessage(1, result.getMessage());
                            }
                        }

                        @Override
                        public void fail(AzException e) {
                            try {
                                messageHandler.postException(e);
                            }finally {
                                actionLock.unlock();
                            }
                        }
                    });
                }
            });
        }
    }

    class MessageHandler extends BaseHandler{
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what){
                case 0: {
                    try {
                        List<Partjob09Response.Body.Parttimejob> list = (List<Partjob09Response.Body.Parttimejob>) msg.obj;
                        partjobListAdapter.addData(list);
                        partjobListAdapter.notifyDataSetChanged();

                        if(0 == mCurrentStart){
                            partjobListView.setVisibility(View.VISIBLE);
                            layout_empty.setVisibility(View.GONE);
                        }
                        mCurrentStart += list.size();
                        onLoad();

                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            partjobListView.setPullLoadEnable(false);
                        }
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try{
                        showToast("获取消息失败:" + msg.obj);
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try{
                        partjobListView.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                    }finally {
                        actionLock.unlock();
                    }
                    break;
                }
                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }

    private class SearchSpecialClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.search_special_0:{
                    tv_search_all.setText("全部");
                    tv_search_all.setTag(null);
                    break;
                }
                case R.id.search_special_1:{
                    if(checkAndPerformLogin()) {
                        tv_search_all.setText("老东家");
                        tv_search_all.setTag("1");
                        break;
                    }else{
                        return;
                    }
                }
                case R.id.search_special_2:{
                    tv_search_all.setText("周末汇");
                    tv_search_all.setTag("2");
                    break;
                }
                case R.id.search_special_3:{
                    tv_search_all.setText("紧急令");
                    tv_search_all.setTag("3");
                    break;
                }
            }
            mDrawer.animateClose();
            changeArrowDirection(iv_search_all, false);
            // 检索 search
            doReSearch();
        }
    }

    @Override
    protected void afterLoginSuccess() {
        tv_search_all.setText("老东家");
        tv_search_all.setTag("1");
        mDrawer.animateClose();
        changeArrowDirection(iv_search_all, false);
        // 检索 search
        doReSearch();
    }

    private class SearchNormalClickListener implements View.OnClickListener {
        private int length = getResources().getDimensionPixelSize(R.dimen.margin_1dp);
        private String wayFlag;

        public SearchNormalClickListener(String wayFlag){
            this.wayFlag = wayFlag;
        }
        @Override
        public void onClick(View v) {
            if(wayFlag.equals(lastSelectedWayFlag)){
                if (mDrawer.isOpened()) {
                    mDrawer.animateClose();
                    changeArrowDirection(getSearchViewArrow(wayFlag), false);
                    return;
                }else{
//                    mDrawer.animateOpen();
                }
            }else{
                changeArrowDirection(getSearchViewArrow(lastSelectedWayFlag), false);
            }
            FrameLayout.LayoutParams layoutParam = null;
            if("-1".equals(wayFlag)){
                listview_search_normal.setVisibility(View.GONE);
                linear_search_special.setVisibility(View.VISIBLE);
                //显示特殊搜索，隐藏通用搜索
                layoutParam = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, length * 120);
            }else{
                listview_search_normal.setVisibility(View.VISIBLE);
                linear_search_special.setVisibility(View.GONE);
                //隐藏特殊搜索，显示通用搜索
                layoutParam = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, length * ("0".equals(wayFlag)?153:240));

                searchAdapter.changeWayFlag(wayFlag);
            }
            //layoutParam.setMargins(0, getResources().getDimensionPixelSize(R.dimen.index_partjob_list_handle_bottom), 0, 0);
            mDrawer.setLayoutParams(layoutParam);
            if (!mDrawer.isOpened()) {
                mDrawer.animateOpen();
            }
            changeArrowDirection(getSearchViewArrow(wayFlag), true);
            lastSelectedWayFlag = wayFlag;
        }
    }

    private View getSearchViewArrow(String wayFlag) {
        if("0".equalsIgnoreCase(wayFlag)){
            return iv_gender_limit;
        }else if("1".equalsIgnoreCase(wayFlag)){
            return iv_worktype_limit;
        }else if("2".equalsIgnoreCase(wayFlag)){
            return iv_area_limit;
        }else if("-1".equalsIgnoreCase(wayFlag)){
            return iv_search_all;
        }
        return null;
    }

    private PartjobSearchAdapter.Callback normalSearchCallback = new PartjobSearchAdapter.Callback() {
        @Override
        public String getSelectedKey(String wayflag) {
            Object val = null;
            if ("0".equals(wayflag)) {
                val = tv_gender_limit.getTag();
            } else if ("1".equals(wayflag)) {
                val = tv_worktype_limit.getTag();
            } else if ("2".equals(wayflag)) {
                val = tv_area_limit.getTag();
            }
            if (null != val) {
                return String.valueOf(val);
            }
            return "";
        }

        @Override
        public void selectOption(String wayflag, String key, String value) {
            if ("0".equals(wayflag)) {
                tv_gender_limit.setText(value);
                tv_gender_limit.setTag(key);
                changeArrowDirection(iv_gender_limit, false);
            } else if ("1".equals(wayflag)) {
                tv_worktype_limit.setText(value);
                tv_worktype_limit.setTag(key);
                changeArrowDirection(iv_worktype_limit, false);
            } else if ("2".equals(wayflag)) {//选择区域编码
                tv_area_limit.setText(value);
                tv_area_limit.setTag(key);
                changeArrowDirection(iv_area_limit, false);
            }
            mDrawer.animateClose();
            // 检索
            doReSearch();
        }
    };
    private boolean searchFlag;
  /*  private void changeSearchStatus(){
        if(searchFlag){
            rl_index_partjob_normal_head.setVisibility(View.GONE);
            rl_index_partjob_search_head.setVisibility(View.VISIBLE);
            ((MainFragment) getParentFragment()).setTabHostVisiable(View.GONE);
        }else{
            rl_index_partjob_normal_head.setVisibility(View.VISIBLE);
            rl_index_partjob_search_head.setVisibility(View.GONE);
            ((MainFragment) getParentFragment()).setTabHostVisiable(View.VISIBLE);
        }
    }*/


}
