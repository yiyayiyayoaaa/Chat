package com.microcardio.chat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microcardio.chat.R;
import com.microcardio.chat.po.Constants;
import com.microcardio.chat.po.Message;
import com.microcardio.chat.po.User;
import com.microcardio.chat.service.SocketService;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditMeActivity extends AppCompatActivity {
    CircleImageView civ_edit_portrait;
    EditText et_edit_nickname;
    Button btn_logout;
    int portrait;
    String username;
    String nickname;
    Intent service;
    SocketService.SendMessageBinder sendMessageBinder;
    EditMeServiceConn editMeServiceConn;
    LocalBroadcastManager localBroadcastManager;
    EditMeMsgReceiver editMeMsgReceiver = new EditMeMsgReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_me);
        initControl();
        initData();
        registerBroadcast();
        bindSocketService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SocketService.count --;
    }

    //修改昵称头像
    public void edit(View view){
        nickname = et_edit_nickname.getText().toString();
        Message message = new Message();
        message.setCmd(Constants.EDIT_ME);
        User editor = new User();
        editor.setUsername(username);
        editor.setNickname(nickname);
        editor.setPortrait(portrait);
        message.setSender(editor);
        sendMessageBinder.sendMessage(message);
    }
    //初始化控件
    private void initControl(){
        civ_edit_portrait = (CircleImageView) findViewById(R.id.civ_edit_portrait);
        et_edit_nickname = (EditText) findViewById(R.id.et_edit_nickname);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("isRemember",false);
                ed.putBoolean("isAutoLogin",false);
               // ed.putString("username",et_login_username.getText().toString());
                //if(cb_remember.isChecked()){
                ed.putString("password","");
              //  }
                ed.commit();
                Intent intent = new Intent();
                setResult(Constants.CMD_LOGOUT,intent);
                finish();

            }
        });
        civ_edit_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPortrait = new Intent(EditMeActivity.this,PortraitActivity.class);
                startActivityForResult(toPortrait,0);
            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    //初始化数据
    private void initData(){
        Intent intent = getIntent();
        portrait = intent.getIntExtra("portrait",R.drawable.p1);
        nickname = intent.getStringExtra("myNickname");
        username = intent.getStringExtra("myUsername");
        et_edit_nickname.setText(nickname);
        civ_edit_portrait.setImageResource(portrait);
        setTitle("设置");
        et_edit_nickname.setSelection(nickname.length());
        et_edit_nickname.clearFocus();
    }

    //绑定socketService
    private void bindSocketService(){
        service = new Intent(this, SocketService.class);
        editMeServiceConn = new EditMeServiceConn();
        bindService(service,editMeServiceConn,BIND_AUTO_CREATE);
    }

    //解绑socketService
    private void unbindSocketService(){
        unbindService(editMeServiceConn);
    }

    //注册广播接收者
    private void registerBroadcast(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.microcardio.chat.MESSAGE_BROADCAST");
        localBroadcastManager.registerReceiver(editMeMsgReceiver,intentFilter);
    }
    //注销广播接收者
    private void unregisterBroadcast(){
        localBroadcastManager.unregisterReceiver(editMeMsgReceiver);
    }
    class EditMeServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sendMessageBinder = (SocketService.SendMessageBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    //广播接收者
    class EditMeMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json");
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
            Message message = gson.fromJson(json,Message.class);
            int cmd = message.getCmd();
            switch (cmd){
                case Constants.EDIT_SUCCESS:
                    Toast.makeText(EditMeActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra("nickname",nickname);
                    data.putExtra("portrait",portrait);
                    EditMeActivity.this.setResult(0,data);
                    finish();
                    break;
                case Constants.EDIT_ERROR:
                    Toast.makeText(EditMeActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            portrait = data.getIntExtra("portrait", R.drawable.p4);
            civ_edit_portrait.setImageResource(portrait);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindSocketService();
        unregisterBroadcast();
    }
}
