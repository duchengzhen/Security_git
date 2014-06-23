package com.calvin.security.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.telephony.ITelephony;
import com.calvin.security.R;
import com.calvin.security.dao.BlackNumberDao;
import com.calvin.security.engine.NumberAddressService;
import com.calvin.security.ui.NumberSecurityActivity;

import java.lang.reflect.Method;

public class AddressService extends Service {
    private TelephonyManager telephonyManager;
    private MyPhoneListener listener;
    private WindowManager windowManager;
    private View view;
    private BlackNumberDao dao;

    private SharedPreferences sp;
    private long start;
    private long end;

    @Override
    public IBinder onBind(Intent intent) {

        //return new MyBinder().showLoaction(address);
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();

    }

    private void init() {
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        dao = new BlackNumberDao(this);

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        listener = new MyPhoneListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁时,停止监听
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    private void endCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getServices", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(iBinder);
            telephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //清除通话记录
    private void cleanCallLog(String incomingNumbe) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, " number=? ", new String[]{incomingNumbe}, null);
        if (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            resolver.delete(CallLog.CONTENT_URI, "_id=? ", new String[]{id});
        }
    }

    //显示归属地的窗体WindowManager
    private void showLocation(String address) {
        LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE//无法获得焦点
                | LayoutParams.FLAG_NOT_TOUCHABLE//无法点击
                | LayoutParams.FLAG_KEEP_SCREEN_ON;//保持屏幕常亮
        layoutParams.format = PixelFormat.TRANSLUCENT;//半透明
        layoutParams.type = LayoutParams.TYPE_TOAST;//Toast类型
        layoutParams.setTitle("Toast");
        //确认坐标系是从左上角开始,避免设置位置时麻烦
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = sp.getInt("lastX", 0);
        layoutParams.y = sp.getInt("lastY", 0);

        //tv=new TextView(this);
        //tv.setText("归属地:"+address);
        //windowManager.addView(tv, layoutParams);
        view = View.inflate(this, R.layout.show_location, null);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_show_location);
        int type = sp.getInt("background", 0);

        switch (type) {
            case 0:
                ll.setBackgroundResource(R.drawable.call_locate_white);
                break;
            case 1:
                ll.setBackgroundResource(R.drawable.call_locate_orange);
                break;
            case 2:
                ll.setBackgroundResource(R.drawable.call_locate_green);
                break;
            case 3:
                ll.setBackgroundResource(R.drawable.call_locate_blue);
                break;
            case 4:
                ll.setBackgroundResource(R.drawable.call_locate_gray);
                break;
            default:
                break;
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_show_location);
        tv.setText("归属地:" + address);
        windowManager.addView(ll, layoutParams);
    }

    private void showNotification(String number) {
        //取得Notification管理器
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //new 出一个Notification
        Notification notification = new Notification(R.drawable.notification, "发现响铃一声电话", System.currentTimeMillis());
        Context context = getApplicationContext();
        //设置点击后消失
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Intent notificationIntent = new Intent(context, NumberSecurityActivity.class);
        notificationIntent.putExtra("number", number);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        //激活Notification
        notificationManager.notify(0, notification);

    }

    //===================暂无用,用于Activity调用Service中方法======================
    public class MyBinder extends Binder {
        public void showLoaction(String address) {
            showLocation(address);
        }
    }

    //============================================================================
    private class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    end = System.currentTimeMillis();
                    if (end > start && (end - start) < 2000) {
                        start = end = 0;
                        showNotification(incomingNumber);
                    }
                    if (view != null) {
                        windowManager.removeView(view);//移除来电归属view
                        view = null;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接通状态
                    if (view != null) {
                        windowManager.removeView(view);//移除来电归属view
                        view = null;
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    start = System.currentTimeMillis();
                    if (dao.find(incomingNumber)) {
                        endCall();
                        //注册一个内容观察者,如果内容发生了改变之后,就执行删除的操作
                        getContentResolver().registerContentObserver(
                                CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(), incomingNumber));

                    }
                    String address = NumberAddressService.getAddress(incomingNumber);
                    showLocation(address);
                    break;

                default:
                    break;
            }
        }
    }

    private class MyObserver extends ContentObserver {
        private String number;

        public MyObserver(Handler handler, String number) {
            super(handler);
            this.number = number;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            cleanCallLog(number);

            getContentResolver().unregisterContentObserver(this);
        }
    }
}
