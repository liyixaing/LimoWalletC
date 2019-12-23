package com.xms.limowallet.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.xms.limowallet.R;
import com.xms.limowallet.constant.Constant;
import com.xms.limowallet.core.LMCheckoutProgress;
import com.xms.limowallet.core.LMRepo;
import com.xms.limowallet.core.LMTransferProgress;
import com.xms.limowallet.utils.ToastUtils;

import java.net.URL;

import io.dcloud.EntryProxy;

public class MainActivity extends BaseActivity {
    Context context;//上下文对象
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    EntryProxy mEntryProxy = null;
    private long timeMillis;
    ProgressBar progressbar1;//精度条
    TextView tv_speed;//进度数据
    ImageView iv_imageView;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取手机读写权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        context = getApplicationContext();
////        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (mEntryProxy == null) {
//            FrameLayout rootView = new FrameLayout(this);
//            // 创建5+内核运行事件监听
//            WebviewModeListener wm = new WebviewModeListener(this, rootView);
//            // 初始化5+内核
//            mEntryProxy = EntryProxy.init(this, wm);
//            // 启动5+内核
//            mEntryProxy.onCreate(this, savedInstanceState, SDK.IntegratedMode.WEBVIEW, null);
//            setContentView(rootView);
//        }
        initView();
        doClone();
    }

    //初始化view
    public void initView() {
        //控件初始化
        iv_imageView = findViewById(R.id.iv_imageView);
        //设置图片旋转
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        iv_imageView.startAnimation(rotateAnimation);
        progressbar1 = findViewById(R.id.progressbar1);
        tv_speed = findViewById(R.id.tv_speed);
        tv_title = findViewById(R.id.tv_title);//标题
    }

    public void doClone() {

        final Activity ctx = this;

        new Thread(new Runnable() {

            @Override
            public void run() {
                /// Clone 进度
                LMTransferProgress cloneProgress = new LMTransferProgress() {
                    @Override
                    public void ProgressUpdate(LMTransferProgress tp) {
                        Log.e("Clone进度", String.format("TP:%d/%d", tp.getReceivedObjects(), tp.getTotalObjects()));
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /// TODO 更新UI

                                progressbar1.setMax(tp.getTotalObjects());//设置总数
                                progressbar1.setProgress(tp.getReceivedObjects());//设置下载数量·
                                tv_speed.setText(tp.getReceivedObjects() + "/" + tp.getTotalObjects());
                            }
                        });
                    }
                };

                /// Checkout 进度
                LMCheckoutProgress checkoutProgress = new LMCheckoutProgress() {
                    @Override
                    public void ProgressUpdate(LMCheckoutProgress cp) {
                        Log.e("Checkout 进度", String.format("CP:%.2f KB/%.2f KB", cp.getCurSize() / 1024.0, cp.getTotalSize() / 1024.0));
                        ctx.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /// TODO 更新UI
                            }
                        });
                    }
                };

                try {
                    Log.e("Thread", "Main");

                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /// TODO 更新UI
                        }
                    });
                    LMRepo repoMain = new LMRepo(ctx, new URL(Constant.kLMROOTAPP));
                    if (!repoMain.CloneForce(cloneProgress, checkoutProgress)) {
                    }
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /// TODO 更新UI
                            //第一个工程克隆完成
                            tv_title.setText("Dapps");
                            Log.e("TAG", "第一工程克隆完成");
                        }
                    });
                    LMRepo repoDapps = new LMRepo(ctx, new URL(Constant.kLMROOTAPP_DAPPS));
                    if (!repoDapps.CloneForce(cloneProgress, checkoutProgress)) {
                    }
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /// TODO 更新UI
                            //第二个工程克隆完成
                            tv_title.setText("Asets");
                            Log.e("TAG", "第二个工程克隆完成");
                        }
                    });
                    LMRepo repoAsset = new LMRepo(ctx, new URL(Constant.kLMROOTAPP_ASSETS));
                    if (!repoAsset.CloneForce(cloneProgress, checkoutProgress)) {

                    }
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /// TODO 更新UI
                            tv_title.setText("main");
                            Log.e("TAG", "第三个工程克隆完成");
                            Intent intent = new Intent(context, WellComeActivity.class);
                            startActivity(intent);
                            finish();//关闭当前界面
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("异常", "异常");
                    Intent intent = new Intent(context, WellComeActivity.class);
                    startActivity(intent);
                    finish();//跳转后记得关闭当前界面
                }

            }
        }).start();
    }


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
