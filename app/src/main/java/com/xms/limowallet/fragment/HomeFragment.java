package com.xms.limowallet.fragment;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.xms.limowallet.Model.HomeModel;
import com.xms.limowallet.R;
import com.xms.limowallet.constant.Constant;

import java.io.File;
import java.util.List;

public class HomeFragment extends BaseFragment {

    int position;
    Context context;
    View view;
    List<HomeModel.itemsBean> mDataList;
    WebView wv_webview;
    LinearLayout ll_time;

    public static Fragment newInstanc(int position, List<HomeModel.itemsBean> mDataList) {
        HomeFragment fragment = new HomeFragment();
        fragment.position = position;
        fragment.mDataList = mDataList;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        initView();//初始化View
        initUrl();//加载url
        return view;
    }

    View view_tow;

    //初始化View
    public void initView() {
        ll_time = view.findViewById(R.id.ll_time);
        head();//添加头部
    }

    //添加头部布局
    public void head() {
        if (position != 0) {//判断当前界面
            ll_time.setVisibility(View.GONE);//如果不为首页隐藏这个控件
        }
        view_tow = LayoutInflater.from(context).inflate(R.layout.item_tabview, ll_time, false);
        TextView textView = view_tow.findViewById(R.id.tv_name);
        textView.setText("名称");
        textView.setTextColor(Color.parseColor("#ffffff"));
        ll_time.addView(view_tow);
    }

    public void initUrl() {
        wv_webview = view.findViewById(R.id.wv_webview);
        //使用webView控件打开本地链接
        File sdDir = Environment.getExternalStorageDirectory();//获取本地路径
        //拼接本地路径
//        String url = "file://" + sdDir + "/com.xms.lmwallet/Repo/gitee.com/Limoversion/main-dev.git/index.html";
        String url = "http://" + Constant.NATIVE_LAN + ":" + Constant.PORT_MAIN + mDataList.get(position).getPagePath();
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


