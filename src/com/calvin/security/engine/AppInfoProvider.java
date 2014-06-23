package com.calvin.security.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import com.calvin.security.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfoProvider {
    private PackageManager packageManager;

    public AppInfoProvider(Context context) {
        //得到一个包管理器
        this.packageManager = context.getPackageManager();
    }

    public List<AppInfo> getAllApps() {
        List<AppInfo> list = new ArrayList<AppInfo>();
        AppInfo myAppInfo;
        //获取所有安装的应用程序,包括哪些卸载了的,但还没有清除数据
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(
                PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo info : packageInfos) {
            myAppInfo = new AppInfo();
            //得到包名
            String packageName = info.packageName;
            //得到应用程序的信息
            ApplicationInfo appInfo = info.applicationInfo;
            //得到应用程序的图标
            Drawable icon = appInfo.loadIcon(packageManager);
            //得到程序的程序名
            String appName = appInfo.loadLabel(packageManager).toString();

            myAppInfo.setAppName(appName);
            myAppInfo.setIcon(icon);
            myAppInfo.setPackageName(packageName);

            if (filterApp(appInfo)) {
                myAppInfo.setSystemApp(false);
            } else {
                myAppInfo.setSystemApp(true);
            }
            list.add(myAppInfo);
        }
        return list;
    }

    /**
     * 判断程序是否是系统程序
     *
     * @param info
     * @return 如果是返回true
     */
    private boolean filterApp(ApplicationInfo info) {
        /*
		 * 有些系统应用可以更新,如果用户更新覆盖了原来的系统程序,他还是系统程序
		 * 官方源码!!!
		 */
        //TODO (info.flags & ApplicationInfo.FLAG_SYSTEM)???
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;//已经更新的系统程序
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }


}
