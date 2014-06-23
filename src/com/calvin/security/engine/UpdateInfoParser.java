package com.calvin.security.engine;

import android.text.TextUtils;
import android.util.Xml;
import com.calvin.security.domain.UpdateInfo;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * 最底层的解析xml文件的服务类
 *
 * @author calvin
 */
public class UpdateInfoParser {

    public static UpdateInfo getUpdateInfo(InputStream is) throws Exception {
        UpdateInfo info = new UpdateInfo();

        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(is, "utf-8");
        int type = xmlPullParser.getEventType();
        while (type != XmlPullParser.END_DOCUMENT) {

            switch (type) {
                case XmlPullParser.START_TAG:
                    if (xmlPullParser.getName().equals("version")) {
                        info.setVersion(xmlPullParser.nextText());
                    } else if (TextUtils.equals(xmlPullParser.getName(), "description")) {
                        info.setDescription(xmlPullParser.nextText());
                    } else if (TextUtils.equals(xmlPullParser.getName(), "apkurl")) {
                        info.setUrl(xmlPullParser.nextText());
                    }
                    break;
                default:
                    break;
            }
            type = xmlPullParser.next();
        }
        return info;
    }

}
