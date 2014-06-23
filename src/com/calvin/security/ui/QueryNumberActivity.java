package com.calvin.security.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.calvin.security.R;
import com.calvin.security.engine.NumberAddressService;

public class QueryNumberActivity extends Activity {
    private EditText et_query_number;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_number);

        et_query_number = (EditText) findViewById(R.id.et_query_number);
        tv_result = (TextView) findViewById(R.id.tv_query_result);
    }

    public void query(View view) {
        String number = et_query_number.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_query_number.startAnimation(shake);
            Toast.makeText(this, "号码不能为空哦...", Toast.LENGTH_SHORT).show();
        } else {
            String address = NumberAddressService.getAddress(number);
            tv_result.setText("归属地信息:" + address);
        }
    }

}
