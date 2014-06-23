package com.calvin.security.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.calvin.security.R;

public class SetupAntiGuide extends Activity implements OnClickListener {
    private Button btnNext;
    //private Button bt_previous;
    private Button btnSelect;
    private EditText etPhoneNumber;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anti_theft_guide_2);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);

        btnNext = (Button) findViewById(R.id.btn_guide2_next);
        btnSelect = (Button) findViewById(R.id.btn_guide2_select_contact);
        btnNext.setOnClickListener(this);
        btnSelect.setOnClickListener(this);

        etPhoneNumber = (EditText) findViewById(R.id.et_guide2_phone_number);
    }

    @Override //重写该方法,从Activity里获得返回数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //requestCode请求码是区分,从数据从哪一个Activity获得
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            //
            String number = data.getStringExtra("number");
            etPhoneNumber.setText(number);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_guide2_select_contact:
                Intent intent = new Intent(this, SelectContactActivity.class);
                //启动Activity并返回数据
                startActivityForResult(intent, 1);
                //启动动画
                overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
                break;
            case R.id.btn_guide2_next:
                String number = etPhoneNumber.getText().toString().trim();
                if (number.equals("")) {
                    Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    Editor editor = sp.edit();
                    editor.putString("number", number);
                    editor.commit();

                    Intent i = new Intent(this, SetupAntiGuide2.class);
                    finish();
                    startActivity(i);
                    //切换动画
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                }
                break;
            default:
                break;
        }
    }

}
