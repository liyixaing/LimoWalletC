package com.xms.limowallet.Model;

import java.util.List;
import java.util.Map;

public class HomeModel {
    private String backgroundColor;
    private String itemNormalColor;
    private String itemSelectedColor;
    public List<itemsBean> items;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getItemNormalColor() {
        return itemNormalColor;
    }

    public void setItemNormalColor(String itemNormalColor) {
        this.itemNormalColor = itemNormalColor;
    }

    public String getItemSelectedColor() {
        return itemSelectedColor;
    }

    public void setItemSelectedColor(String itemSelectedColor) {
        this.itemSelectedColor = itemSelectedColor;
    }

    public List<itemsBean> getItems() {
        return items;
    }

    public void setItems(List<itemsBean> items) {
        this.items = items;
    }

    public static class itemsBean {
        private String pagePath;
        private String iconPath;
        private Map<String, String> texts;

        public String getPagePath() {
            return pagePath;
        }

        public void setPagePath(String pagePath) {
            this.pagePath = pagePath;
        }

        public String getIconPath() {
            return iconPath;
        }

        public void setIconPath(String iconPath) {
            this.iconPath = iconPath;
        }

        public Map<String, String> getTexts() {
            return texts;
        }

        public void setTexts(Map<String, String> texts) {
            this.texts = texts;
        }
    }
}
