package com.jingcai.apps.aizhuan.service.business.advice.advice01;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class Advice01Response extends BaseResponse<Advice01Response.Advice01Body> {

    public class Advice01Body {
        private List<Message> message_list;

        public List<Message> getMessage_list() {
            return message_list;
        }

        public void setMessage_list(List<Message> message_list) {
            this.message_list = message_list;
        }

        public class Message {
            private String id;
            private String title;
            private String contentsimple;
            private String createtime;
            private String isread;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContentsimple() {
                return contentsimple;
            }

            public void setContentsimple(String contentsimple) {
                this.contentsimple = contentsimple;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getIsread() {
                return isread;
            }

            public void setIsread(String isread) {
                this.isread = isread;
            }
        }
    }
}
