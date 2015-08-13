package com.jingcai.apps.aizhuan.service.business.partjob.partjob18;


import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpListItem;
import com.jingcai.apps.aizhuan.adapter.mine.help.MineHelpResponse;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob18Response extends MineHelpResponse<Partjob18Response.Body> {

    @Override
    public List getList() {
        Body body = getBody();
        return null == body?null:body.getParttimejob_list();
    }

    public static class Body {
        private List<Parttimejob> parttimejob_list;

        public List<Parttimejob> getParttimejob_list() {
            return parttimejob_list;
        }

        public void setParttimejob_list(List<Parttimejob> parttimejob_list) {
            this.parttimejob_list = parttimejob_list;
        }
    }

    public static class Parttimejob implements MineHelpListItem {
        private String helpid;
        private String optime;
        private String money;
        private String gender;
        private String status;
        private String content;
        private String targetid;
        private String targetname;
        private String targetschool;
        private String targetcollege;
        private String targetimgurl;

        public String getContent() {
            return content;
        }

        public String getGender() {
            return gender;
        }

        public String getHelpid() {
            return helpid;
        }

        public String getMoney() {
            return money;
        }

        public String getOptime() {
            return optime;
        }

        public String getStatus() {
            return status;
        }

        public String getTargetcollege() {
            return targetcollege;
        }

        public String getTargetid() {
            return targetid;
        }

        public String getTargetimgurl() {
            return targetimgurl;
        }

        public String getTargetname() {
            return targetname;
        }

        public String getTargetschool() {
            return targetschool;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setHelpid(String helpid) {
            this.helpid = helpid;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public void setOptime(String optime) {
            this.optime = optime;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setTargetcollege(String targetcollege) {
            this.targetcollege = targetcollege;
        }

        public void setTargetid(String targetid) {
            this.targetid = targetid;
        }

        public void setTargetimgurl(String targetimgurl) {
            this.targetimgurl = targetimgurl;
        }

        public void setTargetname(String targetname) {
            this.targetname = targetname;
        }

        public void setTargetschool(String targetschool) {
            this.targetschool = targetschool;
        }
    }
}
