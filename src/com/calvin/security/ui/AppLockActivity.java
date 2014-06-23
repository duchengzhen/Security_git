package com.calvin.security.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import com.calvin.security.R;
import com.calvin.security.adapter.AppLockAdapter;
import com.calvin.security.dao.AppLockDao;
import com.calvin.security.domain.AppInfo;
import com.calvin.security.engine.AppInfoProvider;

import java.util.List;

public class AppLockActivity extends Activity {
    private ListView lv_app_lock;
    private AppLockAdapter adapter;
    private List<AppInfo> list;
    private AppInfoProvider provider;
    private AppLockDao dao;
    private List<String> lockApps;//保存被加锁的程序

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new AppLockAdapter(list, lockApps, AppLockActivity.this);
            lv_app_lock.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.app_lock);

        lv_app_lock = (ListView) findViewById(R.id.lv_app_lock);
        provider = new AppInfoProvider(this);

        dao = new AppLockDao(this);
        lockApps = dao.getAllPackageName();

        initAppInfos();

        lv_app_lock.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //添加动画效果,之后将锁的图片改变
                TranslateAnimation anim = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setDuration(300);
                view.startAnimation(anim);

                AppInfo info = list.get(position);
                String name = info.getPackageName();
                ImageView iv_lock = (ImageView) view.findViewById(R.id.iv_app_lock);

                if (dao.find(name)) {
                    dao.delete(name);
                    lockApps.remove(name);
                    iv_lock.setImageResource(R.drawable.unlock);
                } else {
                    dao.add(name);
                    lockApps.add(name);
                    iv_lock.setImageResource(R.drawable.lock);
                }
            }
        });
    }

    private void initAppInfos() {
        new Thread() {
            @Override
            public void run() {
                list = provider.getAllApps();
                handler.sendEmptyMessage(0);
            }

            ;
        }.start();
    }

}
