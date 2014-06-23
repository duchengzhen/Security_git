package com.calvin.security.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.calvin.security.dao.AppLockDao;

/**
 * applock表的内容提供者
 * 简单起见，我们只处理了insert和delete这两个方法
 *
 * @author calvin
 */
public class AppLockProvider extends ContentProvider {
    //两个返回值
    private static final int INSERT = 1;
    private static final int DELETE = 0;

    //先new一个UriMatcher出来,参数就是当没有匹配到的时候,返回值是什么
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    //定义要匹配的Uri
    private static Uri matchUri = Uri.parse("content://com.calvin.security.applockprovider");
    private AppLockDao dao;

    static {
        matcher.addURI("com.calvin.security.applockprovider", "insert", INSERT);
        matcher.addURI("com.calvin.security.applockprovider", "delete", DELETE);
    }

    @Override
    public boolean onCreate() {
        dao = new AppLockDao(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri paramUri, String[] paramArrayOfString1,
                        String paramString1, String[] paramArrayOfString2,
                        String paramString2) {
        return null;
    }

    @Override
    public String getType(Uri paramUri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //上面定义的返回值
        int result = matcher.match(uri);
        if (result == INSERT) {
            String packageName = values.getAsString("packageName");
            dao.add(packageName);
            //如果数据变化了就通知改变
            getContext().getContentResolver().notifyChange(matchUri, null);
        } else {
            new IllegalArgumentException("URI地址不正确");
        }
        return null;
    }

    @Override
    public int delete(Uri paramUri, String paramString,
                      String[] paramArrayOfString) {
        int result = matcher.match(paramUri);
        if (result == DELETE) {
            String packageName = paramArrayOfString[0];
            dao.delete(packageName);
            //通知数据发生了改变
            getContext().getContentResolver().notifyChange(matchUri, null);
        } else {
            new IllegalArgumentException("URI地址不正确");
        }
        return 0;
    }

    @Override
    public int update(Uri paramUri, ContentValues paramContentValues,
                      String paramString, String[] paramArrayOfString) {
        return 0;
    }

}
