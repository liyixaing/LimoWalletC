package com.xms.limowallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xms.limowallet.Plugins.ThemeManager.ThemeColor;
import com.xms.limowallet.Plugins.ThemeManager.ThemeManager;
import com.xms.limowallet.R;
import com.xms.limowallet.db.ListDataSave;
import com.xms.limowallet.utils.ToastUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 确认助记词
 */
public class ConfirmMnemonicActivity extends BaseActivity {
    Context context;
    private ImageView iv_return;
    private TextView tv_title;
    private TextView tv_tips_title;
    TagFlowLayout tag_correct_mnemonics;//这个位置是用来显示正确的助记词的
    TagFlowLayout tag_error_mnemonics;//这个位置使用来显示乱序的助记词
    List<String> mlistNew = new ArrayList<>();//乱序助记词
    List<String> list = new ArrayList<>();
    List<String> lists = new ArrayList<>();
    private LinearLayout ll_establish;//完成

    ListDataSave dataSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_mnemonic);
        context = getApplicationContext();
        initView();
    }

    //初始化View
    public void initView() {
        //控件初始化
        tv_title = findViewById(R.id.tv_title);
        tv_tips_title = findViewById(R.id.tv_tips_title);
        tag_correct_mnemonics = findViewById(R.id.tag_correct_mnemonics);
        tag_error_mnemonics = findViewById(R.id.tag_error_mnemonics);
        //控件点击事件初始化
        iv_return = findViewById(R.id.iv_return);
        iv_return.setOnClickListener(onClickListener);
        ll_establish = findViewById(R.id.ll_establish);
        ll_establish.setOnClickListener(onClickListener);
        dataSave = new ListDataSave(context, "wallet");
        list = dataSave.getDataList("mnemonic");
        //进行乱序
        Map<Integer, String> mmap = new HashMap<Integer, String>();
        while (mmap.size() < 12) {
            int random = (int) (Math.random() * list.size());
            if (!mmap.containsKey(random)) {
                mmap.put(random, "");
                mlistNew.add(list.get(random));
            }
        }
        ErrorMnemonic();//助记词打乱
        ColorSetting();//调整颜色
    }


    //显示错误助记词
    public void ErrorMnemonic() {
        //添加控件的适配器
        tag_error_mnemonics.setAdapter(new TagAdapter<String>(mlistNew) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context)
                        .inflate(R.layout.item_error_mnemonic, tag_error_mnemonics, false);
                TextView tv_test = linearLayout.findViewById(R.id.tv_test);
                tv_test.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.FrameBox_Content));//动态调整字体颜色
                tv_test.setText(s);
                return linearLayout;
            }

        });

        //子控件点击事件
        tag_error_mnemonics.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                TextView textView = view.findViewById(R.id.tv_test);//初始化控件
                textView.setVisibility(View.GONE);//隐藏控件
                lists.add(mlistNew.get(position));
                mlistNew.remove(position);
                CorrectMnemonic();//显示执行
                ErrorMnemonic();//更新当前下标
                Log.e("list", mlistNew.toString());
                return false;
            }
        });
        tag_error_mnemonics.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
            }
        });
    }


    //显示框内显示助记词
    public void CorrectMnemonic() {
        //控件适配器
        tag_correct_mnemonics.setAdapter(new TagAdapter<String>(lists) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context)
                        .inflate(R.layout.item_error_mnemonic, tag_error_mnemonics, false);
                TextView tv_test = linearLayout.findViewById(R.id.tv_test);
                tv_test.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.FrameBox_Content));//动态调整字体颜色
                tv_test.setText(s);
                return linearLayout;
            }
        });
        //子控件点击事件
        tag_correct_mnemonics.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                TextView tv_item = view.findViewById(R.id.tv_test);
                tv_item.setVisibility(View.GONE);
                Iterator<String> it = lists.iterator();//创建迭代器
                while (it.hasNext()) {
                    String s = it.next();
                    if (s.equals(lists.get(position))) {
                        mlistNew.add(lists.get(position));
                        it.remove();
                        ErrorMnemonic();
                        CorrectMnemonic();
                        Log.e("lists", lists.toString());
                        break;
                    }
                }
                return false;
            }
        });
        tag_correct_mnemonics.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
            }
        });
    }

    //设置动态背景色
    public void ColorSetting() {
        tv_title.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.ContentTitle));
        tv_tips_title.setTextColor(ThemeManager.getColorWithKey(context, ThemeColor.ContentTitle));
    }

    //按钮点击事件
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_return://返回
                    finish();
                    break;
                case R.id.ll_establish:
                    Log.e("TAG", dataSave.getDataList("mnemonic").toString());
                    Log.e("TAG", lists.toString());
                    if (dataSave.getDataList(" mnemonic").toString().equals(lists.toString())) {
                        Log.e("TAG", "助记词输入正确跳转到首页");
                        //跳转到主界面
                        Intent intent = new Intent(context, HomeTestActivtiy.class);
                        startActivity(intent);
                    } else {
                        Log.e("TAG", "助记词输入不正确，让用户重新助记词");
                        ToastUtils.showToast(context, "请正确选择助记词");
                    }

                    break;
            }
        }
    };
}
