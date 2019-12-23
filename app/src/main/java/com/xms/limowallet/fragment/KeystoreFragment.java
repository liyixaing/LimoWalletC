package com.xms.limowallet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xms.limowallet.R;
import com.xms.limowallet.utils.ToastUtils;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.io.IOException;

/**
 * Keystore 导入钱包
 */
public class KeystoreFragment extends BaseFragment {
    View view;
    Context context;
    private EditText et_keystore_text, et_towpwd;//keystore输入文本, 密码输入框
    private LinearLayout ll_dismiss;//开始导入按钮

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_keystore, null);
        context = getActivity();
        initView();
        return view;
    }

    //初始化View
    private void initView() {
        ll_dismiss = view.findViewById(R.id.ll_dismiss);
        ll_dismiss.setOnClickListener(onClickListener);
        et_keystore_text = view.findViewById(R.id.et_keystore_text);
        et_towpwd = view.findViewById(R.id.et_towpwd);
    }

    //控件点击事件处理
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_dismiss:
                    ToastUtils.showToast(context, "开始导入");
                    if (et_keystore_text.getText().toString().equals("")) {
                        ToastUtils.showToast(context, "请输入密码");
                    } else if (et_towpwd.getText().toString().equals("")) {
                        ToastUtils.showToast(context, "请输入明文ketstore");
                    } else {
                        initkeystore(et_towpwd.getText().toString(), et_keystore_text.getText().toString());
                    }
                    break;
            }
        }
    };

    //keystore导入钱包
    public void initkeystore(String password, String keystore) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        WalletFile walletFile = null;
        try {
            walletFile = objectMapper.readValue(keystore, WalletFile.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ECKeyPair keyPair = Wallet.decrypt(password, walletFile);
            WalletFile generateWalletFile = Wallet.createLight(password, keyPair);
            Log.e("+++", "keyStoreImportAddress:" + generateWalletFile.getAddress());
            Log.e("address:", generateWalletFile.getAddress());
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }

}








