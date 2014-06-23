package com.calvin.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.calvin.security.R;
import com.calvin.security.adapter.AppManagerAdapter;
import com.calvin.security.domain.AppInfo;
import com.calvin.security.engine.AppInfoProvider;
import com.calvin.security.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements OnClickListener {
    public static final int GET_ALL_APP_FINISH = 1;
    public static final int GET_USER_APP_FINISH = 2;

    private ListView lv_app_manager;
    private LinearLayout ll_app_manager_progress;
    private AppInfoProvider provider;
    private AppManagerAdapter adapter;
    private List<AppInfo> list;
    private PopupWindow popupWindow;
    private TextView tv_app_title;
    //判断是不是加载中,如果是,就不能进行系统程序,用户程序切换
    private boolean flag = false;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ll_app_manager_progress.setVisibility(View.GONE);

            switch (msg.what) {
                case GET_ALL_APP_FINISH:
                    //进度条设为不可见
                    adapter = new AppManagerAdapter(list, AppManagerActivity.this);
                    lv_app_manager.setAdapter(adapter);
                    flag = true;
                    break;
                case GET_USER_APP_FINISH:
                    adapter = new AppManagerAdapter(getUserApp(), AppManagerActivity.this);
                    lv_app_manager.setAdapter(adapter);
                    flag = true;
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_manager);

        tv_app_title = (TextView) findViewById(R.id.tv_app_title);
        tv_app_title.setOnClickListener(this);
        lv_app_manager = (ListView) findViewById(R.id.lv_manager);
        ll_app_manager_progress = (LinearLayout) findViewById(R.id.ll_app_manager_progress);
        ll_app_manager_progress.setVisibility(View.VISIBLE);

        initUi(false);

        lv_app_manager.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                dismissPopupWindow();
                //用来存放当前item的坐标,第一个是x,的二个是y
                int[] location = new int[2];
                //将当前item坐标放入int[]数组中
                view.getLocationInWindow(location);

                View popupView = View.inflate(AppManagerActivity.this, R.layout.popup_item, null);
                LinearLayout ll_app_uninstall = (LinearLayout) popupView.findViewById(R.id.ll_app_uninstall);
                LinearLayout ll_app_start = (LinearLayout) popupView.findViewById(R.id.ll_app_start);
                LinearLayout ll_app_share = (LinearLayout) popupView.findViewById(R.id.ll_app_share);

                ll_app_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_app_share.setOnClickListener(AppManagerActivity.this);
                ll_app_start.setOnClickListener(AppManagerActivity.this);

                //拿到当时点击的条目,并设置在view里面
                AppInfo info = (AppInfo) lv_app_manager.getItemAtPosition(position);
                ll_app_uninstall.setTag(info);
                ll_app_start.setTag(info);
                ll_app_share.setTag(info);

                //添加启动动画
                LinearLayout ll_app_popup = (LinearLayout) popupView.findViewById(R.id.ll_app_popup);
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
                scaleAnimation.setDuration(300);

                //将popupWindow new出来
                popupWindow = new PopupWindow(
                        popupView, DensityUtil.dip2px(AppManagerActivity.this, 230), DensityUtil.dip2px(AppManagerActivity.this, 70));
                /*
				 * 一定要个popupwindow设置一个北京图片,否则会有很多未知问题
				 * 如果不加动画也会有问题,就加一个透明背景色
				 */
                Drawable drawable = new ColorDrawable(Color.TRANSPARENT);
                popupWindow.setBackgroundDrawable(drawable);
                int x = location[0] + 60;
                int y = location[1];
                //将popupWindow显示出来
                popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, x, y);
                //开启动画
                ll_app_popup.startAnimation(scaleAnimation);
            }
        });

        //listview滚动时调用的方法,dismiss掉popupWindow窗口
        lv_app_manager.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                dismissPopupWindow();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();
            }
        });
    }

    @Override//当程序卸载后,刷新数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ("用户程序".equals(tv_app_title.getText().toString().trim())) {
            initUi(true);
            adapter.setAppInfos(getUserApp());
            adapter.notifyDataSetChanged();
        } else {
            initUi(false);
        }
    }

    private void initUi(final boolean isUserApp) {
        flag = false;
        ll_app_manager_progress.setVisibility(View.VISIBLE);

        //搜索载入程序列表可能耗时,新开子线程,交个Handle处理
        new Thread() {
            @Override
            public void run() {
                provider = new AppInfoProvider(AppManagerActivity.this);
                list = provider.getAllApps();

                Message message = new Message();
                if (isUserApp) {
                    message.what = GET_USER_APP_FINISH;
                } else {
                    message.what = GET_ALL_APP_FINISH;
                }
                handler.sendMessage(message);
            }

            ;
        }.start();
    }

    /**
     * 判断popupWindow是不是存在,存在就dismiss掉
     */
    private void dismissPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }


    @Override
    public void onClick(View v) {
        if (!flag) {
            return;
        }

        AppInfo item = (AppInfo) v.getTag();
        switch (v.getId()) {
            //点击标题栏,仅仅显示用户程序
            case R.id.tv_app_title:
                if ("所有程序".equals(tv_app_title.getText().toString().trim())) {
                    tv_app_title.setText("用户程序");
                    //将所有用户程序数据更新到adapter中
                    adapter.setAppInfos(getUserApp());
                    //通知ListView数据发生变化
                    adapter.notifyDataSetChanged();
                } else {
                    tv_app_title.setText("所有程序");
                    adapter.setAppInfos(list);
                    adapter.notifyDataSetChanged();
                }
                break;

            case R.id.ll_app_uninstall://点击卸载
                if (item.isSystemApp()) {
                    Toast.makeText(this, "系统程序不能被卸载", Toast.LENGTH_SHORT).show();
                } else {
                    //调用卸载程序的方法
                    String strUri = "package:" + item.getPackageName();
                    Uri uri = Uri.parse(strUri);
                    Intent deleteIntent = new Intent();
                    deleteIntent.setAction(Intent.ACTION_DELETE);
                    deleteIntent.setData(uri);
                    startActivityForResult(deleteIntent, 0);
                }
                break;
            case R.id.ll_app_start:
                try {
				/*
				 * 启动一个程序的原理:
				 * 1,得到一个PackageInfo,通过AndroidMainfest找到所有的Activity
				 * 2.通过Main节点判断入口Activity
				 * 3,intent启动
				 */
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(
                            item.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
                    //获取所有Activity
                    ActivityInfo[] activities = packageInfo.activities;
                    //有些应用无法启动,判断
                    if (activities != null && activities.length > 0) {
                        //第一个Activity节点,具有启动意义
                        ActivityInfo startActivity = activities[0];
                        Intent intent = new Intent();
                        intent.setClassName(item.getPackageName(), startActivity.name);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "该程序无法启动...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_app_share:
                //Toast.makeText(AppManagerActivity.this, "分享功能", Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent();
                //设置Intent的action
                shareIntent.setAction(Intent.ACTION_SEND);
                //设置Intent的类型为纯文本
                shareIntent.setType("text/plain");
                //设置分享主题
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                //设置分享的文本
                shareIntent.putExtra(Intent.EXTRA_TEXT, "有一个很好应用,你懂得..." + item.getAppName());
                startActivity(shareIntent);
                break;

            default:
                break;
        }
        dismissPopupWindow();
    }

    /**
     * 过滤出用户程序
     *
     * @return
     */
    private List<AppInfo> getUserApp() {
        List<AppInfo> userApps = new ArrayList<AppInfo>();
        for (AppInfo info : list) {
            if (!info.isSystemApp()) {
                userApps.add(info);
            }
        }
        return userApps;
    }

}
