package com.microcardio.chat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microcardio.chat.R;
import com.microcardio.chat.po.Constants;
import com.microcardio.chat.po.Message;
import com.microcardio.chat.po.User;
import com.microcardio.chat.service.SocketService;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    AutoCompleteTextView et_login_username;
    EditText et_login_password;
    CheckBox cb_remember;
    CheckBox cb_autoLogin;
    CircleImageView civ_login_portrait;
    Button bt_username_clear;
    Button bt_pwd_clear;
    String username;   //用户名
    String password;   //密码
    String nickname;   //昵称
    int portrait;      //头像
    SocketService.SendMessageBinder sendMessageBinder;
    Intent socketService;     //socket服务
    LoginServiceConn  loginServiceConn = new LoginServiceConn();
    LoginMsgReceiver loginMsgReceiver = new LoginMsgReceiver(); //登录信息广播接收者对象
    LocalBroadcastManager localBroadcastManager;
    //MyHandler handler = new MyHandler(this);
    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case Constants.GET_PORTRAIT:
                    civ_login_portrait.setImageResource(portrait);
                    break;
                case Constants.LOGIN_SUCCESS:
                    //提示登录成功，跳转到主页面
                    writeUsername();
                    writeConfig();
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("myUsername",username);
                    intent.putExtra("myNickname",nickname);
                    intent.putExtra("myPortrait",portrait);
                    startActivity(intent);
                    finish();
                    break;
                case Constants.LOGIN_ERROR:
                    //提示登录失败
                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initControl();//初始化控件
        readUsername();
        startSocketService(); //开启服务

        registerReceiver();//注册广播接收者

        bindSocketService();//绑定服务
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();//解绑服务
        unregisterReceiver();//注销广播接收者
    }

    //读取配置
    private void readConfig(){
        SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
        username = sp.getString("username","");

        boolean isRemember = sp.getBoolean("isRemember",false);
        boolean isAutoLogin = sp.getBoolean("isAutoLogin",false);
        if(isRemember){
            password = sp.getString("password","");
        }
        civ_login_portrait.setImageResource(sp.getInt("portrait",R.drawable.p1));
        et_login_username.setText(username);
        et_login_password.setText(password);
        cb_remember.setChecked(isRemember);
        cb_autoLogin.setChecked(isAutoLogin);
        if(isAutoLogin){
            Button login = (Button) findViewById(R.id.btn_login);
            login.performClick();
        }
        et_login_username.setSelection(username.length());
        et_login_username.dismissDropDown();
    }
    //写入配置文件
    private void writeConfig(){
        SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("isRemember",cb_remember.isChecked());
        ed.putBoolean("isAutoLogin",cb_autoLogin.isChecked());
        ed.putString("username",et_login_username.getText().toString());
        ed.putInt("portrait",portrait);
        if(cb_remember.isChecked()){
            ed.putString("password",et_login_password.getText().toString());
        }
        ed.commit();
    }

    //记录登录过的用户名
    private void writeUsername(){
        SharedPreferences sp = getSharedPreferences("username",MODE_APPEND);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(username,"");
        ed.commit();
    }
    //读取用户名
    private void readUsername(){
        SharedPreferences sp = getSharedPreferences("username",MODE_APPEND);
        Map map = sp.getAll();
        //System.out.println(map.size());
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_array_item,map.keySet().toArray());
        et_login_username.setAdapter(adapter);

    }

    //登录
    public void login(View v){
        username = et_login_username.getText().toString();
        password = et_login_password.getText().toString();
        User sender = new User();
        sender.setUsername(username);
        sender.setPassword(password);
        Message loginMsg = new Message();
        loginMsg.setCmd(Constants.CMD_LOGIN);
        loginMsg.setSender(sender);
        sendMessageBinder.sendMessage(loginMsg);//向服务器发送登录信息
    }

    //跳转到注册页面
    public void register(View v){
        Intent toRegister = new Intent(this,RegisterActivity.class);
        startActivity(toRegister);
        //finish();
    }

    //初始化控件
    public void initControl(){
        et_login_username = (AutoCompleteTextView) findViewById(R.id.et_login_username);
       // et_login_username.setFocusable(false);
       // et_login_username.setCursorVisible(false);

        et_login_password = (EditText) findViewById(R.id.et_login_password);
        cb_remember = (CheckBox) findViewById(R.id.cb_remember);
        bt_username_clear = (Button)findViewById(R.id.bt_username_clear);
        bt_pwd_clear = (Button)findViewById(R.id.bt_pwd_clear);

        et_login_username.addTextChangedListener(new EditChangedListener(et_login_username));
        et_login_password.addTextChangedListener(new EditChangedListener(et_login_password));

        cb_autoLogin = (CheckBox) findViewById(R.id.cb_autoLogin);
        civ_login_portrait = (CircleImageView) findViewById(R.id.civ_login_portrait);
        et_login_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Message message = new Message();
                    message.setCmd(Constants.GET_PORTRAIT);
                    User user = new User();
                    message.setSender(user);
                    user.setUsername(et_login_username.getText().toString());
                    sendMessageBinder.sendMessage(message);
                }
            }
        });
        et_login_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    Message message = new Message();
                    message.setCmd(Constants.GET_PORTRAIT);
                    User user = new User();
                    message.setSender(user);
                    user.setUsername(et_login_username.getText().toString());
                    sendMessageBinder.sendMessage(message);
                }
            }
        });

    }



    //开启服务
    public void startSocketService(){
        Log.d(TAG, "startSocketService: 开启服务");
        socketService = new Intent(this, SocketService.class);
        startService(socketService);
    }
    //绑定服务
    public void bindSocketService(){
        Log.d(TAG, "bindSocketService: start");
        bindService(socketService,loginServiceConn,BIND_AUTO_CREATE);
        Log.d(TAG, "bindSocketService: end");
    }
    //解绑服务
    public void unbindService(){
        unbindService(loginServiceConn);
    }
    //注册广播接收者
    public void registerReceiver(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.microcardio.chat.MESSAGE_BROADCAST");
        localBroadcastManager.registerReceiver(loginMsgReceiver,intentFilter);
    }
    //注销广播接收者
    public void unregisterReceiver(){
        localBroadcastManager.unregisterReceiver(loginMsgReceiver);
    }
    //绑定服务
    class LoginServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sendMessageBinder = (SocketService.SendMessageBinder) service;
            readConfig();
            Log.d(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    //登录信息广播接收者
    class LoginMsgReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json");
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
            Message message = gson.fromJson(json,Message.class);
            int cmd = message.getCmd();
            switch (cmd){
                case Constants.GET_PORTRAIT:
                    portrait = message.getReceived().getPortrait();
                    handler.sendEmptyMessage(Constants.GET_PORTRAIT);
                    break;
                case Constants.LOGIN_SUCCESS:
                    portrait = message.getSender().getPortrait();
                    nickname = message.getSender().getNickname();
                    username = message.getSender().getUsername();
                    handler.sendEmptyMessage(Constants.LOGIN_SUCCESS);
                    break;
                case Constants.LOGIN_ERROR:
                    handler.sendEmptyMessage(Constants.LOGIN_ERROR);
                    break;
            }
        }
    }
    //显示清除图标
    class EditChangedListener implements TextWatcher {
        EditText et;

        public EditChangedListener(EditText et) {
            this.et = et;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (et.getId() == R.id.et_login_username) {
                if (s.toString().length() > 0) {
                    bt_username_clear.setVisibility(View.VISIBLE);
                } else {
                    bt_username_clear.setVisibility(View.INVISIBLE);
                }
            } else {
                if (s.toString().length() > 0) {
                    bt_pwd_clear.setVisibility(View.VISIBLE);
                } else {
                    bt_pwd_clear.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
        //清除文本
        public void clear(View view) {
            switch (view.getId()) {
                case R.id.bt_username_clear:
                    et_login_username.setText("");
                    et_login_password.setText("");
                    break;
                case R.id.bt_pwd_clear:
                    et_login_password.setText("");
                    break;
                default:
            }
        }

}
