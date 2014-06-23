package com.calvin.security.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {
    public static String encode(String pwd) {
        try {
            //拿到MD5加密的对象
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(pwd.getBytes());//返回一个加密后的字节数组
            StringBuffer sb = new StringBuffer();
            String temp;
            for (int i = 0; i < bytes.length; i++) {
                //将字节转换为16进制的字符串
                temp = Integer.toHexString(0xff & bytes[i]);
                if (temp.length() == 1) {//如果该字符串只有一个字符,就要补0
                    sb.append("0" + temp);
                } else {
                    sb.append(temp);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有该加密算法", e);
        }
    }

}
