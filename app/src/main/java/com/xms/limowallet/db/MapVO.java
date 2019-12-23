package com.xms.limowallet.db;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "n_map")
public class MapVO {
    //VO 相当于Bean
    /**
     * id
     */
    @Id(column = "id")
    @Column(column = "id")
    public int id;

    /**
     * key值
     */
    @Column(column = "key")
    public String key;

    /**
     * value值
     */
    @Column(column = "value")
    public String value;

    public MapVO() {
        super();
    }

    public MapVO(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public MapVO(int id, String key, String value) {
        super();
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
