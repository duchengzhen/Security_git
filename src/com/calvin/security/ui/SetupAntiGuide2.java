package com.calvin.security.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.calvin.security.R;
import com.calvin.security.receiver.MyAdminReceiver;

public class SetupAntiGuide2 extends Activity implements OnClickListener {
    
    private Button btnFinish;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.anti_theft_guide_3);

        btnFinish = (Button) findViewById(R.id.btn_guide3_finish);
        btnFinish.setOnClickListener(this);

        sp = getSharedPreferences("config", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_guide3_finish:
                //TODO 其他一些处理逻辑
                if (1!=1) {
                    // 创建AlertDialog标准写法
                    Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("强烈建议你开启防盗保护");
                    builder.setCancelable(false);
                    builder.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Editor edit = sp.edit();
                                    edit.putBoolean("setupGuide", true);// 记录Guide完成
                                    edit.commit();
                                    finish();
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Editor edit = sp.edit();
                                    edit.putBoolean("setupGuide", true);// 记录Guide完成
                                    edit.commit();
                                }
                            });
                    builder.create().show();

                } else {
                    finishSetupGuide();
                    finish();
                }
                break;

            default:
                break;
        }
    }

    private void finishSetupGuide() {
        Editor edit = sp.edit();
        edit.putBoolean("setupGuide", true);// 记录已经完成设置向导
        edit.commit();
        //绑定sim卡信息,将sim信息存储到config文件中
        setSimInfo();

        //拿到一个设备管理器
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        //new一个新组建,用来注册管理器页面
        ComponentName componentName = new ComponentName(this, MyAdminReceiver.class);
        if (!devicePolicyManager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivity(intent);
        }
    }

    /**
     * 绑定sim卡信息
     */
    protected void setSimInfo() {
        TelephonyManager telephoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String simSerial = telephoneManager.getSimSerialNumber();// 拿到sim卡的序列号,唯一的
        Editor editor = sp.edit();
        editor.putString("simSerial", simSerial);
        editor.commit();
    }
}
