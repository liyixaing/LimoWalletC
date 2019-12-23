package com.xms.limowallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xms.limowallet.R;
import com.xms.limowallet.manager.ServerManager;
import com.yanzhenjie.andserver.annotation.Website;
import com.yanzhenjie.andserver.framework.website.StorageWebsite;

import java.util.LinkedList;
import java.util.List;

public class ServerActivity extends BaseActivity {

    private ServerManager mServerManager;
    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnBrowser;
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
            }
        }
    };

    public void onServerStart(String ip) {
        mBtnStart.setVisibility(View.GONE);
        mBtnStop.setVisibility(View.VISIBLE);
        mBtnBrowser.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(ip)) {
            List<String> addressList = new LinkedList<>();
            mRootUrl = "http://" + ip + ":8080/";
            addressList.add(mRootUrl);
            addressList.add("http://" + ip + ":8080/login.html");
            mTvMessage.setText(TextUtils.join("\n", addressList));
        } else {
            mRootUrl = null;
            mTvMessage.setText("未获取服务器ip地址");
        }
    }

    @Website
    public static class InternalWebsite extends StorageWebsite {
        public InternalWebsite() {
            super("/sdcard/AndServer/web");
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
