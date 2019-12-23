package com.xms.limowallet.db;

import android.content.Context;

public class UserInfo {


    /**
     * @param context
     * @return
     */
    public static boolean isLogin(Context context) {
        String loginName = new MapDBService(context).getString("privatekey", "").trim();
        String id = new MapDBService(context).getString("userId", "0");
        if (!"".equals(loginName) && !id.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取私钥
     *
     * @param context
     * @return
     */
    public static String getUserName(Context context) {
        return new MapDBService(context).getString("privatekey", "none");
    }

    /**
     * 获取公钥
     *
     * @param context
     * @return
     */
    public static String getUserID(Context context) {
        return new MapDBService(context).getString("publickey", "0");
    }

    /**
     * 保存用户信息到share-dp
     */
    public static void recordData2sp(Context context, String publickey,
                                     String privatekey) {
        //创建钱包成功后 保存数据
        MapDBService db = new MapDBService(context);
        db.putString("publickey", publickey);
        db.putString("privatekey", privatekey);
    }

    /**
     * 删除用户信息
     *
     * @param context
     * @return
     */
    public static boolean removeLogin(Context context) {
        try {
            MapDBService db = new MapDBService(context);
            db.remove("publickey");
            db.remove("privatekey");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param context
     * @return
     */
    public static String getPublicKey(Context context) {
        return new MapDBService(context).getString("publickey", "none");
    }

    /**
     * @param context
     * @return
     */
    public static String getPrivateKey(Context context) {
        return new MapDBService(context).getString("privatekey", "none");
    }
}
