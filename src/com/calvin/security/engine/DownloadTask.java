package com.calvin.security.engine;

import android.app.ProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask {
    public static File getFile(String path, String filePath, ProgressDialog progressDialog) throws Exception {
        URL url = new URL(path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(2000);
        httpURLConnection.setRequestMethod("GET");
        if (httpURLConnection.getResponseCode() == 200) {//获得正确响应
            int total = httpURLConnection.getContentLength();//连接对象的内存大小
            progressDialog.setMax(total);//设置进度对话框最大值

            InputStream is = httpURLConnection.getInputStream();
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);//创建用于写file的输入流
            byte[] buffer = new byte[1024];
            int len;//读取的数据长度
            int process = 0;
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, len);
                process += len;
                progressDialog.setProgress(process);
            }
            fos.flush();
            fos.close();
            is.close();
            return file;
        }
        return null;

    }
}
