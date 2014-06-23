package com.calvin.security.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.calvin.security.R;
import com.calvin.security.domain.TaskInfo;
import com.calvin.security.utils.TextFormater;

import java.util.ArrayList;
import java.util.List;

public class TaskInfoAdapter extends BaseAdapter {
    private Context context;
    private SharedPreferences sp;
    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTaskInfo;
    private List<TaskInfo> systemTaskInfo;

    // 在构造器中将系统\用户进程区分开
    public TaskInfoAdapter(List<TaskInfo> taskInfos, Context context) {
        this.taskInfos = taskInfos;
        this.context = context;
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        userTaskInfo = new ArrayList<TaskInfo>();
        systemTaskInfo = new ArrayList<TaskInfo>();
        for (TaskInfo taskInfo : taskInfos) {
            if (taskInfo.isSystemProcess()) {
                systemTaskInfo.add(taskInfo);
            } else {
                userTaskInfo.add(taskInfo);
            }
        }
    }

    @Override
    public int getCount() {
        boolean showSystemProcess = sp.getBoolean("showSystemProcess", false);
        // 加上连个标签,一个系统标签,一个用户标签
        if (showSystemProcess) {
            return taskInfos.size() + 2;
        } else {
            return userTaskInfo.size() + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return 0;// 显示用户应用标签
        } else if (position <= userTaskInfo.size()) {
            return userTaskInfo.get(position - 1);
        } else if (position == userTaskInfo.size() + 1) {
            return position;// 显示成系统进程标签
        } else if (position <= taskInfos.size()) {
            // 显示应用进程的条目
            return systemTaskInfo.get(position - userTaskInfo.size() - 2);
        } else {
            return position;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TaskInfo taskInfo;
        TaskInfoViews views;
        if (position == 0) {
            // 显示用户应用的标签
            return newTextView("用户程序(" + userTaskInfo.size() + ")");
        } else if (position <= userTaskInfo.size()) {
            taskInfo = userTaskInfo.get(position - 1);
        } else if (position == userTaskInfo.size() + 1) {
            // 显示成系统应用标签
            return newTextView("系统程序(" + systemTaskInfo.size() + ")");
        } else if (position <= taskInfos.size() + 2) {
            // 系统应用的条目
            taskInfo = systemTaskInfo.get(position - userTaskInfo.size() - 2);
        } else {
            taskInfo = new TaskInfo();
        }

        if (convertView == null || convertView instanceof TextView) {
            view = View.inflate(context, R.layout.process_manager_item, null);
            views = new TaskInfoViews();

            views.iv_process_icon = (ImageView) view
                    .findViewById(R.id.iv_process_manager_icon);
            views.tv_process_name = (TextView) view
                    .findViewById(R.id.tv_process_manager_name);
            views.tv_process_memory = (TextView) view
                    .findViewById(R.id.tv_process_manager_memory);
            views.cb_process_state = (CheckBox) view
                    .findViewById(R.id.cb_process_manager_state);

            view.setTag(views);
        } else {
            view = convertView;
            views = (TaskInfoViews) view.getTag();
        }
        views.iv_process_icon.setImageDrawable(taskInfo.getIcon());
        views.tv_process_name.setText(taskInfo.getName());
        views.tv_process_memory.setText("占用内存:"
                + TextFormater.dataSizeFormat(taskInfo.getMemory() * 1024));
        if ("com.calvin.security".equals(taskInfo.getPackageName())) {
            views.cb_process_state.setVisibility(View.INVISIBLE);
        } else {
            views.cb_process_state.setVisibility(View.VISIBLE);
        }
        views.cb_process_state.setChecked(taskInfo.isCheck());

        return view;
    }

    private TextView newTextView(String title) {
        TextView tv_title = new TextView(context);
        tv_title.setText(title);
        return tv_title;
    }

    public List<TaskInfo> getUserTaskInfo() {
        return userTaskInfo;
    }

    public List<TaskInfo> getSystemTaskInfo() {
        return systemTaskInfo;
    }

    private class TaskInfoViews {
        ImageView iv_process_icon;
        TextView tv_process_name;
        TextView tv_process_memory;
        CheckBox cb_process_state;
    }

}
