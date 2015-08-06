package com.jingcai.apps.aizhuan.service.upload;


import com.jingcai.apps.aizhuan.service.base.BaseResponse;

/**
 * Created by Json Ding on 2015/5/9.
 */
public class ImageResponse extends BaseResponse<ImageResponse.Body> {

        public class Body {
            private File file;

            public File getFile() {
                return file;
            }

            public void setFile(File file) {
                this.file = file;
            }

            public class File{
                private String path;

                public String getPath() {
                    return path;
                }

                public void setPath(String path) {
                    this.path = path;
                }
            }
        }

}
