package com.calvin.security.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.calvin.security.R;
import com.calvin.security.adapter.MainGridViewAdapter;
import com.calvin.security.ui.*;

/**
 * Created by calvin on 2014/6/19.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainGridViewFragmentNew extends Fragment implements AdapterView.OnItemClickListener {
    private Context context;
    private GridView gridView;
    private MainGridViewAdapter adapter;
    private SharedPreferences sp;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        adapter = new MainGridViewAdapter(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        View rootView = inflater.inflate(R.layout.main_gradview_fargment, container,false);
        gridView = (GridView) rootView.findViewById(R.id.gv_main);
        initListener();
        gridView.setAdapter(adapter);
        return rootView;
    }

    /** 初始化监听*/
    private void initListener() {
        gridView.setOnItemClickListener(this);
        //gridview长按事件
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                           int position, long id) {
                if (position == 0) {//如果手机被盗,给这个item设置一个重命名功能
                    //新建一个AlertDialog的标准写法
                    newAlertDialog(view);
                }
                return false;
            }
        });
    }

    /**
     * 新建一个AlertDialog的标准写法
     * @param view
     */
    private void newAlertDialog(final View view) {
        //新建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("重命名");
        //builder.setMessage("请输入要更改的名字");
        //在dialog中加入EditText
        final EditText et = new EditText(context);
        et.setHint("请输入新名称");
        builder.setView(et);
        //确定按键点击事件
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = et.getText().toString().trim();
                if (name.equals("")) {
                    Toast.makeText(context, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                    //TODO 弹出Toast后不让对话框自动消失
                } else {
                    //新名字存储在ShardPreferences中
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("lostName", name);
                    editor.commit();
                    //将新名字市值在TextView中,并通知adapter做更新显示
                    TextView tv = (TextView) view.findViewById(R.id.tv_main_name);
                    tv.setText(name);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override //条目时间点击监听器
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        switch (position) {
            case 0://手机防盗
                //finish();
                Intent intent = new Intent(context, LostProtectedActivity.class);
                startActivity(intent);
                break;
            case 1://通讯卫士
                //finish(); 指已经被弹出栈
                Intent i = new Intent(context, NumberSecurityActivity.class);
                startActivity(i);
                break;
            case 2://软件管理
                Intent apps = new Intent(context, AppManagerActivity.class);
                startMyActivity(apps);

                break;
            case 3://流量管理
                Intent traffic = new Intent(context, TrafficManagerActivity.class);
                startActivity(traffic);

                break;
            case 4://任务管理
                Intent process = new Intent(context, ProcessManagerActivity.class);
                startMyActivity(process);
                break;
            case 5://缓存清理
                Intent cacheClean = new Intent(context, CacheClearActivity.class);
                startActivity(cacheClean);
                break;
            case 6://系统优化
                Intent processIntent = new Intent(context, ProcessManagerActivity.class);
                startMyActivity(processIntent);
                break;
            case 7://高级工具
                Intent intent7 = new Intent(context, AToolActivity.class);
                startActivity(intent7);
                break;
            case 8://设置中心
                Intent settingIntent = new Intent(context, SettingActivity.class);
                startActivity(settingIntent);
                break;
            default:
                break;
        }
    }

    public void startMyActivity(Intent intent) {
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.translate_up_in, R.anim.translate_up_out);
    }
}
