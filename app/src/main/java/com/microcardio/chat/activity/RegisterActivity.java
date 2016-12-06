package com.microcardio.chat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microcardio.chat.R;
import com.microcardio.chat.po.Constants;
import com.microcardio.chat.po.Message;
import com.microcardio.chat.po.User;
import com.microcardio.chat.service.SocketService;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    CircleImageView civ_register_portrait; //头像
    EditText et_register_username;        //用户名
    EditText et_register_password;        //密码
    EditText et_register_confirm;         //确认密码
    EditText et_register_nickname;        //昵称
    ImageView iv_username_prompt;
    ImageView iv_password_prompt;
    ImageView iv_confirm_prompt;
    ImageView iv_nickname_prompt;
    int portrait;               //头像
    String username;             //用户名
    String password;            //密码
    String confirm;             //确认密码
    String nickname;            //昵称
    boolean validate_username;
    boolean validate_password;
    boolean validate_confirm;
    //boolean validate_nickname;

    SocketService.SendMessageBinder sendMessageBinder;
    Intent socketService;     //socket服务
    RegisterServiceConn  registerServiceConn;
    RegisterMsgReceiver registerMsgReceiver; //登录信息广播接收者对象
    LocalBroadcastManager localBroadcastManager;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case Constants.IS_EXIST:
                    iv_username_prompt.setImageResource(R.drawable.no);
                    validate_username = false;
                    Toast.makeText(RegisterActivity.this,"用户名已存在",Toast.LENGTH_SHORT).show();
                    break;
                case Constants.IS_NOT_EXIST:
                    iv_username_prompt.setImageResource(R.drawable.yes);
                    validate_username = true;
                    break;
                case Constants.REGISTER_SUCCESS:
                    Toast.makeText(RegisterActivity.this,"注册成功，正在登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    intent.putExtra("myUsername",username);
                    intent.putExtra("myNickname",nickname);
                    intent.putExtra("myPortrait",portrait);
                    startActivity(intent);
                    finish();
                    break;
                case Constants.REGISTER_ERROR:
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initControl();
        initData();
        registerReceiver();
        bindSocketService();
        SocketService.count ++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SocketService.count --;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
        unregisterReceiver();
    }

    //注册
    public void register(View v){
        Message message = new Message();
        message.setCmd(Constants.CMD_REGISTER);
        nickname = et_register_nickname.getText().toString();
        //System.out.println(nickname);
        if(portrait == 0){
            portrait = R.drawable.p1;
        }
        User register = new User(username,password,nickname,portrait);
        message.setSender(register);
        if(validate_username&&validate_confirm&&validate_password){
            sendMessageBinder.sendMessage(message);
        }else{
            Toast.makeText(this,"信息填写不正确",Toast.LENGTH_SHORT).show();
        }
    }

    private void initControl(){
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        civ_register_portrait = (CircleImageView) findViewById(R.id.civ_register_portrait);
        et_register_username = (EditText) findViewById(R.id.et_register_username);
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        et_register_confirm = (EditText) findViewById(R.id.et_register_confirm);
        et_register_nickname = (EditText) findViewById(R.id.et_register_nickname);
        iv_username_prompt = (ImageView) findViewById(R.id.iv_username_prompt);
        iv_password_prompt = (ImageView) findViewById(R.id.iv_password_prompt);
        iv_confirm_prompt = (ImageView) findViewById(R.id.iv_confirm_prompt);
        iv_nickname_prompt = (ImageView) findViewById(R.id.iv_nickname_prompt);
        civ_register_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPortrait = new Intent(RegisterActivity.this,PortraitActivity.class);
                startActivityForResult(toPortrait,0);
            }
        });
        et_register_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                password = et_register_password.getText().toString();
                if(hasFocus){
                    iv_password_prompt.setImageDrawable(null);
                }else{
                    if("".equals(password.trim())){
                        iv_password_prompt.setImageResource(R.drawable.no);
                        validate_password = false;
                    } else {
                        iv_password_prompt.setImageResource(R.drawable.yes);
                        validate_password = true;
                    }
                }
            }
        });
        et_register_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                username = et_register_username.getText().toString();
                if(hasFocus){
                    iv_username_prompt.setImageDrawable(null);
                } else {
                    if("".equals(username.trim())){
                        iv_username_prompt.setImageResource(R.drawable.no);
                        validate_username = false;
                        //System.out.println("用户名为空");
                    }else{
                        Message message = new Message();
                        message.setCmd(Constants.VALIDATE_USERNAME);
                        User validate = new User();
                        validate.setUsername(username);
                        message.setSender(validate);
                        sendMessageBinder.sendMessage(message);
                    }
                }
            }
        });
        et_register_confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                confirm = et_register_confirm.getText().toString();
                if(hasFocus){
                    iv_confirm_prompt.setImageDrawable(null);
                } else {
                    if(!confirm.equals(password)){
                        iv_confirm_prompt.setImageResource(R.drawable.no);
                        validate_confirm = false;
                    }else{
                        iv_confirm_prompt.setImageResource(R.drawable.yes);
                        validate_confirm = true;
                    }
                }
            }
        });
    }

    private void initData(){
        //
        socketService = new Intent(this, SocketService.class);
        registerMsgReceiver = new RegisterMsgReceiver();
        registerServiceConn = new RegisterServiceConn();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            portrait = data.getIntExtra("portrait", R.drawable.p4);
            civ_register_portrait.setImageResource(portrait);
        }
    }

    //绑定服务
    public void bindSocketService(){
        bindService(socketService,registerServiceConn,BIND_AUTO_CREATE);
    }
    //解绑服务
    public void unbindService(){
        unbindService(registerServiceConn);
    }
    //注册广播接收者
    public void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.microcardio.chat.MESSAGE_BROADCAST");
        localBroadcastManager.registerReceiver(registerMsgReceiver,intentFilter);
    }
    //注销广播接收者
    public void unregisterReceiver(){
        localBroadcastManager.unregisterReceiver(registerMsgReceiver);
    }

    //绑定服务
    class RegisterServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sendMessageBinder = (SocketService.SendMessageBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    //广播接收者
    class RegisterMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json");
            //System.out.println(json);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
            Message message = gson.fromJson(json,Message.class);
            int cmd = message.getCmd();
            //System.out.println(cmd);
            switch (cmd){
                case Constants.IS_EXIST:
                    handler.sendEmptyMessage(Constants.IS_EXIST);
                    break;
                case Constants.IS_NOT_EXIST:
                    handler.sendEmptyMessage(Constants.IS_NOT_EXIST);
                    break;
                case Constants.REGISTER_SUCCESS:
                    handler.sendEmptyMessage(Constants.REGISTER_SUCCESS);
                    break;
                case Constants.REGISTER_ERROR:
                    handler.sendEmptyMessage(Constants.REGISTER_ERROR);
                    break;
            }
            //handler.sendEmptyMessage(cmd);
        }
    }

}
