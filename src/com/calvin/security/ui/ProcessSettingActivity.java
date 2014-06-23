package com.calvin.security.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.calvin.security.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class ProcessSettingActivity extends RoboActivity {
    @InjectView(R.id.tv_process_setting_tips)
    private TextView tv_process_setting_tips;
    @InjectView(R.id.cb_process_setting_state)
    private CheckBox cb_process_setting_state;
    @InjectView(R.id.tv_process__clean_tips)
    private TextView tv_process_clean_tips;
    @InjectView(R.id.cb_process_clean_state)
    private CheckBox cb_process_clean_state;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.process_setting);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean showSystemProcess = sp.getBoolean("showSystemProcess", false);
        if (showSystemProcess) {
            tv_process_setting_tips.setText("显示系统进程");
        } else {
            tv_process_setting_tips.setText("不显示系统进程");
        }
        cb_process_setting_state.setChecked(showSystemProcess);

        cb_process_setting_state.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean isChecked) {
                if (isChecked) {
                    tv_process_setting_tips.setText("显示系统进程");
                    Editor edit = sp.edit();
                    edit.putBoolean("showSystemProcess", true);
                    edit.commit();
                    //与前面ProcessManagerActivity里面的那个resultCode对应
                    setResult(200);
                } else {
                    tv_process_setting_tips.setText("不显示系统进程");
                    Editor edit = sp.edit();
                    edit.putBoolean("showSystemProcess", false);
                    edit.commit();
                    setResult(200);
                }

            }
        });

        boolean killProcess = sp.getBoolean("killProcess", false);
        if (killProcess) {
            tv_process_clean_tips.setText("锁屏清理内存");
        } else {
            tv_process_clean_tips.setText("锁屏不清理内存");
        }
        cb_process_clean_state.setChecked(killProcess);

        cb_process_clean_state.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean isChecked) {
                if (isChecked) {
                    tv_process_clean_tips.setText("锁屏清理内存");
                    Editor edit = sp.edit();
                    edit.putBoolean("killProcess", true);
                    edit.commit();
                } else {
                    tv_process_clean_tips.setText("锁屏不清理内存");
                    Editor edit = sp.edit();
                    edit.putBoolean("showSystemProcess", false);
                    edit.commit();
                }
            }
        });

    }

}
