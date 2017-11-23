package com.qingyuan.game.gather.sdklibrary;

public interface QyPayNotifier {

    void onSuccess(String sdkOrderID, String cpOrderID, String extrasParams);

    void onCancel(String cpOrderID);

    void onFailed(int code, String message, String cpOrderID);

}
