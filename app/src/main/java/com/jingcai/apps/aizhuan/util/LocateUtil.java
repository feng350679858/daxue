package com.jingcai.apps.aizhuan.util;

import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.school.school07.School07Request;
import com.jingcai.apps.aizhuan.service.business.school.school07.School07Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lejing on 15/5/13.
 */
public class LocateUtil {
    public static long lastRequestTime = -1;
    public static List<Area> areaList = new CopyOnWriteArrayList<Area>();
    public Callback callback;

    public LocateUtil(Callback callback) {
        this.callback = callback;
    }

    public void locate() {
        if ((System.currentTimeMillis() - lastRequestTime) < 5 * 60 * 1000 && areaList.size() > 0) {
            call();
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
                                for (School07Response.Body.Areainfo a : areainfo_list) {
                                    areaList.add(new Area(a.getCode(), a.getName()));
                                }
                            }
                        }
                        call();
                    }

                    @Override
                    public void fail(AzException e) {
                        call();
                    }
                });
            }
        });
    }

    private void call() {
        if(areaList.size()<2) {
            areaList.clear();
            areaList.add(new Area(GlobalConstant.AREA_CODE_ZHEJIANG, GlobalConstant.AREA_NAME_ZHEJIANG));
            areaList.add(new Area(GlobalConstant.AREA_CODE_HANGZHOU, GlobalConstant.AREA_NAME_HANGZHOU));
        }

        lastRequestTime = System.currentTimeMillis();
        callback.locateSuccess(areaList.get(1), areaList);

    }

    public interface Callback {
        void locateSuccess(Area cityArea, List<Area> areaList);
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
