package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.AreaAdapter;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.school.school03.School03Request;
import com.jingcai.apps.aizhuan.service.business.school.school03.School03Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu03.Stu03Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.view.ClearableEditText;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

public class ProfileImproveActivity extends BaseActivity {
    public static final String TAG = "ProfileImproveActivity";
    private TextInputLayout name, gender, location, spreading_code;
    private ClearableEditText name_input, spreading_code_input;
    private EditText gender_input, location_input;
    private TextView warning;
    private Button next;
    private PopupWin genderWin, areaWin, connectionWin;

    private AzService azService;
    private MessageHandler messageHandler;

    private List<School03Response.Body.Areainfo> provinceList, cityList, areaList;
    private AreaAdapter provinceAdapter, cityAdapter, areaAdapter;

    private String cityCode, areaCode;
    private boolean provinceScrolling = false, cityScrolling = false;
    private MyTextWatcher myTextWatcher = new MyTextWatcher();
    private Stu03Request req03 = new Stu03Request();
    private WheelView provinces;
    private WheelView cities;
    private WheelView areas;
    private String joindate, school, college, professional, schoolname, collegename, email, qq;
    private String gender_string, areaname_string, areacode_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_improve);
        messageHandler = new MessageHandler(this);
        initHeader();
        initView();
        initDate();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("资料完善");
        findViewById(R.id.iv_bird_badge).setVisibility(View.GONE);
        findViewById(R.id.iv_func).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_func)).setText("联系客服");
        ((TextView) findViewById(R.id.tv_func)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_func)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == connectionWin) {
                    View parentView = ProfileImproveActivity.this.getWindow().getDecorView();
                    View contentView = LayoutInflater.from(ProfileImproveActivity.this).inflate(R.layout.comfirm_contact_merchant_dialog, null);

                    connectionWin = PopupWin.Builder.create(ProfileImproveActivity.this)
                            .setParentView(parentView)
                            .setContentView(contentView)
                            .build();
                    //logo
                    ((ImageView) contentView.findViewById(R.id.iv_contact_merchant_dialog_logo)).setImageResource(R.drawable.ic_launcher);
                    //title
                    ((TextView) contentView.findViewById(R.id.tv_contact_merchant_dialog_title)).setText("客服赚赚");
                    //phone
                    ((TextView) contentView.findViewById(R.id.tv_contact_merchant_dialog_phone)).setText(R.string.mine_contact_service_tel_num);
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
                            intent.setData(Uri.parse("tel:" + R.string.mine_contact_service_tel_num));
                            startActivity(intent);
                        }
                    });
                }
                connectionWin.show();
            }
        });
    }

    private void initView() {
        name = (TextInputLayout) findViewById(R.id.profile_improve_name);
        name_input = (ClearableEditText) name.getEditText();
        name_input.addTextChangedListener(myTextWatcher);
        gender = (TextInputLayout) findViewById(R.id.profile_improve_gender);
        gender_input = gender.getEditText();
        gender_input.addTextChangedListener(myTextWatcher);
        gender_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFocus();
                gender_input.requestFocus();
                if (null == genderWin) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("0", "男");
                    map.put("1", "女");
                    View parentView = ProfileImproveActivity.this.getWindow().getDecorView();
                    genderWin = PopupWin.Builder.create(ProfileImproveActivity.this)
                            .setData(map, new PopupWin.Callback() {
                                @Override
                                public void select(String key, String val) {
                                    gender_input.setText(val);
                                    gender_input.setTag(key);
                                }
                            })
                            .setParentView(parentView)
                            .build();

                    genderWin.setOnShowStateChangeCallBack(new PopupWin.OnShowStateChangeCallBack() {
                        @Override
                        public void onShow() {

                        }

                        @Override
                        public void onDimiss() {
                            clearAllFocus();
                        }
                    });

                }
                genderWin.show();
            }
        });

        location = (TextInputLayout) findViewById(R.id.profile_improve_location);


        location_input = location.getEditText();
        location_input.addTextChangedListener(myTextWatcher);
        location_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFocus();
                location_input.requestFocus();
                areaWin.show();
            }
        });

        spreading_code = (TextInputLayout) findViewById(R.id.profile_improve_spreading_code);
        spreading_code_input = (ClearableEditText) spreading_code.getEditText();
        warning = (TextView) findViewById(R.id.profile_improve_warning);
        next = (Button) findViewById(R.id.profile_improve_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileImproveActivity.this, ProfileImprove2Activity.class);
                intent.putExtra("name", name_input.getText().toString());
                intent.putExtra("gender", gender_input.getTag().toString());
                if (location_input.isEnabled())
                    intent.putExtra("areacode", areaAdapter.getItemText(areas.getCurrentItem()).toString());
                else
                    intent.putExtra("areacode", "");
                intent.putExtra("promotioncode", spreading_code_input.getText().toString());
                intent.putExtra("joindate", joindate);
                intent.putExtra("school", school);
                intent.putExtra("schoolname", schoolname);
                intent.putExtra("college", college);
                intent.putExtra("collegename", collegename);
                intent.putExtra("professional", professional);
                intent.putExtra("email", email);
                intent.putExtra("qq", qq);
                startActivity(intent);
                finish();
            }
        });
        //在TextVew中加入图片
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.msg_state_fail_resend);
        ImageSpan imgSpan = new ImageSpan(this, b);
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        warning.setText(spanString);
        warning.append(getResources().getString(R.string.profile_improve_warning));
    }

    private void clearAllFocus() {
        name_input.clearFocus();
        gender_input.clearFocus();
        location_input.clearFocus();
        spreading_code_input.clearFocus();
    }

    protected class MyTextWatcher implements TextWatcher {
        private int flag = 3;
        private boolean isempty;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if ("".equals(s.toString()) || "真实姓名".equals(s.toString()))
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

    private void initProvinceWheel() {

        if (null == areaWin) {
            View parentView = ProfileImproveActivity.this.getWindow().getDecorView();
            View contentView = LayoutInflater.from(ProfileImproveActivity.this).inflate(R.layout.area_popupwin, null);
            areaWin = PopupWin.Builder.create(ProfileImproveActivity.this)
                    .setParentView(parentView)
                    .setContentView(contentView)
                    .build();
            areaWin.setOnShowStateChangeCallBack(new PopupWin.OnShowStateChangeCallBack() {
                @Override
                public void onShow() {
                    areaname_string = location_input.getText().toString();
                    if (null != location_input.getTag())
                        areacode_string = location_input.getTag().toString();
                    else
                        areacode_string = "";
                }

                @Override
                public void onDimiss() {
                    location_input.setText(areaname_string);
                    location_input.setTag(areacode_string);
                    clearAllFocus();
                }
            });

            provinces = (WheelView) contentView.findViewById(R.id.area_popupwin_province);
            provinces.setVisibleItems(5);
            cities = (WheelView) contentView.findViewById(R.id.area_popupwin_city);
            cities.setVisibleItems(5);
            areas = (WheelView) contentView.findViewById(R.id.area_popupwin_area);
            areas.setVisibleItems(5);

            provinceAdapter = new AreaAdapter(this, provinceList);
            provinces.setViewAdapter(provinceAdapter);
            ((ImageView) contentView.findViewById(R.id.area_popupwin_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    areaWin.dismiss();
                }
            });
            ((ImageView) contentView.findViewById(R.id.area_popupwin_confirm)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    areaname_string = provinceAdapter.getItemName(provinces.getCurrentItem()).toString()
                            + "  " + cityAdapter.getItemName(cities.getCurrentItem()).toString()
                            + "  " + areaAdapter.getItemName(areas.getCurrentItem()).toString();
                    areacode_string = areaAdapter.getItemText(areas.getCurrentItem()).toString();
                    areaWin.dismiss();
                }
            });

            provinces.setShadowColor(0xFFFFFFFF, 0x88FFFFFF, 0x00FFFFFF);
            provinces.addScrollingListener(new OnWheelScrollListener() {
                @Override
                public void onScrollingStarted(WheelView wheel) {
                    provinceScrolling = true;
                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    provinceScrolling = false;
                    updateAreas(provinces.getCurrentItem(), 0);
                }
            });
            provinces.setCurrentItem(2);
            updateAreas(0, 0);

        }

    }

    private void initCityWheel() {
        if (cityList == null || 0 == cityList.size()) {
            School03Response school03Response = new School03Response();
            School03Response.Body body = school03Response.new Body();
            School03Response.Body.Areainfo areainfo = body.new Areainfo();
            areainfo.setName(provinceAdapter.getItemName(provinces.getCurrentItem()).toString());
            areainfo.setCode(provinceAdapter.getItemText(provinces.getCurrentItem()).toString());
            cityList = new ArrayList<School03Response.Body.Areainfo>();
            cityList.add(areainfo);
        }
        cityAdapter = new AreaAdapter(this, cityList);

        cities.setViewAdapter(cityAdapter);
        cities.setShadowColor(0xFFFFFFFF, 0x88FFFFFF, 0x00FFFFFF);
        cities.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                cityScrolling = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                cityScrolling = false;
                updateAreas(cities.getCurrentItem(), 1);
            }
        });
        cities.setCurrentItem(2);

        updateAreas(0, 1);

    }

    private void initAreaWheel() {
        if (areaList == null || 0 == areaList.size()) {
            School03Response school03Response = new School03Response();
            School03Response.Body body = school03Response.new Body();
            School03Response.Body.Areainfo areainfo = body.new Areainfo();
            areainfo.setName(cityAdapter.getItemName(cities.getCurrentItem()).toString());
            areainfo.setCode(cityAdapter.getItemText(cities.getCurrentItem()).toString());
            areaList = new ArrayList<School03Response.Body.Areainfo>();
            areaList.add(areainfo);
        }
        areaAdapter = new AreaAdapter(this, areaList);
        areas.setViewAdapter(areaAdapter);
        areas.setShadowColor(0xFFFFFFFF, 0x88FFFFFF, 0x00FFFFFF);

        areas.setCurrentItem(2);
    }

    //更新城市或地区信息
    private void updateAreas(int index, int flag) {
        if (flag == 0) {
            areaCode = provinceAdapter.getItemText(index).toString();
            getAreaInfo(2);
        } else {
            areaCode = cityAdapter.getItemText(index).toString();
            getAreaInfo(3);
        }
    }

    private void getAreaInfo(final int type) {
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(ProfileImproveActivity.this);
                School03Request req = new School03Request();
                School03Request.Areainfo areainfo = req.new Areainfo();
                areainfo.setCode(areaCode);
                req.setAreainfo(areainfo);
                azService.doTrans(req, School03Response.class, new AzService.Callback<School03Response>() {
                    @Override
                    public void success(School03Response response) {
                        ResponseResult result = response.getResult();
                        School03Response.Body School03Body = response.getBody();
                        List<School03Response.Body.Areainfo> areainfo = School03Body.getAreainfo_list();
                        switch (type) {
                            case 1:
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(2);
                                } else {
                                    messageHandler.postMessage(3, areainfo);
                                }
                                break;
                            case 2:
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(4);
                                } else {
                                    messageHandler.postMessage(5, areainfo);
                                }
                                break;
                            default:
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(6);
                                } else {
                                    messageHandler.postMessage(7, areainfo);
                                }
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        }));
    }

    private void initDate() {
        showProgressDialog("数据加载中...");
        areaCode = "100000";
        getAreaInfo(1);
        loadProfileData();
    }

    private void loadProfileData() {
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(ProfileImproveActivity.this);
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(UserSubject.getStudentid());
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        ResponseResult result = response.getResult();
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(0);
                        } else {
                            messageHandler.postMessage(1, student);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandler.postException(e);
                    }
                });
            }
        }));
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    showToast("获取失败");
                    Log.i(TAG, "获取失败:" + msg.obj);
                    break;
                }
                case 1: {
                    Stu02Response.Stu02Body.Student student = (Stu02Response.Stu02Body.Student) msg.obj;
                    fillStudentInView(student);
                    break;
                }
                case 2: {
                    showToast("已开通省份获取失败");
                    Log.i(TAG, "已开通省份获取失败:" + msg.obj);
                    break;
                }
                case 3: {
                    provinceList = (List<School03Response.Body.Areainfo>) msg.obj;
                    Log.i(TAG, provinceList.toString());
                    initProvinceWheel();
                    break;
                }
                case 4: {
                    showToast("已开通城市获取失败");
                    Log.i(TAG, "已开通城市获取失败:" + msg.obj);
                    break;
                }
                case 5: {
                    cityList = (List<School03Response.Body.Areainfo>) msg.obj;
                    Log.i(TAG, cityList.toString());
                    initCityWheel();//加载区域滚轮
                    break;
                }
                case 6: {
                    showToast("已开通地区获取失败");
                    Log.i(TAG, "已开通地区获取失败:" + msg.obj);
                    break;
                }
                case 7: {
                    areaList = (List<School03Response.Body.Areainfo>) msg.obj;
                    Log.i(TAG, areaList.toString());
                    initAreaWheel();//加载区域滚轮
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }


    private void fillStudentInView(Stu02Response.Stu02Body.Student student) {
        if (null != student.getName() && !"".equals(student.getName())) {
            name_input.setText(student.getName());
            name_input.setEnabled(false);
            name_input.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        if (null != student.getGender() && !"".equals((student.getGender()))) {
            if ("0".equals(student.getGender()))
                gender_input.setText("男");
            else
                gender_input.setText("女");
            gender_input.setTag(student.getGender());
            gender_input.setEnabled(false);
        }
        if (null != student.getAreaname() && !"".equals(student.getAreaname())) {
            location_input.setText(student.getAreaname());
            location_input.setEnabled(false);
        }
        if (null != student.getPromotioncode() && !"".equals(student.getPromotioncode())) {
            spreading_code_input.setText(student.getPromotioncode());
            spreading_code_input.setEnabled(false);
            spreading_code_input.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        joindate = student.getJoindate();
        college = student.getCollege();
        collegename = student.getCollegename();
        school = student.getSchoolname();
        schoolname = student.getSchoolname();
        professional = student.getProfessional();
        email = student.getEmail();
        qq = student.getQq();
        clearAllFocus();
    }
}
