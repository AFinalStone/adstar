package com.cloudTop.starshare.ui.wangyi.event;

import android.text.TextUtils;
import com.netease.nim.uikit.OnlineStateContentProvider;
import com.cloudTop.starshare.ui.wangyi.DemoCache;

/**
 * Created by hzchenkang on 2017/3/31.
 */

public class DemoOnlineStateContentProvider implements OnlineStateContentProvider {

    @Override
    public String getSimpleDisplay(String account) {
        String content = getDisplayContent(account, true);
        if (!TextUtils.isEmpty(content)) {
            content = "[" + content + "]";
        }
        return content;
    }

    @Override
    public String getDetailDisplay(String account) {
        return getDisplayContent(account, false);
    }

    private String getDisplayContent(String account, boolean simple) {
        if (account == null || account.equals(DemoCache.getAccount())) {
            return "";
        }
        // 检查是否订阅过
        OnlineStateEventManager.checkSubscribe(account);

        OnlineState onlineState = OnlineStateEventCache.getOnlineState(account);
        return OnlineStateEventManager.getOnlineClientContent(DemoCache.getContext(), onlineState, simple);
    }
}
