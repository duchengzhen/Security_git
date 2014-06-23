package com.calvin.security.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.calvin.security.R;
import com.calvin.security.domain.AppInfo;

import java.util.List;

public class AppLockAdapter extends BaseAdapter {
    private List<AppInfo> infos;
    private Context context;
    private List<String> lockApps;

    public AppLockAdapter(List<AppInfo> infos, List<String> lockApps, Context context) {
        this.context = context;
        this.infos = infos;
        this.lockApps = lockApps;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfo info = infos.get(position);

        if (convertView == null) {
            View view = View.inflate(context, R.layout.app_lock_item, null);
            AppManagerViews views = new AppManagerViews();
            views.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_lock_icon);
            views.tv_app_name = (TextView) view.findViewById(R.id.tv_app_lock_name);
            views.iv_app_lock = (ImageView) view.findViewById(R.id.iv_app_lock);

            views.iv_app_icon.setImageDrawable(info.getIcon());
            views.tv_app_name.setText(info.getAppName());

            if (lockApps.contains(info.getPackageName())) {
                views.iv_app_lock.setImageResource(R.drawable.lock);
            } else {
                views.iv_app_lock.setImageResource(R.drawable.unlock);
            }
            view.setTag(views);
            return view;
        } else {
            AppManagerViews views = (AppManagerViews) convertView.getTag();
            views.iv_app_icon.setImageDrawable(info.getIcon());
            views.tv_app_name.setText(info.getAppName());

            if (lockApps.contains(info.getPackageName())) {
                views.iv_app_lock.setImageResource(R.drawable.lock);
            } else {
                views.iv_app_lock.setImageResource(R.drawable.unlock);
            }
            return convertView;
        }
    }

    //优化ListView显示
    private class AppManagerViews {
        ImageView iv_app_icon;
        TextView tv_app_name;
        ImageView iv_app_lock;
    }

}
