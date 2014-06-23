package com.calvin.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.calvin.security.R;

public class SetupGuide1 extends Activity implements OnClickListener {

    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置不要显示标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setup_guide1);

        next = (Button) findViewById(R.id.bt_guide1_next);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_guide1_next:
                Intent intent = new Intent(this, SetupGuide2.class);
                finish();//TODO finish()
                startActivity(intent);
                //这个是定义activity切换时的动画效果
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;

            default:
                break;
        }
    }

}
