package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.common.GoldWatcher;
import com.jingcai.apps.aizhuan.adapter.help.GroupAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Request;
import com.jingcai.apps.aizhuan.service.business.base.base04.Base04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DateUtil;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.markmao.pulltorefresh.widget.XListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lejing on 15/7/14.
 */
public class HelpWendaDeployActivity extends BaseActivity {
    private final static String TAG = "WendaHelpDeployActivity";
    private MessageHandler messageHandler;

    private Button btn_wenda_help;
    private EditText et_content;

    // 定义字符串数组作为提示的文本
    String[] books = new String[] {"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
            "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
            "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
            "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
            "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
            "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
            "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
            //只写了一部分
            "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help_wenda_deploy);
        messageHandler = new MessageHandler(this);

        initHeader();
        initView();
    }

    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setText("发布求问");

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {
        // 创建一个ArrayAdapter封装数组
        ArrayAdapter<String> av = new ArrayAdapter<String>(this,
                R.layout.popup_list_item2, books);
        AutoCompleteTextView auto = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        auto.setAdapter(av);

//        et_content = (EditText) findViewById(R.id.et_content);

        btn_wenda_help = (Button) findViewById(R.id.btn_wenda_help);


        btn_wenda_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionLock.tryLock()) {
                    messageHandler.postMessage(3);
                }
            }
        });
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
                    try {
//                        List<Base04Response.Body.Region> list = (List<Base04Response.Body.Region>) msg.obj;
//                        groupAdapter.addData(list);
//                        groupAdapter.notifyDataSetChanged();
//                        mCurrentStart += list.size();
//                        onLoad();
//                        if (list.size() < GlobalConstant.PAGE_SIZE) {
//                            groupListView.setPullLoadEnable(false);
//                        }
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast("获取商家失败:" + msg.obj);
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2:{
                    break;
                }
                case 3: {
                    showToast("发布求问成功！");
                    finish();
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }


    private void initGroupData() {
        //TODO 接入服务端接口
        if (actionLock.tryLock()) {
//            showProgressDialog("获取圈子中...");
            final Context context = this;
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if (GlobalConstant.debugFlag) {
//                        List<Base04Response.Body.Region> regionList = new ArrayList<Base04Response.Body.Region>();
//                        for (int i = 0; i < 10 && mCurrentStart < 24; i++) {
//                            Base04Response.Body.Region region = new Base04Response.Body.Region();
//                            region.setRegionid("" + (i + mCurrentStart));
//                            region.setRegionname("浙江大学" + (i + mCurrentStart));
//                            regionList.add(region);
//                        }
//                        messageHandler.postMessage(0, regionList);
                    } else {
//                        final AzService azService = new AzService(context);
//                        final Base04Request req = new Base04Request();
//                        final Base04Request.Region region = req.new Region();
//                        region.setStudentid(UserSubject.getStudentid());  //从UserSubject中获取studentId
//                        region.setAreacode(GlobalConstant.gis.getAreacode());
//                        region.setStart(String.valueOf(mCurrentStart));
//                        region.setPagesize(String.valueOf(GlobalConstant.PAGE_SIZE));
//                        req.setRegion(region);
//                        azService.doTrans(req, Base04Response.class, new AzService.Callback<Base04Response>() {
//                            @Override
//                            public void success(Base04Response response) {
//                                ResponseResult result = response.getResult();
//                                if (!"0".equals(result.getCode())) {
//                                    messageHandler.postMessage(1, result.getMessage());
//                                } else {
//                                    Base04Response.Body partjob07Body = response.getBody();
//                                    List<Base04Response.Body.Region> regionList = partjob07Body.getRegion_list();
//                                    //                                if (regionList.size() < 1 && 0 == mCurrentStart) {
//                                    //                                    messageHandler.postMessage(2);
//                                    //                                } else {
//                                    messageHandler.postMessage(0, regionList);
//                                    //                                }
//                                }
//                            }
//
//                            @Override
//                            public void fail(AzException e) {
//                                messageHandler.postException(e);
//                            }
//                        });
                    }
                }
            });
        }

    }
}
