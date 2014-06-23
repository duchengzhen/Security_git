package com.calvin.security.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.calvin.security.R;
import com.calvin.security.iService.IService;
import com.calvin.security.service.WatchDogService;
import com.calvin.security.utils.MD5Encoder;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class LockActivity extends RoboActivity {
    @InjectView(R.id.iv_lock_app_icon)
    ImageView iv_app_icon;
    @InjectView(R.id.tv_lock_app_name)
    TextView tv_app_name;
    @InjectView(R.id.et_lock_pwd)
    EditText et_app_pwd;

    private String password;
    private MyConnection conn;
    private IService iService;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lock);

        //TODO 这里为了方便,直接拿手机防盗里面那个密码,可做成需要用户自己设置
        password = getSharedPreferences("config", Context.MODE_PRIVATE).getString("password", "");

        packageName = getIntent().getStringExtra("packageName");
        //通过包拿到applicationInfo
        try {
            ApplicationInfo appInfo = getPackageManager().getPackageInfo(packageName, 0).applicationInfo;
            //应用图标
            Drawable icon = appInfo.loadIcon(getPackageManager());
            //应用名字
            String appName = appInfo.loadLabel(getPackageManager()).toString();
            iv_app_icon.setImageDrawable(icon);
            tv_app_name.setText(appName);

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        //绑定服务,主要是为了能够调用服务里面的方法
        Intent intent = new Intent(this, WatchDogService.class);
        conn = new MyConnection();
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

    }

    @Override//不让用户按后退键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //屏蔽后退键
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            return true;//终止事件
        }
        return super.onKeyDown(keyCode, event);

    }

    //按钮点击事件
    public void confirm(View v) {
        String input = et_app_pwd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "你的密码还未设置,请在手机防盗中设定...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "密码不能为空...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.equals(MD5Encoder.encode(input), password)) {
            finish();
            //调用服务中的方法,将该程序加入临时关闭的名单里
            iService.stopApp(packageName);
        } else {
            Toast.makeText(this, "密码错误!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override//解除绑定服务
    protected void onDestroy() {
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        super.onDestroy();
    }

    //====================================================================================
    private class MyConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //我们之前在Service里面已经实现的了IService接口
            iService = (IService) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    }

}
