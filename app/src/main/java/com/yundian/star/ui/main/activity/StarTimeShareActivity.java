package com.yundian.star.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.yundian.star.R;
import com.yundian.star.app.AppConstant;
import com.yundian.star.base.BaseActivity;
import com.yundian.star.ui.main.adapter.StartTimeShareAdpter;
import com.yundian.star.ui.main.fragment.AuctionMarketFragment;
import com.yundian.star.ui.main.fragment.CommentMarketFragment;
import com.yundian.star.ui.main.fragment.FansHotFragment;
import com.yundian.star.ui.main.fragment.StarIntroFragment;
import com.yundian.star.widget.NormalTitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/5/20.
 * 行情主界面
 */

public class StarTimeShareActivity extends BaseActivity {

    @Bind(R.id.nt_title)
    NormalTitleBar nt_title;
    @Bind(R.id.tabs)
    SlidingTabLayout tabs ;
    @Bind(R.id.view_pager)
    ViewPager viewPager ;

    private List<String> listType = new ArrayList<>();
    private StartTimeShareAdpter fragmentAdapter;
    private List<Fragment> mNewsFragmentList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_star_time_share;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        nt_title.setBackVisibility(true);
        nt_title.setTitleText("柳岩");
        initType();
        initListener();
        //initTab();

    }

    private void initListener() {
        nt_title.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({ R.id.rb_1, R.id.rb_2, R.id.rb_3,R.id.rb_4})
    public void onRadioButtenClick(View view){
        switch (view.getId()){
            case R.id.rb_1:
                Intent intent = new Intent(this,BuyTransferIndentActivity.class);
                intent.putExtra(AppConstant.BUY_TRANSFER_INTENT_TYPE,0);
                startActivity(intent);
                break;
            case R.id.rb_2:
                Intent intent2 = new Intent(this,BuyTransferIndentActivity.class);
                intent2.putExtra(AppConstant.BUY_TRANSFER_INTENT_TYPE,1);
                startActivity(intent2);
                break;
            case R.id.rb_3:

                break;
            case R.id.rb_4:
                break;
        }
    }


    //内部fragment的tab头部
    private void initType() {
        listType.add(getString(R.string.star_time_intro));
        listType.add(getString(R.string.star_time_fans));
        listType.add(getString(R.string.star_time_auction));
        listType.add(getString(R.string.star_time_comment));
        initFragment();
    }
    private void initFragment() {
        mNewsFragmentList = new ArrayList<>();
        createListFragments();
        if(fragmentAdapter==null) {
            fragmentAdapter = new StartTimeShareAdpter(getSupportFragmentManager(), mNewsFragmentList, listType);
        }else {
            //刷新fragment
            fragmentAdapter.setFragments(getSupportFragmentManager(),mNewsFragmentList,listType);
        }
        viewPager.setAdapter(fragmentAdapter);
        tabs.setViewPager(viewPager);
        //MyTabLayoutUtils.dynamicSetTabLayoutMode(tabs);
        setPageChangeListener();
    }


    private void createListFragments() {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.MARKET_DETAIL_IN_TYPE,"1001");
        StarIntroFragment starIntroFragment = new StarIntroFragment();
        starIntroFragment.setArguments(bundle);
        mNewsFragmentList.add(starIntroFragment);
        FansHotFragment fansHotFragment = new FansHotFragment();
        fansHotFragment.setArguments(bundle);

        AuctionMarketFragment auctionMarketFragment =new AuctionMarketFragment();
        auctionMarketFragment.setArguments(bundle);

        CommentMarketFragment commentMarketFragment = new CommentMarketFragment();
        commentMarketFragment.setArguments(bundle);

        mNewsFragmentList.add(fansHotFragment);
        mNewsFragmentList.add(auctionMarketFragment);
        mNewsFragmentList.add(commentMarketFragment);


    }

    private void setPageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
