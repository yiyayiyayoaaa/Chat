package com.microcardio.chat.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microcardio.chat.po.Constants;
import com.microcardio.chat.po.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by AMOBBS on 2016/11/15.
 */
public class SocketService extends Service {
    public static int count;
    private static final String TAG = "SocketService" ;
    Socket socket ;
    DataOutputStream dos;           //输出流
    DataInputStream dis;            //输入流
    boolean bConnected = false;  //连接状态
    LocalBroadcastManager localBroadcastManager; //本地广播管理对象
    NotificationManager notificationManager;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            Thread receivedThread = new Thread(new RecvThread());
            receivedThread.start();
            Log.d(TAG, "handleMessage: " + "接收服务器信息线程开启");
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + "服务已绑定");
        return new SendMessageBinder();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: 服务正在启动。。。" );
        localBroadcastManager  = LocalBroadcastManager.getInstance(this);
        Thread connectThread = new Thread(){
            @Override
            public void run() {
                connect();
                handler.sendEmptyMessage(1);
            }
        };
        connectThread.start();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect(); //关闭连接
    }


    //向服务器发送信息
    public void sendMessage(Message message){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
        String json = gson.toJson(message,Message.class);
        try {
            dos.writeUTF(json);
            dos.flush();
        } catch (Exception e) {
          //  connect();
           // Log.d(TAG, "disconnect: 断线重连");
            e.printStackTrace();
        }
    }

    //连接服务器
    public void connect() {
        try {
            socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
            socket.setKeepAlive(true);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            Log.d(TAG, "connect: 与服务器建立连接");
            bConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //关闭连接
    public void disconnect() {
        try {
            dos.close();
            dis.close();
            socket.close();
        } catch (Exception e) {
           // connect();
            e.printStackTrace();
        }
    }

    /**
     *  通过绑定服务返回此类对象完成向服务器发送信息的功能
     */
    public class SendMessageBinder extends Binder {
        public void sendMessage(Message message){
            SocketService.this.sendMessage(message);
        }
    }
    /**
     * 通过开启该线程来接收服务器返回的信息，并将信息通过本地广播发送出去
     */
    private class RecvThread implements Runnable {
        public void run() {
            try {
                while(bConnected) {
                    String json = null;
                    if(socket.isConnected()) {
                        json = dis.readUTF();
                    }
//                    System.out.println("-------------count-------------"+count);
//                    if (count <= 0){
//                        System.out.println("-------------count-------------"+count);
//                        Gson gson = new Gson();
//                        Message message = gson.fromJson(json,Message.class);
//                        if(message.getCmd() == Constants.CMD_CHAT){
//                            notification();
//                        }
//                    }
                    Intent intent = new Intent("com.microcardio.chat.MESSAGE_BROADCAST");
                    intent.putExtra("json",json);
                    localBroadcastManager.sendBroadcast(intent);
                    Log.d(TAG, "service发出本地广播" + json);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
