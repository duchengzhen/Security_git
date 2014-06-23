package com.calvin.security.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.calvin.security.R;

import java.util.List;

public class NumberAdapter extends BaseAdapter {
    private List<String> numbers;
    private Context context;


    public NumberAdapter(Context context, List<String> numbers) {
        this.context = context;
        this.numbers = numbers;
    }

    @Override
    public int getCount() {
        return numbers.size();
    }

    @Override
    public Object getItem(int position) {
        return numbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            View view = View.inflate(context, R.layout.number_security_item, null);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_number_item);
            tv_item.setText(numbers.get(position));
            return view;
        } else {
            TextView tv_item = (TextView) convertView.findViewById(R.id.tv_number_item);
            tv_item.setText(numbers.get(position));
            return convertView;
        }
    }

}
