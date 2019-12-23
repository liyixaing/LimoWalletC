package com.xms.limowallet.db;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

public class MapDBService {

    private DbUtils.DbUpgradeListener dbUpgradeListener = new DbUtils.DbUpgradeListener() {

        @Override
        public void onUpgrade(DbUtils arg0, int arg1, int arg2) {
        }
    }; // 升级监听事件

    public Context context;
    public DbUtils db;

    public MapDBService(Context context) {
        super();
        this.context = context;
        db = DbUtils.create(context, "DouWang",
                1, dbUpgradeListener);
    }

    /**
     * 保存键值对:String
     *
     * @return
     */
    public boolean putString(String key, String value) {
        try {
            MapVO map = getMap(key);
            if (map == null) {
                map = new MapVO(key, value);
                db.save(map);
            } else {
                map.setValue(value);
                db.update(map);
            }
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存键值对:Int
     *
     * @return
     */
    public boolean putInt(String key, int value) {
        try {
            MapVO map = getMap(key);
            if (map == null) {
                map = new MapVO(key, String.valueOf(value));
                db.save(map);
            } else {
                map.setValue(String.valueOf(value));
                db.update(map);
            }
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存键值对:boolean
     *
     * @return
     */
    public boolean putBoolean(String key, boolean value) {
        try {
            MapVO map = getMap(key);
            if (map == null) {
                if (value == true) {
                    map = new MapVO(key, "1");
                } else {
                    map = new MapVO(key, "0");
                }
                db.save(map);
            } else {
                if (value == true) {
                    map.setValue("1");
                } else {
                    map.setValue("0");
                }
                db.update(map);
            }
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key查询value:String
     *
     * @return
     */
    public MapVO getMap(String key) {

        MapVO entity = null;
        try {
            entity = db.findFirst(Selector.from(MapVO.class).where("key", "=",
                    key));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 根据key查询value:String
     *
     * @return
     */
    public String getString(String key, String defaultStr) {

        MapVO entity = null;
        try {
            entity = db.findFirst(Selector.from(MapVO.class).where("key", "=",
                    key));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return entity == null || entity.getValue() == null ? defaultStr
                : entity.getValue();
    }

    /**
     * 根据key查询value:int
     *
     * @return
     */
    public int getInt(String key, int defaultStr) {

        MapVO entity = null;
        try {
            entity = db.findFirst(Selector.from(MapVO.class).where("key", "=",
                    key));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (entity == null || entity.getValue() == null) {
            return defaultStr;
        } else {
            return Integer.valueOf(entity.getValue());
        }
    }

    /**
     * 根据key查询value:boolean
     *
     * @return
     */
    public boolean getBoolean(String key, boolean defaultStr) {

        MapVO entity = null;
        try {
            entity = db.findFirst(Selector.from(MapVO.class).where("key", "=",
                    key));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (entity == null || entity.getValue() == null) {
            return defaultStr;
        } else {
            if ("1".equals(entity.getValue())) {
                return true;
            } else if ("0".equals(entity.getValue())) {
                return false;
            } else {
                return defaultStr;
            }
        }
    }

    /**
     * 根据键删除数据
     *
     * @return
     */
    public void remove(String key) {

        try {
            db.delete(MapVO.class, WhereBuilder.b("key", "==", key));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
