package com.calvin.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;
import com.calvin.security.R;

public class SetupGuide2 extends Activity implements OnClickListener {
    private Button bt_bind;
    private Button bt_next;
    private Button bt_previous;
    private CheckBox cb_bind;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置不要显示标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setup_guide2);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        bt_bind = (Button) findViewById(R.id.bt_guide_bind_sim);
        bt_next = (Button) findViewById(R.id.bt_guide_next);
        bt_previous = (Button) findViewById(R.id.bt_guide_previous);

        bt_bind.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_previous.setOnClickListener(this);

        cb_bind = (CheckBox) findViewById(R.id.cb_guide2_bind);
        // 初始化CheckBox状态
        String sim = sp.getString("simSerial", null);
        if (sim != null) {
            cb_bind.setText("已经绑定");
            cb_bind.setChecked(true);
        } else {
            cb_bind.setText("没有绑定");
            cb_bind.setChecked(false);
        }

        cb_bind.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    cb_bind.setText("已绑定");
                    setSimInfo();// 绑定操作
                    Toast.makeText(SetupGuide2.this, "绑定成功", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    cb_bind.setText("未绑定");
                    unbindSimInfo();//
                    Toast.makeText(SetupGuide2.this, "解绑绑定成功",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 解绑sim卡信息
     */
    protected void unbindSimInfo() {
        Editor editor = sp.edit();
        editor.putString("simSerial", null);
        editor.commit();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_guide_bind_sim:
                setSimInfo();
                cb_bind.setText("已绑定");
                cb_bind.setChecked(true);
                break;
            case R.id.bt_guide_next:
                Intent intent = new Intent(this, SetupAntiGuide.class);
                finish();
                startActivity(intent);
                // 切换Activity的过度动画
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            default:
                break;
        }
    }

}
