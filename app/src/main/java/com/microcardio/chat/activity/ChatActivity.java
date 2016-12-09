package com.microcardio.chat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microcardio.chat.R;
import com.microcardio.chat.adapter.MsgAdapter;
import com.microcardio.chat.po.Constants;
import com.microcardio.chat.po.Message;
import com.microcardio.chat.po.User;
import com.microcardio.chat.service.SocketService;
import com.microcardio.chat.util.AudioRecorderButton;
import com.microcardio.chat.util.FileNameUtil;
import com.microcardio.chat.util.HttpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private AudioRecorderButton mAudioRecorderButton;
    String senderUsername;
    String receivedUsername;
    String receivedNickname;
    static List<Message> messageList;
    User received;
    User sender;
    static ListView lv_chatList;
    public static boolean foreground= false; //活动可见标志
   // TextView tv_title_nickname;
    EditText et_send_content;
    LinearLayout ll_function;
    LinearLayout ll_recorder;
    ImageButton ib_toRecorder;
    ImageButton ib_toImage;
    ImageButton ib_toFile;
    ImageButton ib_toCamera;
    Button back;
    Button send;//发送按钮
    AudioRecorderButton arb_recorder;
    static SocketService.SendMessageBinder sendMessageBinder;
    String imageSavePath;
    Intent socketService;     //socket服务
    LoginServiceConn loginServiceConn;
    LoginMsgReceiver loginMsgReceiver; //登录信息广播接收者对象
    static LocalBroadcastManager localBroadcastManager;
    static MsgAdapter messageAdapter;
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 111:
                    sendMessageBinder.sendMessage((Message) msg.obj);
                    //messageList.add((Message) msg.obj);
                    sendBroadcast((Message) msg.obj);

                    break;
            }
            messageAdapter.notifyDataSetChanged(messageList.size()-1);
            lv_chatList.setSelection(messageAdapter.getCount() -1);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initControl();
        initData();
        registerReceiver();
        bindSocketService();

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

    protected void onStart() {
        super.onStart();
        foreground = true;
        Log.d("TAG", String.valueOf(ChatActivity.foreground));
    }

    protected void onStop() {
        super.onStop();
        foreground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
        unregisterReceiver();
    }

    //发送广播
    public static void sendBroadcast(Message message){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
        String json  = gson.toJson(message,Message.class);
        Intent intent = new Intent("com.microcardio.chat.MESSAGE_BROADCAST");
        intent.putExtra("json",json);
        localBroadcastManager.sendBroadcast(intent);
    }
    //发送信息
    public void sendMsg(View v){
        String content = et_send_content.getText().toString();
        Message message = new Message(Constants.CMD_CHAT,content,sender,received,new Date());
        sendBroadcast(message);
        sendMessageBinder.sendMessage(message);
        et_send_content.setText(null);
       // messageList.add(message);
        handler.sendEmptyMessage(1);
    }
    //内容为空不能发送消息
    class EditChangedListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().length() > 0){
                send.setEnabled(true);
            }else{
                send.setEnabled(false);
            }
        }
    }

    //初始化控件
    private void initControl(){
        lv_chatList = (ListView) findViewById(R.id.lv_chatList);
      //  tv_title_nickname = (TextView) findViewById(R.id.tv_title_nickname);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        send = (Button)findViewById(R.id.send);
        et_send_content = (EditText) findViewById(R.id.et_send_content);
        //文本监听
        et_send_content.addTextChangedListener(new EditChangedListener());

        ll_function = (LinearLayout) findViewById(R.id.ll_function);
        ll_recorder = (LinearLayout) findViewById(R.id.ll_recorder);
        ib_toRecorder = (ImageButton) findViewById(R.id.ib_toRecorder);
        ib_toImage = (ImageButton) findViewById(R.id.ib_toImage);
        ib_toCamera = (ImageButton)findViewById(R.id.ib_toCamera);
        // ib_toFile = (ImageButton) findViewById(R.id.ib_toFile);
//        b_cancel_record = (Button) findViewById(R.id.b_cancel_record);
//        arb_recorder = (AudioRecorderButton) findViewById(R.id.arb_recorder);
        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_function.setVisibility(View.VISIBLE);
                ll_recorder.setVisibility(View.GONE);
            }
        });
        ib_toRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_function.setVisibility(View.GONE);
                ll_recorder.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_send_content.getWindowToken(), 0);
            }
        });
