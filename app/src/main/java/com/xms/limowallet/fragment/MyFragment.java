package com.xms.limowallet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xms.limowallet.R;

/**
 * 我的
 */
public class MyFragment extends BaseFragment {
    Context context;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_my, null);
        return view;
    }
}
