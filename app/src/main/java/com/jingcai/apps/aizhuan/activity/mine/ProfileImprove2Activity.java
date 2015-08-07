package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.SchoolListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.school.school04.School04Request;
import com.jingcai.apps.aizhuan.service.business.school.school04.School04Response;
import com.jingcai.apps.aizhuan.service.business.school.school05.School05Request;
import com.jingcai.apps.aizhuan.service.business.school.school05.School05Response;
import com.jingcai.apps.aizhuan.service.business.school.school06.School06Request;
import com.jingcai.apps.aizhuan.service.business.school.school06.School06Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu03.Stu03Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.StringUtil;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfileImprove2Activity extends BaseActivity {
    public static final String TAG = "ProfileImprove2Activity";
    private TextInputLayout school, college, professional, joindate;
    private EditText school_input, college_input, professional_input, joindate_input;
    private Button next;
    private XListView mListSchool, mListProfessional;
    private SchoolListAdapter mListSchoolAdapter, mListProfessionalAdapter;
    private PopupWin college_popupWin, joindate_popupWin,connectionWin;

    private AzService azService;
    private String mSchoolSearchKey = "";
    private String mProfessionalSearchKey = "";
    private MessageHandler messageHandler;
    private int mSchoolCurrentStart = 0;  //当前的开始
    private int mProfessionalCurrentStart = 0;
    private int flag = 4;
    private boolean mSchoolSelected, mProfessionalSelected, isempty;

    private MyTextWatcher myTextWatcher=new MyTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_improve2);
        messageHandler = new MessageHandler(this);
        azService = new AzService(this);
        initHeader();
        initView();
        initDate();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("资料完善");
        findViewById(R.id.iv_bird_badge).setVisibility(View.GONE);
        findViewById(R.id.iv_func).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回主界面
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_func)).setText("联系客服");
        ((TextView) findViewById(R.id.tv_func)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_func)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == connectionWin) {
                    View parentView = ProfileImprove2Activity.this.getWindow().getDecorView();
                    View contentView = LayoutInflater.from(ProfileImprove2Activity.this).inflate(R.layout.comfirm_contact_merchant_dialog, null);

                    connectionWin = PopupWin.Builder.create(ProfileImprove2Activity.this)
                            .setParentView(parentView)
                            .setContentView(contentView)
                            .build();
                    //logo
                    ((ImageView) contentView.findViewById(R.id.iv_contact_merchant_dialog_logo)).setImageDrawable(getResources().getDrawable(R.drawable.logo_merchant_default));
                    //title
                    ((TextView) contentView.findViewById(R.id.tv_contact_merchant_dialog_title)).setText("联系人");
                    //phone
                    final TextView phone = (TextView) contentView.findViewById(R.id.tv_contact_merchant_dialog_phone);
                    phone.setText("15712345678");
                    //2 button
                    contentView.findViewById(R.id.btn_confirm_false).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            connectionWin.dismiss();
                        }
                    });
                    contentView.findViewById(R.id.btn_confirm_true).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + phone.getText().toString()));
                            startActivity(intent);
                        }
                    });
                }
                connectionWin.show();
            }
        });
    }

    private void initView() {
        school = (TextInputLayout) findViewById(R.id.profile_improve_school);
        school_input = school.getEditText();
   //     school_input.addTextChangedListener(myTextWatcher);
        college = (TextInputLayout) findViewById(R.id.profile_improve_college);
        college_input = college.getEditText();
        college_input.addTextChangedListener(myTextWatcher);
        professional = (TextInputLayout) findViewById(R.id.profile_improve_professional);
        professional_input = professional.getEditText();
    //    professional_input.addTextChangedListener(myTextWatcher);
        joindate = (TextInputLayout) findViewById(R.id.profile_improve_joindate);
        joindate_input = joindate.getEditText();
        joindate_input.addTextChangedListener(myTextWatcher);
        //学校列表
        mListSchool = (XListView) findViewById(R.id.lv_mine_profile_improve_school);
        mListSchool.setPullRefreshEnable(false);
        mListSchool.setPullLoadEnable(true);


        mListSchool.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initSchoolData(mSchoolSearchKey);
            }
        });
        mListSchool.setAutoLoadEnable(true);
        mListSchoolAdapter = new SchoolListAdapter(this);
        mListSchoolAdapter.isShowIndicator(false);

        mListSchool.setAdapter(mListSchoolAdapter);

        mListSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> listData = mListSchoolAdapter.getListData(position - 1);
                String name = listData.get("name");
                school_input.setTag(listData.get("schoolid"));
                mSchoolSelected = true;
                mSchoolSearchKey = name;
                school_input.setText(name);
                clearAllFocus();
            }
        });
        school_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if ("".equals(s.toString()))
                    isempty = true;
                else
                    isempty = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("".equals(s.toString()) && !isempty)
                    flag++;
                if (!"".equals(s.toString()) && isempty)
                    flag--;
                if (0 == flag)
                    next.setEnabled(true);
                else
                    next.setEnabled(false);
                System.out.println(flag);
                int length = s.length();
                if (length >= 2) {
                    if (mSchoolSelected && mSchoolSearchKey.equals(s.toString())) {
                        mListSchoolAdapter.clearListData();
                        mListSchool.setVisibility(View.GONE);
                        return;
                    }

                    //文字长度大于2,清空列表
                    resetSchoolLoad();
                    mSchoolSearchKey = s.toString();
                    initSchoolData(mSchoolSearchKey);
                }

            }


        });
    college_input.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearAllFocus();
            college_input.requestFocus();
            if (!"".equals(school_input.getText().toString())) {
                college_input.requestFocus();
                initCollegePopupWin();
            }
        }
    });
