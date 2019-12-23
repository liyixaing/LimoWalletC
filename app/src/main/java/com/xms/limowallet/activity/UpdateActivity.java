package com.xms.limowallet.activity;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xms.limowallet.R;

public class UpdateActivity extends BaseActivity {

    ImageView iv_logo_update;//顶部logo
    ImageView iv_imageView;
    ProgressBar progressbar1;
    TextView tv_speed;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_update);
        initView();//初始化View
    }

    //初始化View
    public void initView() {
        iv_logo_update = findViewById(R.id.iv_logo_update);
        iv_imageView = findViewById(R.id.iv_imageView);
        progressbar1 = findViewById(R.id.progressbar1);
        tv_speed = findViewById(R.id.tv_speed);
    }
}
