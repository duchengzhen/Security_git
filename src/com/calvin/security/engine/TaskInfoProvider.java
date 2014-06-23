package com.calvin.security.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import com.calvin.security.R;
import com.calvin.security.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

public class TaskInfoProvider {
    private PackageManager packageManager;
    private ActivityManager activityManager;

    private Drawable defaultIcon;//给没有icon的程序设置默认的icon

    public TaskInfoProvider(Context context) {
        defaultIcon = context.getResources().getDrawable(R.drawable.ic_launcher);
        packageManager = context.getPackageManager();
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

    }

    public TaskInfoProvider(PackageManager packageManager,
                            ActivityManager activityManager) {
        this.packageManager = packageManager;
        this.activityManager = activityManager;
    }

    public List<TaskInfo> getAllTask(List<RunningAppProcessInfo> runningAppProcessInfos) {
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        for (RunningAppProcessInfo rapi : runningAppProcessInfos) {
            TaskInfo taskInfo = new TaskInfo();
            int id = rapi.pid;
            taskInfo.setId(id);

            String packageName = rapi.processName;
            taskInfo.setPackageName(packageName);
            if (packageName.equals("android.process.acore")) {
                taskInfo.setName("Android核心应用");
                taskInfo.setMemory(0);
                taskInfo.setSystemProcess(true);
            } else {
                //TODO 比较两个applicationInfo效果是否一样
                ApplicationInfo applicationInfo;
                try {
                    applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                    //applicationInfo是AndroidMainfest文件里面整个Application节点的封装
                    //ApplicationInfo applicationInfo = packageManager.getPackageInfo(packageName, 0).applicationInfo;
                    //应用icon图标
                    Drawable icon = applicationInfo.loadIcon(packageManager);
                    taskInfo.setIcon(icon);
                    //应用的名字
                    String name = applicationInfo.loadLabel(packageManager).toString();
                    taskInfo.setName(name);
                    //设置是否为系统应用
                    taskInfo.setSystemProcess(!filterApp(applicationInfo));
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                    //当遇到没有界面和图标的一些进程的时候的处理方式
                    taskInfo.setName(packageName);
                    taskInfo.setSystemProcess(true);
                    taskInfo.setIcon(defaultIcon);
                }
                //返回一个内存信息组,传进去的i点有多少,就返回多少id的内存信息
                MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(new int[]{id});
                //拿到该id程序占用的内存空间
                int memory = memoryInfos[0].getTotalPrivateDirty();
                taskInfo.setMemory(memory);
            }

            taskInfos.add(taskInfo);
            System.out.println(taskInfo.getId() + "..." + taskInfo.getName() + "..." + taskInfo.getMemory());
            taskInfo = null;

        }
        return taskInfos;
    }

    /**
     * 判断某个程序是不是用户程序
     *
     * @param applicationInfo
     * @return true说明是用户程序
     */
    private boolean filterApp(ApplicationInfo applicationInfo) {
        /*
		 * 有些系统程序可以更新,更新后系统就不认为它是程序程序,我们要进行判断
		 */
        if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }

}
