package com.jingcai.apps.aizhuan.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5摘要算法
 */
public class Md5 {

    public static String encode(String plainText) {
        return encode(plainText, "UTF-8").toUpperCase();
    }

    public static String encode16(String plainText) {
        String encodeStr = encode(plainText);
        if (null != encodeStr) {
            return encodeStr.substring(8, 24).toUpperCase();
        }
        return null;
    }

    public static String encode(String plainText, String charsetName) {
        if (null != plainText) {
            try {
                byte[] bytes = plainText.getBytes(charsetName);
                return encode(bytes);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return null;
    }

    public static String encode(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
