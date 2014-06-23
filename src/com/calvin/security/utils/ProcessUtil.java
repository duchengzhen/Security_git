package com.calvin.security.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import java.util.List;

public class ProcessUtil {

    public static void killAllProcess(Context context) {
        //获取包管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取所有正在运行的进程信息
        List<RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
        //遍历后杀死
        for (RunningAppProcessInfo info : list) {
            activityManager.killBackgroundProcesses(info.processName);
        }
    }

    public static String getProcessCount(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
        return list.size() + "";
    }

    public static String getAvailMemory(Context context) {
        //获取包管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //new一个内存对象
        MemoryInfo memoryInfo = new MemoryInfo();
        //拿到现在系统里面的内存信息
        activityManager.getMemoryInfo(memoryInfo);
        return TextFormater.dataSizeFormat(memoryInfo.availMem);
    }

}
