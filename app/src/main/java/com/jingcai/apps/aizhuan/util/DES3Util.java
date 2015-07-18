package com.jingcai.apps.aizhuan.util;

import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2015/7/17.
 */
public class DES3Util {
    private final static String DES3_KEY = "514345744E41596C4E41496C";
    private final static String algorithm = "DESede/ECB/PKCS7Padding";
//	private final static String algorithm = "DESede/ECB/PKCS5Padding";

    private static DES3Util instance;

    public static DES3Util getInstance() {
        if (null == instance) {
            synchronized(DES3Util.class){
                if(null == instance){
                    //Security.addProvider(new com.sun.crypto.provider.SunJCE());
                    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());//添加PKCS7Padding支持
                    instance = new DES3Util();
                }
            }
        }
        return instance;
    }

    /**
     * 获取密钥
     * @param keyString  字符串key
     * @return
     */
    private Key getKey(String keyString) {
        Key key = new SecretKeySpec(keyString.getBytes(), algorithm);
        return key;
    }

    /**
     * 获取密钥
     * @param keyByte  字节数组key
     * @return
     */
    private Key getKey(byte [] keyByte) {
        Key key = new SecretKeySpec(keyByte, algorithm);
        return key;
    }

    /**
     * 3DES加密
     * @param sourceString  加密前的原字符串
     * @param keyString     3des加密的key
     * @return String       加密之后的16进制密文
     * @throws Exception
     */
    public String encrypt(String sourceString,String keyString) throws Exception {
        Cipher encryptCipher = Cipher.getInstance(algorithm);
        Key key = getKey(keyString);
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        return toHexString(encrypt(sourceString.getBytes(),keyString.getBytes()));
    }

    /**
     * 3DES加密
     * @param sourceByte  加密前的原字符串字节数组
     * @param keyByte     3des加密的key字节数组
     * @return byte       加密之后的密文字节数组
     * @throws Exception
     */
    public byte[] encrypt(byte[] sourceByte, byte[] keyByte) throws Exception {
        Cipher encryptCipher = Cipher.getInstance(algorithm);
        Key key = getKey(keyByte);
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptCipher.doFinal(sourceByte);
    }

    /**
     * 3DES解密
     * @param sourceByte 需解密的字节数组
     * @param keyByte    解密的密钥字节数组
     * @return byte      解密后的字节数组
     * @throws Exception
     */
    public byte[] decrypt(byte[] sourceByte,byte[] keyByte) throws Exception {
        Cipher decryptCipher = Cipher.getInstance(algorithm);
        Key key = getKey(keyByte);
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
        return decryptCipher.doFinal(sourceByte);
    }

    /**
     * 3DES解密
     * @param sourceString 	需解密的密文字符串
     * @param keyString    	解密的密钥
     * @return byte      	解密后的字符串
     */
    public String decrypt(String sourceString,String keyString) throws Exception {
        return new String(decrypt(fromHexString(sourceString),keyString.getBytes()));
    }

    public static String decrypt(String sourceString){
        try {
            Cipher decryptCipher = Cipher.getInstance(algorithm);
            Key key = new SecretKeySpec(DES3_KEY.getBytes(), algorithm);
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
            return new String(decryptCipher.doFinal(fromHexString(sourceString)));
        }catch (Exception e){
            return null;
        }
    }


    /**
     * 字节数组转16进制数组
     * @param digestByte
     * @return
     */
    private byte[] toHex(byte[] digestByte) {
        byte[] rtChar = new byte[digestByte.length * 2];
        for (int i = 0; i < digestByte.length; i++) {
            byte b1 = (byte) (digestByte[i] >> 4 & 0x0f);
            byte b2 = (byte) (digestByte[i] & 0x0f);
            rtChar[i * 2] = (byte) (b1 < 10 ? b1 + 48 : b1 + 55);
            rtChar[i * 2 + 1] = (byte) (b2 < 10 ? b2 + 48 : b2 + 55);
        }
        return rtChar;
    }

    /**
     *
     * 字节数组转16进制字符串
     * @param digestByte
     * @return
     */
    private String toHexString(byte[] digestByte) {
        return new String(toHex(digestByte));
    }

    private static byte[] fromHex(byte[] sc) {
        byte[] res = new byte[sc.length / 2];
        for (int i = 0; i < sc.length; i++) {
            byte c1 = (byte) (sc[i] - 48 < 17 ? sc[i] - 48 : sc[i] - 55);
            i++;
            byte c2 = (byte) (sc[i] - 48 < 17 ? sc[i] - 48 : sc[i] - 55);
            res[i / 2] = (byte) (c1 * 16 + c2);
        }
        return res;
    }

    public static String encrypt(String password){
        DES3Util tu = DES3Util.getInstance();
        try {
            password = tu.encrypt(password, DES3_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    private static byte[] fromHexString(String hex) {
        return fromHex(hex.getBytes());
    }

    /**
     public static void main(String[] args) throws Exception {
     TripleDESUtil pk7 = TripleDESUtil.getInstance();
     String key = "4irksuwl459ektueot943OFE";
     String string = "lianliantest";
     String miwenString = pk7.encrypt(string, key);
     System.out.println("加密之后密文:" + miwenString);

     String mingwenString = pk7.decrypt(miwenString, key);
     System.out.println("解密之后明文:" + mingwenString);
     }
     */
}
