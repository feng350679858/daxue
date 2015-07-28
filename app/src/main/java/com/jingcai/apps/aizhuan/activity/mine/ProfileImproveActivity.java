package com.jingcai.apps.aizhuan.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.wheel.AreaAdapter;
import com.jingcai.apps.aizhuan.adapter.mine.wheel.CityAdapter;
import com.jingcai.apps.aizhuan.adapter.mine.wheel.ProvinceAdapter;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.school.school01.School01Request;
import com.jingcai.apps.aizhuan.service.business.school.school01.School01Response;
import com.jingcai.apps.aizhuan.service.business.school.school02.School02Request;
import com.jingcai.apps.aizhuan.service.business.school.school02.School02Response;
import com.jingcai.apps.aizhuan.service.business.school.school03.School03Request;
import com.jingcai.apps.aizhuan.service.business.school.school03.School03Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu01.Stu01Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.stu.stu03.Stu03Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.view.ClearableEditText;
import android.support.design.widget.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

public class ProfileImproveActivity extends BaseActivity {
    private TextInputLayout name;
    private ClearableEditText name_input;
    private TextView gender, gender_input;
    private TextView location, location_input;
    private TextView spreading_code;
    private ClearableEditText spreading_code_input;
    private TextView warning;
    private Button next;
    private PopupWin genderWin, areaWin;

    private AzService azService;
    private MessageHandler messageHandler;

    private Stu03Request.Student student03;
    private List<School01Response.Body.Areainfo> provinceList;
    private List<School02Response.Body.Areainfo> cityList;
    private List<School03Response.Body.Areainfo> areaList;
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    private AreaAdapter areaAdapter;

