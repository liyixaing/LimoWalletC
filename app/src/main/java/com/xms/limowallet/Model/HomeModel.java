package com.xms.limowallet.Model;

import java.util.List;

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
        public TextsBean texts;

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

        public TextsBean getTexts() {
            return texts;
        }

        public void setTexts(TextsBean texts) {
            this.texts = texts;
        }

        public enum TextsBean {
            en_us("en-us"), zh_cn("zh-cn");
            private String language;

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            TextsBean(String s) {
                this.language = s;
            }
        }
    }


}
