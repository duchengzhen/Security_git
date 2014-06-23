package com.calvin.security.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import com.calvin.security.R;
import com.calvin.security.engine.GPSInfoProvider;

/**
 * 短信广播接收者,拦截到我们发送的指令短信,作出分析做相应的处理
 *
 * @author calvin
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //获取以数组的形式存储的所有短信内容
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        for (Object pdu : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            //获取短息内容
            String content = smsMessage.getMessageBody();
            //获取发信人电话号码
            String sender = smsMessage.getOriginatingAddress();

            if (content.contains("#*location*#")) {
                abortBroadcast();//终止该条广播
                //获取位置信息
                GPSInfoProvider gpsInfoProvider = GPSInfoProvider.getInstance(context);
                String location = gpsInfoProvider.getLocation();
                System.out.println(location);
                //不能确定一定能获取到信息(比如定位没开..)
                if (!location.equals("")) {
                    //发送出短信
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sender, null, location, null, null);
                }
            } else if (content.contains("#*lockscreen*#")) {//锁屏操作
                DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                //重新设置密码,第二个参数暂时没用,填0
                manager.resetPassword("1234", 0);
                //进行锁屏
                manager.lockNow();
                abortBroadcast();
            } else if (content.contains("#*wipe*#")) {//恢复出厂设置
                DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                manager.wipeData(0);
                abortBroadcast();
            } else if (content.contains("#*alarm*#")) {//发出报警声音
                //该方法已经调用了prepare这个方法,所以不用再次调用mediaPlayer.prepare();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alert);
                //将音量调至最大
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();
                abortBroadcast();
            }

        }
    }


}
