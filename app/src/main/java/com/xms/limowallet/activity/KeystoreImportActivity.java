package com.xms.limowallet.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.xms.limowallet.Plugins.ThemeManager.ThemeColor;
import com.xms.limowallet.Plugins.ThemeManager.ThemeManager;
import com.xms.limowallet.R;
import com.xms.limowallet.adapter.MainFragmentPagerAdapter;
import com.xms.limowallet.fragment.AuxiliariesFragment;
import com.xms.limowallet.fragment.KeyFragment;
import com.xms.limowallet.fragment.KeystoreFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入钱包
 */
public class KeystoreImportActivity extends BaseActivity {
    Context context;
    private ImageView iv_return;
    private ViewPager vp_input_wallet;
    private RadioGroup rg;
    private RadioButton rb_privatekey, rb_auxiliaries, rb_keystore;
    List<Fragment> fragmentslist = new ArrayList<>();
    MainFragmentPagerAdapter adapter;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keystore_import);
        context = getApplicationContext();
        initView();
    }

    //初始化View
    private void initView() {
        iv_return = findViewById(R.id.iv_return);
        iv_return.setOnClickListener(onClickListener);
        vp_input_wallet = findViewById(R.id.vp_input_wallet);
        vp_input_wallet = findViewById(R.id.vp_input_wallet);
        rg = findViewById(R.id.rg);
        rb_keystore = findViewById(R.id.rb_keystore);
        rb_keystore.setOnClickListener(onClickListener);
        rb_auxiliaries = findViewById(R.id.rb_auxiliaries);
        rb_auxiliaries.setOnClickListener(onClickListener);
        rb_privatekey = findViewById(R.id.rb_privatekey);
        rb_privatekey.setOnClickListener(onClickListener);
        tv_title = findViewById(R.id.tv_title);

        initData();
        ColorSetting();
    }

    //动态设置颜色
    public void ColorSetting() {
        tv_title.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.ContentTitle));

    }

    //给pager添加适配器
    private void initData() {
        fragmentslist.add(new KeyFragment());//私钥导入
        fragmentslist.add(new AuxiliariesFragment());//助记词导入
        fragmentslist.add(new KeystoreFragment());//Keystore导入
        adapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), fragmentslist);
        vp_input_wallet.setAdapter(adapter);
        vp_input_wallet.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        rg.check(R.id.rb_privatekey);
                        break;
                    case 1:
                        rg.check(R.id.rb_auxiliaries);
                        break;
                    case 2:
                        rg.check(R.id.rb_keystore);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
//        rg.setOnCheckedChangeListener(this);
        rg.getChildAt(0).performClick();
        int id = getIntent().getIntExtra("flag", 0);

        if (id == 0) {
            vp_input_wallet.setCurrentItem(0);
        } else if (id == 2) {
            vp_input_wallet.setCurrentItem(2);
        } else if (id == 4) {
            vp_input_wallet.setCurrentItem(2);
        }
    }


    //点击事件处理
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_return:
                    finish();
                    break;
                case R.id.rb_privatekey://私钥导入
                    vp_input_wallet.setCurrentItem(0);
                    break;
                case R.id.rb_auxiliaries://助记词导入
                    vp_input_wallet.setCurrentItem(1);
                    break;
                case R.id.rb_keystore://keystore导入
                    vp_input_wallet.setCurrentItem(2);
                    break;
            }
        }
    };


}
