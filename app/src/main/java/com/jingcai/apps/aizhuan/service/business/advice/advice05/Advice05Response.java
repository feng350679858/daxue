package com.jingcai.apps.aizhuan.service.business.advice.advice05;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Advice05Response extends BaseResponse<Advice05Response.Body> {

    public class Body {
        private List<Notice> notice_list;

        public List<Notice> getNotice_list() {
            return notice_list;
        }

        public void setNotice_list(List<Notice> notice_list) {
            this.notice_list = notice_list;
        }

        public class Notice {
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
