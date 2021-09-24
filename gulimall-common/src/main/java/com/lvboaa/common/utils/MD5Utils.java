package com.lvboaa.common.utils;

import org.springframework.util.DigestUtils;

public class MD5Utils {

    /**
     * 给密码盐值加密
     * @param pwd
     * @return
     * @throws Exception
     */
    public static String md5(String pwd){
        return DigestUtils.md5DigestAsHex((pwd+Constant.PASSWORD_SECRET).getBytes());
    }

    /**
     * 指定盐值加密密码
     * @param pwd
     * @param key
     * @return
     * @throws Exception
     */
    public static String md5(String pwd,String key){
        return DigestUtils.md5DigestAsHex((pwd+key).getBytes());
    }

    /**
     *  根据系统盐值加密验证密码是否正确
     * @param pwd 输入密码
     * @param tPwd 真实密码
     * @return
     * @throws Exception
     */
    public static boolean verify(String pwd,String tPwd){
        String md5Pwd= null;
        try {
            md5Pwd = md5(pwd, Constant.PASSWORD_SECRET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5Pwd.equals(tPwd);
    }

    /**
     * 根据指定盐值加密验证密码是否正确
     * @param pwd 输入密码
     * @param tPwd 真实密码
     * @param key 盐值
     * @return
     * @throws Exception
     */
    public static boolean verify(String pwd,String tPwd,String key){
        String md5Pwd = null;
        try {
            md5Pwd = md5(pwd, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5Pwd.equals(tPwd);
    }

}