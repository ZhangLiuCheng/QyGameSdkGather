package com.qingyuan.game.gather.qygamesdkgather;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.qingyuan.game.gather.sdklibrary.QyLoginNotifier;
import com.qingyuan.game.gather.qysdk.QySdk;
import com.qingyuan.game.gather.sdklibrary.QyLogoutNotifier;
import com.qingyuan.game.gather.sdklibrary.QyPayNotifier;
import com.qingyuan.game.gather.sdklibrary.bean.UserInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT > 14) {
            Window _window = getWindow();
            WindowManager.LayoutParams params = _window.getAttributes();
        	params.systemUiVisibility=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN;
            _window.setAttributes(params);
        }

        setContentView(R.layout.activity_main);

        QySdk.getInstance().init(this);
        QySdk.getInstance().setLoginNotifier(new QyLoginNotifier() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code, String message) {
                Toast.makeText(MainActivity.this, "登录失败 " + message, Toast.LENGTH_SHORT).show();
            }
        });

        QySdk.getInstance().setLogoutNotifier(new QyLogoutNotifier() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(MainActivity.this, "注销失败： " + message, Toast.LENGTH_SHORT).show();
            }
        });

        QySdk.getInstance().setPayNotifier(new QyPayNotifier() {
            @Override
            public void onSuccess(String sdkOrderID, String cpOrderID, String extrasParams) {

            }

            @Override
            public void onCancel(String cpOrderID) {

            }

            @Override
            public void onFailed(int code, String message, String cpOrderID) {

            }
        });



        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QySdk.getInstance().login();
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QySdk.getInstance().logout();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QySdk.getInstance().destroy();
    }
}
