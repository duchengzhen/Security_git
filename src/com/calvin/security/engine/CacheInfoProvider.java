package com.calvin.security.engine;

import android.content.Context;
import android.content.pm.*;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.RemoteException;
import com.calvin.security.domain.CacheInfo;
import com.calvin.security.ui.CacheClearActivity;
import com.calvin.security.utils.TextFormater;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

/**
 * Created by calvin on 2014/5/31.
 */
public class CacheInfoProvider {

    private Handler handler;
    private PackageManager packageManager;
    private Vector<CacheInfo> cacheInfos;
    private int size = 0;

    //构造器
    public CacheInfoProvider(Handler handler, Context context) {
        packageManager = context.getPackageManager();
        this.handler = handler;
        cacheInfos = new Vector<CacheInfo>();
    }

    public void initCacheInfo() {
        /*
         *获取所有安装的程序信息,包括已经卸载但还没清楚数据的
         */
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        size = packageInfos.size();
        for (int i = 0; i < size; i++) {
            PackageInfo packageInfo = packageInfos.get(i);
            CacheInfo cacheInfo = new CacheInfo();
            //拿到包名
            String packageName = packageInfo.packageName;
            cacheInfo.setPackageName(packageName);
            //拿到应用的信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //拿到应用的程序名
            String name = applicationInfo.loadLabel(packageManager).toString();
            cacheInfo.setName(name);
            //拿到程序的图标
            Drawable icon = applicationInfo.loadIcon(packageManager);
            cacheInfo.setIcon(icon);
        }
    }

    /**
     * 通过AIDL方法获取应用的缓存信息,getPackageSizeInfo是PackageManager中一个私有方法
     * 只能通过反射调用,传递的参数IPackageStateObserver.Stub对象,通过AIDL获取
     * 因为是通过Handler异步处理,所以用到线程安全的Vector
     *
     * @param cacheInfo
     * @param position
     */
    private void initdataSize(final CacheInfo cacheInfo, final int position) {
        try {
            Method method = PackageManager.class.getMethod("getPackageSizeInfo", new Class[]{String.class, IPackageStatsObserver.class});
            method.invoke(packageManager, new Object[]{cacheInfo.getPackageName(), new IPackageStatsObserver.Stub() {

                @Override
                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    System.out.println("onGetStatsCompleted方法:" + position);
                    long cacheSize = pStats.cacheSize;
                    long dataSize = pStats.dataSize;
                    long codeSize = pStats.codeSize;

                    cacheInfo.setCacheSize(TextFormater.dataSizeFormat(cacheSize));
                    cacheInfo.setDataSize(TextFormater.dataSizeFormat(dataSize));
                    cacheInfo.setCodeSize(TextFormater.dataSizeFormat(codeSize));

                    cacheInfos.add(cacheInfo);

                    if (position == (size - 1)) {
                        //当完全获取信息之后,发送一条消息1,对应的是FINISH
                        handler.sendEmptyMessage(CacheClearActivity.FINISH);
                    }
                }
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Vector<CacheInfo> getCacheInfos() {
        return cacheInfos;
    }

    public void setCacheInfos(Vector<CacheInfo> cacheInfos) {
        this.cacheInfos = cacheInfos;
    }
}
