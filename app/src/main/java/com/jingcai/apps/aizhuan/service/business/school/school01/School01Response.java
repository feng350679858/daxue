package com.jingcai.apps.aizhuan.service.business.school.school01;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/27.
 */
public class School01Response extends BaseResponse<School01Response.Body> {

public class Body {

    private List<Areainfo> areainfo_list;

    public List<Areainfo> getAreainfo_list() {
        return areainfo_list;
    }

    public void setAreainfo_list(List<Areainfo> areainfo_list) {
        this.areainfo_list = areainfo_list;
    }

    public class Areainfo {
        private String code;
        private String name;

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
}
