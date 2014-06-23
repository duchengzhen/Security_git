package com.calvin.security.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.calvin.security.R;
import com.calvin.security.engine.GPSInfoProvider;
import com.calvin.security.utils.MD5Encoder;

import java.util.concurrent.locks.Lock;

public class LostProtectedActivity extends Activity implements OnClickListener {
    private SharedPreferences sp;
    private Dialog dialog;
    private EditText etPwd;
    private EditText etConfirmPwd;
    private Button btnLock;
    private Button btnLocate;
    private Button btnReset;
    private Button btnBackupCons;
    private Button btnAlarm;
    private Button btnSendOrder;

    private DevicePolicyManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        if (isSetPwd() && isSetNumber()) {
            setContentView(R.layout.anti_home);
            showLoginDialog();
            initHomePage();
        } else if (!isSetPwd()) {//没有设置密码
            setContentView(R.layout.anti_theft_guide_1);
            init();
        } else if (!isSetNumber()) {//没有设置保密号码
            Intent guide2 = new Intent(this, SetupAntiGuide.class);
            startActivity(guide2);
        }
    }

    /**初始化主页demo按钮*/
    private void initHomePage() {
        btnLock= (Button) findViewById(R.id.btn_lock_device);
        btnLock.setOnClickListener(this);
        btnLocate= (Button) findViewById(R.id.btn_locate_device);
        btnLocate.setOnClickListener(this);
        btnReset= (Button) findViewById(R.id.btn_reset_device);
        btnReset.setOnClickListener(this);
        btnBackupCons = (Button) findViewById(R.id.btn_backup_contacts);
        btnBackupCons.setOnClickListener(this);
        btnAlarm= (Button) findViewById(R.id.btn_alarm);
        btnAlarm.setOnClickListener(this);
        btnSendOrder= (Button) findViewById(R.id.btn_send_order);
        btnSendOrder.setOnClickListener(this);

        manager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    /**
     * 判断是否有密码存在
     */
    private boolean isSetPwd() {
        String pwd = sp.getString("password", "");
        return !pwd.equals("");
    }

    /**
     * 判断用户是否设置安全号码
     */
    private boolean isSetNumber() {
        String number = sp.getString("number", "");
        return !number.equals("");
    }

    /**
     * 第一次设置防盗功能
     */
    private void init() {
        etPwd = (EditText) findViewById(R.id.et_protected_first_pwd);
        etConfirmPwd = (EditText) findViewById(R.id.et_protected_first_confirm_pwd);
        Button btnNext = (Button) findViewById(R.id.btn_guide1_next);
        btnNext.setOnClickListener(this);
    }

    /**
     * 再次登录设置
     */
    private void showLoginDialog() {
        dialog = new Dialog(this, R.style.MyDialog);
        View view = View.inflate(this, R.layout.login_dialog, null);
        etPwd = (EditText) view.findViewById(R.id.et_protected_pwd);
        Button ok = (Button) view.findViewById(R.id.bt_protected_login_yes);
        Button cancel = (Button) view.findViewById(R.id.bt_protected_login_no);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dialog.setContentView(view);
        //对dialog设置键盘监听,屏蔽返回按键
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_BACK){
                    System.out.println("按下了返回键");
                    return true;
                }
                return false;
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_guide1_next://向导下一步
                String firstPwd = etPwd.getText().toString().trim();
                String secondPwd = etConfirmPwd.getText().toString().trim();
                if (firstPwd.equals("") || secondPwd.equals("")) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (firstPwd.equals(secondPwd)) {
                        Editor editor = sp.edit();
                        //MD5加密后存入配置文件
                        editor.putString("password", MD5Encoder.encode(firstPwd));
                        editor.commit();
                        //然后启动下一个界面
                        Intent guide2 = new Intent(this, SetupAntiGuide.class);
                        startActivity(guide2);
                        finish();
                    } else {
                        Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;

            case R.id.bt_protected_login_yes://登录框的确定按钮
                String password = etPwd.getText().toString().trim();
                if (password.equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    String str = sp.getString("password", "");
                    if (MD5Encoder.encode(password).equals(str)) {
                        dialog.dismiss();
                        //显示防盗主界面
                    } else {
                        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.bt_protected_login_no://登陆框的取消按钮
                finish();
                dialog.dismiss();
                break;
            case R.id.tv_lost_Protected_guide://重新设置向导
                finish();
                Intent intent = new Intent(this, SetupGuide1.class);
                startActivity(intent);
                break;
            case R.id.btn_lock_device://锁定手机
                AlertDialog.Builder lockBuilder = getAlertDialog("将锁定手机", "锁定后,默认解锁密码为1234");
                lockBuilder.setNegativeButton("取消!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                lockBuilder.setPositiveButton("确定?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //重新设置密码,第二个参数暂时没用,填0
                        manager.resetPassword("1234", 0);
                        //锁屏
                        manager.lockNow();
                    }
                });
                lockBuilder.create().show();
                break;
            case R.id.btn_locate_device://定位
                GPSInfoProvider gpsInfoProvider = GPSInfoProvider.getInstance(this);
                String location = gpsInfoProvider.getLocation();
                location= TextUtils.isEmpty(location)?"暂时无法获取位置,稍后重试":location;
                Toast.makeText(this,location,Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_reset_device://销毁数据
                //显示对话框
                AlertDialog.Builder resetBuilder = getAlertDialog("强烈建议你不要尝试!", "这将清除你手机中全部数据!!!");
                resetBuilder.setNegativeButton("取消!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                resetBuilder.setPositiveButton("确定?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LostProtectedActivity.this, "将清除设备中所有内容...", Toast.LENGTH_SHORT).show();
                        //清除数据方法
                        manager.wipeData(0);
                    }
                });
                resetBuilder.create().show();
                break;
            case R.id.btn_backup_contacts://备份联系人
                Toast.makeText(this, "已备份信息人到手机根目录", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_alarm://警报
                //该方法已经调用了prepare这个方法,所以不用再次调用mediaPlayer.prepare();
                final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alert);
                //将音量调至最大
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("警告");
                //新建文本框
                TextView textView = new TextView(this);
                textView.setText("正在播放报警音,取消可关闭");
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                builder.setView(textView);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.stop();
                        }
                    }
                });
                builder.create().show();
                break;
            case R.id.btn_send_order://备份短信指令
                Toast.makeText(this,R.string.order,Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    /**
     * 生成对话框
     * @param title 对话框标题
     * @param textViewContent 对话框中textView文本内容
     * @return 返回builder
     */
    private AlertDialog.Builder getAlertDialog(String title, String textViewContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        //新建文本框
        TextView textView = new TextView(this);
        textView.setText(textViewContent);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        builder.setView(textView);
        return builder;
    }
}
