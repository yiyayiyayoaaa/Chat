package com.microcardio.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.microcardio.chat.R;
import com.microcardio.chat.adapter.PortraitAdapter;
import com.microcardio.chat.service.SocketService;

public class PortraitActivity extends AppCompatActivity {
    GridView gv_portrait;
    int[] portrait = {R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5,R.drawable.p6,R.drawable.p7,R.drawable.p8
            ,R.drawable.p9,R.drawable.p10,R.drawable.p11,R.drawable.p12,R.drawable.p13,R.drawable.p14};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("选择头像");
        gv_portrait = (GridView) findViewById(R.id.gv_portrait);
        gv_portrait.setAdapter(new PortraitAdapter(this,portrait));
        gv_portrait.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("portrait",portrait[position]);
                setResult(0,intent);
                finish();
            }
        });
        SocketService.count ++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SocketService.count --;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
