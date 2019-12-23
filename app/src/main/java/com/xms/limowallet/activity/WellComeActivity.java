package com.xms.limowallet.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xms.limowallet.Plugins.ThemeManager.ThemeColor;
import com.xms.limowallet.Plugins.ThemeManager.ThemeManager;
import com.xms.limowallet.R;
import com.xms.limowallet.popupwindow.LanguagePopup;
import com.xms.limowallet.utils.ToastUtils;

import java.util.Locale;

/**
 * 钱包创建导入界面
 */
public class WellComeActivity extends BaseActivity {
    Context context;
    private LinearLayout ll_create_wallet, ll_import_wallet;//创建钱包,导入钱包

    LanguagePopup languagePopup;
    private TextView tv_create_wallet;
    private TextView tv_wellet_tips;
    private TextView tv_establish_title;
    private TextView tv_blockchain;
    private LinearLayout ll_establish_icon;
    private LinearLayout ll_import_icon;
    private ImageView iv_logo;
    private TextView tv_title;
    private ImageView iv_icon_title;
    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_come);
        context = getApplicationContext();
        initView();

    }

    //初始化View
    private void initView() {
        //点击事件绑定
        ll_create_wallet = findViewById(R.id.ll_create_wallet);
        ll_create_wallet.setOnClickListener(onClickListener);
        ll_import_wallet = findViewById(R.id.ll_import_wallet);
        ll_import_wallet.setOnClickListener(onClickListener);

        tv_create_wallet = findViewById(R.id.tv_create_wallet);
        tv_establish_title = findViewById(R.id.tv_establish_title);
        tv_blockchain = findViewById(R.id.tv_blockchain);
        tv_wellet_tips = findViewById(R.id.tv_wellet_tips);
        ll_establish_icon = findViewById(R.id.ll_establish_icon);
        ll_import_icon = findViewById(R.id.ll_import_icon);
        iv_logo = findViewById(R.id.iv_logo);
        tv_title = findViewById(R.id.tv_title);
        iv_icon_title = findViewById(R.id.iv_icon_title);
        iv_icon_title.setOnClickListener(onClickListener);

        ColorSetting();//当前界面的颜色匹配
    }

    //颜色赋值
    public void ColorSetting() {
        tv_create_wallet.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.FrameBox_Content));
        tv_title.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.ButtonDanger_Title));
        tv_establish_title.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.TabBarItemNomal));
        tv_wellet_tips.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.TabBarItemNomal));

    }

    //点击事件处理
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_create_wallet://创建钱包
                    CreateWallet();
                    break;
                case R.id.ll_import_wallet://导入钱包
//                    languagepopup();//切换语言弹出框
                    ImportWallet();//导入钱包跳转

                    //跳转到测试开启本地服务器的界面
//                    Intent intent = new Intent(context, ServerActivity.class);
//                    startActivity(intent);

                    break;
                case R.id.tv_china://中文选择
                    changeAppLanguage(Locale.SIMPLIFIED_CHINESE);//中文切换
                    languagePopup.dismiss();//关闭选择语言弹窗
                    break;
                case R.id.tv_english://英文选择
                    changeAppLanguage(Locale.US);//英文切换
                    languagePopup.dismiss();//关闭选择语言弹窗`
                    break;
                case R.id.iv_icon_title:
                    initback();
                    break;
            }
        }
    };

    //创建钱包按钮点击
    public void CreateWallet() {
        if (type == 0) {
            tv_create_wallet.setText(getResources().getString(R.string.first_use));//点击创建钱包改变文本显示
            tv_establish_title.setText(getResources().getString(R.string.establish_title));//文本提示
            tv_blockchain.setText(getResources().getString(R.string.skilled_use));//熟练使用内容
            tv_wellet_tips.setText(getResources().getString(R.string.import_hint));//熟练使用内容提示
            ll_establish_icon.setVisibility(View.GONE);//隐藏图标
            ll_import_icon.setVisibility(View.GONE);//隐藏图标
            type = 1;
        } else {
            //跳转到创建钱包
            Intent CreateActivity = new Intent(context, KeystoreCreateActivity.class);
            CreateActivity.putExtra("type", "0");//传入一个类型来判断是熟练用户还是小白
            startActivity(CreateActivity);
        }
    }

    //导入钱包
    public void ImportWallet() {
        if (type == 0) {//type为0则是导入钱包, 否则跳转创建钱包
            Intent intent = new Intent(context, KeystoreImportActivity.class);
            startActivity(intent);
        } else {
            Intent CreateActivity = new Intent(context, KeystoreCreateActivity.class);
            CreateActivity.putExtra("type", "1");//同上
            startActivity(CreateActivity);
        }
    }

    //点击按钮回退到创建导入
    public void initback() {
        if (type == 1) {
            tv_create_wallet.setText(getResources().getString(R.string.create_wallet));
            tv_establish_title.setText(getResources().getString(R.string.etc_wallet));
            tv_blockchain.setText(getResources().getString(R.string.import_wallet));
            tv_wellet_tips.setText(getResources().getString(R.string.wallet_assistant));
            ll_establish_icon.setVisibility(View.VISIBLE);//隐藏图标
            ll_import_icon.setVisibility(View.VISIBLE);//隐藏图标
            type = 0;
        } else {
            ToastUtils.showToast(context, "没有可返回的界面了");
        }
    }

    //弹出语言选择框
    public void languagepopup() {
        languagePopup = new LanguagePopup(context, onClickListener);
        languagePopup.showAsDropDown(findViewById(R.id.ll_import_wallet), Gravity.CENTER, 0, 0);
    }

    //切换语言
    public void changeAppLanguage(Locale locale) {
        //得到显示指示
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        //获取设置对象
        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        //更新显示的语言
        getResources().updateConfiguration(configuration, metrics);
        //重新启动Activity
        Intent intent = new Intent(this, WellComeActivity.class);
        //用于开始到达新的Activity之前移除之前的Activity。这样我们点击back键就会直接回桌面了
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //重写返回按钮
    private long timeMillis;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - timeMillis) > 2000) {
                ToastUtils.showToast(context, getResources().getString(R.string.press_exit_again));
                timeMillis = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
