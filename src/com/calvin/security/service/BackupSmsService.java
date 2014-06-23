package com.calvin.security.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.util.Xml;
import android.widget.Toast;
import com.calvin.security.domain.SmsInfo;
import com.calvin.security.engine.SmsService;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class BackupSmsService extends Service {
    SmsService smsService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        smsService = new SmsService(this);

        //备份操作比较耗时,新开线程
        new Thread() {
            @Override
            public void run() {
                List<SmsInfo> infos = smsService.getSmsInfos();
                File dir = new File(Environment.getExternalStorageDirectory() + "/security/backup");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/security/backup/smsbackup.xml");
                //创建一个xml序列化器
                XmlSerializer serializer = Xml.newSerializer();
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    serializer.setOutput(fos, "utf-8");
                    serializer.startDocument("utf-8", true);
                    serializer.startTag(null, "smss");
                    int count = 0;
                    for (SmsInfo info : infos) {
                        count++;
                        serializer.startTag(null, "sms");

                        serializer.startTag(null, "id");
                        serializer.text(info.getId());
                        serializer.endTag(null, "id");

                        serializer.startTag(null, "address");
                        serializer.text(info.getAddress());
                        serializer.endTag(null, "address");

                        serializer.startTag(null, "date");
                        serializer.text(info.getDate());
                        serializer.endTag(null, "date");

                        serializer.startTag(null, "type");
                        serializer.text(info.getType() + "");
                        serializer.endTag(null, "type");

                        serializer.startTag(null, "body");
                        serializer.text(info.getBody());
                        serializer.endTag(null, "body");

                        serializer.endTag(null, "sms");
                    }
                    serializer.startTag(null, "count");
                    serializer.text(count + "");
                    serializer.endTag(null, "count");

                    serializer.endTag(null, "smss");
                    serializer.endDocument();

                    fos.flush();
                    fos.close();

                    //在子线程里面的不能弹出一个Toast,因为子线程里面没有looper
                    //可通过一下步骤在子线程里面弹出Toast
                    Looper.prepare();//创建一个Looper
                    Toast.makeText(getApplicationContext(), "备份成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();//轮询一次Looper
                } catch (Exception e) {
                    Looper.prepare();//创建一个Looper
                    Toast.makeText(getApplicationContext(), "备份失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();//轮询一次Looper
                    e.printStackTrace();
                }

            }

            ;
        }.start();
    }

}
