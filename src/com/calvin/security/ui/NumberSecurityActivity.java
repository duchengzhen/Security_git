package com.calvin.security.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.calvin.security.R;
import com.calvin.security.adapter.NumberAdapter;
import com.calvin.security.dao.BlackNumberDao;

import java.util.List;

public class NumberSecurityActivity extends Activity {
    private ListView lv_number;
    private Button bt_number_add;
    private BlackNumberDao dao;
    private List<String> numbers;
    private NumberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.number_security);

        init();

        bt_number_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addNumber(null);
            }
        });
    }

    @Override//在该Activity变为可响应状态时显示电话号码
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String number = intent.getStringExtra("number");
        if (number != null) {
            addNumber(number);
        }
    }

    private void init() {
        dao = new BlackNumberDao(this);
        numbers = dao.findAll();
        adapter = new NumberAdapter(this, numbers);

        lv_number = (ListView) findViewById(R.id.lv_number);
        lv_number.setAdapter(adapter);

        //TODO registerForContextMenu(lv_number);
        registerForContextMenu(lv_number);//给listView注册一个上下文Menu
        bt_number_add = (Button) findViewById(R.id.bt_number_add);
    }

    private void addNumber(String number) {
        Builder builder = new AlertDialog.Builder(NumberSecurityActivity.this);
        builder.setTitle("添加黑名单");
        final EditText et_number = new EditText(NumberSecurityActivity.this);
        et_number.setInputType(InputType.TYPE_CLASS_PHONE);
        et_number.setHint("请输入黑名单号码");

        if (number != null) {//自动将号码填写到EditView中
            et_number.setText(number);
        }

        builder.setView(et_number);
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number = et_number.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    //TODO 如果为空,点击添加后对话框消失
                    et_number.startAnimation(AnimationUtils.loadAnimation(NumberSecurityActivity.this, R.anim.shake));
                    Toast.makeText(NumberSecurityActivity.this, "号码不能为空哦", Toast.LENGTH_SHORT).show();

                } else {
                    dao.add(number);//添加到数据库
                    numbers = dao.findAll();
                    adapter.notifyDataSetChanged();
                }
            }

        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.number, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //获取点击菜单的信息
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.update_number:
                String oldNumber = numbers.get((int) info.id);
                updateNumber(oldNumber);
                break;
            case R.id.delete_number:
                String number = numbers.get((int) info.id);
                dao.delete(number);
                numbers = dao.findAll();
                adapter.notifyDataSetChanged();
                break;

            default:
                break;
        }
        return super.onContextItemSelected(item);

    }

    private void updateNumber(final String oldNumber) {
        Builder builder = new AlertDialog.Builder(NumberSecurityActivity.this);
        builder.setTitle("更新黑名单");
        final EditText et_number = new EditText(NumberSecurityActivity.this);
        et_number.setInputType(InputType.TYPE_CLASS_PHONE);
        et_number.setHint("请输入新号码");
        builder.setView(et_number);
        builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number = et_number.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    et_number.setAnimation(AnimationUtils.loadAnimation(NumberSecurityActivity.this, R.anim.shake));
                    Toast.makeText(NumberSecurityActivity.this, "号码不能为空哦", Toast.LENGTH_SHORT).show();
                } else {
                    dao.update(oldNumber, number);
                    ;//更新数据库
                    numbers = dao.findAll();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.create().show();
    }
}
