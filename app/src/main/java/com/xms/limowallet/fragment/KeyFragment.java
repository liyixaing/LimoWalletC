package com.xms.limowallet.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xms.limowallet.Plugins.ThemeManager.ThemeColor;
import com.xms.limowallet.Plugins.ThemeManager.ThemeManager;
import com.xms.limowallet.R;
import com.xms.limowallet.activity.HomeActivtiy;
import com.xms.limowallet.utils.ToastUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

/**
 * 私钥导入钱包
 */
public class KeyFragment extends BaseFragment {
    View view;
    Context context;

    private EditText et_privatekey;//私钥输入框
    private EditText et_welletname;//钱包名称
    private EditText et_welletpassword;//钱包密码
    private EditText et_verify_password;//验证密码输入框
    private EditText et_password_prompt;//密码提示信息
    private TextView tv_text_tips;//错误提示信息
    private LinearLayout ll_dismiss;//导入钱包按钮

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_key, null);
        context = getActivity();
        initView();
        return view;
    }

    //初始化View
    public void initView() {
        //控件初始化
        et_privatekey = view.findViewById(R.id.et_privatekey);
        et_welletname = view.findViewById(R.id.et_welletname);
        et_welletpassword = view.findViewById(R.id.et_welletpassword);
        et_verify_password = view.findViewById(R.id.et_verify_password);
        et_password_prompt = view.findViewById(R.id.et_password_prompt);
        tv_text_tips = view.findViewById(R.id.tv_text_tips);

        //可点击控件初始化
        ll_dismiss = view.findViewById(R.id.ll_dismiss);
        ll_dismiss.setOnClickListener(onClickListener);
        ColorSetting();
    }

    //设置主题颜色
    public void ColorSetting() {
        et_privatekey.setHintTextColor(ThemeManager.getColorWithKey(context, ThemeColor.InputerPlaceholder));
        et_welletname.setHintTextColor(ThemeManager.getColorWithKey(context, ThemeColor.InputerPlaceholder));
        et_welletpassword.setHintTextColor(ThemeManager.getColorWithKey(context, ThemeColor.InputerPlaceholder));
        et_verify_password.setHintTextColor(ThemeManager.getColorWithKey(context, ThemeColor.InputerPlaceholder));
        et_password_prompt.setHintTextColor(ThemeManager.getColorWithKey(context, ThemeColor.InputerPlaceholder));
        tv_text_tips.setHintTextColor(ThemeManager.getColorWithKey(context, ThemeColor.InputerPlaceholder));
    }


    //按钮点击事件
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initjudge();
            switch (v.getId()) {
                case R.id.ll_dismiss://点击导入钱包按钮
                    initjudge();//判断输入框是否为空
                    break;
            }
        }
    };

    //判断输入框是否有内容
    public void initjudge() {
        if (et_privatekey.getText().toString().equals("")) {
//            ToastUtils.showToast(context, "请输入明文私钥");
            tv_text_tips.setText("请输入明文私钥");
        } else if (et_welletname.getText().toString().equals("")) {
//            ToastUtils.showToast(context, "请输入钱包名称");
            tv_text_tips.setText("请输入钱包名称");
        } else if (et_welletpassword.getText().toString().equals("")) {
//            ToastUtils.showToast(context, "请输入钱包密码");
            tv_text_tips.setText("请输入钱包密码");
        } else if (et_verify_password.getText().toString().equals("")) {
//            ToastUtils.showToast(context, "请输入验证密码");
            tv_text_tips.setText("请输入验证码");
        } else if (!et_welletpassword.getText().toString().equals(et_verify_password.getText().toString())) {
//            ToastUtils.showToast(context, "两次密码输入不一致");
            tv_text_tips.setText("两次密码不一样");
        } else {
            inputprivatekey(et_privatekey.getText().toString());//私钥导入钱包
            tv_text_tips.setText("");
        }


        //定时执行 隐藏提示文字
        Integer time = 4000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_text_tips.setText("");
            }
        }, time);



    }

    //私钥导入钱包(此处的导入钱包是有带0x前缀的)
    public void inputprivatekey(String privateKey) {
        try {
            Credentials credentials = Credentials.create(privateKey);
            ECKeyPair ecKeyPair = credentials.getEcKeyPair();
            String msg = "address:\n" + credentials.getAddress()
                    + "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
                    + "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());
            Log.e("+++", "daoru:" + msg);
            ToastUtils.showToast(context, "导入钱包成功");
            //导入成功后跳转到首页
            Intent intent = new Intent(context, HomeActivtiy.class);
            startActivity(intent);

        } catch (Exception e) {
//            ToastUtils.showToast(context, "请输入正确的私钥");
            tv_text_tips.setText("请输入正确的私钥");
        }

    }

}
