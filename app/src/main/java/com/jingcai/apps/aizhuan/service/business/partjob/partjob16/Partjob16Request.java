package com.jingcai.apps.aizhuan.service.business.partjob.partjob16;

import com.jingcai.apps.aizhuan.service.base.BaseRequest;
import com.jingcai.apps.aizhuan.service.business.BizConstant;

/**
 * Created by Administrator on 2015/7/18.
 */
public class Partjob16Request extends BaseRequest {

    @Override
    public String getTranscode() {
        return BizConstant.BIZ_PARTTIME_JOB_16;
    }

    private Parttimejob parttimejob;

    public Parttimejob getParttimejob() {
        return parttimejob;
    }

    public void setParttimejob(Parttimejob parttimejob) {
        this.parttimejob = parttimejob;
    }

    public class Parttimejob {
        private String studentid;
        private String type;
        private String publiccontent;
        private String privatecontent;
        private String money;
        private String validtime;
        private String genderlimit;
        private String regionid;
        private String friendid;
        private String gisx;
        private String gisy;
        private String paypassword;

        public String getPaypassword() {
            return paypassword;
        }

        public void setPaypassword(String paypassword) {
            this.paypassword = paypassword;
        }

        public String getStudentid() {
            return studentid;
        }

        public void setStudentid(String studentid) {
            this.studentid = studentid;
        }

        public String getGisx() {
            return gisx;
        }

        public void setGisx(String gisx) {
            this.gisx = gisx;
        }

        public String getGisy() {
            return gisy;
        }

        public void setGisy(String gisy) {
            this.gisy = gisy;
        }

        public String getFriendid() {
            return friendid;
        }

        public void setFriendid(String friendid) {
            this.friendid = friendid;
        }

        public String getGenderlimit() {
            return genderlimit;
        }

        public void setGenderlimit(String genderlimit) {
            this.genderlimit = genderlimit;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getPrivatecontent() {
            return privatecontent;
        }

        public void setPrivatecontent(String privatecontent) {
            this.privatecontent = privatecontent;
        }

        public String getPubliccontent() {
            return publiccontent;
        }

        public void setPubliccontent(String publiccontent) {
            this.publiccontent = publiccontent;
        }

        public String getRegionid() {
            return regionid;
        }

        public void setRegionid(String regionid) {
            this.regionid = regionid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValidtime() {
            return validtime;
        }

        public void setValidtime(String validtime) {
            this.validtime = validtime;
        }
    }


}
