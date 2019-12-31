package com.xms.limowallet.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.xms.limowallet.Model.HomeModel;

import java.util.List;

/**
 * 首页碎片化适配器
 */
public class HomeAdapter extends PagerAdapter {

    private List<HomeModel.itemsBean> mDataList;

    public HomeAdapter(List<HomeModel.itemsBean> DataList) {
        this.mDataList = DataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 文本回调
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView textView = new TextView(container.getContext());
        textView.setText(mDataList.get(position).getTexts().get("zh-cn"));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(24);
        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 控件调用
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        TextView textView = (TextView) object;
        String text = textView.getText().toString();
        int index = mDataList.indexOf(text);
        if (index >= 0) {
            return index;
        }
        return POSITION_NONE;
    }

    /**
     * 文本使用下标赋值
     *
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mDataList.get(position).getTexts().get("zh-cn");
    }
}
