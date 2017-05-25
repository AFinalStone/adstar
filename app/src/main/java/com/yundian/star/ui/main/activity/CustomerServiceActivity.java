package com.yundian.star.ui.main.activity;


import com.yundian.star.R;
import com.yundian.star.base.BaseActivity;
import com.yundian.star.widget.NormalTitleBar;

import butterknife.Bind;

/**
 * 客服
 * Created by sll on 2017/5/24.
 */

public class CustomerServiceActivity extends BaseActivity {
    @Bind(R.id.nt_title)
    NormalTitleBar ntTitle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_customer_service;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        ntTitle.setTitleText(getResources().getString(R.string.service_center));
    }


}
