package com.yundian.star.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.mixpush.MixPushService;
import com.yundian.star.R;
import com.yundian.star.app.AppConstant;
import com.yundian.star.base.BaseActivity;
import com.yundian.star.been.TabEntity;
import com.yundian.star.ui.im.fragment.DifferAnswerFragment;
import com.yundian.star.ui.main.fragment.MarketFragment;
import com.yundian.star.ui.main.fragment.NewsInfoFragment;
import com.yundian.star.ui.main.fragment.TestFragment;
import com.yundian.star.ui.wangyi.chatroom.helper.ChatRoomHelper;
import com.yundian.star.ui.wangyi.config.preference.UserPreferences;
import com.yundian.star.utils.LogUtils;
import com.yundian.star.utils.SharePrefUtil;

import java.util.ArrayList;

import butterknife.Bind;


public class MainActivity extends BaseActivity {
    @Bind(R.id.tab_bottom_layout)
    CommonTabLayout tabLayout ;
    private String[] mTitles = {"资讯", "行情","买卖","分答","我的"};
    private int[] mIconUnselectIds = {
            R.drawable.ic_home_normal,R.drawable.ic_home_normal,R.drawable.ic_home_normal,R.drawable.ic_home_normal,R.drawable.ic_home_normal};
    private int[] mIconSelectIds = {
            R.drawable.ic_home_selected,R.drawable.ic_home_selected, R.drawable.ic_home_selected,R.drawable.ic_home_selected,R.drawable.ic_home_selected};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private NewsInfoFragment newsInfoFragment;
    private MarketFragment marketFragment;
    private TestFragment testFragment3;
    private DifferAnswerFragment differAnswerFragment;
    private final int BASIC_PERMISSION_REQUEST_CODE = 100;
    public static int CHECHK_LOGIN = 0;
    private TestFragment testFragment5;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0 :

