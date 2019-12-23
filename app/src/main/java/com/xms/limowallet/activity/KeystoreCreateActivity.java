package com.xms.limowallet.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xms.limowallet.R;
import com.xms.limowallet.db.ListDataSave;
import com.xms.limowallet.utils.ToastUtils;
import com.xms.limowallet.widget.InputPwdPopup;
import com.xms.limowallet.widget.UserInfoPopup;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

/**
 * 创建钱包
 */
public class KeystoreCreateActivity extends BaseActivity {
    Context context;
    private ImageView iv_return;//返回图标
    private LinearLayout ll_establish;//创建按钮
    private boolean isHideFirst = true;//用做判断密码的密文和密文的显示
    InputPwdPopup inputPwdPopup;//弹出的输入密码界面
    private ImageView iv_display, iv_hide;
    private EditText et_wellet_name;//钱包名称
    private EditText et_wallet_password;//钱包密码
    private EditText et_confirm_wallet_code;//确认钱包密码输入框
    private CheckBox cb_backups;//备份勾选按钮


    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    private String type;//这个变量是用来判断用户是老鸟还是小白

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keystore_create);
        context = getApplicationContext();
        type = getIntent().getStringExtra("type");
        initView();//执行初始化
        checkPermission();


    }

    //初始化View
    public void initView() {
        //初始化可点击控件
        iv_return = findViewById(R.id.iv_return);
        iv_return.setOnClickListener(onClickListener);
        ll_establish = findViewById(R.id.ll_establish);
        ll_establish.setOnClickListener(onClickListener);
        iv_display = findViewById(R.id.iv_display);
        iv_display.setOnClickListener(onClickListener);
        iv_hide = findViewById(R.id.iv_hide);
        iv_hide.setOnClickListener(onClickListener);

        //初始化不可点击的控件
        et_wellet_name = findViewById(R.id.et_wellet_name);//钱包名称
        et_confirm_wallet_code = findViewById(R.id.et_confirm_wallet_code);//钱包密码
        et_wallet_password = findViewById(R.id.et_wallet_password);//二次密码
        cb_backups = findViewById(R.id.cb_backups);

        //判断用户是老鸟还是小白
        if (type.equals("0")) {
            cb_backups.setChecked(true);//立即备份不勾选
        } else if (type.equals("1")) {
            cb_backups.setChecked(false);//立即备份默认勾选
        }

    }

    /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    public static String ETH_JAXX_TYPE = "m/44'/60'/0'/0/0";
    public static String ETH_LEDGER_TYPE = "m/44'/60'/0'/0";
    public static String ETH_CUSTOM_TYPE = "m/44'/60'/1'/0/0";

    //点击事件处理
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_return://返回事件
                    finish();
                    break;
                case R.id.iv_display://显示小眼睛图标
                    initpassword();
                    iv_display.setVisibility(View.GONE);
                    iv_hide.setVisibility(View.VISIBLE);
                    break;
                case R.id.iv_hide://隐藏小眼睛图标
                    initpassword();
                    iv_display.setVisibility(View.VISIBLE);
                    iv_hide.setVisibility(View.GONE);
                    break;
                case R.id.ll_establish://创建按钮
                    try {
                        initjudge();//创建
//                        CreateMnemonicWallet("12345678");//测试使用
                    } catch (CipherException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    //用户信息弹出框
    public void popupuserinfo() {
        UserInfoPopup userInfoPopup = new UserInfoPopup(context, onClickListener);
        userInfoPopup.showAsDropDown(findViewById(R.id.ll_establish), Gravity.CENTER, 0, 0);

    }

    //点击确认后弹出的popup
    public void popupInputPwd() {
        inputPwdPopup = new InputPwdPopup(context, onClickListener);
        inputPwdPopup.showAsDropDown(findViewById(R.id.ll_establish), Gravity.CENTER, 0, 0);

    }

    //密码显示隐藏显示状态处理
    public void initpassword() {
        if (isHideFirst == true) {
            //密文
            HideReturnsTransformationMethod method1 = HideReturnsTransformationMethod.getInstance();
            et_wallet_password.setTransformationMethod(method1);
            et_confirm_wallet_code.setTransformationMethod(method1);
            isHideFirst = false;
        } else {
            //明文
            TransformationMethod method = PasswordTransformationMethod.getInstance();
            et_wallet_password.setTransformationMethod(method);
            et_confirm_wallet_code.setTransformationMethod(method);
            isHideFirst = true;
        }
        //光標的位置
        int index = et_wallet_password.getText().toString().length();
        et_wallet_password.setSelection(index);//设置第一个光标显示
        int index1 = et_confirm_wallet_code.getText().toString().length();
        et_confirm_wallet_code.setSelection(index1);//设置第二个光标显示
    }

    //点击创建按钮判断规则（规则成立跳转到创建界面，规则不成立则输出原因）
    public void initjudge() throws CipherException, IOException {
        String name = et_wellet_name.getText().toString();//钱包名称
        String password = et_confirm_wallet_code.getText().toString();//设置钱包密码
        String passworddouble = et_wallet_password.getText().toString();//验证输入的密码
        if (name.equals("")) {//判断钱包名称是否输入
            ToastUtils.showToast(context, getResources().getString(R.string.wallet_name));
        } else if (password.equals("")) {
            ToastUtils.showToast(context, getResources().getString(R.string.wallet_Password));
        } else if (password.equals("")) {
            ToastUtils.showToast(context, getResources().getString(R.string.repeat_password));
        } else if (password.length() < 8) {
            ToastUtils.showToast(context, getResources().getString(R.string.lowest_password));
        } else if (password.equals(passworddouble)) {//判断两次输入的密码是不是一样的
            CreateMnemonicWallet(password);
        } else {
            ToastUtils.showToast(context, getResources().getString(R.string.dissimilarity));
        }
    }


    //创建助记词钱包
    public void CreateMnemonicWallet(String password) throws CipherException, IOException {
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        byte[] seedBytes = ds.getSeedBytes();
        List<String> mnemonic = ds.getMnemonicCode();//助记词
        Log.d("助记词", "mnemonic: " + mnemonic);
        if (seedBytes == null) {
            Log.e("空执行", "未成功生成助记词");
        }
        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }
        ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
        File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/com.xms.lmwallet" + "/.Keystore");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        //用来生成地址和keystore（此处需要密码）
        WalletFile keyStoreFile = null;
        try {
            keyStoreFile = Wallet.create(password, keyPair, 1024, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String privatekey = Numeric.toHexStringWithPrefix(keyPair.getPrivateKey());//私钥
        String publickey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());//公钥
        String address = "0x" + keyStoreFile.getAddress();
        Log.e("私钥:", privatekey);
        Log.e("公钥:", publickey);
        Log.e("地址:", address);
        Log.e("keystore:", fileDir.getAbsolutePath());
        //声明空间暂存助记词
        ListDataSave dataSave = new ListDataSave(context, "wallet");
        dataSave.setDataList("mnemonic", mnemonic);
//        UserInfo user = new UserInfo();
//        user.setMnemonic(mnemonic);

        //判断是否勾选立即备份
        if (cb_backups.isChecked() == true) {
            //未勾选立即备份 跳转到首页
            Intent mainintent = new Intent(context, MainLimoActivtiy.class);
            startActivity(mainintent);

        } else {
            //已勾选立即备份 跳转到备份界面
            Intent backupsintent = new Intent(context, BackupHintsActivity.class);
            startActivity(backupsintent);
        }

    }

    /**
     * android6.0以后动态获取读写权限的方法
     */
    public void checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            Log.i("tag", "isGranted == " + isGranted);
            if (!isGranted) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            }
        }

    }

}

