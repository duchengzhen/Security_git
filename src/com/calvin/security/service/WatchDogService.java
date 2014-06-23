package com.calvin.security.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import com.calvin.security.dao.AppLockDao;
import com.calvin.security.iService.IService;
import com.calvin.security.ui.LockActivity;

import java.util.ArrayList;
import java.util.List;

public class WatchDogService extends Service {
    private AppLockDao dao;
    private List<String> apps;
    private ActivityManager activityManager;
    private Intent intent;
    private boolean flag = true;

    private List<String> stopApps;//存放已经输入密码,暂时不保护的程序
    private MyBinder myBinder;

    //键盘的管理器 ???
    private KeyguardManager keyguardManager;

    /**
     * 服务一但用bindService进行绑定之后，
     * 就会马上返回一个Binder对象的，
     * 我们之前在服务里面返回的Binder对象就是我们实现了IService接口的对象啦，
     * 所以我们就在绑定的时候，拿到那个Binder对象，
     * 然后把它转换成IService，这样我们就可以调用服务里面的方法啦
     */
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        stopApps = new ArrayList<String>();
        myBinder = new MyBinder();

        dao = new AppLockDao(this);
        apps = dao.getAllPackageName();
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        intent = new Intent(this, LockActivity.class);
        //服务中没有任务栈,所以要指定一个新的任务栈,否则无法在服务中启动Activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //注册一个内容观察者,观察AppLockProvider
        getContentResolver().registerContentObserver(
                Uri.parse("content://com.calvin.security.applockprovider"),
                true, new MyObserver(new Handler()));

        new Thread() {
            @Override
            public void run() {
                while (flag) {
                    try {
                        //当解锁后,就再一次输入密码
                        if (keyguardManager.inKeyguardRestrictedInputMode()) {
                            stopApps.clear();//
                        }

						/*
						 * 得到当前运行的任务栈,参数就是得到多个任务栈,
						 * 1就是拿到一个任务栈,也即是正在运行的任务栈
						 */
                        List<RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
                        //拿到当前运行的任务栈
                        RunningTaskInfo taskInfo = taskInfos.get(0);
                        //得到要运行的Activity的包名
                        String packageName = taskInfo.topActivity.getPackageName();
                        //临时取消应用保护
                        if (stopApps.contains(packageName)) {
                            sleep(1000);
                            continue;
                        }
                        //判断当前运行程序是否在被锁定额列表中
                        if (apps.contains(packageName)) {
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                            //flag=false;
                        } else {
                            //TODO
                        }
                        sleep(1000);//每隔1秒,启动服务检测一次
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        //TODO 当后台服务被销毁的时候(比如被清理软件清理掉),重新开启服务(有问题!)
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        if (sp.getBoolean("appLock", false)) {
            intent = new Intent(WatchDogService.this, WatchDogService.class);
            //服务中没有任务栈,所以要指定一个新的任务栈,否则无法在服务中启动Activities
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
        }
    }

    //临时开启对某个程序的保护
    private void invokeMethodStartApp(String packageName) {
        if (stopApps.contains(packageName)) {
            stopApps.remove(packageName);
        }
    }

    //临时停止对某个程序的保护
    private void invokeMethodStopApps(String packageName) {
        stopApps.add(packageName);
    }

    /**
     * 当服务一但绑定的时候，就返回这个类的对象
     * @author calvin
     */
    private class MyBinder extends Binder implements IService {

        @Override
        public void startApp(String packageName) {
            invokeMethodStartApp(packageName);
        }

        @Override
        public void stopApp(String packageName) {
            invokeMethodStopApps(packageName);
        }

    }

    private class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
            //重新更新apps里面的内容
            apps = dao.getAllPackageName();
            System.out.println("数据库内容发生了改变");
        }

    }

}
