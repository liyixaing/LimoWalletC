package com.xms.limowallet.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.xms.limowallet.R;

public class InputPwdPopup extends PopupWindow {
    View view;
    Context context;

    public InputPwdPopup(Context context, View.OnClickListener onClickListener) {
        this.view = LayoutInflater.from(context).inflate(R.layout.popup_input_pwd, null);
        this.context = context;

        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.popup_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
//                        dismiss();//关闭弹窗  这里禁止外部点击
                    }
                }
                return true;
            }
        });
        this.setContentView(this.view);
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x60000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.dialog_from_bottom_anim);
    }
}
