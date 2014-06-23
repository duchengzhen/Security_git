package com.calvin.security.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.calvin.security.R;

public class MainUiAdapter_Original extends BaseAdapter {
    private static final String[] NAMES = {"手机防盗", "通讯卫士", "软件管理", "流量管理", "任务管理",
            "缓存清理", "系统优化", "高级工具", "设置中心"};

    private static final int[] ICONS = {R.drawable.icon_anti_burglay, R.drawable.icon_communication_tools,
            R.drawable.icon_softmgr, R.drawable.icon_traffic, R.drawable.icon_notify_mgr,
            R.drawable.icon_virus_kill, R.drawable.icon_rocket, R.drawable.ic_hips_icon, R.drawable.ic_child_configs};
    //声明静态成员变量,有一定的优化作用
    private static ImageView imageView;
    private static TextView textView;

    private static LayoutInflater inflater;
    private static Context context;
    private static SharedPreferences sp;

    public MainUiAdapter_Original(Context context) {
        MainUiAdapter_Original.context = context;
        inflater = LayoutInflater.from(MainUiAdapter_Original.context);
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return NAMES.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.main_gridview_item, null);
        imageView = (ImageView) view.findViewById(R.id.iv_main_icon);
        textView = (TextView) view.findViewById(R.id.tv_main_name);
        imageView.setImageResource(ICONS[position]);
        textView.setText(NAMES[position]);

        if (position == 0) {
            String name = sp.getString("lostName", "");
            if (!name.equals("")) {
                textView.setText(name);
            }
        }
        return view;
    }

}
