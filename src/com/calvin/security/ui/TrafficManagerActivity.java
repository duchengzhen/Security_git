package com.calvin.security.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.calvin.security.R;
import com.calvin.security.domain.TrafficInfo;
import com.calvin.security.utils.TextFormater;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrafficManagerActivity extends RoboActivity {
    @InjectView(R.id.tv_traffic_2g_3g)
    private TextView tv_traffic_2g_3g;
    @InjectView(R.id.tv_traffic_wifi)
    private TextView tv_traffic_wifi;
    @InjectView(R.id.lv_traffic_content)
    private ListView lv_traffic_content;

    private List<TrafficInfo> trafficInfos;
    private Timer timer;
    private TimerTask timerTask;
    private TrafficAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //通知adapter,更新显示
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.traffic_manager);

        setTotalTraffic();
        trafficInfos = new ArrayList<TrafficInfo>();
        initResolveInfos();

        adapter = new TrafficAdapter();
        lv_traffic_content.setAdapter(adapter);
    }


    /**
     * 获取总流量
     */
    private void setTotalTraffic() {
        //获取2g和3g的总共接收到的数据大小
        long total_2g_3g_received = TrafficStats.getMobileRxBytes();
        //拿到2g和3g的总共发送出的数据大小
        long total_2g_3g_transmitted = TrafficStats.getMobileTxBytes();
        //相加得到总数据
        long total_2g_3g = total_2g_3g_received + total_2g_3g_transmitted;

        tv_traffic_2g_3g.setText("2G/3G 总流量:" + TextFormater.dataSizeFormat(total_2g_3g));

        //拿到总共接收到的流量
        long total_received = TrafficStats.getTotalRxBytes();
        //拿到总共发送的流量
        long total_transmitted = TrafficStats.getTotalTxBytes();
        //拿到总数据大小
        long total = total_received + total_transmitted;
        //计算wifi的总数据
        long total_wifi = total - total_2g_3g;

        tv_traffic_wifi.setText("WiFi总流量:" + TextFormater.dataSizeFormat(total_wifi));
    }

    @Override//做流量显示的定时刷新
    protected void onStart() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask, 1000, 3000);
        super.onStart();
    }

    @Override//停止流量刷新
    protected void onStop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
        super.onStop();
    }

    /**
     * 获取所有会产生流量的应用信息
     */
    private void initResolveInfos() {
        trafficInfos.clear();
        //拿到一个包管理器
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent();
        //android.intent.action.MAIN代表程序的入口
        intent.setAction("android.intent.action.MAIN");
        //android.intent.category.LAUNCHER代表桌面创建一个图标
        intent.addCategory("android.intent.category.LAUNCHER");
        //查询出有主界面和程序入口的程序
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            //得到应用的名字
            String name = resolveInfo.loadLabel(packageManager).toString();
            //得到程序图标
            Drawable icon = resolveInfo.loadIcon(packageManager);
            //得到应用的包名
            String packageName = resolveInfo.activityInfo.packageName;
            int uid = 0;
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                //得到应用对应的uid
                uid = packageInfo.applicationInfo.uid;
                //根据uid得到该应用接受数据大小
                long received = TrafficStats.getUidRxBytes(uid);
                //根据uid得到该应用发送数据大小
                long transmitted = TrafficStats.getUidTxBytes(uid);
                //不产生流量的应用,得到的值是-1,就不把该类程序加入到list里面
                //TODO 这段代码在模拟器上有问题
                /*if(received==-1&&transmitted==-1){
					continue;
				}*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            TrafficInfo trafficInfo = new TrafficInfo();
            trafficInfo.setName(name);
            trafficInfo.setIcon(icon);
            trafficInfo.setUid(uid);
            trafficInfos.add(trafficInfo);
        }
    }

    private class TrafficAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return trafficInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return trafficInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            TrafficInfo info = trafficInfos.get(position);
            if (convertView == null) {
                view = View.inflate(TrafficManagerActivity.this, R.layout.traffic_manager_item, null);
                holder = new ViewHolder();
                holder.iv_traffic_icon = (ImageView) view.findViewById(R.id.iv_traffic_icon);
                holder.tv_traffic_name = (TextView) view.findViewById(R.id.tv_traffic_name);
                holder.tv_traffic_received = (TextView) view.findViewById(R.id.tv_traffic_received);
                holder.tv_traffic_transmitted = (TextView) view.findViewById(R.id.tv_traffic_transmitted);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.iv_traffic_icon.setImageDrawable(info.getIcon());
            holder.tv_traffic_name.setText(info.getName());
            //根据uid得到应用接受数据大小
            long received = TrafficStats.getUidRxBytes(info.getUid());
            //根据uid得到应用发送数据大小
            long transmitted = TrafficStats.getUidTxBytes(info.getUid());
            holder.tv_traffic_received.setText(TextFormater.dataSizeFormat(received));
            holder.tv_traffic_transmitted.setText(TextFormater.dataSizeFormat(transmitted));
            return view;
        }

    }

    private class ViewHolder {
        ImageView iv_traffic_icon;
        TextView tv_traffic_name;
        TextView tv_traffic_received;
        TextView tv_traffic_transmitted;
    }

}
