package com.calvin.security.dao;

import android.database.sqlite.SQLiteDatabase;

public class AddressDao {

    public static SQLiteDatabase getAddressDb(String path) {
        return SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READONLY);
    }

}
