package com.xms.limowallet.Plugins.ThemeManager;

import androidx.annotation.NonNull;

public enum ThemeColor {

    //NavBG": "导航条背景色(不支持渐变)"
    NavBG,

    //NavTitle": "导航条标题色"
    NavTitle,

    //NavItem": "导航条左右两侧Item颜色"
    NavItem,

    //ContentViewBG_S": "内容页主背景色渐变起始"
    ContentViewBG_S,

    //ContentViewBG_E": "内容页主背景色渐变终止"
    ContentViewBG_E,

    //ContentTitle": "内容页中的标题颜色"
    ContentTitle,

    //ContentSubTitle": "内容页中的子标题颜色",
    ContentSubTitle,

    //ContentDescTitle": "内容页中的描述颜色",
    ContentDescTitle,

    //PopViewBG": "底部弹框背景色",
    PopViewBG,

    //PopViewNavBG": "底部弹框导航颜色(不支持渐变)",
    PopViewNavBG,

    //PopViewNavItem": "底部弹框导航左右两侧Item颜色",
    PopViewNavItem,

    //PopViewContentIcon": "底部弹框其他icon颜色",
    PopViewContentIcon,

    //ButtonInfo_BG": "Info级按钮背景色",
    ButtonInfo_BG,

    //ButtonInfo_Title": "Info级按钮标题色",
    ButtonInfo_Title,

    //ButtonInfo_Desc": "info级按钮描述色",
    ButtonInfo_Desc,

    //ButtonWarrning_BG": "Warrning级按钮背景渐色",
    ButtonWarrning_BG,

    //ButtonWarrning_Title": "Warrning级按钮标题色",
    ButtonWarrning_Title,

    //ButtonWarrning_Desc": "Warrning级按钮描述色",
    ButtonWarrning_Desc,

    //ButtonDanger_BG": "Danger级按钮背景色",
    ButtonDanger_BG,

    //ButtonDanger_Title": "Danger级按钮标题色",
    ButtonDanger_Title,

    //ButtonDanger_Desc": "Danger级按钮描述色",
    ButtonDanger_Desc,

    //Slider_BG_Left": "拖动进度条左侧颜色",
    Slider_BG_Left,

    //Slider_BG_Right": "拖动进度条右侧颜色",
    Slider_BG_Right,

    //Slider_Item": "拖动进度条其他附件Item颜色",
    Slider_Item,

    //Cell_BG": "表格类各行背景色",
    Cell_BG,

    //Cell_Title": "表格类各行页标题颜色",
    Cell_Title,

    //Cell_Content": "表格类各行内容颜色",
    Cell_Content,

    //Cell_BreakLine": "表格类各行分割线颜色",
    Cell_BreakLine,

    //InputerBG": "输入框背景",
    InputerBG,

    //InputerPlaceholder": "输入框的提示文字颜色",
    InputerPlaceholder,

    //InputerText": "输入框的文字颜色",
    InputerText,

    //FrameBox_BG": "子内容框背景(不支持渐变)",
    FrameBox_BG,

    //FrameBox_Content": "子内容框内容颜色",
    FrameBox_Content,

    //FrameBox_Desc": "子内容框二级内容颜色",
    FrameBox_Desc,

    //FrameBox_Highlight": "子内容框高亮色",
    FrameBox_Highlight,

    //FrameBox_Placeholder": "自内容框提示文字颜色",
    FrameBox_Placeholder,

    //TabBarBG": "底部导航条背景颜色",
    TabBarBG,

    //TabBarItemNomal": "底部导航条按钮和标题的正常状态颜色",
    TabBarItemNomal,

    //TabBarItemSelected": "底部导航条按钮和文字的选中时颜色",
    TabBarItemSelected;

    @NonNull
    @Override
    public String toString() {
        return "ThemeColor_" + super.toString();
    }


}