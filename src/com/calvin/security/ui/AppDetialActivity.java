package com.calvin.security.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.calvin.security.MyApplication;
import com.calvin.security.R;
import com.calvin.security.domain.TaskInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AppDetialActivity extends Activity {
    private TextView tv_app_detail_name;
    private ScrollView sv_app_detail_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_detail);

        tv_app_detail_name = (TextView) findViewById(R.id.tv_app_detail_name);
        sv_app_detail_desc = (ScrollView) findViewById(R.id.sv_app_detail_desc);

        //拿到自定义的application对象,然后设置在里面的taskInfo对象
        MyApplication myApplication = (MyApplication) getApplication();
        //taskInfo对象是在ProcessManager中设置
        TaskInfo taskInfo = myApplication.getTaskInfo();
        if (taskInfo != null) {
            tv_app_detail_name.setText(taskInfo.getName());
            try {
                /*
				 * 通过反射获取,调用
				 * android.widget.AppSecurityPermissions类中的方法
				 * getPermissionView,返回是是一个权限列表的View
				 */
                Class<?> clazz = getClass().getClassLoader().loadClass("android.widget.AppSecurityPermissions");
                //获得构造方法,两个参数Context,String
                Constructor<?> constructor = clazz.getConstructor(new Class[]{Context.class, String.class});
                //通过的构造器new出一个对象
                Object object = constructor.newInstance(new Object[]{this, taskInfo.getPackageName()});
                //得到我们要调用的getPermissionView方法,该方法没有参数
                Method method = clazz.getDeclaredMethod("getPermissionsView", new Class[]{});
                //调用这个方法,返回一个View对象
                View view = (View) method.invoke(object, new Object[]{});
                //然后把返回的view设置到scrollview中
                sv_app_detail_desc.addView(view);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("这里出问题了!!!");
            }
        }
    }

}
