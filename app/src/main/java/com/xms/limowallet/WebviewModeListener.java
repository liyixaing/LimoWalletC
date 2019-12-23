package com.xms.limowallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.io.File;

import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;

public class WebviewModeListener implements ICore.ICoreStatusListener {

    LinearLayout btns = null;
    Activity activity = null;
    ViewGroup mRootView = null;
    IWebview webview = null;
    ProgressDialog pd = null;

    public WebviewModeListener(Activity activity, ViewGroup rootView) {
        this.activity = activity;
        mRootView = rootView;
        btns = new LinearLayout(activity);
        mRootView.setBackgroundColor(0xffffffff);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                webview.onRootViewGlobalLayout(mRootView);
            }
        });
    }

    /**
     * 5+内核初始化完成时触发
     */
    @Override
    public void onCoreInitEnd(ICore coreHandler) {
        // 设置单页面集成的appid
        String appid = "TestAppid";
        // 单页面集成时要加载页面的路径，可以是本地文件路径也可以是网络路径
        File sdDir = Environment.getExternalStorageDirectory();//获取本地路径
//        DirUtils.delete(sdDir.getAbsolutePath() + "/com.xms.lmwallet");
//        String url = "https://www.baidu.com/";
        String url = "file://" + sdDir + "/com.xms.lmwallet/Repo/gitee.com/Limoversion/main-dev.git/index.html";
        Log.e("本地主页路径", url);
//        String url = Environment.getExternalStorageDirectory().getPath() + "/com.xms.lmwallet" +"";
        webview = SDK.createWebview(activity, url, appid, new IWebviewStateListener() {
            @Override
            public Object onCallBack(int pType, Object pArgs) {
                switch (pType) {
                    case IWebviewStateListener.ON_WEBVIEW_READY:
                        // 准备完毕之后添加webview到显示父View中，设置排版不显示状态，避免显示webview时，html内容排版错乱问题
                        ((IWebview) pArgs).obtainFrameView().obtainMainView().setVisibility(View.INVISIBLE);
                        SDK.attach(mRootView, ((IWebview) pArgs));
                        break;
                    case IWebviewStateListener.ON_PAGE_STARTED:
                        // 首页面开始加载事件
                        break;
                    case IWebviewStateListener.ON_PROGRESS_CHANGED:
                        // 首页面加载进度变化
                        break;
                    case IWebviewStateListener.ON_PAGE_FINISHED:
                        // 页面加载完毕，设置显示webview
                        webview.obtainFrameView().obtainMainView().setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        });

        final WebView webviewInstance = webview.obtainWebview();
        // 监听返回键
        webviewInstance.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (webviewInstance.canGoBack()) {
                        webviewInstance.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    // 5+SDK 开始初始化时触发
    @Override
    public void onCoreReady(ICore coreHandler) {
        try {
            // 初始化5+ SDK，
            // 5+SDK的其他接口需要在SDK初始化后才能調用
            SDK.initSDK(coreHandler);
            // 当前应用可使用全部5+API
            SDK.requestAllFeature();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * // 通过代码注册扩展插件的示例
     * private void regNewApi() {
     * // 扩展插件在js层的标识
     * String featureName = "T";
     * // 扩展插件的原生类名
     * String className = "com.HBuilder.integrate.webview.WebViewMode_FeatureImpl";
     * // 扩展插件的JS层封装的方法
     * String content = "(function(plus){function test(){return plus.bridge.execSync('T','test',[arguments]);}plus.T = {test:test};})(window.plus);";
     * // 向5+SDK注册扩展插件
     * SDK.registerJsApi(featureName, className, content);
     * }
     **/

    @Override
    public boolean onCoreStop() {
        // TODO Auto-generated method stub
        return false;
    }
}
