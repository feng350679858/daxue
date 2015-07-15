package com.jingcai.apps.aizhuan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by lejing on 15/5/3.
 */
public class BitmapUtil {
    private RequestQueue queue;
    private ImageLoader imageLoader;
    private static ImageLoader.ImageCache bitmapCache = new ImageLoader.ImageCache() {
        private int maxSize = 10 * 1024 * 1024;
        private LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(maxSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }
    };

    public BitmapUtil(Context context) {
        this();
    }

    public BitmapUtil() {
        this.queue = VolleyQueueUtil.getInstance().getRequestQueue();
        this.imageLoader = new ImageLoader(queue, bitmapCache);
    }

    /**
     * 根据网址获得图片，优先从本地获取，本地没有则从网络下载
     *
     * @param imageView
     * @param imgpath
     */
    public void getImage(final ImageView imageView, final String imgpath) {
        getImage(imageView, imgpath, false, 0);
    }

    /**
     * 根据网址获得图片，优先从本地获取，本地没有则从网络下载
     *
     * @param imageView
     * @param imgpath
     * @param defaultImageResId
     */
    public void getImage(final ImageView imageView, final String imgpath, @DrawableRes final int defaultImageResId) {
        getImage(imageView, imgpath, false, defaultImageResId, defaultImageResId);
    }

    /**
     * 根据网址获得图片，优先从本地获取，本地没有则从网络下载
     *
     * @param imageView
     * @param imgpath
     * @param saveToDisk
     * @param defaultImageResId
     */
    public void getImage(final ImageView imageView, final String imgpath, final boolean saveToDisk, final int defaultImageResId) {
        getImage(imageView, imgpath, saveToDisk, defaultImageResId, defaultImageResId);
    }

    /**
     * 根据网址获得图片，优先从本地获取，本地没有则从网络下载
     *
     * @param imageView
     * @param imgpath
     * @param saveToDisk
     * @param defaultImageResId
     * @param errorImageResId
     */
    public void getImage(final ImageView imageView, final String imgpath, final boolean saveToDisk, final int defaultImageResId, final int errorImageResId) {
        if(null == imageView){
            return;
        }
        if (null == imgpath) {
            imageView.setImageResource(defaultImageResId);
            return;
        }
        File file = getImageFile(imgpath);
        if (file.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
            return;
        }
        if (defaultImageResId != 0) {
            imageView.setImageResource(defaultImageResId);
        }
        loadImage(imgpath, saveToDisk, new Callback() {
            @Override
            public void fail() {
                if (errorImageResId != 0) {
                    imageView.setImageResource(errorImageResId);
                }
            }

            @Override
            public void success(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });

    }

    public interface Callback {
        void fail();

        void success(Bitmap bitmap);
    }

    public void loadImage(final String imgpath, final boolean saveToDisk, final Callback callback) {
        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != callback) {
                    callback.fail();
                }
            }
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    if (null != callback) {
                        callback.success(bitmap);
                    }
                    if (saveToDisk) {
                        File file = getImageFile(imgpath);
                        saveToDisk(bitmap, file);
                    }
                }
            }
        };
        String imgurl = imgpath;
        if (!imgpath.startsWith("http://")) {
            imgurl = GlobalConstant.imageUrl + imgpath;
        }
        imageLoader.get(imgurl, listener);
    }

    private void saveToDisk(Bitmap bitmap, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (Exception e1) {
                out = null;
            }
        }
    }

    public static File getImageFile(String imgpath) {
        String imageName = Md5.encode(imgpath);
        return new File(getPath(), imageName);
    }

//    public void doTrans2(String imgpath, final ImageView imageView){
//        ImageRequest imageRequest = new ImageRequest(imgpath,
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap response) {
//                        imageView.setImageBitmap(response);
//                    }
//                }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                imageView.setImageResource(R.drawable.default_image);
//            }
//        });
//        queue.add(imageRequest);
//    }


    /**
     * 获取图片的存储目录，在有sd卡的情况下为 “/sdcard/apps_images/本应用包名/cach/images/”
     * 没有sd的情况下为“/data/data/本应用包名/cach/images/”
     *
     * @return 本地图片存储目录
     */
    private static String getPath() {
        String path = null;
        String packageName = GlobalConstant.packageName + "/cache/images/";
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            path = "/sdcard/apps_images/" + packageName;
        } else {
            path = "/data/data/" + packageName;
        }
        File file = new File(path);
        boolean isExist = file.exists();
        if (!isExist) {
            file.mkdirs();
        }
        return file.getPath();
    }

}
