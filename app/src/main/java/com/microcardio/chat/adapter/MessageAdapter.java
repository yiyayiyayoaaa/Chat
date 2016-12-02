package com.microcardio.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microcardio.chat.R;
import com.microcardio.chat.activity.ViewPicActivity;
import com.microcardio.chat.po.Constants;
import com.microcardio.chat.po.Message;
import com.microcardio.chat.util.BitMapUtil;
import com.microcardio.chat.util.MediaPlayerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by AMOBBS on 2016/11/8.
 */
public class MessageAdapter extends BaseAdapter {
    Activity activity;
    List<Message> messages;
    String senderUsername;
    int sendPortrait;
    int receivedPortrait;
    //item的最小宽度
    private int mMinWidth;
    //item的最大宽度
    private int mMaxWidth;
    private LayoutInflater mInflater;
    View animView;
    public MessageAdapter(Activity activity, List<Message> messages, String senderUsername,int sendPortrait,int receivedPortrait){
        this.activity = activity;
        this.messages = messages;
        this.senderUsername = senderUsername;
        this.sendPortrait = sendPortrait;
        this.receivedPortrait = receivedPortrait;

        //获取屏幕的宽度
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //最大宽度为屏幕宽度的百分之七十
        mMaxWidth = (int) (outMetrics.widthPixels * 0.7f);
        //最大宽度为屏幕宽度的百分之十五
        mMinWidth = (int) (outMetrics.widthPixels * 0.15f);

    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messages.get(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null) {
            view = View.inflate(activity, R.layout.chat_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_send_msg = (TextView) view.findViewById(R.id.tv_send_msg);
            viewHolder.iv_send_msg = (ImageView) view.findViewById(R.id.iv_send_msg);
            viewHolder.tv_received_msg = (TextView) view.findViewById(R.id.tv_received_msg);
            viewHolder.iv_received_msg = (ImageView) view.findViewById(R.id.iv_received_msg);
            viewHolder.iv_send_portrait = (ImageView) view.findViewById(R.id.iv_send_portrait);
            viewHolder.iv_received_portrait = (ImageView) view.findViewById(R.id.iv_received_portrait);
            viewHolder.ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
            viewHolder.ll_right = (LinearLayout) view.findViewById(R.id.ll_right);

            viewHolder.send_seconds = (TextView) view.findViewById(R.id.send_recorder_time);
            viewHolder.received_seconds = (TextView) view.findViewById(R.id.receive_recorder_time);
            viewHolder.send_length = view.findViewById(R.id.send_recorder_length);
            viewHolder.receive_length = view.findViewById(R.id.receive_recorder_length);
            viewHolder.ll_audio_right = (LinearLayout) view.findViewById(R.id.ll_audio_right);
            viewHolder.ll_audio_left = (LinearLayout) view.findViewById(R.id.ll_audio_left);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(message.getSender().getUsername().equals(senderUsername)){
            viewHolder.ll_right.setVisibility(View.VISIBLE);
            viewHolder.ll_left.setVisibility(View.GONE);
            viewHolder.iv_send_portrait.setImageResource(sendPortrait);
            if(isImage(message.getContent())){//如果内容为图片
                viewHolder.ll_audio_right.setVisibility(View.GONE);
                viewHolder.tv_send_msg.setVisibility(View.GONE);
                viewHolder.iv_send_msg.setVisibility(View.VISIBLE);
                downAsynFile(message.getContent(), viewHolder.iv_send_msg);
            }else if(isAudio(message.getContent())){//如果内容为语音
                String[] s  = message.getContent().split("\\?");
                viewHolder.ll_audio_right.setVisibility(View.VISIBLE);
                viewHolder.tv_send_msg.setVisibility(View.GONE);
                viewHolder.iv_send_msg.setVisibility(View.GONE);
                viewHolder.send_seconds.setText(Math.round(Float.parseFloat(s[1])) + "\"");
                ViewGroup.LayoutParams lp = viewHolder.send_length.getLayoutParams();
                lp.width = (int) (mMinWidth + (mMaxWidth / 60f) * (Float.parseFloat(s[1])));
                //加载语音文件
                downAsynAudioRight(s[0],viewHolder.ll_audio_right,view);
            }else{
                viewHolder.ll_audio_right.setVisibility(View.GONE);
                viewHolder.iv_send_msg.setVisibility(View.GONE);
                viewHolder.tv_send_msg.setVisibility(View.VISIBLE);
                viewHolder.tv_send_msg.setText(message.getContent());
            }
        }else {
            viewHolder.ll_left.setVisibility(View.VISIBLE);
            viewHolder.ll_right.setVisibility(View.GONE);
            viewHolder.iv_received_portrait.setImageResource(receivedPortrait);
            if(isImage(message.getContent())){
                viewHolder.tv_received_msg.setVisibility(View.GONE);
                viewHolder.iv_received_msg.setVisibility(View.VISIBLE);
                downAsynFile(message.getContent(),viewHolder.iv_received_msg);
            }else if(isAudio(message.getContent())){
                String[] s  = message.getContent().split("\\?");
                viewHolder.ll_audio_left.setVisibility(View.VISIBLE);
                viewHolder.tv_received_msg.setVisibility(View.GONE);
                viewHolder.iv_received_msg.setVisibility(View.GONE);
                viewHolder.received_seconds.setText(Math.round(Float.parseFloat(s[1])) + "\"");
                ViewGroup.LayoutParams lp = viewHolder.receive_length.getLayoutParams();
                lp.width = (int) (mMinWidth + (mMaxWidth / 60f) * (Float.parseFloat(s[1])));

                //加载语音文件
                downAsynAudioLeft(s[0],viewHolder.ll_audio_left,view);
            }else{
                viewHolder.ll_audio_left.setVisibility(View.GONE);
                viewHolder.iv_received_msg.setVisibility(View.GONE);
                viewHolder.tv_received_msg.setVisibility(View.VISIBLE);
                viewHolder.tv_received_msg.setText(message.getContent());
            }
        }
        return view;
    }

    private boolean isImage(String content){
        if(content.startsWith(Constants.FILE_PATH) && !content.contains(".amr")){
            return true;
        }else {
            return false;
        }
    }


    private boolean isAudio(String content){
        String regex = "(http://"+ Constants.SERVER_ADDRESS + ":8080/upload/)(.)+(\\.amr)(.)+";
//        System.out.println("regex"+regex);
//        System.out.println("regex"+content.matches(regex));
        if(content.matches(regex)){
            return true;
        }else {
            return false;
        }
    }
    class ViewHolder{
        TextView tv_send_msg;
        TextView tv_received_msg;
        ImageView iv_send_msg;
        ImageView iv_received_msg;
        ImageView iv_send_portrait;
        ImageView iv_received_portrait;
        LinearLayout ll_left;
        LinearLayout ll_right;
        LinearLayout ll_audio_left;
        LinearLayout ll_audio_right;
        // 显示时间
        TextView send_seconds;
        TextView received_seconds;
        //控件Item显示的长度
        View send_length;
        View receive_length;


    }

    //进入查看图片界面
    public void viewPic(String path){
        Intent toView = new Intent(activity, ViewPicActivity.class);
        toView.putExtra("path",path);
        activity.startActivity(toView);
    }
    //加载图片
    private void downAsynFile(final String url, final ImageView imageView) {
        final String filename = url.substring(url.lastIndexOf("/"));
        final File file = new File(activity.getCacheDir(),filename);
        final String path = file.getAbsolutePath();
        System.out.println("--------文件是否存在"+file.exists()+"-------");
        if(file.exists()){
            Bitmap bitmap = BitMapUtil.narrowImage(path,activity);
            imageView.setImageBitmap(bitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity,"dasdadadadad",Toast.LENGTH_SHORT).show();
                    viewPic(path);
                }
            });
        }else {

            OkHttpClient mOkHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) {
                    InputStream inputStream = response.body().byteStream();
                    FileOutputStream fileOutputStream;
                    try {
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        fileOutputStream.flush();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = BitMapUtil.narrowImage(path,activity);
                                imageView.setImageBitmap(bitmap);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        viewPic(path);
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputStream.close();
                            response.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    }



    //加载语音
    private void downAsynAudioRight(final String url, final View frame, final View view) {
        final String filename = url.substring(url.lastIndexOf("/"));
        final File file = new File(activity.getCacheDir(),filename);
        final String path = file.getAbsolutePath();
        System.out.println("--------文件是否存在"+file.exists()+"-------");
        if(file.exists()){
            frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 声音播放动画
                    if (animView != null) {
                        animView.setBackgroundResource(R.drawable.adj);
                        animView = null;
                    }
                    animView = view.findViewById(R.id.send_recorder_anim);
                    animView.setBackgroundResource(R.drawable.play_anim);
                    AnimationDrawable animation = (AnimationDrawable) animView.getBackground();
                    animation.start();
                    // 播放录音
                    MediaPlayerManager.playSound(path, new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            //播放完成后修改图片
                            animView.setBackgroundResource(R.drawable.adj);
                        }
                    });
                }
            });
        }else {

            OkHttpClient mOkHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) {
                    InputStream inputStream = response.body().byteStream();
                    FileOutputStream fileOutputStream;
                    try {
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        fileOutputStream.flush();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                frame.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // 声音播放动画
                                        if (animView != null) {
                                            animView.setBackgroundResource(R.drawable.adj);
                                            animView = null;
                                        }
                                        animView = view.findViewById(R.id.send_recorder_anim);
                                        animView.setBackgroundResource(R.drawable.play_anim);
                                        AnimationDrawable animation = (AnimationDrawable) animView.getBackground();
                                        animation.start();
                                        // 播放录音
                                        MediaPlayerManager.playSound(path, new MediaPlayer.OnCompletionListener() {

                                            public void onCompletion(MediaPlayer mp) {
                                                //播放完成后修改图片
                                                animView.setBackgroundResource(R.drawable.adj);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputStream.close();
                            response.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
    }

    //加载接收语音
    private void downAsynAudioLeft(final String url, final View frame, final View view) {
        final String filename = url.substring(url.lastIndexOf("/"));
        final File file = new File(activity.getCacheDir(),filename);
        final String path = file.getAbsolutePath();
        System.out.println("--------文件是否存在"+file.exists()+"-------");
        if(file.exists()){
            frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 声音播放动画
                    if (animView != null) {
                        animView.setBackgroundResource(R.drawable.jda);
                        animView = null;
                    }
                    animView = view.findViewById(R.id.receive_recorder_anim);
                    animView.setBackgroundResource(R.drawable.play_anim2);
                    AnimationDrawable animation = (AnimationDrawable) animView.getBackground();
                    animation.start();
                    // 播放录音
                    MediaPlayerManager.playSound(path, new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            //播放完成后修改图片
                            animView.setBackgroundResource(R.drawable.jda);
                        }
                    });
                }
            });
        }else {

            OkHttpClient mOkHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) {
                    InputStream inputStream = response.body().byteStream();
                    FileOutputStream fileOutputStream;
                    try {
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        fileOutputStream.flush();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                frame.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // 声音播放动画
                                        if (animView != null) {
                                            animView.setBackgroundResource(R.drawable.jda);
                                            animView = null;
                                        }
                                        animView = view.findViewById(R.id.receive_recorder_anim);
                                        animView.setBackgroundResource(R.drawable.play_anim2);
                                        AnimationDrawable animation = (AnimationDrawable) animView.getBackground();
                                        animation.start();
                                        // 播放录音
                                        MediaPlayerManager.playSound(path, new MediaPlayer.OnCompletionListener() {

                                            public void onCompletion(MediaPlayer mp) {
                                                //播放完成后修改图片
                                                animView.setBackgroundResource(R.drawable.jda);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputStream.close();
                            response.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }

    }
}
