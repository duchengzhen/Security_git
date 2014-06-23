package com.calvin.security.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.calvin.security.utils.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberDao {
    private DbHelper dbHelper;

    public BlackNumberDao(Context context) {
        dbHelper = new DbHelper(context);
    }

    public boolean find(String number) {// 查找
        boolean result = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean isOpen = db.isOpen();
        if (isOpen) {
            Cursor cursor = db.rawQuery(
                    "select number from blacknumber where number = ?",
                    new String[]{number});
            if (cursor.moveToNext()) {
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    public void add(String number) {// 新增
        boolean isFind = find(number);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean isOpen = db.isOpen();
        if (!isFind && isOpen) {
            db.execSQL("insert into blacknumber (number) values (?)",
                    new String[]{number});
            db.close();
        }
    }

    public void delete(String number) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from blacknumber where number=?",
                    new String[]{number});
            db.close();
        }
    }

    public void update(String oldNumber, String newNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update blacknumber set number =? where number=?",
                    new String[]{newNumber, oldNumber});
            db.close();
        }
    }

    public List<String> findAll() {
        List<String> numbers = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select number from blacknumber", null);
            while (cursor.moveToNext()) {
                numbers.add(cursor.getString(0));
            }
            cursor.close();
            db.close();
        }
        return numbers;
    }

}
