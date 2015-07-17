package com.jingcai.apps.aizhuan.service.business.partjob.partjob02;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by chenchao on 2015/7/14.
 */
public class Partjob02Response extends BaseResponse<Partjob02Response.Partjob02Body> {

    public class Partjob02Body {
        private Parttimejob parttimejob;

        public Parttimejob getParttimejob() {
            return parttimejob;
        }
        public void setParttimejob(Parttimejob parttimejob){
            this.parttimejob=parttimejob;
        }

        public class Parttimejob {
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
            private String heightupperlimit;
            private String heightlowerlimit;
            private String genderlimit;
            private String healthlimit;
            private String remarks;
            private String joinnum;
            private String workernum;
            private String isjoin;
            private String phone;
            private String status;
            private String gisx;
            private String gisy;
            private String ismutifyaddr;
            private Schoolmate_list schoolmate_list;

            public class Schoolmate_list{
                private String logopath;
                private String name;
                private String gender;
                private String phone;
                private String college;

                public String getLogopath() {
                    return logopath;
                }

                public void setLogopath(String logopath) {
                    this.logopath = logopath;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getGender() {
                    return gender;
                }

                public void setGender(String gender) {
                    this.gender = gender;
                }

                public String getPhone() {
                    return phone;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }

                public String getCollege() {
                    return college;
                }

                public void setCollege(String college) {
                    this.college = college;
                }
            }

            public String getHeightupperlimit() {
                return heightupperlimit;
            }

            public void setHeightupperlimit(String heightupperlimit) {
                this.heightupperlimit = heightupperlimit;
            }

            public String getHeightlowerlimit() {
                return heightlowerlimit;
            }

            public void setHeightlowerlimit(String heightlowerlimit) {
                this.heightlowerlimit = heightlowerlimit;
            }

            public String getGenderlimit() {
                return genderlimit;
            }

            public void setGenderlimit(String genderlimit) {
                this.genderlimit = genderlimit;
            }

            public String getHealthlimit() {
                return healthlimit;
            }

            public void setHealthlimit(String healthlimit) {
                this.healthlimit = healthlimit;
            }

            public String getJoinnum() {
                return joinnum;
            }

            public void setJoinnum(String joinnum) {
                this.joinnum = joinnum;
            }

            public String getRemarks() {
                return remarks;
            }

            public void setRemarks(String remarks) {
                this.remarks = remarks;
            }

            public String getWorkernum() {
                return workernum;
            }

            public void setWorkernum(String workernum) {
                this.workernum = workernum;
            }

            public String getIsjoin() {
                return isjoin;
            }

            public void setIsjoin(String isjoin) {
                this.isjoin = isjoin;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public Schoolmate_list getSchoolmate_list() {
                return schoolmate_list;
            }

            public void setSchoolmate_list(Schoolmate_list schoolmate_list) {
                this.schoolmate_list = schoolmate_list;
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


        }
    }
}
