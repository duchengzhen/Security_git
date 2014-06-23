package com.calvin.security.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import com.calvin.security.service.UpdateWidgetService;

public class ProcessWidget extends AppWidgetProvider {
    private Intent intent;

    @Override//onUpdate 为组件在桌面上生成时调用
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //开启服务
        intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //开启服务
        intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //停止服务
        intent = new Intent(context, UpdateWidgetService.class);
        context.stopService(intent);
    }

    @Override//onReceiver 为接收广播时调用更新UI
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //开启服务
        intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }
}
