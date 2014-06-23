package com.calvin.security.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.calvin.security.domain.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class ContactsService {
    public static List<ContactInfo> getContacts(Context context) {
        List<ContactInfo> contacts = new ArrayList<ContactInfo>();
        ContentResolver resolver = context.getContentResolver();
        // raw_contacts表的Uri
        Uri rawUri = Uri.parse("content://com.android.contacts/raw_contacts");
        // data表的Uri
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        Cursor idCursor = resolver.query(rawUri, new String[]{"contact_id"},
                null, null, null);

        while (idCursor.moveToNext()) {
            // 得raw_contacts表中联系人的id
            String id = idCursor.getString(0);
            if (id != null) {
                // 根据id查询姓名和数据类型
                Cursor dataCursor = resolver.query(dataUri, new String[]{
                                "data1", "mimetype"}, "raw_contact_id=?",
                        new String[]{id}, null);

                ContactInfo contact = new ContactInfo();

                while (dataCursor.moveToNext()) {
                    String mimetype = dataCursor.getString(1);
                    // 判断字段类型
                    if ("vnd.android.cursor.item/name".equals(mimetype)) {
                        String name = dataCursor.getString(dataCursor
                                .getColumnIndex("data1"));
                        contact.setName(name);
                    } else if ("vnd.android.cursor.item/phone_v2"
                            .equals(mimetype)) {
                        String phone = dataCursor.getString(dataCursor
                                .getColumnIndex("data1"));
                        contact.setPhone(phone);
                    }
                }
                contacts.add(contact);
                dataCursor.close();
            }
        }
        idCursor.close();
        return contacts;
    }
}
