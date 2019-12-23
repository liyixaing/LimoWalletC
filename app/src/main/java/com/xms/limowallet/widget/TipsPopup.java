package com.xms.limowallet.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xms.limowallet.R;

public class TipsPopup extends PopupWindow {

    Context mContext;
    View view;
    Button btn_cancel, btn_got;//取消， 知道了 两个按钮
    TextView tv_screenshots_not;

    public TipsPopup(Context context, View.OnClickListener onClickListener, String string) {
        this.mContext = context;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.popup_choose_wallet, null);

        tv_screenshots_not = view.findViewById(R.id.tv_screenshots_not);
        tv_screenshots_not.setText(string);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(onClickListener);
        btn_got = view.findViewById(R.id.btn_got);
        btn_got.setOnClickListener(onClickListener);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.popup_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height) {
//                        dismiss();//关闭弹窗
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
        this.setAnimationStyle(R.style.dialog_from_bottom_anim);//dialog_scale_anim  dialog_from_bottom_anim
    }
}
