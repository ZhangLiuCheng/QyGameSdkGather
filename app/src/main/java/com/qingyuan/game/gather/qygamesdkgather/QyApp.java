package com.qingyuan.game.gather.qygamesdkgather;

import android.app.Application;

import com.qingyuan.game.gather.qysdk.QySdk;

public class QyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        QySdk.getInstance().init(this);
    }
}
