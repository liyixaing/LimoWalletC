package com.xms.limowallet.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.xms.limowallet.bean.WallerBean;

@Database(entities = {WallerBean.class}, version = 1, exportSchema = false)
public abstract class MyDb extends RoomDatabase {
    private static MyDb instance = null;
    private static String DB_NAME = "MyDatabase.db";

    public static MyDb getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static MyDb create(Context context) {
        return Room.databaseBuilder(context, MyDb.class, DB_NAME).build();
    }


    public abstract WallerDao getWallerDao();
}
