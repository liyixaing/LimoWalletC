package com.xms.limowallet.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xms.limowallet.Plugins.ThemeManager.ThemeColor;
import com.xms.limowallet.Plugins.ThemeManager.ThemeManager;
import com.xms.limowallet.R;
import com.xms.limowallet.db.ListDataSave;
import com.xms.limowallet.widget.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 备份助记词
 */
public class BackupMnemonicsActivity extends BaseActivity {
    Context context;
    private ImageView iv_return;
    private FlowLayout flowLayout;
    TagFlowLayout id_flowlayout;
    private LinearLayout ll_establish;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_mnemonics);
        context = getApplicationContext();
        initView();
    }

    //初始化View
    public void initView() {
        //控件点击事件初始化
        iv_return = findViewById(R.id.iv_return);
        iv_return.setOnClickListener(onClickListener);
        ll_establish = findViewById(R.id.ll_establish);
        ll_establish.setOnClickListener(onClickListener);
        ListDataSave dataSave = new ListDataSave(context, "wallet");//从暂存空间获取助记词
        list = dataSave.getDataList("mnemonic");
        id_flowlayout = findViewById(R.id.id_flowlayout);//初始化控件
        MnemonicDisplay();//显示助记词
    }

    // 使用瀑布流式布局显示助记词
    public void MnemonicDisplay() {
        id_flowlayout.setAdapter(new TagAdapter<String>(list) {
            @Override
            public View getView(com.zhy.view.flowlayout.FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.item_backups, id_flowlayout, false);
                tv.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.FrameBox_Content));
                tv.setText(s);
                return tv;
            }
        });
    }

    //按钮点击事件
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_return:
                    finish();
                    break;
                case R.id.ll_establish:
                    Intent establish = new Intent(context, ConfirmMnemonicActivity.class);
                    startActivity(establish);
                    break;
            }
        }
    };
}
