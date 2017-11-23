package com.qingyuan.game.gather.sdklibrary;

import android.app.Activity;

public abstract class QySdkBridge {

    private QyLoginNotifier loginNotifier;
    private QyLogoutNotifier logoutNotifier;
    private QyPayNotifier payNotifier;

    public void setLoginNotifier(QyLoginNotifier loginNotifier) {
        this.loginNotifier = loginNotifier;
    }

    public void setPayNotifier(QyPayNotifier payNotifier) {
        this.payNotifier = payNotifier;
    }

    public void setLogoutNotifier(QyLogoutNotifier logoutNotifier) {
        this.logoutNotifier = logoutNotifier;
    }

    public abstract void init(Activity activity);
    public abstract void login();
    public abstract void logout();
    public abstract void destroy();

    protected void loginSuccess(String uid) {
        if (null != loginNotifier) {
            loginNotifier.onSuccess(null);
        }
    }

    protected void loginCancel() {
        if (null != loginNotifier) {
            loginNotifier.onCancel();
        }
    }

    protected void loginFailed(String message) {
        if (null != loginNotifier) {
            loginNotifier.onFailed(-1, message);
        }
    }

    protected void logoutSuccess() {
        if (null != logoutNotifier) {
            logoutNotifier.onSuccess();
        }
    }

    protected void logoutFailed(String message) {
        if (null != logoutNotifier) {
            logoutNotifier.onFailed(message);
        }
    }
}