                    break;
            }
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            overridePendingTransition(R.anim.activity_open_down_in,0);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initPresenter() {
    }

    @Override
    public void initView() {
        initTab();
        checkIsLogin();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestBasicPermission();
        //初始化frament
        initFragment(savedInstanceState);
        initWangYi();
    }



    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (savedInstanceState != null) {
            newsInfoFragment = (NewsInfoFragment) getSupportFragmentManager().findFragmentByTag("NewsInfoFragment");
            marketFragment = (MarketFragment) getSupportFragmentManager().findFragmentByTag("MarketFragment");
            testFragment3 = (TestFragment) getSupportFragmentManager().findFragmentByTag("TestFragment3");
            differAnswerFragment = (DifferAnswerFragment) getSupportFragmentManager().findFragmentByTag("DifferAnswerFragment");
            testFragment5 = (TestFragment) getSupportFragmentManager().findFragmentByTag("TestFragment5");
            currentTabPosition = savedInstanceState.getInt(AppConstant.HOME_CURRENT_TAB_POSITION);
        } else {
            newsInfoFragment = new NewsInfoFragment();
            marketFragment = new MarketFragment();
            testFragment3 = new TestFragment();
            differAnswerFragment = new DifferAnswerFragment();
            testFragment5 = new TestFragment();
            transaction.add(R.id.fl_main, newsInfoFragment, "NewsInfoFragment");
            transaction.add(R.id.fl_main, marketFragment, "MarketFragment");
            transaction.add(R.id.fl_main, testFragment3, "TestFragment3");
            transaction.add(R.id.fl_main, differAnswerFragment, "DifferAnswerFragment");
            transaction.add(R.id.fl_main, testFragment5, "TestFragment5");
        }
        transaction.commit();
        SwitchTo(currentTabPosition);
        tabLayout.setCurrentTab(currentTabPosition);
    }

    /**
     * 入口
     * @param activity
     */
    public static void startAction(Activity activity){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in,
                R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //奔溃前保存位置
        LogUtils.loge("onSaveInstanceState进来了1");
        if (tabLayout != null) {
            LogUtils.loge("onSaveInstanceState进来了2");
            outState.putInt(AppConstant.HOME_CURRENT_TAB_POSITION, tabLayout.getCurrentTab());
        }
    }

    private void initWangYi() {
        // 等待同步数据完成
        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {

                syncPushNoDisturb(UserPreferences.getStatusConfig());

                DialogMaker.dismissProgressDialog();
            }
        });

        LogUtils.logd("sync completed = " + syncCompleted);
        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MainActivity.this, getString(R.string.prepare_data)).setCanceledOnTouchOutside(false);
        } else {
            syncPushNoDisturb(UserPreferences.getStatusConfig());
        }
        // 聊天室初始化
        ChatRoomHelper.init();
    }
    /**
     * 切换
     */
    private void SwitchTo(int position) {
        LogUtils.logd("主页菜单position" + position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                transaction.hide(marketFragment);
                transaction.hide(testFragment3);
                transaction.hide(differAnswerFragment);
                transaction.hide(testFragment5);
                transaction.show(newsInfoFragment);
                transaction.commitAllowingStateLoss();
                break;
            case 1:
                transaction.hide(newsInfoFragment);
                transaction.hide(testFragment3);
                transaction.hide(differAnswerFragment);
                transaction.hide(testFragment5);
                transaction.show(marketFragment);
                transaction.commitAllowingStateLoss();
                break;
            case 2:
                transaction.hide(marketFragment);
                transaction.hide(newsInfoFragment);
                transaction.hide(differAnswerFragment);
                transaction.hide(testFragment5);
                transaction.show(testFragment3);
                transaction.commitAllowingStateLoss();
                break;
            case 3:
                transaction.hide(marketFragment);
                transaction.hide(testFragment3);
                transaction.hide(newsInfoFragment);
                transaction.hide(testFragment5);
                transaction.show(differAnswerFragment);
                transaction.commitAllowingStateLoss();
                break;
            case 4:
                transaction.hide(marketFragment);
                transaction.hide(testFragment3);
                transaction.hide(newsInfoFragment);
                transaction.hide(differAnswerFragment);
                transaction.show(testFragment5);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }
    /**
     * 初始化tab
     */
    private void initTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayout.setTabData(mTabEntities);
        //点击监听
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                SwitchTo(position);
            }
            @Override
            public void onTabReselect(int position) {
            }
        });
    }
    private void checkIsLogin() {
        int firstlogin = SharePrefUtil.getInstance().getFirstlogin();
        String phoneNum = SharePrefUtil.getInstance().getPhoneNum();
        if (TextUtils.isEmpty(phoneNum)) { // 第一次登录, 需要走登录流程
            handler.postDelayed(runnable,400);
        }
    }

    /**
     * 基本权限管理
     *//*
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_NETWORK_STATE
    };

    private void requestBasicPermission() {
        MPermission.printMPermissionResult(true, this, BASIC_PERMISSIONS);
        MPermission.with(MainActivity.this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "未全部授权，部分功能可能无法正常运行！", Toast.LENGTH_SHORT).show();
        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }*/

    /**
     * 若增加第三方推送免打扰（V3.2.0新增功能），则：
     * 1.添加下面逻辑使得 push 免打扰与先前的设置同步。
     * 2.设置界面 com.netease.nim.demo.main.activity.SettingsActivity 以及
     * 免打扰设置界面 com.netease.nim.demo.main.activity.NoDisturbActivity 也应添加 push 免打扰的逻辑
     * <p>
     * 注意：isPushDndValid 返回 false， 表示未设置过push 免打扰。
     */
    private void syncPushNoDisturb(StatusBarNotificationConfig staConfig) {

        boolean isNoDisbConfigExist = NIMClient.getService(MixPushService.class).isPushNoDisturbConfigExist();

        if (!isNoDisbConfigExist && staConfig.downTimeToggle) {
            NIMClient.getService(MixPushService.class).setPushNoDisturbConfig(staConfig.downTimeToggle,
                    staConfig.downTimeBegin, staConfig.downTimeEnd);
        }
    }
}