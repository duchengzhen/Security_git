package com.calvin.security.engine;

import android.content.Context;
import com.calvin.security.domain.UpdateInfo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 从config.xml文件中获取地址
 * 然后读取update.xml的url进行加载,得到输入流(InputStream)
 * 调用其他类解析xml文件的方法解析
 *
 * @author calvin(杜承祯)
 */
public class UpdateInfoService {
    private Context context;

    public UpdateInfoService(Context context) {
        this.context = context;
    }

    public UpdateInfo getUpdateInfo(int urlId) throws Exception {
        String path = context.getResources().getString(urlId);//获取config.xml文件
        URL url = new URL(path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();//开启一个http连接
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setConnectTimeout(5000);//设置连接超时时间(s)
        InputStream is = httpURLConnection.getInputStream();//得到一个输入流,里面包含了update.xml的信息
        // 添加UpdateInfoParse方法的返回值

        return UpdateInfoParser.getUpdateInfo(is);

    }

}
