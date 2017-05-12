package com.yundian.star.ui.main.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.yundian.star.R;
import com.yundian.star.base.BaseFragment;
import com.yundian.star.ui.main.adapter.NewsInforAdapter;
import com.yundian.star.ui.main.contract.NewInfoContract;
import com.yundian.star.ui.main.model.NewsInforModel;
import com.yundian.star.ui.main.presenter.NewsInfoPresenter;
import com.yundian.star.utils.AdViewpagerUtil;
import com.yundian.star.utils.LogUtils;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/5/8.
 */

public class NewsInfoFragment extends BaseFragment<NewsInfoPresenter, NewsInforModel> implements NewInfoContract.View {
    @Bind(R.id.lrv)
    LRecyclerView lrv;
    //    @Bind(R.id.loadingTip)
//    LoadingTip loadingTip ;
    private ArrayList<NewsInforModel> arrayList;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private NewsInforAdapter newsInfoAdapter;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1 :
                    stopProgressDialog();
                    newsInfoAdapter.clear();
                    lRecyclerViewAdapter.notifyDataSetChanged();
                    mCurrentCounter = 0;
                    mPresenter.getMoreData();
                    lrv.refreshComplete(REQUEST_COUNT);
                    break;
            }
        }
    };
    /**
     * 已经获取到多少条数据了
     */
    private static int mCurrentCounter = 0;
    /**
     * 服务器端一共多少条数据
     */
    private static final int TOTAL_COUNTER = 34;
    /**
     * 每一页展示多少条数据
     */
    private static final int REQUEST_COUNT = 10;
    private AdViewpagerUtil adViewpagerUtil;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_news_info;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this, mModel);
    }

    @Override
    protected void initView() {
        lrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
               // if (mCurrentCounter < TOTAL_COUNTER) {
                    // loading more
                    int currentSize = lRecyclerViewAdapter.getItemCount();
                    mPresenter.getMoreData();
                    lrv.refreshComplete(REQUEST_COUNT);
               // } else {
                    //the end
                    //lrv.setNoMore(true);
               // }
            }
        });
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }

    @Override
    public void initDatas(ArrayList<NewsInforModel> list) {
        arrayList = list;
        newsInfoAdapter = new NewsInforAdapter(getActivity());
        newsInfoAdapter.setDataList(arrayList);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(newsInfoAdapter);
        lrv.setAdapter(lRecyclerViewAdapter);
        DividerDecoration divider = new DividerDecoration.Builder(getContext())
                .setHeight(R.dimen.dp_1)
                .setColorResource(R.color.color_cccccc)
                .build();
        //mRecyclerView.setHasFixedSize(true);
        lrv.addItemDecoration(divider);
        lrv.setLayoutManager(new LinearLayoutManager(getContext()));
        lrv.setPullRefreshEnabled(false);
        //add a HeaderView
        final View header = LayoutInflater.from(getContext()).inflate(R.layout.adv_layout, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        RelativeLayout rl_adroot = (RelativeLayout) header.findViewById(R.id.rl_adroot);
        ViewPager viewpager = (ViewPager) header.findViewById(R.id.viewpager);
        LinearLayout ly_dots = (LinearLayout) header.findViewById(R.id.ly_dots);
        String adList[] = {"http://img02.tooopen.com/downs/images/2010/9/16/sy_2010091620583620405.jpg", "http://pic15.nipic.com/20110731/8022110_162804602317_2.jpg"};
        adViewpagerUtil = new AdViewpagerUtil(getActivity(), viewpager, ly_dots, adList);
        adViewpagerUtil.setOnAdItemClickListener(new AdViewpagerUtil.OnAdItemClickListener() {
            @Override
            public void onItemClick(View v, int position, String url) {
            }
        });
        lRecyclerViewAdapter.addHeaderView(header);
        lrv.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {

            }

            @Override
            public void onScrollStateChanged(int state) {
                RecyclerView.LayoutManager layoutManager = lrv.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    LogUtils.loge(state+ "...." + firstItemPosition+"...."+lastItemPosition );
                }
            }
        });
    }

    @Override
    public void addItems(ArrayList<NewsInforModel> list) {
        newsInfoAdapter.addAll(list);
        mCurrentCounter += list.size();
    }

    //生命周期控制
    @Override
    public void onPause() {
        super.onPause();
        if (adViewpagerUtil != null) {
            adViewpagerUtil.stopLoopViewPager();
            LogUtils.logd("广告停止");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adViewpagerUtil != null) {
            adViewpagerUtil.startLoopViewPager();
            LogUtils.logd("广告开始");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden==false){
            startProgressDialog("刷新中");
            new Thread() {

                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(-1);
                }
            }.start();
            LogUtils.loge("刷新");
        }
    }


    /* @Override
    public void onDestroy() {
        super.onDestroy();
        if (adViewpagerUtil != null) {
            adViewpagerUtil.destroyAdViewPager();
        }
    }*/


}
