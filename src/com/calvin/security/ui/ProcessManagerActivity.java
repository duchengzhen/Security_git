package com.calvin.security.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.calvin.security.MyApplication;
import com.calvin.security.R;
import com.calvin.security.adapter.TaskInfoAdapter;
import com.calvin.security.domain.TaskInfo;
import com.calvin.security.engine.TaskInfoProvider;
import com.calvin.security.view.MyToast;
import com.calvin.security.utils.TextFormater;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import java.util.List;

@SuppressLint("HandlerLeak")
public class ProcessManagerActivity extends RoboActivity implements
        OnClickListener {

    private static final int LOAD_SUCCESS = 1;
    private static final int LOAD_FAIL = -1;
    @InjectView(R.id.tv_process_count)
    private TextView tv_process_count;
    @InjectView(R.id.tv_process_memory)
    private TextView tv_process_memory;
    @InjectView(R.id.lv_process_manager)
    private ListView lv_process_manager;
    @InjectView(R.id.ll_process_manager_progress)
    private LinearLayout ll_process_manager;
    @InjectView(R.id.bt_process_clean)
    private Button bt_process_clean;
    @InjectView(R.id.bt_process_setting)
    private Button bt_process_setting;
    @InjectView(R.id.ll_process_button)
    private LinearLayout ll_process_button;
    private CheckBox cb_process_state;
    private ActivityManager activityManager;
    private List<RunningAppProcessInfo> runningAppProcessInfos;
    private TaskInfoProvider taskInfoProvider;
    private List<TaskInfo> taskInfos;
    private TaskInfoAdapter adapter;
    private String totalMemory;
    private String availMemory;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    ll_process_manager.setVisibility(View.GONE);
                    ll_process_button.setVisibility(View.VISIBLE);

                    adapter = new TaskInfoAdapter(taskInfos,
                            ProcessManagerActivity.this);
                    lv_process_manager.setAdapter(adapter);
                    tv_process_memory.setText(availMemory + "/" + totalMemory);
                    break;
                case LOAD_FAIL:
                    Toast.makeText(ProcessManagerActivity.this, "载入失败,请重试...",
                            Toast.LENGTH_SHORT).show();
                    ProcessManagerActivity.this.finish();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.process_manager);

        bt_process_clean.setOnClickListener(this);
        bt_process_setting.setOnClickListener(this);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		/*
         * 请求一个自定义title,但有一些Android系统是被修改过的, 所以有可能无法请求的,所以需要判断一下 boolean
		 * flags=requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		 */
		/*
		 * if (flags) { //设置自定义标题
		 * getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		 * R.layout.process_manager_title); }
		 */
        initData();
        // 给ListView增加条目点击事件
        lv_process_manager.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> paramAdapterView, View view,
                                    int position, long id) {
                // adapter中getItem的返回值
                Object obj = lv_process_manager.getItemAtPosition(position);
                if (obj instanceof TaskInfo) {
                    cb_process_state = (CheckBox) view
                            .findViewById(R.id.cb_process_manager_state);
                    TaskInfo taskInfo = (TaskInfo) obj;
                    // 设置不能杀死自己的进程,还有一些系统进程
                    if ("com.calvin.security".equals(taskInfo.getPackageName())) {
                        cb_process_state.setVisibility(View.INVISIBLE);
                        return;
                    }

                    if (taskInfo.isCheck()) {
                        taskInfo.setCheck(false);
                        cb_process_state.setChecked(false);
                    } else {
                        taskInfo.setCheck(true);
                        cb_process_state.setChecked(true);
                    }
                }

            }
        });

        // 条目增加长点击事件
        lv_process_manager
                .setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {
                        // adapter里面的getItem的返回值
                        Object obj = lv_process_manager
                                .getItemAtPosition(position);
                        if (obj instanceof TaskInfo) {
                            TaskInfo taskInfo = (TaskInfo) obj;
                            // 拿到我们自定义的application对象
                            MyApplication myApplication = (MyApplication) getApplication();
                            // 把TaskInfo对象设置进去
                            myApplication.setTaskInfo(taskInfo);

                            Intent intent = new Intent(
                                    ProcessManagerActivity.this,
                                    AppDetialActivity.class);
                            startActivity(intent);
                        }
                        return false;
                    }

                });

    }

    @Override//该Activity启动的Activity返回数据后,执行的方法
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //刷新数据
        if (resultCode == 200) {
            initData();
        }
    }

    private void initData() {
        availMemory = getAvailMemory();
        tv_process_count.setText("进程数目:" + getRunningAppCount());
        // 该方法,将会在handler被覆盖
        tv_process_memory.setText("剩余内存:" + availMemory);

        // 将ProcessBar设置为可见
        ll_process_manager.setVisibility(View.VISIBLE);
        ll_process_button.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                taskInfoProvider = new TaskInfoProvider(
                        ProcessManagerActivity.this);
                try {
                    taskInfos = taskInfoProvider
                            .getAllTask(runningAppProcessInfos);
                    // 计算总内存大小,因为不可以直接获取,所以只能自己计算
                    long total = 0;
                    for (TaskInfo taskInfo : taskInfos) {
                        total += taskInfo.getMemory();
                    }
                    // new一个内存对象
                    MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                    // 拿到现在系统里面的内存信息
                    activityManager.getMemoryInfo(memoryInfo);
                    // 拿到有效的内存空间
                    long size = memoryInfo.availMem;
                    // 因为我们拿到的进程占用的内存是KB为单位,所以要乘以1024,左移10位
                    total = total << 10;
                    // 加上可用内存,得到总内存
                    total += size;
                    totalMemory = TextFormater.dataSizeFormat(total);

                    Message message = new Message();
                    message.what = LOAD_SUCCESS;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    // 载入失败
                    Message message = new Message();
                    message.what = LOAD_FAIL;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    /**
     * 获取剩余内存
     *
     * @return
     */
    private String getAvailMemory() {
        // new 一个内存的对象
        MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        // 获取系统中内存信息
        activityManager.getMemoryInfo(memoryInfo);
        // 获取有效的内存空间
        long availMem = memoryInfo.availMem;
        return TextFormater.dataSizeFormat(availMem);
    }

    /**
     * 获得当前运行的条目
     *
     * @return
     */
    private int getRunningAppCount() {
        runningAppProcessInfos = activityManager.getRunningAppProcesses();
        return runningAppProcessInfos.size();
    }

    /**
     * 一键清理的方法
     */
    private void killTask() {
        int total = 0;
        int memorySize = 0;
        for (TaskInfo taskInfo : adapter.getSystemTaskInfo()) {
            if (taskInfo.isCheck()) {
                // 杀死进程
                activityManager.killBackgroundProcesses(taskInfo
                        .getPackageName());
                total++;
                memorySize += taskInfo.getMemory();
                taskInfos.remove(taskInfo);
            }
        }

        for (TaskInfo taskInfo : adapter.getUserTaskInfo()) {
            if (taskInfo.isCheck()) {
                // 杀死进程
                activityManager.killBackgroundProcesses(taskInfo
                        .getPackageName());
                total++;
                memorySize += taskInfo.getMemory();
                taskInfos.remove(taskInfo);
            }
        }

        if (total > 0) {
            // 使用自定义的Toast显示
            MyToast.showToast(this, R.drawable.notification, "已将清理了" + total
                    + "个进程!释放了" + TextFormater.getSizeFromKB(memorySize) + "空间");
			/*
			 * Toast.makeText(this,
			 * "已将清理了"+total+"个进程!释放了"+TextFormater.dataSizeFormat
			 * (memorySize)+"空间", Toast.LENGTH_LONG).show();
			 */
            // 重新加载界面
            adapter = new TaskInfoAdapter(taskInfos, this);
            lv_process_manager.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_process_clean:
                // 执行清理操作
                killTask();
                break;
            case R.id.bt_process_setting:
                Intent setting = new Intent(this, ProcessSettingActivity.class);
                startActivityForResult(setting, 0);
                break;

            default:
                break;
        }

    }

}
