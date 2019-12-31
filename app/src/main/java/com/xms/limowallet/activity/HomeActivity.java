package com.xms.limowallet.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xms.limowallet.Model.HomeModel;
import com.xms.limowallet.R;
import com.xms.limowallet.adapter.HomeAdapter;
import com.xms.limowallet.constant.Constant;
import com.xms.limowallet.tabview.CommonNavigator;
import com.xms.limowallet.tabview.CommonNavigatorAdapter;
import com.xms.limowallet.tabview.CommonPagerTitleView;
import com.xms.limowallet.tabview.IPagerIndicator;
import com.xms.limowallet.tabview.IPagerTitleView;
import com.xms.limowallet.tabview.MagicIndicator;
import com.xms.limowallet.tabview.ViewPagerHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;

/**
 * 这个界面是主界面显示
 */
public class HomeActivity extends BaseActivity {
    Context context;
    private ViewPager viewPager;//碎片化容器
    private MagicIndicator tab_magicindcator;//底部控件
    List<HomeModel.itemsBean> ViewList;
    HomeModel homeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();
        initView();
        inithttps();
    }

    private void initView() {
    }

    //配置底部TabView
    public void SettingTabBottom() {
        viewPager = findViewById(R.id.viewPager_main);
        tab_magicindcator = findViewById(R.id.tab_magicindcator);
        HomeAdapter homeAdapter = new HomeAdapter(ViewList);//适配器
        viewPager.setAdapter(homeAdapter);
        tab_magicindcator.setBackgroundColor(Color.parseColor(homeModel.getBackgroundColor()));//设置界面背景色
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return ViewList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
                View customLayout = LayoutInflater.from(context).inflate(R.layout.simple_pager_title_layout, null);
                final ImageView titleImg = (ImageView) customLayout.findViewById(R.id.title_img);
                final TextView titleText = (TextView) customLayout.findViewById(R.id.title_text);
//                titleImg.setImageResource(R.mipmap.ic_launcher);
                String url = "http://" + Constant.NATIVE_LAN + ":" + Constant.PORT_MAIN + ViewList.get(index).getIconPath();
                Picasso.with(context)
                        .load(url)
                        .into(titleImg);
                titleText.setText(ViewList.get(index).getTexts().get("zh-cn"));
                commonPagerTitleView.setContentView(customLayout);

                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int index, int totalCount) {
                        //选中状态的颜色
                        titleText.setTextColor(Color.parseColor(homeModel.getItemSelectedColor()));
                        titleImg.setColorFilter(Color.parseColor(homeModel.getItemSelectedColor()));
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        //未选中状态的颜色
                        titleText.setTextColor(Color.parseColor(homeModel.getItemNormalColor()));
                        titleImg.setColorFilter(Color.parseColor(homeModel.getItemNormalColor()));
                    }

                    //点击动画效果
                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
//                        titleImg.setScaleX(1.3f + (0.8f - 1.3f) * leavePercent);
//                        titleImg.setScaleY(1.3f + (0.8f - 1.3f) * leavePercent);
                    }

                    //点击动画效果
                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
//                        titleImg.setScaleX(0.8f + (1.3f - 0.8f) * enterPercent);
//                        titleImg.setScaleY(0.8f + (1.3f - 0.8f) * enterPercent);
                    }
                });

                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });

                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        tab_magicindcator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(tab_magicindcator, viewPager);

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
                        homeModel = gson.fromJson(response, HomeModel.class);
                        String locase = Locale.getDefault().getLanguage();//获取系统使用的语言
                        System.out.println(locase);//测试输出系统的语种
                        for (int i = 0; i < homeModel.getItems().size(); i++) {//遍历列表中有几个选项
                            ViewList = homeModel.getItems();
                            for (Map.Entry<String, String> entry : homeModel.getItems().get(i).getTexts().entrySet()) {//这一步是遍历出语种
                                Log.e("TAG", "Key = " + entry.getKey() + ", Value = " + entry.getValue());//输出语种
                                //判断系统语言是否为中文
                                if (locase.equals("zh")) {
                                    //中文显示
                                } else {
                                    //其他语言
                                }
                            }
                        }
                        SettingTabBottom();
                    }
                });
    }
}
