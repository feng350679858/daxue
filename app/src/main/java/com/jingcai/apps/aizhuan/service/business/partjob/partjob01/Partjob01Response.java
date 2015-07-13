package com.jingcai.apps.aizhuan.service.business.partjob.partjob01;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.ArrayList;

/**
 * Created by Json Ding on 2015/4/29.
 */
public class Partjob01Response extends BaseResponse<Partjob01Response.Body> {

    public class Body {
        private ArrayList<Parttimejob> parttimejob_list;

        public ArrayList<Parttimejob> getParttimejob_list() {
            return parttimejob_list;
        }

        public void setParttimejob_list(ArrayList<Parttimejob> parttimejob_list) {
            this.parttimejob_list = parttimejob_list;
        }

        public class Parttimejob {
            private String id;
            private String title;
            private String logopath;
            private String salary;
            private String salaryunit;
            private String workdays;
            private String settlelength;
            private String distance;
            private String label;
            private String worktimetype;

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

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public String getWorktimetype() {
                return worktimetype;
            }

            public void setWorktimetype(String worktimetype) {
                this.worktimetype = worktimetype;
            }
        }
    }
}
