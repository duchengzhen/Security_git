package com.calvin.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * 手机重启的广播接收者,判断当前的Sim与config中的是否一致
 *
 * @author calvin 2014-5-21
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        //判断是否开启保护
        boolean isProtected = sp.getBoolean("isProtected", false);
        if (isProtected) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //重启开机后获得当前SIM卡的标识,与config中存档对比
            String currentSim = manager.getSimSerialNumber();
            String protectedSim = sp.getString("simSerial", "");
            //判断是否更换sim卡
            if (!currentSim.equals(protectedSim)) {
                SmsManager smsManager = SmsManager.getDefault();
                String number = sp.getString("number", "");
                //五个参数分别为,发送地址,第二个是发送人,第三个是信息内容,第四个是发送状态,第五个是发送回执
                smsManager.sendTextMessage(number, null, "Sim卡已经变更了,手机可能被盗了", null, null);
            }
        }
    }


}
