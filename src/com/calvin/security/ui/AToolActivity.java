package com.calvin.security.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.calvin.security.R;
import com.calvin.security.engine.DownloadTask;
import com.calvin.security.engine.SmsService;
import com.calvin.security.service.AddressService;
import com.calvin.security.service.BackupSmsService;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import java.io.File;

/**
 * 查询来电归属地
 *
 * @author calvin
 */
public class AToolActivity extends RoboActivity implements OnClickListener {
    private static final int ERROR = -1;
    private static final int SUCCESS = 1;
    // 处理子线程更新UI
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ERROR:
                    Toast.makeText(AToolActivity.this, "更新来电归属数据失败...",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    Toast.makeText(AToolActivity.this, "更新来电归属数据成功!",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };
    private TextView tv_atool_query;
    private TextView tv_atool_number_service_state;
    private CheckBox cb_atool_state;
    private TextView tv_atool_select_bg;
    private TextView tv_atool_change_location;
    private Intent serviceIntent;
    private ProgressDialog pd;
    private TextView tv_atool_sms_backup;
    private TextView tv_atool_sms_restore;
    private TextView tv_atool_app_lock;
    @InjectView(R.id.tv_atool_common_number)
    private TextView tv_atool_common_number;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atool);

        init();
        cb_atool_state
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            startService(serviceIntent);// 启动服务
                            tv_atool_number_service_state
                                    .setTextColor(Color.BLACK);
                            tv_atool_number_service_state.setText("归属地服务已开启");
                        } else {
                            stopService(serviceIntent);
                            tv_atool_number_service_state
                                    .setTextColor(Color.RED);
                            tv_atool_number_service_state.setText("归属地服务未开启");
                        }

                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_atool_query:
                query();
                break;
            case R.id.tv_atool_select_bg:
                selectStyle();
                break;
            case R.id.tv_atool_change_location:
                Intent intent = new Intent(this, DrawViewActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_atool_backupSms:
                Intent backupIntent = new Intent(this, BackupSmsService.class);
                startService(backupIntent);
                break;
            case R.id.tv_atool_restoreSms:
                restore();
                break;
            case R.id.tv_atool_app_lock:
                Intent appLock = new Intent(this, AppLockActivity.class);
                startActivity(appLock);
                break;
            case R.id.tv_atool_common_number:
                Intent common = new Intent(this, CommonNumberActivity.class);
                startActivity(common);
                break;
            default:
                break;
        }

    }

    private void restore() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("短信恢复");
        pd.setMessage("正在恢复短信...");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        final SmsService smsService = new SmsService(this);

        new Thread() {
            @Override
            public void run() {
                try {
                    smsService.restore(Environment.getExternalStorageDirectory() + "security/backup/smsbackup.xml", pd);
                    pd.dismiss();
                    Looper.prepare();
                    Toast.makeText(AToolActivity.this, "恢复成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } catch (Exception e) {
                    pd.dismiss();
                    Looper.prepare();
                    Toast.makeText(AToolActivity.this, "恢复失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }

            ;

        }.start();

    }

    private void selectStyle() {
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地显示风格");
        String[] items = {"半透明", "活力橙", "苹果绿", "孔雀蓝", "金属灰"};
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editor edit = sp.edit();
                        edit.putInt("background", which);
                        edit.commit();
                    }
                });

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void query() {
        if (isDbExist()) {
            Intent intent = new Intent(this, QueryNumberActivity.class);
            startActivity(intent);
        } else {// 数据库不存在就下载
            pd = new ProgressDialog(this);
            pd.setMessage("正在下载来电归属数据...");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(false);
            pd.show();
            // 开子线程用于下载
            new Thread() {
                @Override
                public void run() {
                    String path = getResources().getString(R.string.serverDb);
                    File dir = new File(
                            Environment.getExternalStorageDirectory()
                                    + "/security/db");

                    if (!dir.exists())
                        dir.mkdirs();

                    String dbPath = Environment.getExternalStorageDirectory()
                            + "/security/db/data.db";
                    try {
                        // TODO 之前写好的工具类
                        DownloadTask.getFile(path, dbPath, pd);
                        pd.dismiss();

                        Message message = new Message();
                        message.what = SUCCESS;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        pd.dismiss();
                        Message message = new Message();
                        message.what = ERROR;
                        handler.sendMessage(message);
                    }
                }

                ;
            }.start();
        }
    }

    /**
     * 判断来电归属地数据库是否存在
     *
     * @return
     */
    private boolean isDbExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/security/db/data.db");
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 简单初始化工作
     */
    private void init() {
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        tv_atool_change_location = (TextView) findViewById(R.id.tv_atool_change_location);
        tv_atool_change_location.setOnClickListener(this);

        tv_atool_select_bg = (TextView) findViewById(R.id.tv_atool_select_bg);
        tv_atool_select_bg.setOnClickListener(this);

        tv_atool_number_service_state = (TextView) findViewById(R.id.tv_atool_number_service_state);
        tv_atool_sms_backup = (TextView) findViewById(R.id.tv_atool_backupSms);
        tv_atool_sms_backup.setOnClickListener(this);

        tv_atool_sms_restore = (TextView) findViewById(R.id.tv_atool_restoreSms);
        tv_atool_sms_restore.setOnClickListener(this);

        cb_atool_state = (CheckBox) findViewById(R.id.cb_atool_state);

        serviceIntent = new Intent(this, AddressService.class);
        tv_atool_query = (TextView) findViewById(R.id.tv_atool_query);
        tv_atool_query.setOnClickListener(this);

        tv_atool_app_lock = (TextView) findViewById(R.id.tv_atool_app_lock);
        tv_atool_app_lock.setOnClickListener(this);

        tv_atool_common_number.setOnClickListener(this);
    }

}
