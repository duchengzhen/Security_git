package com.calvin.security;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import com.calvin.security.domain.TaskInfo;
import com.calvin.security.receiver.LockScreenReceiver;

/**
 * app运行环境(大保健)
 */
public class MyApplication extends Application {
    private TaskInfo taskInfo;
    private SharedPreferences sp;

    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    @Override//手动代码注册广播接收者
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        intentFilter.setPriority(1000);
        LockScreenReceiver lockScreenReceiver = new LockScreenReceiver();
        registerReceiver(lockScreenReceiver, intentFilter);
    }

    public SharedPreferences getConfigSp(){
        if(sp==null){
           return getSharedPreferences("config",MODE_PRIVATE);
        }
        return sp;

    }

    /**
     * 判断用户是否设置安全号码
     */
    private boolean isSetNumber() {
        String number = getConfigSp().getString("number", "");
        return !number.equals("");
    }

}
