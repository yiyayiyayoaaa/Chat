package com.microcardio.chat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.microcardio.chat.R;
import com.microcardio.chat.util.BitMapUtil;

public class ViewPicActivity extends AppCompatActivity {
    ImageView iv_view_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pic);
        setTitle("查看图片");
        if(getSupportActionBar() != null) {
            System.out.println("dsadsadadsaad"+getSupportActionBar().getHeight());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = getIntent().getStringExtra("path");
        iv_view_pic = (ImageView) findViewById(R.id.iv_view_pic);
        iv_view_pic.setImageBitmap(BitMapUtil.viewImage(path,ViewPicActivity.this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                item.setTitle("返回");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
