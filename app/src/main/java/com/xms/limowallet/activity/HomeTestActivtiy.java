package com.xms.limowallet.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.xms.limowallet.R;
import com.xms.limowallet.adapter.MainAdapter;
import com.xms.limowallet.fragment.ApplicationFragment;
import com.xms.limowallet.fragment.MyFragment;
import com.xms.limowallet.fragment.WalletFragment;
import com.xms.limowallet.Server.ServerManager;
import com.xms.limowallet.utils.ToastUtils;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示主界面
 */
public class HomeTestActivtiy extends BaseActivity {
    Context context;
    private ViewPager viewPager_main;
    List<Fragment> fragmentList = new ArrayList<>();
    MainAdapter mainAdapter;
    private RadioGroup rg;
    RadioButton rb_wallet, rb_application, rb_my;//创建三个可点击的底部按钮
    ServerManager mServerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lomo_main);
        context = getApplicationContext();

        initView();
        initData();
    }

    //初始化View
    public void initView() {
        //控件点击绑定与初始化
        rb_wallet = findViewById(R.id.rb_wallet);
        rb_wallet.setOnClickListener(onClickListener);
        rb_application = findViewById(R.id.rb_application);
        rb_application.setOnClickListener(onClickListener);
        rb_my = findViewById(R.id.rb_my);
        rb_my.setOnClickListener(onClickListener);
    }

    public void initData() {
        viewPager_main = findViewById(R.id.viewPager_main);
        rg = findViewById(R.id.rg);
        fragmentList.add(new WalletFragment());
        fragmentList.add(new ApplicationFragment());
        fragmentList.add(new MyFragment());
        mainAdapter = new MainAdapter(getSupportFragmentManager(), fragmentList);
        viewPager_main.setAdapter(mainAdapter);
        viewPager_main.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        rg.check(R.id.rb_wallet);
                        break;
                    case 1:
                        rg.check(R.id.rb_application);
                        break;
                    case 2:
                        rg.check(R.id.rb_my);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        rg.getChildAt(0).performClick();
        int id = getIntent().getIntExtra("flag", 0);
        if (id == 0) {
            viewPager_main.setCurrentItem(0);
        } else if (id == 2) {
            viewPager_main.setCurrentItem(2);
        } else if (id == 4) {
            viewPager_main.setCurrentItem(2);
        }
    }

    //按钮点击事件
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rb_wallet:
                    viewPager_main.setCurrentItem(0);
                    break;
                case R.id.rb_application:
                    viewPager_main.setCurrentItem(1);
                    break;
                case R.id.rb_my:
                    viewPager_main.setCurrentItem(2);
                    break;
            }
        }
    };

    //读取本地json
    public void initJsonBean() {
        try {
            InputStream is = this.getResources().getAssets().open("lmbooter.json");
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            Reader response = new StringReader(new String(buffer));


        } catch (Exception e) {
            System.out.println(e);
        }


    }


    //进入到这个界面后点击后退按钮直接提示退出
    private long timeMillis;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - timeMillis) > 2000) {
                ToastUtils.showToast(context, getResources().getString(R.string.press_exit_again));
                timeMillis = System.currentTimeMillis();
            } else {
                exitAPP();//点击退出按钮直接摧毁堆栈
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
