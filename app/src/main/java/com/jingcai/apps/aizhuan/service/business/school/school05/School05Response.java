package com.jingcai.apps.aizhuan.service.business.school.school05;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class School05Response extends BaseResponse<School05Response.Body> {

    public class Body {

        private List<College> college_list ;

        public List<College> getCollege_list() {
            return college_list;
        }

        public void setCollege_list(List<College> college_list) {
            this.college_list = college_list;
        }

        public class College{
            private String id;
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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
