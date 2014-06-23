package com.calvin.security.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {
    private String name;
    private Drawable icon;
    private int id;
    //以kb为单位
    private int memory;
    private boolean isCheck;
    private String packageName;
    //是否是系统进程
    private boolean isSystemProcess;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSystemProcess() {
        return isSystemProcess;
    }

    public void setSystemProcess(boolean isSystemProcess) {
        this.isSystemProcess = isSystemProcess;
    }

}
