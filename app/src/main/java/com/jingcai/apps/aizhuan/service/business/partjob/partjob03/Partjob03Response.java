package com.jingcai.apps.aizhuan.service.business.partjob.partjob03;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob01.Partjob01Response;

import java.util.ArrayList;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Partjob03Response extends BaseResponse<Partjob03Response.Partjob03Body> {

    public class Partjob03Body{
        private ArrayList<Partjob01Response.Body.Parttimejob> joininfo_list;

        public ArrayList<Partjob01Response.Body.Parttimejob> getJoininfo_list() {
            return joininfo_list;
        }

        public void setJoininfo_list(ArrayList<Partjob01Response.Body.Parttimejob> joininfo_list) {
            this.joininfo_list = joininfo_list;
        }

//        public class Joininfo{
//            private String id;
//            private String title;
//            private String logopath;
//            private String salary;
//            private String salaryunit;
//            private String workdays;
//            private String settlelength;
//            private String distance;
//            private String label;
//
//            public String getId() {
//                return id;
//            }
//
//            public void setId(String id) {
//                this.id = id;
//            }
//
//            public String getTitle() {
//                return title;
//            }
//
//            public void setTitle(String title) {
//                this.title = title;
//            }
//
//            public String getLogopath() {
//                return logopath;
//            }
//
//            public void setLogopath(String logopath) {
//                this.logopath = logopath;
//            }
//
//            public String getSalary() {
//                return salary;
//            }
//
//            public void setSalary(String salary) {
//                this.salary = salary;
//            }
//
//            public String getSalaryunit() {
//                return salaryunit;
//            }
//
//            public void setSalaryunit(String salaryunit) {
//                this.salaryunit = salaryunit;
//            }
//
//            public String getWorkdays() {
//                return workdays;
//            }
//
//            public void setWorkdays(String workdays) {
//                this.workdays = workdays;
//            }
//
//            public String getSettlelength() {
//                return settlelength;
//            }
//
//            public void setSettlelength(String settlelength) {
//                this.settlelength = settlelength;
//            }
//
//            public String getDistance() {
//                return distance;
//            }
//
//            public void setDistance(String distance) {
//                this.distance = distance;
//            }
//
//            public String getLabel() {
//                return label;
//            }
//
//            public void setLabel(String label) {
//                this.label = label;
//            }
//        }
    }
}