//        college_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus && !"".equals(school_input.getText().toString())) {
//                    initCollegePopupWin();
//
//                }
//            }
//        });
        //专业列表
        mListProfessional = (XListView) findViewById(R.id.lv_mine_profile_improve_professional);
        mListProfessional.setPullRefreshEnable(false);
        mListProfessional.setPullLoadEnable(true);
        mListProfessional.setAutoLoadEnable(true);
        mListProfessional.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                initProfessionalData();
            }
        });
        //重用SchoolListAdapter
        mListProfessionalAdapter = new SchoolListAdapter(this);
        mListProfessionalAdapter.isShowIndicator(false);
        mListProfessional.setAdapter(mListProfessionalAdapter);
        mListProfessional.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> listData = mListProfessionalAdapter.getListData(position - 1);
                String name = listData.get("name");
                mProfessionalSelected = true;
                mProfessionalSearchKey = name;
                professional_input.setText(name);
                clearAllFocus();
            }
        });
        professional_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if ("".equals(s.toString()))
                    isempty = true;
                else
                    isempty = false;

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("".equals(s.toString()) && !isempty)
                    flag++;
                if (!"".equals(s.toString()) && isempty)
                    flag--;
                if (0 == flag)
                    next.setEnabled(true);
                else
                    next.setEnabled(false);
                System.out.println(flag);
                int length = s.length();
                if (length >= 1) {
                    if (mProfessionalSelected && mProfessionalSearchKey.equals(s.toString())) {
                        mListProfessionalAdapter.clearListData();
                        mListProfessional.setVisibility(View.GONE);
                        return;
                    }

                    //文字长度大于2,清空列表
                    resetProfessionalLoad();
                    mProfessionalSearchKey = s.toString();
                    initProfessionalData();
                }
            }
        });
        joindate_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFocus();
                joindate_input.requestFocus();
                if (joindate_popupWin == null) {
                    View parentView = ProfileImprove2Activity.this.getWindow().getDecorView();
                    View contentView = LayoutInflater.from(ProfileImprove2Activity.this).inflate(R.layout.mine_profile_improve_joindate_dialog, null);

                    joindate_popupWin = PopupWin.Builder.create(ProfileImprove2Activity.this)
                            .setParentView(parentView)
                            .setContentView(contentView)
                            .build();
                    ListView listView = (ListView) contentView.findViewById(R.id.lv_mine_profile_improve_joindate);
                    SchoolListAdapter adapter = new SchoolListAdapter(ProfileImprove2Activity.this);
                    adapter.isShowIndicator(false);
                    adapter.setTextGravity(Gravity.CENTER);

                    final List<Map<String, String>> years = new ArrayList<>();
                    for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 2000; i--) {
                        Map<String, String> year = new HashMap<>();
                        year.put("code", String.valueOf(i));
                        year.put("name", String.valueOf(i));
                        years.add(year);
                        clearAllFocus();
                    }

                    adapter.setListData(years);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selected = years.get(position).get("name");
                            joindate_input.setText(selected);
                            joindate_popupWin.dismiss();
                            clearAllFocus();
                        }
                    });

                }
                joindate_popupWin.show();

            }
        });
