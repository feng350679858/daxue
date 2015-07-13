package com.jingcai.apps.aizhuan.service.business.school.school04;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class School04Response extends BaseResponse<School04Response.Body> {

    public class Body {

        private List<Schoolinfo> schoolinfo_list;

        public List<Schoolinfo> getSchoolinfo_list() {
            return schoolinfo_list;
        }

        public void setSchoolinfo_list(List<Schoolinfo> schoolinfo_list) {
            this.schoolinfo_list = schoolinfo_list;
        }

        public class Schoolinfo {
            private String schoolid;
            private String name;

            public String getSchoolid() {
                return schoolid;
            }

            public void setSchoolid(String schoolid) {
                this.schoolid = schoolid;
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
