package com.yundian.star.ui.main.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.yundian.star.R;
import com.yundian.star.app.AppConstant;
import com.yundian.star.base.BaseFragment;
import com.yundian.star.been.OptionsStarListBeen;
import com.yundian.star.listener.OnAPIListener;
import com.yundian.star.networkapi.NetworkAPIFactoryImpl;
import com.yundian.star.ui.main.activity.StarTimeShareActivity;
import com.yundian.star.ui.main.adapter.MarketDetailAdapter;
import com.yundian.star.utils.LogUtils;

import java.util.ArrayList;

import butterknife.Bind;


/**
 * Created by Administrator on 2017/5/15.
 */

public class MarketDetailFragment extends BaseFragment {
    @Bind(R.id.ll_select_order)
    LinearLayout ll_select_order ;
    @Bind(R.id.iv_select)
    ImageView iv_select ;
    @Bind(R.id.lrv)
    LRecyclerView lrv ;
    MarketDetailAdapter marketDetailAdapter;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private static int mCurrentCounter = 1;
    private static final int REQUEST_COUNT = 10;
    private static final int GET_DATA = 10;
    private static final int LOAD_DATA = 11;
    private int ORDER = 0;
    private ArrayList<OptionsStarListBeen.ListBean> list = new ArrayList<>();
    private ArrayList<OptionsStarListBeen.ListBean> loadList = new ArrayList<>();
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA:
                    showData();
                    break;
                case LOAD_DATA:
                    mCurrentCounter =list.size();
                    loadMoreData();
                    break;

            }
        }
    };
    private String marketDetailName;
    private int marketDetailType;

    private void loadMoreData() {
        if (loadList == null || list.size() == 0) {
            lrv.setNoMore(true);
        } else {
            list.addAll(loadList);
            marketDetailAdapter.addAll(loadList);
            mCurrentCounter += loadList.size();
            lrv.refreshComplete(REQUEST_COUNT);
        }
    }

    public void showData() {
        mCurrentCounter =list.size();
        lRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
        marketDetailAdapter.addAll(list);
        lrv.refresh();
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_market_detail;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {
        if (getArguments()!=null){
            marketDetailName = getArguments().getString(AppConstant.MARKET_DETAIL_NAME);
            marketDetailType = getArguments().getInt(AppConstant.MARKET_DETAIL_TYPE);
        }
        initAdpter();
        getData(false,1,REQUEST_COUNT);
    }

    private void getData(final boolean isLoadMore, int start, int end) {
        if (marketDetailType==0){
            NetworkAPIFactoryImpl.getInformationAPI().getOptionsStarList(/*SharePrefUtil.getInstance().getPhoneNum()*/1770640+"",start,end,ORDER, new OnAPIListener<OptionsStarListBeen>() {
                @Override
                public void onError(Throwable ex) {

                }

                @Override
                public void onSuccess(OptionsStarListBeen optionsStarListBeen) {
                    if (optionsStarListBeen.getList()==null){
                        lrv.setNoMore(true);
                        return;
                    }
                    if (isLoadMore){
                        loadList.clear();
                        loadList = optionsStarListBeen.getList();
                        myHandler.sendEmptyMessage(LOAD_DATA);
                    }else {
                        list.clear();
                        list = optionsStarListBeen.getList();
                        myHandler.sendEmptyMessage(GET_DATA);
                    }
                }
            });
        }else {
            NetworkAPIFactoryImpl.getInformationAPI().getMarketstar(marketDetailType, start, end,ORDER, new OnAPIListener<OptionsStarListBeen>() {
                @Override
                public void onError(Throwable ex) {

                }

                @Override
                public void onSuccess(OptionsStarListBeen optionsStarListBeen) {
                    LogUtils.loge("行情每个页面请求数据返回的retult:"+optionsStarListBeen);
                    if (optionsStarListBeen.getList()==null){
                        lrv.setNoMore(true);
                        return;
                    }
                    if(optionsStarListBeen.getResult()==1){
                        if (isLoadMore){
                            loadList.clear();
                            loadList = optionsStarListBeen.getList();
                            myHandler.sendEmptyMessage(LOAD_DATA);
                        }else {
                            list.clear();
                            list = optionsStarListBeen.getList();
                            myHandler.sendEmptyMessage(GET_DATA);
                        }
                    }

                }
            });
        }



    }

    private void initAdpter() {
        marketDetailAdapter = new MarketDetailAdapter(getActivity());
        lRecyclerViewAdapter = new LRecyclerViewAdapter(marketDetailAdapter);
        lrv.setAdapter(lRecyclerViewAdapter);
        //mRecyclerView.setHasFixedSize(true);
        lrv.setLayoutManager(new LinearLayoutManager(getContext()));
        lrv.setPullRefreshEnabled(false);
        lrv.setLoadMoreEnabled(true);
        lrv.setNoMore(false);
        lrv.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lrv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getData(true,mCurrentCounter+1,mCurrentCounter+REQUEST_COUNT);
            }
        });
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.logd(position+"");
                OptionsStarListBeen.ListBean bean = list.get(position);
                Intent intent = new Intent(getActivity(),StarTimeShareActivity.class);
                intent.putExtra(AppConstant.STAR_CODE,bean.getStarcode());
                intent.putExtra(AppConstant.STAR_NAME,bean.getName());
                startActivity(intent);
            }
        });
    }
}
