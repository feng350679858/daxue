package com.jingcai.apps.aizhuan.service.business.base.base04;

import com.jingcai.apps.aizhuan.service.base.BaseResponse;

import java.util.List;

public class Base04Response extends BaseResponse<Base04Response.Body> {
    public static class Body {
        private List<Region> region_list;

        public List<Region> getRegion_list() {
            return region_list;
        }

        public void setRegion_list(List<Region> region_list) {
            this.region_list = region_list;
        }

        public static class Region {
            private String regionid;
            private String regionname;

            public String getRegionid() {
                return regionid;
            }

            public void setRegionid(String regionid) {
                this.regionid = regionid;
            }

            public String getRegionname() {
                return regionname;
            }

            public void setRegionname(String regionname) {
                this.regionname = regionname;
            }
        }
    }
}
