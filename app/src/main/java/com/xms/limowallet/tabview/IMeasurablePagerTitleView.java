package com.xms.limowallet.tabview;

/**
 * 可测量内容区域的指示器标题
 */
public interface IMeasurablePagerTitleView extends IPagerTitleView {
    int getContentLeft();

    int getContentTop();

    int getContentRight();

    int getContentBottom();
}
