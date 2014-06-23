package com.calvin.security.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.calvin.security.R;
import com.calvin.security.service.WatchDogService;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class SettingActivity extends RoboActivity {
    @InjectView(R.id.tv_lock_tips)
    private TextView tv_lock_tips;
    @InjectView(R.id.cb_custom_setting)
    private CheckBox cb_lock_state;

    private Intent appLockIntent;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);

        appLockIntent = new Intent(this, WatchDogService.class);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean isAppLockStart = sp.getBoolean("appLock", false);
        if (isAppLockStart) {
            tv_lock_tips.setText("服务已经开启");
            cb_lock_state.setChecked(true);
        } else {
            tv_lock_tips.setText("服务没有开启");
            cb_lock_state.setChecked(false);
        }

        cb_lock_state.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(appLockIntent);
                    tv_lock_tips.setText("服务已开启");
                    Editor edit = sp.edit();
                    edit.putBoolean("appLock", true);
                    edit.commit();
                } else {
                    stopService(appLockIntent);
                    tv_lock_tips.setText("服务已关闭");
                    Editor edit = sp.edit();
                    edit.putBoolean("appLock", false);
                    edit.commit();
                }
            }
        });
    }
}