//        joindate_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    if (joindate_popupWin == null) {
//                        View parentView = ProfileImprove2Activity.this.getWindow().getDecorView();
//                        View contentView = LayoutInflater.from(ProfileImprove2Activity.this).inflate(R.layout.mine_profile_improve_joindate_dialog, null);
//
//                        joindate_popupWin = PopupWin.Builder.create(ProfileImprove2Activity.this)
//                                .setParentView(parentView)
//                                .setContentView(contentView)
//                                .build();
//                        ListView listView = (ListView) contentView.findViewById(R.id.lv_mine_profile_improve_joindate);
//                        SchoolListAdapter adapter = new SchoolListAdapter(ProfileImprove2Activity.this);
//                        adapter.isShowIndicator(false);
//                        adapter.setTextGravity(Gravity.CENTER);
//
//                        final List<Map<String, String>> years = new ArrayList<>();
//                        for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 2000; i--) {
//                            Map<String, String> year = new HashMap<>();
//                            year.put("code", String.valueOf(i));
//                            year.put("name", String.valueOf(i));
//                            years.add(year);
//                        }
//
//                        adapter.setListData(years);
//                        listView.setAdapter(adapter);
//
//                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                String selected = years.get(position).get("name");
//                                joindate_input.setText(selected);
//                                joindate_popupWin.dismiss();
//                            }
//                        });
//
//                    }
//                    joindate_popupWin.show();
//                }
//            }
//
//        });
        next = (Button) findViewById(R.id.profile_improve_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });
    }
