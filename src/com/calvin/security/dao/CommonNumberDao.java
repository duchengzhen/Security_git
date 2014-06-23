package com.calvin.security.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CommonNumberDao {
    //获取所有分组信息
    public static List<String> getAllGroup(String path) {
        List<String> group = new ArrayList<String>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select name from classlist", null);
            while (cursor.moveToNext()) {
                group.add(cursor.getString(0));
            }
            cursor.close();
        }
        db.close();
        return group;
    }

    //拿到电话信息
    public static List<List<String>> getAllChildren(String path, int groupCount) {
        List<List<String>> allChildren = new ArrayList<List<String>>();
        StringBuffer sb = new StringBuffer();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (db.isOpen()) {
            for (int i = 1; i <= groupCount; i++) {
                List<String> child = new ArrayList<String>();
                /*
				 *数据库中每个group对应的一张表,
				 *所以我们就可以拼装sql语言 
				 */
                String sql = "select name, number from table" + i;
                Cursor cursor = db.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    //把信息拼装成name-number
                    sb.append(cursor.getString(0));
                    sb.append("-");
                    sb.append(cursor.getString(1));
                    child.add(sb.toString());
                    //清空Stringbuffer中内容
                    sb.setLength(0);
                }
                cursor.close();
                allChildren.add(child);
            }
            db.close();
        }
        return allChildren;
    }
}
