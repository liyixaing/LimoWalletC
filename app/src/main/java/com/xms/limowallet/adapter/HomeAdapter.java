package com.xms.limowallet.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.xms.limowallet.Model.HomeModel;
import com.xms.limowallet.fragment.HomeFragment;

import java.util.List;

/**
 * 首页碎片化适配器
 */
public class HomeAdapter extends FragmentPagerAdapter {

    private List<HomeModel.itemsBean> mDataList;

    public HomeAdapter(FragmentManager supportFragmentManager, List<HomeModel.itemsBean> viewList) {
        super(supportFragmentManager);
        this.mDataList = viewList;
    }

    @Override
    public Fragment getItem(int position) {
        return HomeFragment.newInstanc(position, mDataList);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

}
