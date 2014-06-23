package com.calvin.security.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import com.calvin.security.dao.AddressDao;

public class NumberAddressService {

    public static String getAddress(String number) {
        String pattern = "^1[3|4|5|8][0-9]\\d{4,8}$";
        String address = number;

        if (number.matches(pattern)) {//电话号码
            address = query("select city from info where mobileprefix=? limit 1;",
                    new String[]{number.substring(0, 7)});
            if (address.equals("")) {
                address = number;//避免查不到数据时,显示为空
            }
        } else {//固定电话
            int len = address.length();
            switch (len) {
                case 4://模拟器
                    address = "内部号码";
                    break;
                case 7:
                    address = "本地号码";
                    break;
                case 8:
                    address = "本地号码";
                    break;
                case 10://3为区号,7位号码
                    address = query("select city from info where area=? limit 1",
                            new String[]{number.substring(0, 3)});
                    if (address.equals("")) {
                        address = number;
                    }
                    break;
                case 11://3为区号,8位号码,或4为区号,7号码
                    address = query("select city from info where area=? limit 1",
                            new String[]{number.substring(0, 3)});
                    if (address.equals("")) {
                        address = query("select city from info where area=? limit 1",
                                new String[]{number.substring(0, 4)});
                        if (address.equals(""))
                            address = number;
                    }
                    break;
                case 12://4为区号,8位号码
                    address = query("select city from info where area=? limit 1",
                            new String[]{number.substring(0, 4)});
                    if (address.equals("")) {
                        address = number;
                    }
                    break;

                default:
                    break;
            }
        }
        return address;
    }

    public static String query(String sql, String[] selectionArgs) {
        String result = "";

        String path = Environment.getExternalStorageDirectory() + "/security/db/data.db";
        SQLiteDatabase db = AddressDao.getAddressDb(path);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(sql, selectionArgs);
            if (cursor.moveToNext()) {
                result = cursor.getString(0);
            }
            cursor.close();
            db.close();
        }
        return result;
    }
}
