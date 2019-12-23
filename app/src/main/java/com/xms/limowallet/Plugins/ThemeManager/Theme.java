package com.xms.limowallet.Plugins.ThemeManager;

import android.content.Context;
import android.graphics.Color;

import com.xms.limowallet.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class Theme {

    String ThemeKey;
    String Version;
    String Designer;

    HashMap<String, String> ThemeName;
    HashMap<String, String> Colors;

    private Theme(String jsonContent) {

        try {

            JSONObject jsonObject = new JSONObject(jsonContent);

            this.ThemeKey = jsonObject.getString("ThemeKey");
            this.Version = jsonObject.getString("Version");
            this.Designer = jsonObject.getString("Designer");

            /// 解析ThemeNames字段，类型为HashMap
            JSONObject themeNameJObj = jsonObject.getJSONObject("ThemeName");
            Iterator nameIt = themeNameJObj.keys();
            this.ThemeName = new HashMap<>();
            while (nameIt.hasNext()) {

                String key = (String) nameIt.next();
                String value = themeNameJObj.getString(key);

                this.ThemeName.put(key, value);
            }

            /// Colors，类型为HashMap
            JSONObject colorsJObj = jsonObject.getJSONObject("Colors");
            Iterator colorIt = colorsJObj.keys();
            this.Colors = new HashMap<>();
            while (colorIt.hasNext()) {

                String key = (String) colorIt.next();
                String value = colorsJObj.getString(key);

                this.Colors.put(key, value);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Theme createThemeWithFile(Context context, File file) {

        /// 1.从file对象中读取所有内容
        /// 2.内容转成JSONContentString 如{"key":"value","key2":"value2"}
        /// 3.直接在此调用 createThemeWithJsonContent 返回即可
        return null;
    }

    public static Theme createThemeWithURL(Context context, URL url) {
        /// 1.根据URL下载数据
        /// 2.下载的数据转成JSONcontentString如{"key":"value","key2":"value2"}
        /// 3.直接在此调用 createThemeWithJsonContent 返回即可
        return null;
    }

    static Theme createThemeWithJsonContent(String jsonContnet) {

        return new Theme(jsonContnet);

    }

    static Theme createThemeWithAsstesFileName(Context context, String filename) {

        String json = JsonUtil.getAsstesJson(context, filename);

        return new Theme(json);
    }

    int getColor(ThemeColor key) {

        if (!this.Colors.containsKey(key.toString())) {

            return 0;

        } else {

            String hexColor = this.Colors.get(key.toString()).substring(1).toUpperCase();

            int color = Integer.parseInt(hexColor, 16);

            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0x00FF00) >> 8;
            int b = (color & 0x0000FF);

            return Color.rgb(r, g, b);

        }

    }

}
