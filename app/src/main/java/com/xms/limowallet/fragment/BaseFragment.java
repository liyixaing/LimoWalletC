package com.xms.limowallet.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.gyf.barlibrary.ImmersionBar;

public class BaseFragment extends Fragment {

    protected Context mContext;
    public ImmersionBar mImmersionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();

    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }

    /**
     * 根据当前fragement是否隐藏，初始化沉浸式
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mImmersionBar != null)
            mImmersionBar.init();
    }
}
