package com.calvin.security;

import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import com.calvin.security.domain.UpdateInfo;
import com.calvin.security.engine.DownloadTask;
import com.calvin.security.engine.UpdateInfoService;
import com.calvin.security.service.AddressService;
import com.calvin.security.service.UpdateWidgetService;
import com.calvin.security.ui.MainActivity;

import java.io.File;

public class SplashActivity extends Activity {

    private static final int ISNEEDUPDATE = 1;
    private static final int UPDATEFAIL = -1;
    private TextView tvVersion;
    private LinearLayout ll;
    private UpdateInfo info;
    private ProgressDialog progressDialog;
    private String version;//当前程序的版本
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ISNEEDUPDATE:
                    //判断版本是否需要更新
                    if (isNeedUpdate(version)) {
                        showUpdateDialog();
                    }
                    break;

                case UPDATEFAIL:
                    Toast.makeText(SplashActivity.this, "更新失败...", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置不要显示标题栏
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        //设置通知栏
        setNotification();

        //TODO =============================================
        Intent intent = new Intent(this, AddressService.class);
        startService(intent);
        Intent wintent = new Intent(this, UpdateWidgetService.class);
        startService(wintent);
        //TODO =============================================

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tvVersion = (TextView) findViewById(R.id.tv_splash_version);
        version = getVision();//获取当前版本
        tvVersion.setText("当前版本" + version);
        info = new UpdateInfo();

        //设置启动渐变动画
        ll = (LinearLayout) findViewById(R.id.ll_splash_main);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000);
        ll.setAnimation(alphaAnimation);

        //初始化ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载...");

        new Thread() {
            @Override
            public void run() {
                try {
                    //TODO 为了看到启动界故意设置的休眠
                    sleep(1500);
                    Message message = new Message();
                    message.what = ISNEEDUPDATE;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {}
            }

            ;
        }.start();

        new Thread() {
            @Override
            public void run() {
                UpdateInfoService updateInfoService = new UpdateInfoService(SplashActivity.this);
                try {
                    info = updateInfoService.getUpdateInfo(R.string.serverUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();

    }


    private void showUpdateDialog() {
        Builder builder = new AlertDialog.Builder(this);
        //设置警告对话框的参数
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.AlertDialog_update_title);
        builder.setMessage(info.getDescription());
        builder.setCancelable(false);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File dir = new File(Environment.getExternalStorageDirectory(), "dowmload");
                    if (!dir.exists()) dir.mkdir();//如果目录不存在就创建

                    String apkPath = Environment.getExternalStorageDirectory() + "/download/Security_update.apk";
                    UpdateTask updateTask = new UpdateTask(info.getUrl(), apkPath);
                    progressDialog.show();
                    new Thread(updateTask).start();
                } else {
                    Toast.makeText(SplashActivity.this, "当前SD卡不可用,无法更新", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadMainUI();
            }
        });
        builder.create().show();
    }

    private boolean isNeedUpdate(String version) {
        if (info == null) {
            Toast.makeText(this, "获取更新信息异常请稍后再试", Toast.LENGTH_SHORT).show();
            loadMainUI();//启动主界面
            return false;
        }
        String v = info.getVersion();
        if (TextUtils.equals(v, version)) {
            System.out.println("不用更新");
            return false;
        } else {
            System.out.println("需要更新");
            return true;
        }
    }

    private String getVision() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "未知";
        }
    }

    /**
     * 安装apk的方法
     *
     * @param file
     */
    private void install(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        finish();
        startActivity(intent);
    }

    /**
     * 启动主界面方法
     */
    private void loadMainUI() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 下载文件的子线程
     *
     * @author calvin
     */
    class UpdateTask implements Runnable {
        private String path;
        private String filePath;


        public UpdateTask(String path, String filePath) {
            this.path = path;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                File file = DownloadTask.getFile(path, filePath, progressDialog);
                progressDialog.dismiss();//下载完成后取消progressdialog
                install(file);//调用安装apk方法
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Message message = new Message();
                message.what = UPDATEFAIL;
                handler.sendMessage(message);
                loadMainUI();//如果更新失败启动主界面
            }
        }

    }

    //添加常驻通知
    private void setNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("安全管家正在守护您");
        notification.setOngoing(true);//常驻通知栏
        //notification.flags = Notification.FLAG_ONGOING_EVENT; // 旧版设置常驻 Flag
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contextIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //自定义RemoteViews,通知栏布局
        //RemoteViews rv=new RemoteViews(getPackageName(),R.layout.notification);
        notification.setContentIntent(contextIntent);
        //notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(R.string.information), contextIntent);
        notificationManager.notify(R.string.app_name,notification.build());
    }
}
























