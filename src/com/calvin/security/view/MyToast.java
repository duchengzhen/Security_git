package com.calvin.security.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.calvin.security.R;

/**
 * 自定义Toast
 *
 * @author calvin
 */
public class MyToast {
    /**
     * 静态方法
     *
     * @param context 上下文
     * @param id      资源id
     * @param text    Toast中要显示的文本内容
     */
    public static void showToast(Context context, int id, String text) {
        View view = View.inflate(context, R.layout.mytoast, null);
        TextView tv_toast_msg = (TextView) view.findViewById(R.id.tv_toast_msg);
        Drawable drawable = context.getResources().getDrawable(id);
        //在左边设置一张图片,对应android:drawableLeft这个属性
        tv_toast_msg.setCompoundDrawables(drawable, null, null, null);
        tv_toast_msg.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
