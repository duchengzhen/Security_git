package com.calvin.security.receiver;

import android.content.*;
import android.os.IBinder;
import android.widget.Toast;
import com.calvin.security.service.AddressService;
import com.calvin.security.service.AddressService.MyBinder;
import com.calvin.security.ui.LostProtectedActivity;
import com.calvin.security.ui.MainActivity;

import java.net.SocketPermission;

public class CallPhoneReciver extends BroadcastReceiver {
    private String outPhoneNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 得到广播数据
        outPhoneNumber=getResultData()==null?"1234":getResultData();
System.out.println(outPhoneNumber);
        //showLocation(context);
        Toast.makeText(context,"安全管家检测到外拨电话",Toast.LENGTH_SHORT).show();
        // TODO 该方法有问题,待解决..可以吧拨打的号码做成参数的形式,让用户配置
        if (outPhoneNumber.equals("0000")) {// 当监听到用户拨打的是1234的时候,执行下面操作
            Intent i = new Intent(context, LostProtectedActivity.class);
            // 启动Activity,系统会将其放入栈顶,但Receiver中没有栈,就必须指定Flags,新建栈
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            setResultData(null);// 这行代码是将广播数据设置为null,取消号码拨打
        }
    }

    private void showLocation(Context context) {
        Intent address = new Intent(context, AddressService.class);
        ServiceConnection conn = new MyServiceConnection();
        context.bindService(address, conn, Context.BIND_AUTO_CREATE);

    }

    private class MyServiceConnection implements ServiceConnection {

        @Override//绑定服务成功调用的该方法
        public void onServiceConnected(ComponentName name, IBinder service) {
            AddressService.MyBinder myBinder = (MyBinder) service;
            myBinder.showLoaction(outPhoneNumber);

        }

        @Override//取消连接时调用
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }
    }

}
