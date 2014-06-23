package com.calvin.security.engine;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import com.calvin.security.domain.SmsInfo;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SmsService {
    private Context context;

    private int count;

    public SmsService(Context context) {
        this.context = context;
    }

    public List<SmsInfo> getSmsInfos() {
        List<SmsInfo> infos = new ArrayList<SmsInfo>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"_id", "address",
                "date", "type", "body"}, null, null, "date desc");
        SmsInfo info;
        count = 0;
        while (cursor.moveToNext()) {
            info = new SmsInfo();
            String id = cursor.getString(0);
            String address = cursor.getString(1);
            String date = cursor.getString(2);
            int type = cursor.getInt(3);
            String body = cursor.getString(4);
            info.setAddress(address);
            info.setBody(body);
            info.setDate(date);
            info.setId(id);
            info.setType(type);

            infos.add(info);
            count++;
            info = null;
        }
        cursor.close();
        return infos;
    }

    /**
     * 短信还原
     * @param path 文件路径
     * @param pd   进度对话框ProgressDialog
     * @throws IOException
     * @throws XmlPullParserException
     */
    public void restore(String path, ProgressDialog pd)
            throws XmlPullParserException, IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        //==================有问题
        XmlPullParser parser = Xml.newPullParser();// xml解析器
        parser.setInput(fis, "utf-8");
        int typeC = parser.getEventType();
        while (typeC != XmlPullParser.END_DOCUMENT) {
            if ("count".equals(parser.getName())) {
                int count = Integer.parseInt(parser.nextText());
                pd.setMax(count);
            }
            typeC = parser.next();
        }
        //=====================有问题

        XmlPullParser pullParser = Xml.newPullParser();// xml解析器
        pullParser.setInput(fis, "utf-8");
        ContentValues values = null;
        int type = pullParser.getEventType();
        int index = 0;
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                    if ("count".equals(pullParser.getName())) {
                        int count = Integer.parseInt(pullParser.nextText());
                        pd.setMax(count);
                    }

                    if ("sms".equals(pullParser.getName())) {
                        values = new ContentValues();

                    } else if ("address".equals(pullParser.getName())) {
                        values.put("address", pullParser.nextText());

                    } else if ("date".equals(pullParser.getName())) {
                        values.put("date", pullParser.nextText());

                    } else if ("type".equals(pullParser.getName())) {
                        values.put("type", pullParser.nextText());

                    } else if ("body".equals(pullParser.getName())) {
                        values.put("body", pullParser.nextText());

                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("sms".equals(pullParser.getName())) {
                        ContentResolver resolver = context.getContentResolver();
                        resolver.insert(Uri.parse("content://sms/"), values);
                        values = null;
                        index++;
                        pd.setProgress(index);
                    }
                    break;

                default:
                    break;
            }
            type = pullParser.next();// 相当于将解析器下移
        }

    }
}
