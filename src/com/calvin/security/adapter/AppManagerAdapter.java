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

public class AppManagerAdapter extends BaseAdapter {
    private List<AppInfo> infos;
    private Context context;

    public AppManagerAdapter(List<AppInfo> infos, Context context) {
        this.infos = infos;
        this.context = context;
    }

    public void setAppInfos(List<AppInfo> infos) {
        this.infos = infos;
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
            View view = View.inflate(context, R.layout.app_manager_item, null);
            AppManagerViews views = new AppManagerViews();
            views.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_manager_icon);
            views.tv_app_name = (TextView) view.findViewById(R.id.tv_app_manager_name);
            views.iv_app_icon.setImageDrawable(info.getIcon());
            views.tv_app_name.setText(info.getAppName());
            view.setTag(views);
            return view;
        } else {
            AppManagerViews views = (AppManagerViews) convertView.getTag();
            views.iv_app_icon.setImageDrawable(info.getIcon());
            views.tv_app_name.setText(info.getAppName());
            return convertView;
        }
    }

    /**
     * 优化ListView类???
     *
     * @author calvin
     */
    private class AppManagerViews {
        ImageView iv_app_icon;
        TextView tv_app_name;
    }

}
