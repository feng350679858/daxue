package com.jingcai.apps.aizhuan.util;

import android.content.Context;
import android.os.Message;

import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.school.school07.School07Request;
import com.jingcai.apps.aizhuan.service.business.school.school07.School07Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lejing on 15/5/13.
 */
public class LocateUtil {
    public static long lastRequestTime = -1;
    public static List<Area> areaList = new ArrayList<>();;
    public Context context;
    public MessageHandle messageHandle;
    public Callback callback;

    public LocateUtil(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.messageHandle = new MessageHandle(context);
    }

    public void locate() {
        if ((System.currentTimeMillis() - lastRequestTime) < 5 * 60 * 1000 && areaList.size()>0) {
            messageHandle.postMessage(0);
            return;
        }
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                School07Request req = new School07Request();
                School07Request.Areainfo areainfo = req.new Areainfo();
                areainfo.setProvincename(GlobalConstant.gis.getProvincename());
                areainfo.setCityname(GlobalConstant.gis.getCityname());
                areainfo.setDistrictname(GlobalConstant.gis.getDistrictname());
                req.setAreainfo(areainfo);
                new AzService().doTrans(req, School07Response.class, new AzService.Callback<School07Response>() {
                    @Override
                    public void success(School07Response resp) {
                        ResponseResult result = resp.getResult();
                        String code = result.getCode();
                        areaList.clear();
                        if ("0".equals(code)) {
                            List<School07Response.Body.Areainfo> areainfo_list = resp.getBody().getAreainfo_list();
                            if (null != areainfo_list && areainfo_list.size() > 0) {
                                for(School07Response.Body.Areainfo a:areainfo_list){
                                    areaList.add(new Area(a.getCode(), a.getName()));
                                }
                            }else{
                                areaList.add(new Area(GlobalConstant.AREA_CODE_HANGZHOU, GlobalConstant.AREA_NAME_HANGZHOU));
                            }
                            messageHandle.postMessage(0);
                        } else {
                            areaList.add(new Area(GlobalConstant.AREA_CODE_HANGZHOU, GlobalConstant.AREA_NAME_HANGZHOU));
                            messageHandle.postMessage(0);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        messageHandle.postException(e);
                    }
                });
            }
        });
    }

    class MessageHandle extends BaseHandler {
        public MessageHandle(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    lastRequestTime = System.currentTimeMillis();
                    callback.locateSuccess(areaList.get(areaList.size() - 1), areaList);
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    public interface Callback {
        void locateSuccess(Area area, List<Area> areaList);
    }

    public class Area {
        private String code, name;

        public Area(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
