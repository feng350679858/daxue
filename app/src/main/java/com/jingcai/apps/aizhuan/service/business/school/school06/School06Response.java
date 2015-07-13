package com.jingcai.apps.aizhuan.service.business.school.school06;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class School06Response extends BaseResponse<School06Response.Body> {

    public class Body {

        private List<Professional> professional_list ;

        public List<Professional> getProfessional_list() {
            return professional_list;
        }

        public void setProfessional_list(List<Professional> professional_list) {
            this.professional_list = professional_list;
        }

        public class Professional{
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
