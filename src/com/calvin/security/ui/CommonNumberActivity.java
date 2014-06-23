package com.calvin.security.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import com.calvin.security.R;
import com.calvin.security.adapter.MyBaseExpandableListViewAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CommonNumberActivity extends Activity {
    private static final String DBPATH = Environment.getExternalStorageDirectory() +
            "/security/db/commonum.db";
    private ExpandableListView elv_list;
    private MyBaseExpandableListViewAdapter adapter;
    //private List<String> group;
    //private List<List<String>> childs;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            adapter = new MyBaseExpandableListViewAdapter(
                    CommonNumberActivity.this, DBPATH);
            elv_list.setAdapter(adapter);
            //给子列表添加点击事件
            elv_list.setOnChildClickListener(new OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    List<List<String>> childs = adapter.getChilds();
                    String str = childs.get(groupPosition).get(childPosition);
                    //得到电话号码
                    String number = str.split("-")[1];
                    Intent intent = new Intent(
                            "android.intent.action.CALL",
                            Uri.parse("tel:" + number));
                    startActivity(intent);
                    return false;
                }
            });
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_number);

        elv_list = (ExpandableListView) findViewById(R.id.elv_list);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(DBPATH);
            //如果数据库文件存在,就不用移动
            if (file.exists()) {
                handler.sendEmptyMessage(1);
            } else {
                moveDatabase(file);
            }
        } else {
            Toast.makeText(this, "当前SD卡不可用，请检查后重试...", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 将数据库从assets里面读取到sd卡里面,开启了一条线程进行操作
     * 完成后,发送消息给handler处理
     *
     * @param file
     */
    private void moveDatabase(File file) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/security/db");
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            try {
                final InputStream is = getAssets().open("commonnum.db");
                final FileOutputStream fos = new FileOutputStream(file);
                new Thread() {
                    @Override
                    public void run() {
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        try {
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            fos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //处理完成,发送消息
                        handler.sendEmptyMessage(1);
                    }

                    ;

                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
