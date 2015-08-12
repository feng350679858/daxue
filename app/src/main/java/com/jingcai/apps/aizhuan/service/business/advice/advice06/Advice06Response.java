package com.jingcai.apps.aizhuan.service.business.advice.advice06;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Advice06Response extends BaseResponse<Advice06Response.Body> {

    public class Body {
        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public class Message{
            private String commentcount;
            private String praisecount;
            private String unreadmessageflag;

            public String getCommentcount() {
                return commentcount;
            }

            public void setCommentcount(String commentcount) {
                this.commentcount = commentcount;
            }

            public String getPraisecount() {
                return praisecount;
            }

            public void setPraisecount(String praisecount) {
                this.praisecount = praisecount;
            }

            public String getUnreadmessageflag() {
                return unreadmessageflag;
            }

            public void setUnreadmessageflag(String unreadmessageflag) {
                this.unreadmessageflag = unreadmessageflag;
            }
        }
    }
}
