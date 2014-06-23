package com.calvin.security.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.calvin.security.R;
import com.calvin.security.domain.ContactInfo;

import java.util.List;

public class SelectContactAdapter extends BaseAdapter {
    private Context context;
    private List<ContactInfo> infos;

    public SelectContactAdapter(Context context, List<ContactInfo> infos) {
        this.context = context;
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
        ContactInfo info = infos.get(position);
        View view;
        ContactViews views;
        // 第一次
        if (convertView == null) {
            views = new ContactViews();
            view = View.inflate(context, R.layout.contact_item, null);
            views.tv_name = (TextView) view.findViewById(R.id.tv_contact_name);
            views.tv_number = (TextView) view
                    .findViewById(R.id.tv_contact_number);
            views.tv_name.setText("联系人:" + info.getName());
            views.tv_number.setText("电话号码:" + info.getPhone());

            view.setTag(views);
        } else {
            view = convertView;
        }

        return view;

    }

    private class ContactViews {
        TextView tv_name;
        TextView tv_number;
    }

}
