package com.jingcai.apps.aizhuan.activity.partjob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.adapter.mine.LocationListAdapter;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.business.school.school02.School02Request;
import com.jingcai.apps.aizhuan.service.business.school.school02.School02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.LocateUtil;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationCityActivity extends BaseActivity {
    public static final String KEY_CODE = "code";
    public static final String KEY_NAME = "name";
    private MessageHandler messageHandler;

    private TextView tv_index_partjob_change_city;
    private ListView mListCity;
    private LocationListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_partjob_change_city);
        messageHandler = new MessageHandler(this);
    initHeader();
        initView();  //初始化控件

        initData();
    }

private void initHeader(){
    ((TextView) findViewById(R.id.tv_content)).setText("切换城市");
    findViewById(R.id.iv_func).setVisibility(View.INVISIBLE);
    findViewById(R.id.iv_bird_badge).setVisibility(View.INVISIBLE);
    findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            select(Activity.RESULT_CANCELED, null, null);
        }
    });
}
    /**
     * 初始化控件
     */
    private void initView() {



        //设置已定位的位置
        ((TextView) findViewById(R.id.tv_index_partjob_change_city)).setText(GlobalConstant.getGis().getCityname());

        //点击已定位位置，返回
        findViewById(R.id.ll_located_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != tv_index_partjob_change_city.getTag()) {
                    select(Activity.RESULT_OK, (String) tv_index_partjob_change_city.getTag(), tv_index_partjob_change_city.getText().toString());
                }else{
                    showToast("未定位到城市信息");
                }
            }
        });
        tv_index_partjob_change_city = (TextView)findViewById(R.id.tv_index_partjob_change_city);
        new LocateUtil(this, new LocateUtil.Callback() {
            @Override
            public void locateSuccess(LocateUtil.Area area, List<LocateUtil.Area> areaList) {
                if(null != areaList && areaList.size()>1) {
                    tv_index_partjob_change_city.setText(areaList.get(1).getName());
                    tv_index_partjob_change_city.setTag(areaList.get(1).getCode());
                }
            }
        }).locate();

        mListCity = (ListView) findViewById(R.id.lv_index_partjob_change_city);
        mListAdapter = new LocationListAdapter(this);
        mListCity.setAdapter(mListAdapter);


        mListCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListAdapter.getListData(position)) {
                    Map<String, String> item = mListAdapter.getListData(position);
                    select(RESULT_OK, item.get(KEY_CODE), item.get(KEY_NAME));
                }
            }
        });
    }

    private void select(int result, String code, String name) {
        if(StringUtil.isNotEmpty(code) && StringUtil.isNotEmpty(name)){
            Intent intent = new Intent();
            intent.putExtra("code", code);
            intent.putExtra("name", name);
            setResult(result, intent);
        }else{
            setResult(result);
        }
        finish();
//        overridePendingTransition(0, R.anim.act_top_down);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AzService azService = new AzService(LocationCityActivity.this);
                School02Request req = new School02Request();
                School02Request.Areainfo areainfo = req.new Areainfo();
                req.setAreainfo(areainfo);
                azService.doTrans(req, School02Response.class, new AzService.Callback<School02Response>() {
                    @Override
                    public void success(School02Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            School02Response.Body body = resp.getBody();
                            List<School02Response.Body.Areainfo> areainfo_list = body.getAreainfo_list();
                            messageHandler.postMessage(0, areainfo_list);
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

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    fillListData((List<School02Response.Body.Areainfo>) msg.obj);
                    break;
                }
                case 1: {
                    showToast("已开通城市读取失败:" + msg.obj);
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void fillListData(List<School02Response.Body.Areainfo> obj) {
        List<Map<String, String>> citys = new ArrayList<>();
        for (int i = 0; i < obj.size(); i++) {
            Map<String, String> item = new HashMap<>();
            item.put("code", obj.get(i).getCode());
            item.put("name", obj.get(i).getName());
            citys.add(item);
        }
        mListAdapter.setListData(citys);
        mListAdapter.notifyDataSetChanged();
    }
}
