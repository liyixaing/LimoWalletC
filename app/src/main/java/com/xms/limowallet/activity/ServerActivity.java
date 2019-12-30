package com.xms.limowallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xms.limowallet.R;
import com.xms.limowallet.Server.ServerManager;
import com.xms.limowallet.constant.Constant;
import com.yanzhenjie.andserver.annotation.Website;
import com.yanzhenjie.andserver.framework.website.StorageWebsite;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerActivity extends BaseActivity {

    private ServerManager mServerManager;
    private Button mBtnStart, mBtnStop, mBtnBrowser, btn_http, btn_intentTest;
    private TextView mTvMessage;
    private String mRootUrl;

    /**
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        initVIew();
    }

    //初始化View
    public void initVIew() {
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(onClickListener);
        mBtnStop = findViewById(R.id.btn_stop);
        mBtnStop.setOnClickListener(onClickListener);
        mBtnBrowser = findViewById(R.id.btn_browse);
        mBtnBrowser.setOnClickListener(onClickListener);
        mTvMessage = findViewById(R.id.tv_message);
        btn_intentTest = findViewById(R.id.btn_intentTest);
        btn_intentTest.setOnClickListener(onClickListener);

        btn_http = findViewById(R.id.btn_http);
        btn_http.setOnClickListener(onClickListener);
        // 在服务器在服务中运行。
        mServerManager = new ServerManager(this);
        mServerManager.register();

        // 启动服务器;
        mBtnStart.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServerManager.unRegister();
    }

    //按钮点击事件
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_start:
                    mServerManager.startServer();
                    break;
                case R.id.btn_stop: {
//                showDialog();
                    mServerManager.stopServer();
                    break;
                }
                case R.id.btn_browse: {
                    if (!TextUtils.isEmpty(mRootUrl)) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(Uri.parse(mRootUrl));
                        startActivity(intent);
                    }
                    break;
                }
                case R.id.btn_http:
                    Toast.makeText(context, "请求接口", Toast.LENGTH_SHORT).show();
                    WithHttpClient();
                    break;
                case R.id.btn_intentTest:
                    Intent intent = new Intent(context, HomeActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    //创建一个请求接口
    public void WithHttpClient() {
        //开启异步线程执行网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://" + Constant.NATIVE_LAN + ":" + Constant.PORT_MAIN + "/static/lmbooter.json")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Log.d("xaioqiang", "response.code:" + response.code());
                        Log.d("xiaoqiang", "response.message:" + response.message());
                        Log.d("xiaoqiang", "json数据:" + response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onServerStart(String ip) {
        mBtnStart.setVisibility(View.GONE);
        mBtnStop.setVisibility(View.VISIBLE);
        mBtnBrowser.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(ip)) {
            mRootUrl = "http://" + ip + ":" + Constant.PORT_MAIN + "/index.html";//本地服务器地址以及端口
            mTvMessage.setText(mRootUrl);
        } else {
            mRootUrl = null;
            mTvMessage.setText("未获取服务器ip地址");
        }
    }

    File sdDir = Environment.getExternalStorageDirectory();//获取本地路径
    //拼接本地路径
    String url = "file://" + sdDir + "/com.xms.lmwallet/Repo/gitee.com/Limoversion/main-dev.git/index.html";

    @Website
    public static class InternalWebsite extends StorageWebsite {
        public InternalWebsite() {
            super(Environment.getExternalStorageDirectory() + "/com.xms.lmwallet/Repo/gitee.com/Limoversion/main-dev.git");
        }
    }


    public void onServerError(String error) {
        mRootUrl = null;
        mBtnStart.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.GONE);
        mBtnBrowser.setVisibility(View.GONE);
        mTvMessage.setText(error);
    }

    public void onServerStop() {
        mRootUrl = null;
        mBtnStart.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.GONE);
        mBtnBrowser.setVisibility(View.GONE);
        mTvMessage.setText("服务器已停止");
    }
}
