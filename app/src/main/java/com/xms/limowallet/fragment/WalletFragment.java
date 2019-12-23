package com.xms.limowallet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.xms.limowallet.R;
import com.xms.limowallet.WebviewModeListener;

import java.io.File;

import io.dcloud.EntryProxy;
import io.dcloud.feature.internal.sdk.SDK;

/**
 * 钱包碎片页面
 */
public class WalletFragment extends BaseFragment {
    private View view;
    private Context context;
    private EntryProxy mEntryProxy;
    private FrameLayout rootView;
    private WebView wv_webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_wallet, null);
        rootView = new FrameLayout(context);//初始化界面
        // 创建5+内核运行事件监听
        WebviewModeListener wm = new WebviewModeListener(getActivity(), rootView);
        // 初始化5+内核
        mEntryProxy = EntryProxy.init(getActivity(), wm);
        // 启动5+内核
        mEntryProxy.onCreate(getActivity(), savedInstanceState, SDK.IntegratedMode.WEBVIEW, null);
        initView();
        return view;
    }

    //初始化View
    public void initView() {
        wv_webview = view.findViewById(R.id.wv_webview);//绑定控件
        //使用webView控件打开本地链接
        File sdDir = Environment.getExternalStorageDirectory();//获取本地路径
        //拼接本地路径
        String url = "file://" + sdDir + "/com.xms.lmwallet/Repo/gitee.com/Limoversion/main-dev.git/index.html";
        WebSettings webSettings = wv_webview.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //设置缓存
        webSettings.setJavaScriptEnabled(true);
        wv_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wv_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }
        });
        Log.e("url", url);
        wv_webview.loadUrl(url);

    }
}
