package com.xms.limowallet.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WallerBean {
    @PrimaryKey(autoGenerate = true)//主键是否自动增长，默认为false
    private int id;

    private String name;//名称
    private String address;//地址

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
