package com.qingyuan.game.gather.qysdk;

import android.app.Activity;

import com.qingyuan.game.gather.sdk360.QyBridge360;
import com.qingyuan.game.gather.sdklibrary.QyLoginNotifier;
import com.qingyuan.game.gather.sdklibrary.QyLogoutNotifier;
import com.qingyuan.game.gather.sdklibrary.QyPayNotifier;
import com.qingyuan.game.gather.sdklibrary.QySdkBridge;
import com.qingyuan.game.gather.sdklibrary.bean.OrderInfo;

public class QySdk {

    private static final QySdk sInstance = new QySdk();

    private QySdkBridge sdkBridge = new QyBridge360();

    public static QySdk getInstance() {
        return sInstance;
    }

    public void setLoginNotifier(QyLoginNotifier loginNotifier) {
        sdkBridge.setLoginNotifier(loginNotifier);
    }

    public void setPayNotifier(QyPayNotifier payNotifier) {
        sdkBridge.setPayNotifier(payNotifier);
    }

    public void setLogoutNotifier(QyLogoutNotifier logoutNotifier) {
        sdkBridge.setLogoutNotifier(logoutNotifier);
    }

    public void init(Activity activity) {
        sdkBridge.init(activity);
    }

    public void login() {
        sdkBridge.login();
    }

    public void logout() {
        sdkBridge.logout();
    }

    public void destroy() {
        sdkBridge.destroy();
    }

    public void pay(final OrderInfo orderInfo) {
    }
}
