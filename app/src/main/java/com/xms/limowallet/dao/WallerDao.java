package com.xms.limowallet.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.xms.limowallet.bean.WallerBean;

import java.util.List;

@Dao
public interface WallerDao {
    @Query("SELECT * FROM wallerbean")
    List<WallerBean> getAllUsers();

    @Insert
    void insert(WallerBean... wallerBeans);

    @Update
    void updatea(WallerBean... wallerBeans);

    @Delete
    void deletea(WallerBean... wallerBeans);
}
