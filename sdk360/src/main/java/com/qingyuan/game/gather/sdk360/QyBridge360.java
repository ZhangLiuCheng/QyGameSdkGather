package com.qingyuan.game.gather.sdk360;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.CPCallBackMgr;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;
import com.qingyuan.game.gather.sdklibrary.QySdkBridge;
import com.qingyuan.game.gather.sdklibrary.bean.OrderInfo;

import org.json.JSONObject;

public class QyBridge360 extends QySdkBridge {

    private final static String TAG = "QYSDK";

    private Activity activity;

    protected CPCallBackMgr.MatrixCallBack mSDKCallback = new CPCallBackMgr.MatrixCallBack() {
        @Override
        public void execute(Context context, int functionCode, String functionParams) {
            if (functionCode == ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT) {
                Log.v(TAG, "MatrixCallBack  FUNC_CODE_SWITCH_ACCOUNT");
                doSdkSwitchAccount(getLandscape(context));
            } else if (functionCode == ProtocolConfigs.FUNC_CODE_INITSUCCESS) {
                //这里返回成功之后才能调用SDK 其它接口
                Log.v(TAG, "MatrixCallBack  FUNC_CODE_INITSUCCESS");
            }
        }
    };

    // 登录、注册的回调
    private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {
        @Override
        public void onFinished(String data) {
            Log.v("QYSDK", "LoginCallback  " + data);
            try {
                JSONObject rootData = new JSONObject(data);
                int errno = rootData.optInt("errno", -1);
                if (-1 == errno) {
                    loginCancel();
                    return;
                }
                if (0 == errno) {
                    JSONObject joData = rootData.getJSONObject("data");
                    String accessToken = joData.getString("access_token");
                    getUserInfo(accessToken);
                } else {
                    loginFailed(rootData.optString("errmsg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                loginFailed(e.getMessage());
            }
        }
    };

    @Override
    public void init(Activity activity) {
        this.activity = activity;
        Matrix.initInApplication(activity.getApplication());
        Matrix.setActivity(activity, mSDKCallback, true);
    }

    @Override
    public void login() {
        doSdkLogin(false);
    }

    @Override
    public void logout() {
        Intent intent = getLogoutIntent();
        Matrix.execute(activity, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
//            	2、如果用户是未登录状态，则直接调用“注销登陆”接口 会返回注销登陆失败的结果，请CP根据自己的业务处理该处逻辑即可。
                Log.v("QYSDK", "LogoutCallback  " + data);
                try {
                    JSONObject rootData = new JSONObject(data);
                    int errno = rootData.optInt("errno", -1);
                    if (0 == errno) {
                        logoutSuccess();
                    } else {
                        logoutFailed(rootData.optString("errmsg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logoutFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void destroy() {
        Matrix.destroy(activity);
    }

    protected void doSdkLogin(boolean isLandScape) {
        Intent intent = getLoginIntent(isLandScape);
        Matrix.execute(activity, intent, mLoginCallback);
    }

    protected void doSdkSwitchAccount(boolean isLandScape) {
        Intent intent = getSwitchAccountIntent(isLandScape);
        Matrix.invokeActivity(activity, intent, mLoginCallback);
    }

    private Intent getLoginIntent(boolean isLandScape) {
        Intent intent = new Intent(activity, ContainerActivity.class);
        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        // 必需参数，使用360SDK的登录模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);
        //是否显示关闭按钮
        intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, true);

        //-- 以下参数仅仅针对自动登录过程的控制
        // 可选参数，自动登录过程中是否不展示任何UI，默认展示。
        intent.putExtra(ProtocolKeys.IS_AUTOLOGIN_NOUI, true);
        // 可选参数，静默自动登录失败后是否显示登录窗口，默认不显示
        intent.putExtra(ProtocolKeys.IS_SHOW_LOGINDLG_ONFAILED_AUTOLOGIN, true);
        //-- 测试参数，发布时要去掉
        intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG, true);

        return intent;
    }

    private Intent getSwitchAccountIntent(boolean isLandScape) {
        Intent intent = new Intent(activity, ContainerActivity.class);
        // 必须参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        // 必需参数，使用360SDK的切换账号模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);
        //是否显示关闭按钮
        intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, true);
        // 可选参数，是否支持离线模式，默认值为false
        intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, false);
        // 可选参数，是否隐藏欢迎界面
        intent.putExtra(ProtocolKeys.IS_HIDE_WELLCOME, false);
        // 测试参数，发布时要去掉
        intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG, true);
        return intent;
    }

    private Intent getLogoutIntent(){
        Intent intent = new Intent();
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGOUT);
        return intent;
    }

    protected boolean getLandscape(Context context) {
        if (context == null) {
            return false;
        }
        boolean landscape = (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        return landscape;
    }

    private void getUserInfo(String accessToken) {
        loginSuccess(accessToken);
    }




    protected void doSdkPay(OrderInfo orderInfo) {
        int functionCode = ProtocolConfigs.FUNC_CODE_PAY;
        Intent intent = getPayIntent(getLandscape(activity), orderInfo, functionCode);
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, functionCode);
        // 启动接口
        Matrix.invokeActivity(activity, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String s) {

            }
        });
    }

