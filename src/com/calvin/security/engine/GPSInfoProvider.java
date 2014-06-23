package com.calvin.security.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 这个类,做成单例模式,因为手机里面只有一个gps所以免得每次都得新开一个对象
 *
 * @author calvin
 */
public class GPSInfoProvider {
    private static GPSInfoProvider gpsInfoProvider;
    private static Context context;
    private static MyLocationListener listener;
    private LocationManager locationManager;

    //构造器私有,想要新建该类,必须通过getInstance方法获得
    private GPSInfoProvider() {

    }

    /**
     * 让该方法一定执行,加入synchronized
     * @param context
     * @return
     */
    public static synchronized GPSInfoProvider getInstance(Context context) {
        if (gpsInfoProvider == null) {
            gpsInfoProvider = new GPSInfoProvider();
            GPSInfoProvider.context = context;
        }
        return gpsInfoProvider;
    }

    public String getLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider = getBestProvider();
        //locationManager.getBestProvider(criteria, enabledOnly);
        /*
		 * 该方法用于位置的更新
		 * 参数1,使用定位的设备,比如gps,基站
		 * 参数2,多长时间更新一次定位信息,太频繁耗电
		 * 参数3,位移多少米后,重新获取一次定位
		 * 参数4,监听器,位置变化后的回调
		 */
        //locationManager.getAllProviders();获取手机支持的所有定位方式(gps,基站)
        locationManager.requestLocationUpdates(provider, 60000, 50, getListener());
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		/*Editor edit = sp.edit();
		edit.putString("location", "");*/
        //TODO loatLocation??还是lastLocation??
        String location = sp.getString("lostLocation", "");
        return location;

    }

    //做成单例模式
    private LocationListener getListener() {
        if (listener == null) {
            listener = new MyLocationListener();
        }
        return listener;
    }

    private String getBestProvider() {
        Criteria criteria = new Criteria();
        //设置定位精度
        //Criteria.ACCURACY_FINE 精确定位
        //Criteria.ACCURACY_COARSE 一般定位
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //是否对海拔敏感
        criteria.setAltitudeRequired(false);
        //手机耗电量设置
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        //是否对速度变化敏感
        criteria.setSpeedRequired(true);
        //是否使用网络辅助定位
        criteria.setCostAllowed(true);
        //第二个参数,只会获得已经打开的定位设备
        return locationManager.getBestProvider(criteria, true);
    }

    //===========================================================================
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // 手机位置改变时,执行该方法
            String latitude = "维度" + location.getLatitude();
            String longitude = "经度" + location.getLongitude();
            SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            Editor edit = sp.edit();
            edit.putString("lastLocation", longitude + "-" + latitude);
            edit.commit();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //定位设备发生改变时调用
        }

        @Override
        public void onProviderEnabled(String provider) {
            //定位设备打开时否调用

        }

        @Override
        public void onProviderDisabled(String provider) {
            // 定位设备关闭时否调用

        }


    }


}
