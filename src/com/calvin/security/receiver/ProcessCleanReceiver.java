package com.calvin.security.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.calvin.security.utils.ProcessUtil;

public class ProcessCleanReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //清理内存
        ProcessUtil.killAllProcess(context);
    }

}
