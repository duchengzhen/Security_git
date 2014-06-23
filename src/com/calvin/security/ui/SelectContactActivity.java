package com.calvin.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.calvin.security.R;
import com.calvin.security.adapter.SelectContactAdapter;
import com.calvin.security.domain.ContactInfo;
import com.calvin.security.engine.ContactsService;

import java.util.List;

public class SelectContactActivity extends Activity {
    private List<ContactInfo> infos;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contact);

        //infos=new ContactInfoService(this).getContactInfos();
        infos = ContactsService.getContacts(this);
        lv = (ListView) findViewById(R.id.lv_select_contact);

        lv.setAdapter(new SelectContactAdapter(this, infos));

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String phone = infos.get(position).getPhone();
                Intent intent = new Intent();
                intent.putExtra("number", phone);
                //如果要把数据返回设置中去,通过onActivityForResult(int,int,Intent)拿到
                setResult(1, intent);
                finish();
            }
        });
    }

}
