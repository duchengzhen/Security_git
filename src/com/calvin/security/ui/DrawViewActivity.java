package com.calvin.security.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.calvin.security.R;

public class DrawViewActivity extends Activity implements OnTouchListener {
    private ImageView iv_drag_location;
    private SharedPreferences sp;
    // 初始的xy坐标位置
    private int startX;
    private int startY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drag_view);

        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        iv_drag_location = (ImageView) findViewById(R.id.iv_drag_location);
    }

    @Override
    protected void onResume() {// 有可见后台转为前台是调用的方法
        super.onResume();
        // 加载最后一次的位置
        int x = sp.getInt("lastX", 0);
        int y = sp.getInt("lastY", 0);
        // 读取iv_drag_location的view的配置参数
        LayoutParams params = (LayoutParams) iv_drag_location.getLayoutParams();
        params.leftMargin = x;
        params.rightMargin = y;
        iv_drag_location.setLayoutParams(params);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.iv_drag_location:

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 第一次按下
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 移动事件
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();
                        // 计算移动距离
                        int dx = x - startX;
                        int dy = y - startY;
                        int l = iv_drag_location.getLeft();
                        int r = iv_drag_location.getRight();
                        int t = iv_drag_location.getTop();
                        int b = iv_drag_location.getBottom();
                        // 设置新布局位置
                        iv_drag_location.layout(l + dx, t + dy, r + dx, b + dy);
                        // 重新获取位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:// 手指移开
                        int lastX = iv_drag_location.getLeft();
                        int lastY = iv_drag_location.getRight();
                        Editor edit = sp.edit();
                        edit.putInt("lastX", lastX);
                        edit.putInt("lastY", lastY);
                        edit.commit();
                        break;

                    default:
                        break;
                }

                break;

            default:
                break;
        }
        return false;
    }

}
