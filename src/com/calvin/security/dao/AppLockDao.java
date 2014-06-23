package com.calvin.security.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.calvin.security.utils.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class AppLockDao {
    private DbHelper dbHelper;

    public AppLockDao(Context context) {
        dbHelper = new DbHelper(context);
    }

    public boolean find(String packageName) {
        boolean result = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(
                    "select packagename from applock where packagename=?",
                    new String[]{packageName});
            if (cursor.moveToNext()) {
                result = true;
            }
            db.close();
        }
        return result;
    }

    public void add(String packageName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into applock (packagename) values (?)",
                    new String[]{packageName});
            db.close();
        }
    }

    public void delete(String packageName) {
        if (!find(packageName))
            return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from applock where packagename=? ",
                    new String[]{packageName});
            db.close();
        }
    }

    public List<String> getAllPackageName() {
        List<String> list = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select packagename from applock", null);
            list = new ArrayList<String>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                list.add(name);
                name = null;
            }
            cursor.close();
            db.close();
        }
        return list;
    }

}
