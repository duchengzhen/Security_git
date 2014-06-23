package com.calvin.security.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.calvin.security.R;
import com.calvin.security.dao.CommonNumberDao;

import java.util.List;

public class MyBaseExpandableListViewAdapter extends BaseExpandableListAdapter {
    private List<String> group;
    private List<List<String>> childs;
    private Context context;

    /**
     * @param context 上下文
     * @param path    数据库存放的路径
     */
    public MyBaseExpandableListViewAdapter(Context context, String path) {
        this.context = context;
        group = CommonNumberDao.getAllGroup(path);
        childs = CommonNumberDao.getAllChildren(path, group.size());
    }

    @Override//有多少个分组
    public int getGroupCount() {
        return group.size();
    }

    @Override //对应分组的条目个数
    public int getChildrenCount(int groupPosition) {
        return childs.get(groupPosition).size();
    }

    @Override //第几个分组对应的对象
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    @Override //拿到第几个分组对应的第几个条目
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groupPosition).get(childPosition);
    }

    @Override //第几个分组对应的位置
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override //第几个分组对应第几个条目的位置
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    /*
	 * 官方文档是这样说的：组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。  
     * 返回一个Boolean类型的值，如果为TRUE，意味着相同的ID永远引用相同的对象。  
     * 具体有什么用，我就弄不懂了  
	 * @参看 android.widget.ExpandableListAdapter#hasStableIds()
	 */
    public boolean hasStableIds() {
        return false;
    }

    @Override //返回对应的分组view
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.expandable_group, null);
            TextView tv_group = (TextView) view.findViewById(R.id.expandable_group);

            holder = new ViewHolder();
            holder.tv_group = tv_group;

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_group.setText(group.get(groupPosition));
        return view;
    }

    @Override//返回对应分组的对应条目view
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.expandable_children, null);
            TextView tv_name = (TextView) view.findViewById(R.id.expandable_child_name);
            TextView tv_number = (TextView) view.findViewById(R.id.expandable_child_num);

            holder = new ViewHolder();
            holder.tv_name = tv_name;
            holder.tv_number = tv_number;

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        String str = childs.get(groupPosition).get(childPosition);
        //分割出来
        String[] msg = str.split("-");
        holder.tv_name.setText(msg[0]);
        holder.tv_number.setText(msg[1]);
        return view;
    }

    @Override //是否允许分组的条目接受点击事件 false不允许
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public List<String> getGroup() {
        return group;
    }

    public List<List<String>> getChilds() {
        return childs;
    }

    private class ViewHolder {
        TextView tv_group;
        TextView tv_name;
        TextView tv_number;
    }

}