    private String cityCode, areaCode;
    private boolean provinceScrolling = false, cityScrolling = false;
    private MyTextWatcher myTextWatcher = new MyTextWatcher();
    private Stu03Request req03 = new Stu03Request();
     WheelView provinces;
     WheelView cities;
     WheelView areas;
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
        ((TextView) findViewById(R.id.tv_content)).setText("完善资料");
        findViewById(R.id.iv_bird_badge).setVisibility(View.GONE);
        findViewById(R.id.iv_func).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回主界面
                finish();
            }
        });
    }

    private void initView() {
        name = (TextView) findViewById(R.id.profile_improve_name);

        gender = (TextView) findViewById(R.id.profile_improve_gender);
        gender_input = (TextView) findViewById(R.id.profile_improve_gender_input);
        gender_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender.setVisibility(View.VISIBLE);
                if (!"性别".equals(gender_input.getText().toString()))
                    myTextWatcher.addFlag();
                gender_input.setText("");
                if (null == genderWin) {
                    View parentView = ProfileImproveActivity.this.getWindow().getDecorView();
                    View contentView = LayoutInflater.from(ProfileImproveActivity.this).inflate(R.layout.gender_popupwin, null);

                    genderWin = PopupWin.Builder.create(ProfileImproveActivity.this)
                            .setParentView(parentView)
                            .setContentView(contentView)
                            .build();
                    contentView.findViewById(R.id.gender_popupwin_male).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gender_input.setText("男");
                            myTextWatcher.subFlag();
                            genderWin.dismiss();
                        }
                    });
                    contentView.findViewById(R.id.gender_popupwin_female).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gender_input.setText("女");
                            myTextWatcher.subFlag();
                            genderWin.dismiss();
                        }
                    });
                    contentView.findViewById(R.id.gender_popupwin_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            genderWin.dismiss();
                        }
                    });
                }
                genderWin.show();
            }
        });
        location = (TextView) findViewById(R.id.profile_improve_location);
        initWheel();//加载区域滚轮
        location_input = (TextView) findViewById(R.id.profile_improve_location_input);
        location_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setVisibility(View.VISIBLE);
                if (!"地区".equals(location_input.getText().toString()))
                    myTextWatcher.addFlag();
                location_input.setText("");
                areaWin.show();
            }
        });
        spreading_code = (TextView) findViewById(R.id.profile_improve_spreading_code);
        spreading_code_input = (ClearableEditText) findViewById(R.id.profile_improve_spreading_code_input);
        spreading_code_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spreading_code.setVisibility(View.VISIBLE);
                spreading_code_input.setText("");
            }
        });
        warning = (TextView) findViewById(R.id.profile_improve_warning);
        next = (Button) findViewById(R.id.profile_improve_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gender_string, spreading_code_string = "";
                if ("男".equals(gender_input.getText().toString()))
                    gender_string = "0";
                else
                    gender_string = "1";
                if (!"推广码".equals(spreading_code_input.getText().toString())
                        && !"".equals(spreading_code_input.getText().toString()))
                    spreading_code_string = spreading_code_input.getText().toString();
                Intent intent = new Intent(ProfileImproveActivity.this, ProfileImprove2Activity.class);
                intent.putExtra("name", name_input.getText().toString());
                intent.putExtra("gender", gender_string);
                intent.putExtra("areacode", areaAdapter.getItemText(areas.getCurrentItem()).toString());
                intent.putExtra("promotioncode", spreading_code_string);
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
        }

        public void addFlag() {
            flag++;
        }

        public void subFlag() {
            flag--;
            if (0 == flag)
                next.setEnabled(true);
        }
    }

    private void initWheel() {
        provinceAdapter = new ProvinceAdapter(this, provinceList);
        cityAdapter = new CityAdapter(this, cityList);
        areaAdapter = new AreaAdapter(this, areaList);
        if (null == areaWin) {
            View parentView = ProfileImproveActivity.this.getWindow().getDecorView();
            View contentView = LayoutInflater.from(ProfileImproveActivity.this).inflate(R.layout.area_popupwin, null);
            areaWin = PopupWin.Builder.create(ProfileImproveActivity.this)
                    .setParentView(parentView)
                    .setContentView(contentView)
                    .build();
             provinces = (WheelView) contentView.findViewById(R.id.area_popupwin_province);
             cities = (WheelView) contentView.findViewById(R.id.area_popupwin_city);
             areas = (WheelView) contentView.findViewById(R.id.area_popupwin_area);
            ((ImageView) contentView.findViewById(R.id.area_popupwin_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    areaWin.dismiss();
                }
            });
            ((ImageView) contentView.findViewById(R.id.area_popupwin_confirm)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    location_input.setText(provinceAdapter.getItemName(provinces.getCurrentItem()).toString()
                            + " " + cityAdapter.getItemName(cities.getCurrentItem())
                            + " " + areaAdapter.getItemName(areas.getCurrentItem()));
                    myTextWatcher.subFlag();
                }
            });

            provinces.setVisibleItems(5);
            provinces.setViewAdapter(provinceAdapter);
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
            cities.setVisibleItems(5);
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
            areas.setVisibleItems(5);
            areas.setViewAdapter(areaAdapter);
            areas.setShadowColor(0xFFFFFFFF, 0x88FFFFFF, 0x00FFFFFF);
        }
        getProvinceInfo();
    }

    //更新城市或地区信息
    private void updateAreas(int index, int flag) {
        if (flag == 0) {
            cityCode = provinceAdapter.getItemText(index).toString();
            getCityInfo();
        } else {
            areaCode = cityAdapter.getItemText(index).toString();
            getAreaInfo();
        }
    }

    private void getProvinceInfo() {
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(ProfileImproveActivity.this);
                School01Request req = new School01Request();
                azService.doTrans(req, School01Response.class, new AzService.Callback<School01Response>() {
                    @Override
                    public void success(School01Response response) {
                        ResponseResult result = response.getResult();
                        School01Response.Body school01Body = response.getBody();
                        List<School01Response.Body.Areainfo> areainfo = school01Body.getAreainfo_list();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(2);
                        } else {
                            messageHandler.postMessage(3, areainfo);
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

    private void getCityInfo() {
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(ProfileImproveActivity.this);
                School02Request req = new School02Request();
                School02Request.Areainfo areainfo = req.new Areainfo();
                areainfo.setCode(cityCode);
                azService.doTrans(req, School02Response.class, new AzService.Callback<School02Response>() {
                    @Override
                    public void success(School02Response response) {
                        ResponseResult result = response.getResult();
                        School02Response.Body school02Body = response.getBody();
                        List<School02Response.Body.Areainfo> areainfo = school02Body.getAreainfo_list();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(4);
                        } else {
                            messageHandler.postMessage(5, areainfo);
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

    private void getAreaInfo() {
        new AzExecutor().execute(new Thread(new Runnable() {
            @Override
            public void run() {
                azService = new AzService(ProfileImproveActivity.this);
                School03Request req = new School03Request();
                School03Request.Areainfo areainfo = req.new Areainfo();
                areainfo.setCode(cityCode);
                azService.doTrans(req, School03Response.class, new AzService.Callback<School03Response>() {
                    @Override
                    public void success(School03Response response) {
                        ResponseResult result = response.getResult();
                        School03Response.Body School03Body = response.getBody();
                        List<School03Response.Body.Areainfo> areainfo = School03Body.getAreainfo_list();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(6);
                        } else {
                            messageHandler.postMessage(7, areainfo);
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
                    showToast("获取失败:" + msg.obj);
                    break;
                }
                case 1: {
                    Stu02Response.Stu02Body.Student student = (Stu02Response.Stu02Body.Student) msg.obj;
                    fillStudentInView(student);
                    break;
                }
                case 2: {
                    showToast("已开通省份获取失败:" + msg.obj);
                    break;
                }
                case 3: {
                    provinceList = (List<School01Response.Body.Areainfo>) msg.obj;
                    break;
                }
                case 4: {
                    showToast("已开通城市获取失败:" + msg.obj);
                    break;
                }
                case 5: {
                    cityList = (List<School02Response.Body.Areainfo>) msg.obj;
                    break;
                }
                case 6: {
                    showToast("已开通省份获取失败:" + msg.obj);
                    break;
                }
                case 7: {
                    areaList = (List<School03Response.Body.Areainfo>) msg.obj;
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }


    private void fillStudentInView(Stu02Response.Stu02Body.Student student) {
        name.setVisibility(View.VISIBLE);
        if (null != student.getName() && !"".equals(student.getName())) {
            name_input.setText(student.getName());
            name_input.setEnabled(false);
            myTextWatcher.subFlag();
        }
        gender.setVisibility(View.VISIBLE);
        if (null != student.getGender() && !"".equals((student.getGender()))) {
            if ("0".equals(student.getGender()))
                gender_input.setText("男");
            else
                gender_input.setText("女");
            gender_input.setEnabled(false);
            myTextWatcher.subFlag();
        }
        location.setVisibility(View.VISIBLE);
        if (null != student.getAreaname() && !"".equals(student.getAreaname())) {
            location_input.setText(student.getAreaname());
            location_input.setEnabled(false);
            myTextWatcher.subFlag();
        }
        spreading_code.setVisibility(View.VISIBLE);
        if (null != student.getPromotioncode() && !"".equals(student.getPromotioncode())) {
            spreading_code_input.setText(student.getPromotioncode());
            spreading_code_input.setEnabled(false);
        }
    }
}
