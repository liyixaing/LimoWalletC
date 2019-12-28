package com.xms.limowallet.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.xms.limowallet.Model.HomeModel;
import com.xms.limowallet.R;
import com.xms.limowallet.constant.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 这个界面是主界面显示
 */
public class HomeActivity extends BaseActivity {

    Context context;
    List<Fragment> fragments = new ArrayList<>();
    List<String> url = new ArrayList<>();
    TextView tv_item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();

        initView();
        inithttps();
        tv_item = findViewById(R.id.tv_item);
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        tv_item.setText("Date获取当前日期时间" + simpleDateFormat.format(date));

    }

    private void initView() {

    }

    /**
     * 使用封装方法进行调用接口
     */
    String urls = "http://" + Constant.NATIVE_LAN + ":" + Constant.PORT_MAIN + "/static/lmbooter.json";

    public void inithttps() {
        OkHttpUtils
                .get()
                .url(urls)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("TAG", id + "");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("TAG", response);
                        Gson gson = new Gson();
                        HomeModel homeModel = gson.fromJson(response, HomeModel.class);
                        String locase = Locale.getDefault().getLanguage();//获取系统使用的语言
                        System.out.println(locase);//测试输出系统的语种
                        for (int i = 0; i < homeModel.getItems().size(); i++) {//遍历列表中有几个选项
                            for (Map.Entry<String, String> entry : homeModel.getItems().get(i).getTexts().entrySet()) {//这一步是遍历出语种
                                Log.e("TAG", "Key = " + entry.getKey() + ", Value = " + entry.getValue());//输出语种
                                //判断系统语言是否为中文
                                if (locase.equals("zh")) {
                                    tv_item.setText(homeModel.getItems().get(i).getTexts().get("en-us"));
                                    //中文显示
                                } else {
                                    //其他语言
                                    tv_item.setText(homeModel.getItems().get(i).getTexts().get("zh-cn"));
                                }
                            }
                        }
                    }
                });
    }


    //请求本地json数据
    public void httpasseat() {
        Log.e("TAG", "http://" + Constant.NATIVE_LAN + ":" + Constant.PORT_MAIN + "/static/lmbooter.json");
        //开启异步线程执行数据请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://" + Constant.NATIVE_LAN + ":" + Constant.PORT_MAIN + "/static/lmbooter.json")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request对象
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
                    Log.e("异常捕获", e.toString());
                }
            }
        }).start();

    }
}
