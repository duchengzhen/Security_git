package com.calvin.security.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.calvin.security.domain.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class ContactInfoService {
    private Context context;

    public ContactInfoService(Context context) {
        this.context = context;
    }

    public List<ContactInfo> getContactInfos() {
        List<ContactInfo> infos = new ArrayList<ContactInfo>();
        ContactInfo info;
        // 获取内容提供者的解析实例,解析内容提供者内容
        ContentResolver resolver = context.getContentResolver();
        // 访问联系人Uri
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resolver.query(uri, new String[]{"contact_id"},
                null, null, null);
        while (cursor.moveToNext()) {
            info = new ContactInfo();
            String id = cursor.getString(cursor.getColumnIndex("contact_id"));
            // String name =
            // cursor.getString(cursor.getColumnIndex("display_name"));
            // info.setName(name);

            // 根据在raw_contacts里拿到的id,到data表中找对应数据
            Cursor dataCursor = resolver.query(dataUri, new String[]{
                            "mimetype", "data1"}, "raw_contact_id=?",
                    new String[]{id}, null);
            while (dataCursor.moveToNext()) {
                String type = dataCursor.getString(cursor
                        .getColumnIndex("mimetype"));
                // 根据类型,只要这种类型的数据
                if (type.equals("vnd.android.cursor.item/phone_v2")) {
                    String number = dataCursor.getString(cursor
                            .getColumnIndex("data1"));
                    // 拿到手机号码
                    info.setPhone(number);
                } else if ("vnd.android.cursor.item/name".equals(type)) {
                    String name = cursor.getString(cursor
                            .getColumnIndex("data1"));
                    info.setName(name);
                }
            }
            infos.add(info);
            dataCursor.close();
            info = null;
        }
        return infos;
    }
}