//        b_cancel_record.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ll_function.setVisibility(View.VISIBLE);
//                ll_recorder.setVisibility(View.GONE);
//            }
//        });
        ib_toImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(Intent.ACTION_GET_CONTENT,null);
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                intent.putExtra("return-data", true);
//                intent.putExtra("crop", true);

                startActivityForResult(intent,Constants.IMAGE);
            }
        });

        ib_toCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//调用拍照功能
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String saveFileName = FileNameUtil.randomFileName(".jpg");
                File file = new File(Environment.getExternalStorageDirectory(),"DCIM/Camera/"+saveFileName);
                imageSavePath = file.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent,Constants.TAKE_PHOTO);
            }
        });

        mAudioRecorderButton = (AudioRecorderButton) findViewById(R.id.id_recorder_button);
        mAudioRecorderButton.setFinishRecordCallBack(new AudioRecorderButton.AudioFinishRecordCallBack() {
            @Override
            public void onFinish(float seconds, String filePath) {
                //Recorder recorder = new Recorder(filePath, seconds);
                //System.out.println("recorder" + recorder);
                String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                StringBuffer content = new StringBuffer(Constants.FILE_PATH).append("/").append(fileName).append("?").append(seconds);
                Message message = new Message(Constants.CMD_CHAT,content.toString(),sender,received,new Date());
                message.time = seconds;
                HttpUtil.upAudio(new File(filePath),fileName,message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            switch (requestCode){
                case Constants.FILE:
                    break;
                case Constants.IMAGE: //发送图片到服务器
                    Uri uri1= data.getData();
                    String path = getRealPathFromURI(uri1);
                    //System.out.println(path);
                    File file = new File(path);
                    String fileName = file.getName();
                    String type = fileName.substring(fileName.lastIndexOf("."));
                    String newFileName = FileNameUtil.randomFileName(type);
                    //复制文件到缓存目录
                    try {
                        copyFile(file,new File(getCacheDir(),newFileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuffer content = new StringBuffer(Constants.FILE_PATH).append("/").append(newFileName);
                    Message message = new Message(Constants.CMD_CHAT,content.toString(),sender,received,new Date());
                    HttpUtil.upFile(file,newFileName,message);
                    break;
             }
        }
        if(requestCode == Constants.TAKE_PHOTO){//拍照成功后 发送信息
            String fileName = imageSavePath.substring(imageSavePath.lastIndexOf("/"));
            StringBuffer content = new StringBuffer(Constants.FILE_PATH).append(fileName);
            Message message = new Message(Constants.CMD_CHAT,content.toString(),sender,received,new Date());
            File file = new File(imageSavePath);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);
            HttpUtil.upFile(file,fileName.replace("/",""),message);
        }
    }
    //复制文件
    private static void copyFile(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if(inputChannel !=null) {
                inputChannel.close();
            }
            if(outputChannel != null) {
                outputChannel.close();
            }
        }
    }
    //从uri中获取真实地址
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow( MediaStore.MediaColumns.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res;
    }

    private static final String TAG = "ChatActivity";
    //初始化数据
    private void initData(){
        Intent intent = getIntent();
        senderUsername = intent.getStringExtra("senderUsername");
        receivedUsername = intent.getStringExtra("receivedUsername");
        receivedNickname = intent.getStringExtra("receivedNickname");
        int sendPortrait = intent.getIntExtra("myPortrait",R.drawable.p1);
        int receivedPortrait = intent.getIntExtra("receivedPortrait",R.drawable.p1);
       // tv_title_nickname.setText(receivedNickname);
        setTitle(receivedNickname);
        sender = new User();
        sender.setUsername(senderUsername);
        received =new User();
        received.setUsername(receivedUsername);
        messageList = new ArrayList<>();
        loginMsgReceiver = new LoginMsgReceiver();
        loginServiceConn = new LoginServiceConn();
        socketService = new Intent(this, SocketService.class);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        messageAdapter = new MsgAdapter(this,messageList,senderUsername,sendPortrait,receivedPortrait,lv_chatList);
        lv_chatList.setAdapter(messageAdapter);

    }

    //加载聊天记录
    public void initMessageList(){
        Message message = new Message(Constants.USER_CHAT,null,sender,received,new Date(System.currentTimeMillis()));
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
            initMessageList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    //广播接收者
    class LoginMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json");
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
            Message message = gson.fromJson(json,Message.class);
            int cmd = message.getCmd();
            if(cmd == Constants.USER_CHAT || cmd == Constants.CMD_CHAT){
                if (isThisChat(message) ) {
                    messageList.add(message);
                    handler.sendEmptyMessage(1);
                }
            }
        }
    }

    //是否是当前聊天
    public boolean isThisChat(Message message){
        String s_username = message.getSender().getUsername();
        String r_username = message.getReceived().getUsername();
        return ((s_username.equals(senderUsername) && r_username.equals(receivedUsername))
                || (s_username.equals(receivedUsername) && r_username.equals(senderUsername)));
    }

}
