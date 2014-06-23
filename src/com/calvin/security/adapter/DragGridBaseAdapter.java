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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by calvin on 2014/6/16.
 */
public class DragGridBaseAdapter extends BaseAdapter{
    private static final String[] NAMES = {"手机防盗", "通讯卫士", "软件管理", "流量管理", "任务管理",
            "缓存清理", "系统优化", "高级工具", "设置中心"};

    private static final int[] ICONS = {R.drawable.icon_anti_burglay, R.drawable.icon_communication_tools,
            R.drawable.icon_softmgr, R.drawable.icon_traffic, R.drawable.icon_notify_mgr,
            R.drawable.icon_virus_kill, R.drawable.icon_rocket, R.drawable.ic_hips_icon, R.drawable.ic_child_configs};

    private Context context;
    private LayoutInflater inflater;
    private SharedPreferences sp;
    private List<HashMap<String,Object>> list;
    private int mHidePosition = -1;

    public DragGridBaseAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        list =new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<NAMES.length;i++){
            HashMap<String,Object> map=new HashMap<String, Object>();
            map.put(NAMES[i],ICONS[i]);
            list.add(map);
            map=null;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
		 * convertView相当于缓存一样,只要我们判断一下它是不是null,就可以知道这个view有没有
		 * 绘制过出来,如果没有,那么我们重新绘制,如果有,直接使用缓存,节约view绘制时间
		 */
        HolderViews views;
        View view;
        if (convertView == null) {
            views = new HolderViews();
            view = inflater.inflate(R.layout.main_gridview_item, null);
            views.imageView = (ImageView) view.findViewById(R.id.iv_main_icon);
            views.textView = (TextView) view.findViewById(R.id.tv_main_name);
            views.imageView.setImageResource(ICONS[position]);
            views.textView.setText(NAMES[position]);

            view.setTag(views);
        } else {
            view = convertView;
            views = (HolderViews) view.getTag();
            views.imageView = (ImageView) view.findViewById(R.id.iv_main_icon);
            views.textView = (TextView) view.findViewById(R.id.tv_main_name);
            views.imageView.setImageResource(ICONS[position]);
            views.textView.setText(NAMES[position]);
        }


        if (position == 0) {
            String name = sp.getString("lostName", "");
            if (!name.equals("")) {
                views.textView.setText(name);
            }
        }
        return view;
    }

    /**
     * 重排数据
     * @param oldPosition
     * @param newPosition
     */
    public void reorderItems(int oldPosition, int newPosition) {
        HashMap<String, Object> temp = list.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }

        list.set(newPosition, temp);
    }

    /**
     * 隐藏item
     * @param hidePosition
     */
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }

    /**
     * 一个存放所要绘制的控件的类
     * @author calvin
     */
    private class HolderViews {
        ImageView imageView;
        TextView textView;
    }
}
