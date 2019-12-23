package com.xms.limowallet.Plugins.ThemeManager;

import android.content.Context;

public class ThemeManager {
    private static ThemeManager static_ThemeManager;
    private Theme currentTheme;

    public static ThemeManager shareInstance(Context context) {
        if (ThemeManager.static_ThemeManager == null) {
            ThemeManager.static_ThemeManager = new ThemeManager(context);
        }
        return static_ThemeManager;
    }

    public static int getColorWithKey(Context context, ThemeColor key) {
        return shareInstance(context).getColorWithKey(key);
    }

    private ThemeManager(Context context) {
        // 1.如果用户切换过主题，则使用已切换的主题
        // 2.如果用户没有主题，则使用Default主题

        this.currentTheme = Theme.createThemeWithAsstesFileName(context, "default.json");
    }

    public int getColorWithKey(ThemeColor key) {
        return this.currentTheme.getColor(key);
    }

    public void changeThemeWithContent( String jsonContent ) {
        this.currentTheme = Theme.createThemeWithJsonContent(jsonContent);
    }
}
