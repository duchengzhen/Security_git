package com.calvin.security.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, "security.db", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement, number varchar(20))");
        db.execSQL("create table applock (_id integer primary key autoincrement,packagename varchar(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库版本发生不同的时候调用
        try {
            db.execSQL("create table applock (_id integer primary key autoincrement,packagename varchar(50))");
        } catch (SQLException e) {
            //throw new RuntimeException("重复新建表,取消之",e);
            System.err.println("重复建表,取消之!");
        }
        db.execSQL("insert into applock (packagename) values (?)", new String[]{"JustNoNull"});
    }

}
