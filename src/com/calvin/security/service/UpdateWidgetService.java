package com.calvin.security.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.calvin.security.R;
import com.calvin.security.receiver.ProcessCleanReceiver;
import com.calvin.security.utils.ProcessUtil;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private AppWidgetManager appWidgetManager;
    private ComponentName componentName;
    private RemoteViews remoteViews;
    private Intent intent;
    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override
    public void onCreate() {

        //得到一个AppWidgetManager对象
        appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        //拿到一个componentName对象
        componentName = new ComponentName("com.calvin.security", "com.calvin.security.receiver.ProcessWidget");
        //显示到桌面上的界面,用到的布局文件就是我们写的那个widget布局
        remoteViews = new RemoteViews("com.calvin.security", R.layout.process_widget);
        /*
		 * 这个intent是用来启动一个广播,因为我们的widget是一键清理
		 * 所以我们只要发送一条广播就可以调用里面的清理方法
		 */
        intent = new Intent(this, ProcessCleanReceiver.class);
        //拿到一个PendingIntent对象,用来发送一条广播
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        //java传统计时器
        timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                //设置我们的process_widget这个布局文件的内容
                remoteViews.setTextViewText(R.id.tv_process_count,
                        "进程数目" + ProcessUtil.getProcessCount(getApplicationContext()));
                remoteViews.setTextViewText(R.id.tv_process_memory,
                        "可用内存:" + ProcessUtil.getAvailMemory(getApplicationContext()));
                //给按钮添加点击事件
                remoteViews.setOnClickPendingIntent(R.id.bt_clean, pendingIntent);
                //更新widget里面的内容
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
        };
        //开启计时
        timer.scheduleAtFixedRate(timerTask, 1000, 3000);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消计时器
        timer.cancel();
        timer = null;
        timerTask = null;
        super.onDestroy();
    }

}
