package com.xms.limowallet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xms.limowallet.R;

import java.util.Arrays;
import java.util.List;

/**
 * 助记词导入钱包
 */
public class AuxiliariesFragment extends BaseFragment {
    View view;
    Context context;
    private LinearLayout ll_mnemonic;
    private LinearLayout ll_layout_bg;
    private EditText et_mnemonic_word;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_auxiliaries, null);
        this.context = getActivity();
        initView();
        return view;
    }

    //初始化View
    private void initView() {
        //空间初始化
        view.findViewById(R.id.ll_mnemonic).setOnClickListener(onClickListener);
        et_mnemonic_word = view.findViewById(R.id.et_mnemonic_word);
        ll_layout_bg = view.findViewById(R.id.ll_layout_bg);

        //添加动态颜色
        ColorSetting();
    }

    //动态设置颜色
    public void ColorSetting() {
//        ll_layout_bg.setBackgroundColor(ThemeManager.getColorWithKey(context, ThemeColor.ContentTitle));//设置背景

    }

    //按钮点击事件
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_mnemonic://助记词导入按钮
                    String str = et_mnemonic_word.getText().toString();
                    List<String> lis = Arrays.asList(str.split(" "));
                    Log.e("list", lis.toString());
                    break;
            }
        }
    };

}