    protected Intent getPayIntent(boolean isLandScape, OrderInfo pay, int functionCode) {
        Bundle bundle = new Bundle();
        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);
        // 必需参数，360账号id，整数。
        bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.getPlatformId());
        // 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
        bundle.putString(ProtocolKeys.AMOUNT, pay.getAmount());
        // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
        bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.getProductName());
        // 必需参数，购买商品的商品id，应用指定，最大16字符。
        bundle.putString(ProtocolKeys.PRODUCT_ID, pay.getProductId());
        // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
        bundle.putString(ProtocolKeys.NOTIFY_URI, pay.getNotifyUri());
        // 必需参数，游戏或应用名称，最大16中文字。
        bundle.putString(ProtocolKeys.APP_NAME, pay.getAppName());
        // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
        // 充到统一的用户账户，各区服角色均可使用）。
        bundle.putString(ProtocolKeys.APP_USER_NAME, pay.getAppUserName());
        // 必需参数，应用内的用户id。
        // 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
        bundle.putString(ProtocolKeys.APP_USER_ID, pay.getAppUserId());

        // 可选参数，应用扩展信息1，原样返回，最大255字符。
//        bundle.putString(ProtocolKeys.APP_EXT_1, pay.getAppExt1());
        // 可选参数，应用扩展信息2，原样返回，最大255字符。
//        bundle.putString(ProtocolKeys.APP_EXT_2, pay.getAppExt2());
        // 可选参数，应用订单号，应用内必须唯一，最大32字符。
        bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.getAppOrderId());

        bundle.putInt(ProtocolKeys.FUNCTION_CODE,functionCode);
        // 必需参数，商品数量（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putInt(ProtocolKeys.PRODUCT_COUNT, 100);
        // 必需参数，服务器id（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putString(ProtocolKeys.SERVER_ID, "1025");
        // 必需参数，服务器名称（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putString(ProtocolKeys.SERVER_NAME, "火烧赤壁");
        // 必需参数，兑换比例（demo模拟数据）（游戏内虚拟币兑换人民币） ,游戏内逻辑请传递游戏内真实数据
        bundle.putInt(ProtocolKeys.EXCHANGE_RATE, 10);
        // 必需参数，货币名称（demo模拟数据）（比如：钻石）,游戏内逻辑请传递游戏内真实数据
        bundle.putString(ProtocolKeys.GAMEMONEY_NAME, "钻石");
        // 必需参数，角色id（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putString(ProtocolKeys.ROLE_ID, "888888");
        // 必需参数，角色名称（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putString(ProtocolKeys.ROLE_NAME, "孙悟空");
        // 必需参数，角色等级（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putInt(ProtocolKeys.ROLE_GRADE, 100);
        // 必需参数，虚拟币余额（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putInt(ProtocolKeys.ROLE_BALANCE, 10000);
        // 必需参数，vip等级（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putString(ProtocolKeys.ROLE_VIP, "10");
        // 必需参数，工会名称（demo模拟数据）,游戏内逻辑请传递游戏内真实数据
        bundle.putString(ProtocolKeys.ROLE_USERPARTY, "幽灵大师");
        Intent intent = new Intent(activity, ContainerActivity.class);
        intent.putExtras(bundle);

        return intent;
    }
}
