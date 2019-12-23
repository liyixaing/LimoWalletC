package com.xms.limowallet.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xms.limowallet.R;

public class LanguagePopup extends PopupWindow {

    View view;
    Context context;
    TextView tv_china, tv_english;

    public LanguagePopup(Context context, View.OnClickListener onClickListener) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.popup_language, null);

        tv_china = view.findViewById(R.id.tv_china);//初始化view
        tv_china.setOnClickListener(onClickListener);//绑定按钮点击事件
        tv_english = view.findViewById(R.id.tv_english);//初始化View
        tv_english.setOnClickListener(onClickListener);//绑定按钮点击事件

        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.popup_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height) {
                        dismiss();//关闭弹窗
                    }
                }
                return true;
            }
        });

        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x60000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.dialog_from_bottom_anim);
    }
}
