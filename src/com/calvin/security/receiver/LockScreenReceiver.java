package com.calvin.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.calvin.security.utils.ProcessUtil;

public class LockScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("已经锁屏了");
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean killProcess = sp.getBoolean("killProcess", false);
        if (killProcess) {
            System.out.println("开始清理内存");
            //调用Util中方法,杀死所有进程
            ProcessUtil.killAllProcess(context);
        }
    }

}