private void clearAllFocus(){
    school_input.clearFocus();
    college_input.clearFocus();
    professional_input.clearFocus();
    joindate_input.clearFocus();
}
    private void initDate() {
        if (null != getIntent().getStringExtra("school") && !"".equals(getIntent().getStringExtra("school"))) {
            school_input.setTag(getIntent().getStringExtra("school"));
            school_input.setEnabled(false);
            mListSchool.setVisibility(View.GONE);
        }
        if (null != getIntent().getStringExtra("schoolname") && !"".equals(getIntent().getStringExtra("schoolname"))) {
            school_input.setText(getIntent().getStringExtra("schoolname"));
            school_input.setEnabled(false);
            mListSchool.setVisibility(View.GONE);
        }
        if (null != getIntent().getStringExtra("college") && !"".equals(getIntent().getStringExtra("college"))) {
            college_input.setTag(getIntent().getStringExtra("college"));
            college_input.setEnabled(false);
        }
        if (null != getIntent().getStringExtra("collegename") && !"".equals(getIntent().getStringExtra("collegename"))) {
            college_input.setText(getIntent().getStringExtra("collegename"));
            college_input.setEnabled(false);
        }
        if (null != getIntent().getStringExtra("professional") && !"".equals(getIntent().getStringExtra("professional"))) {
            professional_input.setText(getIntent().getStringExtra("professional"));
            professional_input.setEnabled(false);
            mListProfessional.setVisibility(View.GONE);
        }
        if (null != getIntent().getStringExtra("joindate") && !"".equals(getIntent().getStringExtra("joindate"))) {
            joindate_input.setText(getIntent().getStringExtra("joindate"));
            joindate_input.setEnabled(false);
        }
        clearAllFocus();
    }

    private void complete() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu03Request req = new Stu03Request();
                Stu03Request.Student stu = req.new Student();
                stu.setStudentid(UserSubject.getStudentid());
                stu.setName(getIntent().getStringExtra("name"));
                stu.setGender(getIntent().getStringExtra("gender"));
                stu.setAreacode(getIntent().getStringExtra("areacode"));
                stu.setEmail(getIntent().getStringExtra("email"));
                stu.setQq(getIntent().getStringExtra("qq"));
                stu.setSchool(school_input.getTag().toString());
                stu.setJoindate(joindate_input.getText().toString());
                stu.setCollege(college_input.getTag().toString());
                stu.setProfessional(professional_input.getText().toString());
                stu.setPromotioncode(getIntent().getStringExtra("promotioncode"));
                req.setStudent(stu);
                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(6, result.getMessage());
                        } else {
                            messageHandler.postMessage(7);
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

    private void initCollegePopupWin() {
        if(school_input.getTag()==null)
            return;
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                School05Request req = new School05Request();
                School05Request.Schoolinfo schoolinfo = req.new Schoolinfo();

                schoolinfo.setSchoolid(school_input.getTag().toString());

                req.setSchoolinfo(schoolinfo);
                azService.doTrans(req, School05Response.class, new AzService.Callback<School05Response>() {
                    @Override
                    public void success(School05Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            School05Response.Body body = resp.getBody();
                            List<School05Response.Body.College> college_list = body.getCollege_list();
                            messageHandler.postMessage(2, college_list);
                        } else {
                            messageHandler.postMessage(3);
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

    private void resetSchoolLoad() {
        mSchoolCurrentStart = 0;
        mListSchool.setVisibility(View.VISIBLE);
        mListSchoolAdapter.clearListData();
        mListSchool.setPullLoadEnable(true);
    }

    private void initSchoolData(final String name) {
        if (StringUtil.isNotEmpty(name)) {
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    School04Request req = new School04Request();
                    School04Request.Schoolinfo schoolinfo = req.new Schoolinfo();

                    schoolinfo.setName(name);
                    schoolinfo.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    schoolinfo.setStart(String.valueOf(mSchoolCurrentStart));
                    req.setSchoolinfo(schoolinfo);

                    azService.doTrans(req, School04Response.class, new AzService.Callback<School04Response>() {
                        @Override
                        public void success(School04Response resp) {
                            ResponseResult result = resp.getResult();
                            if ("0".equals(result.getCode())) {
                                List<School04Response.Body.Schoolinfo> schoolinfo_list = resp.getBody().getSchoolinfo_list();
                                messageHandler.postMessage(0, schoolinfo_list);
                            } else {
                                messageHandler.postMessage(1);
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
    }

    private void resetProfessionalLoad() {
        mProfessionalCurrentStart = 0;
        mListProfessional.setVisibility(View.VISIBLE);
        mListProfessionalAdapter.clearListData();
        mListProfessional.setPullLoadEnable(true);
    }

    private void initProfessionalData() {
        if (actionLock.tryLock()) {
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    School06Request req = new School06Request();
                    School06Request.Professional professional = req.new Professional();
                    professional.setName(mProfessionalSearchKey);
                    Log.i(TAG, mProfessionalSearchKey);
                    professional.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
                    professional.setStart(String.valueOf(mProfessionalCurrentStart));
                    req.setProfessional(professional);
                    azService.doTrans(req, School06Response.class, new AzService.Callback<School06Response>() {
                        @Override
                        public void success(School06Response resp) {
                            ResponseResult result = resp.getResult();
                            if ("0".equals(result.getCode())) {
                                List<School06Response.Body.Professional> professional_list = resp.getBody().getProfessional_list();
                                messageHandler.postMessage(4, professional_list);
                            } else {
                                messageHandler.postMessage(5);
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
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    List<Map<String, String>> list = convertSchoolListData((List<School04Response.Body.Schoolinfo>) msg.obj);
                    mListSchoolAdapter.addListData(list);
                    mListSchoolAdapter.notifyDataSetChanged();

                    mSchoolCurrentStart += list.size();

                    mListSchool.stopLoadMore();
                    if (list.size() < GlobalConstant.PAGE_SIZE) {
                        mListSchool.setPullLoadEnable(false);
                    }
                    break;
                }
                case 1: {
                    showToast("学校读取失败");
                    Log.i(TAG,"学校读取失败:" + msg.obj);
                    break;
                }
                case 2: {
                    fillCollegeListData((List<School05Response.Body.College>) msg.obj);
                    break;
                }
                case 3: {
                    showToast("院系读取失败");
                    Log.i(TAG,"院系读取失败:" + msg.obj);
                    break;
                }
                case 4: {
                    try {
                        List<Map<String, String>> list = convertProfessionalListData((List<School06Response.Body.Professional>) msg.obj);
                        if (null == list) {
                            return;
                        }
                        mListProfessionalAdapter.addListData(list);
                        mListProfessionalAdapter.notifyDataSetChanged();

                        mProfessionalCurrentStart += list.size();

                        mListProfessional.stopLoadMore();
                        if (list.size() < GlobalConstant.PAGE_SIZE) {
                            mListProfessional.setPullLoadEnable(false);
                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 5: {
                    try {
                        showToast("专业读取失败");
                        Log.i(TAG,"专业读取失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 6: {
                    showToast("完善资料失败");
                    Log.i(TAG,"完善资料失败：" + msg.obj);
                    break;
                }
                case 7: {
                    showToast("已完善资料");
                    finish();
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private List<Map<String, String>> convertSchoolListData(List<School04Response.Body.Schoolinfo> obj) {
        if(obj==null)
            obj=new ArrayList<>();
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < obj.size(); i++) {
            Map<String, String> item = new HashMap<>();

            item.put("schoolid", obj.get(i).getSchoolid());
            item.put("name", obj.get(i).getName());
            list.add(item);
        }
        return list;
    }

    private List<Map<String, String>> convertProfessionalListData(List<School06Response.Body.Professional> obj) {
        if (null != obj) {
            List<Map<String, String>> list = new ArrayList<>();
            for (int i = 0; i < obj.size(); i++) {
                Map<String, String> item = new HashMap<>();
                item.put("name", obj.get(i).getName());
                list.add(item);
            }
            return list;
        } else {
            return null;
        }
    }

    private void fillCollegeListData(List<School05Response.Body.College> obj) {

        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < obj.size(); i++) {
            map.put(obj.get(i).getId(), obj.get(i).getName());
        }
        View parentView = ProfileImprove2Activity.this.getWindow().getDecorView();
        college_popupWin = PopupWin.Builder.create(ProfileImprove2Activity.this)
                .setParentView(parentView)
                .setData(map, new PopupWin.Callback() {
                    @Override
                    public void select(String key, String val) {
                        college_input.setTag(key);
                        college_input.setText(val);
                        clearAllFocus();
                    }
                })
                .build();
        college_popupWin.setOnShowStateChangeCallBack(new PopupWin.OnShowStateChangeCallBack() {
            @Override
            public void onShow() {

            }

            @Override
            public void onDimiss() {
                clearAllFocus();
            }
        });
        college_popupWin.show();
    }

    protected class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if ("".equals(s.toString()))
                isempty = true;
            else
                isempty = false;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if ("".equals(s.toString()) && !isempty)
                flag++;
            if (!"".equals(s.toString()) && isempty)
                flag--;
            if (0 == flag)
                next.setEnabled(true);
            else
                next.setEnabled(false);
            System.out.println(flag);
        }

        public void addFlag() {
            flag++;
            System.out.println(flag);
        }

        public void subFlag() {
            flag--;
            if (0 == flag)
                next.setEnabled(true);
            System.out.println(flag);
        }
    }
}
