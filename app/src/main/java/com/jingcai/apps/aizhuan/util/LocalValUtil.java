package com.jingcai.apps.aizhuan.util;

/**
 * Created by Administrator on 2015/7/17.
 */
public class LocalValUtil {

    private static ThreadLocal local = new ThreadLocal();

    public static Object getVal(){
        return local.get();
    }

    public static void setVal(Object val){
        local.set(val);
    }
}
