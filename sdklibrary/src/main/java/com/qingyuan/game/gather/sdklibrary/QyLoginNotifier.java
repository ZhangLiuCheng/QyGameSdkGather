package com.qingyuan.game.gather.sdklibrary;

import com.qingyuan.game.gather.sdklibrary.bean.UserInfo;

public interface QyLoginNotifier {

    void onSuccess(UserInfo userInfo);

    void onCancel();

    void onFailed(int code, String message);

}
