package com.xms.limowallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xms.limowallet.R;
import com.xms.limowallet.widget.TipsPopup;

/**
 * 备份提示
 */
public class BackupHintsActivity extends BaseActivity {
    Context context;
    private ImageView iv_return;//返回按钮
    private LinearLayout ll_establish;//下一步按钮
    TipsPopup tipsPopup;//popup弹出框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_hints);
        context = getApplicationContext();
        initView();
    }

    //初始化View
    private void initView() {
        iv_return = findViewById(R.id.iv_return);
        iv_return.setOnClickListener(onClickListener);
        ll_establish = findViewById(R.id.ll_establish);
        ll_establish.setOnClickListener(onClickListener);
    }

    //控件点击操作处理
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_return:
                    finish();//关闭当前界面
                    break;
                case R.id.ll_establish://立即备份按钮
                    initPopup();
                    break;
                case R.id.btn_cancel://popupwindow中的取消按钮
                    tipsPopup.dismiss();
                    break;
                case R.id.btn_got://popupwindow中的确认按钮
                    Intent got = new Intent(context, BackupMnemonicsActivity.class);
                    startActivity(got);
                    tipsPopup.dismiss();
            }
        }
    };

    //popup弹出窗口
    public void initPopup() {
        String string = getResources().getString(R.string.no_screenshots);
        tipsPopup = new TipsPopup(context, onClickListener, string);
        tipsPopup.showAtLocation(findViewById(R.id.ll_establish), Gravity.CENTER, 0, 0);
    }
}
