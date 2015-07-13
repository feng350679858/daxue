package com.jingcai.apps.aizhuan.service.business.partjob.partjob04;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/4/30.
 */
public class Partjob04Response extends BaseResponse<Partjob04Response.Partjob04Body> {

    public class Partjob04Body {
        private Joininfo joininfo;

        public Joininfo getJoininfo() {
            return joininfo;
        }

        public void setJoininfo(Joininfo joininfo) {
            this.joininfo = joininfo;
        }

        public class Joininfo {
            private String id;
            private String title;
            private String logopath;
            private String salary;
            private String salaryunit;
            private String workdays;
            private String settlelength;
            private String worktype;
            private String endtime;
            private String address;
            private String workcontent;
            private String worktime;
            private String worktimetype;
            private String gisx;
            private String gisy;
            private String ismutifyaddr;
            private String iscancel;
            private String phone;
            private String distance;
            private String jobid;
            private String label;
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

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

            public String getLogopath() {
                return logopath;
            }

            public void setLogopath(String logopath) {
                this.logopath = logopath;
            }

            public String getSalary() {
                return salary;
            }

            public void setSalary(String salary) {
                this.salary = salary;
            }

            public String getSalaryunit() {
                return salaryunit;
            }

            public void setSalaryunit(String salaryunit) {
                this.salaryunit = salaryunit;
            }

            public String getWorkdays() {
                return workdays;
            }

            public void setWorkdays(String workdays) {
                this.workdays = workdays;
            }

            public String getSettlelength() {
                return settlelength;
            }

            public void setSettlelength(String settlelength) {
                this.settlelength = settlelength;
            }

            public String getWorktype() {
                return worktype;
            }

            public void setWorktype(String worktype) {
                this.worktype = worktype;
            }

            public String getEndtime() {
                return endtime;
            }

            public void setEndtime(String endtime) {
                this.endtime = endtime;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getWorkcontent() {
                return workcontent;
            }

            public void setWorkcontent(String workcontent) {
                this.workcontent = workcontent;
            }

            public String getWorktime() {
                return worktime;
            }

            public void setWorktime(String worktime) {
                this.worktime = worktime;
            }

            public String getWorktimetype() {
                return worktimetype;
            }

            public void setWorktimetype(String worktimetype) {
                this.worktimetype = worktimetype;
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

            public String getIsmutifyaddr() {
                return ismutifyaddr;
            }

            public void setIsmutifyaddr(String ismutifyaddr) {
                this.ismutifyaddr = ismutifyaddr;
            }

            public String getIscancel() {
                return iscancel;
            }

            public void setIscancel(String iscancel) {
                this.iscancel = iscancel;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getJobid() {
                return jobid;
            }

            public void setJobid(String jobid) {
                this.jobid = jobid;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }
        }

    }
}
