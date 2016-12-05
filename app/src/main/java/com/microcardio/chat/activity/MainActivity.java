package com.microcardio.chat.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microcardio.chat.R;
import com.microcardio.chat.adapter.UserListAdapter;
import com.microcardio.chat.po.Constants;
import com.microcardio.chat.po.Message;
import com.microcardio.chat.po.User;
import com.microcardio.chat.service.SocketService;
import com.microcardio.chat.util.DateUtil;
import com.microcardio.chat.util.FileNameUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView civ_myPortrait;   //显示我的头像
    TextView tv_myNickname;   //显示我的昵称
    ListView lv_userList;    //显示所有联系人
    String myUsername;    //我的用户名
    String myNickname;   //我的昵称
    int myPortrait;     //我的头像
    boolean foreground= true; //活动可见标志
    ImageButton ib_config;
    LinkedList<User> userList;
    //List<Message> messageList;
    SocketService.SendMessageBinder sendMessageBinder;
    Intent socketService;     //socket服务
    LoginServiceConn  loginServiceConn;
    LoginMsgReceiver loginMsgReceiver; //登录信息广播接收者对象
    LocalBroadcastManager localBroadcastManager;
    UserListAdapter userListAdapter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            userListAdapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControl();
        initData();
        registerReceiver();
        bindSocketService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        foreground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        foreground = false;
        ChatActivity.foreground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
        unregisterReceiver();
    }

    //初始化成员
    private void initData(){
        Intent intent = getIntent();
        userList = new LinkedList<>();
        myUsername = intent.getStringExtra("myUsername");
        myNickname = intent.getStringExtra("myNickname");
        myPortrait = intent.getIntExtra("myPortrait",R.drawable.p1);
        System.out.println(myPortrait);
        tv_myNickname.setText(myNickname);
        civ_myPortrait.setImageResource(myPortrait);
        loginMsgReceiver = new LoginMsgReceiver();
        loginServiceConn = new LoginServiceConn();
        userListAdapter = new UserListAdapter(this,userList);
        socketService = new Intent(this, SocketService.class);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        lv_userList.setAdapter(userListAdapter);
        lv_userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = userList.get(i);
                String username = user.getUsername();
                String nickname = user.getNickname();
                int portrait = user.getPortrait();
                //int portrait = user.getPortrait();
                ChatActivity.foreground = true;
                Intent toChat = new Intent(MainActivity.this,ChatActivity.class);
                toChat.putExtra("receivedUsername",username);
                toChat.putExtra("receivedNickname",nickname);
                toChat.putExtra("senderUsername",myUsername);
                toChat.putExtra("myPortrait",myPortrait);
                toChat.putExtra("receivedPortrait",portrait);
                //startActivityForResult(toChat,200);
                startActivity(toChat);
            }
        });
    }

    //初始化控件
    private void initControl(){
        lv_userList = (ListView) findViewById(R.id.lv_userList);
        tv_myNickname = (TextView) findViewById(R.id.tv_myNickname);
        civ_myPortrait = (CircleImageView) findViewById(R.id.civ_myPortrait);
        ib_config = (ImageButton) findViewById(R.id.ib_config);
        ib_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEditMe = new Intent(MainActivity.this,EditMeActivity.class);
                toEditMe.putExtra("myNickname",myNickname);
                toEditMe.putExtra("myUsername",myUsername);
                toEditMe.putExtra("portrait",myPortrait);
                startActivityForResult(toEditMe,Constants.EditMe_RETURN);
            }
        });
    }

    //加载用户列表
    public void initUserList(){
        User user = new User();
        user.setUsername(myUsername);
        Message message = new Message();
        message.setCmd(Constants.USER_LIST);
        message.setSender(user);
        sendMessageBinder.sendMessage(message);
    }

    //绑定服务
    public void bindSocketService(){
        bindService(socketService,loginServiceConn,BIND_AUTO_CREATE);
    }
    //解绑服务
    public void unbindService(){
        unbindService(loginServiceConn);
    }
    //注册广播接收者
    public void registerReceiver(){
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
            initUserList();
            initMessageList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void initMessageList() {
        Message message = new Message();
        User user = new User();
        user.setUsername(myUsername);
    }

    //广播接收者
    class LoginMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json");
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
            Message message = gson.fromJson(json,Message.class);
            int cmd = message.getCmd();
            switch (cmd){
                case Constants.USER_LIST:
                    userList.add(message.getSender());
                    handler.sendEmptyMessage(1);
                    break;
                case Constants.CMD_CHAT: //显示新消息
                    String senderName = message.getSender().getUsername();
                    String receivedName = message.getReceived().getUsername();
                    List<User> users = new ArrayList<>();
                    users.addAll(userList);
                    for(User user : users){
                        if(user.getUsername().equals(senderName) ||user.getUsername().equals(receivedName)){
                            if(FileNameUtil.isImage(message.getContent())){
                                user.setNewInfo("[图片]");
                            }else if(FileNameUtil.isAudio(message.getContent())){
                                user.setNewInfo("[语音]");
                            }else{
                                user.setNewInfo(message.getContent());
                            }
                            user.setNewDate(DateUtil.parseStr(message.getSendDate()));
                            userList.remove(user);
                            userList.addFirst(user);
                            notice(user,message,context);
                            handler.sendEmptyMessage(1);
                        }
                    }
                    break;
                case Constants.USER_UPDATE:
                    String send = message.getSender().getUsername();
                    for(User user:userList){
                        if(user.getUsername().equals(send)){
                            user.setNickname(message.getSender().getNickname());
                            user.setPortrait(message.getSender().getPortrait());
                            handler.sendEmptyMessage(1);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Constants.EditMe_RETURN:
                if(data != null) {
                   if(resultCode ==Constants.CMD_LOGOUT){
                       Intent toLogin = new Intent(this,LoginActivity.class);
                       startActivity(toLogin);
                       finish();
                   }else{
                       civ_myPortrait.setImageResource(data.getIntExtra("portrait", myPortrait));
                       myPortrait = data.getIntExtra("portrait", myPortrait);
                       tv_myNickname.setText(data.getStringExtra("nickname"));
                       myNickname = data.getStringExtra("nickname");
                   }
                }
        }
    }

    //后台消息通知
    public void notice(User user,Message message,Context context){
        String nickname = null;
        int portrait =  0;
        if(myUsername.equals(message.getReceived().getUsername()) && !foreground && !(ChatActivity.foreground)){
            Log.d("TAG", String.valueOf(ChatActivity.foreground));
            Resources res = getResources();
            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if(user.getUsername().equals(message.getSender().getUsername())){
                nickname = user.getNickname();
                portrait = user.getPortrait();
            }
            Intent i = new Intent(this,ChatActivity.class);
            i.putExtra("receivedUsername",message.getSender().getUsername());
            i.putExtra("receivedNickname",nickname);
            i.putExtra("senderUsername",message.getReceived().getUsername());
            i.putExtra("myPortrait",myPortrait);
            i.putExtra("receivedPortrait",portrait);
            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_warning_blue_a400_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(res,myPortrait))
                    .setTicker("有新的消息")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(nickname+"发来的消息")
                    .setContentText(user.getNewInfo())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT))
                    .build();
            manager.notify(1,notification);
        }
    }


}
