package com.microcardio.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.microcardio.chat.R;
import com.microcardio.chat.activity.ViewPicActivity;
import com.microcardio.chat.po.Message;
import com.microcardio.chat.po.MsgType;
import com.microcardio.chat.util.BitMapUtil;
import com.microcardio.chat.util.FileNameUtil;
import com.microcardio.chat.util.MediaPlayerManager;
import com.microcardio.chat.util.ProgressImageView;

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
 * Created by AMOBBS on 2016/12/8.
 */
public class MsgAdapter extends BaseAdapter{
    Activity activity;
    List<Message> messages;
    String senderUsername;
    int sendPortrait;
    int receivedPortrait;
    //item的最小宽度
    private int mMinWidth;
    //item的最大宽度
    private int mMaxWidth;
    View animViewLeft;
    View animViewRight;
    AnimationDrawable animationLeft;
    AnimationDrawable animationRight;
    ListView listView;

    public MsgAdapter(Activity activity, List<Message> messages, String senderUsername,int sendPortrait,int receivedPortrait,ListView listView){
        this.activity = activity;
        this.messages = messages;
        this.senderUsername = senderUsername;
        this.sendPortrait = sendPortrait;
        this.receivedPortrait = receivedPortrait;
        this.listView = listView;
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
    public int getItemViewType(int position) {
        return FileNameUtil.getContentType(messages.get(position).getContent());
    }


    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messages.get(position);
        switch (getItemViewType(position)){
            case MsgType.IS_IMG:
                ImgViewHolder imgViewHolder = null;
                if(convertView == null) {
                    convertView = activity.getLayoutInflater().inflate(R.layout.chat_img_item, null);
                    imgViewHolder = new ImgViewHolder();
                    imgViewHolder.iv_received_img = (ProgressImageView) convertView.findViewById(R.id.iv_received_img);
                    imgViewHolder.iv_send_img = (ProgressImageView) convertView.findViewById(R.id.iv_send_img);
                    imgViewHolder.iv_received_portrait_img = (ImageView) convertView.findViewById(R.id.iv_received_portrait_img);
                    imgViewHolder.iv_send_portrait_img = (ImageView) convertView.findViewById(R.id.iv_send_portrait_img);
                    imgViewHolder.left = convertView.findViewById(R.id.ll_img_left);
                    imgViewHolder.right = convertView.findViewById(R.id.ll_img_right);
                    convertView.setTag(imgViewHolder);
                }else{
                    imgViewHolder = (ImgViewHolder) convertView.getTag();
                }

                if(message.getSender().getUsername().equals(senderUsername)){
                    imgViewHolder.left.setVisibility(View.GONE);
                    imgViewHolder.right.setVisibility(View.VISIBLE);
                    imgViewHolder.iv_send_portrait_img.setImageResource(sendPortrait);
                    if(imgViewHolder.iv_send_img.getTag() == null || !imgViewHolder.iv_send_img.getTag().toString().equals(message.getContent())) {
                        downAsynFile(message.getContent(), imgViewHolder.iv_send_img);
                    }
                }else{
                    imgViewHolder.left.setVisibility(View.VISIBLE);
                    imgViewHolder.right.setVisibility(View.GONE);
                    imgViewHolder.iv_received_portrait_img.setImageResource(receivedPortrait);
                    if(imgViewHolder.iv_received_img.getTag() == null || !imgViewHolder.iv_received_img.getTag().toString().equals(message.getContent())) {
                        downAsynFile(message.getContent(), imgViewHolder.iv_received_img);
                    }

                }
                break;
            case MsgType.IS_AUDIO:
                AudioViewHolder audioViewHolder = null;
                if(convertView == null) {
                    convertView = activity.getLayoutInflater().inflate(R.layout.chat_audio_item, null);
                    audioViewHolder = new AudioViewHolder();
                    audioViewHolder.iv_received_portrait_audio = (ImageView) convertView.findViewById(R.id.iv_received_portrait_audio);
                    audioViewHolder.iv_send_portrait_audio = (ImageView) convertView.findViewById(R.id.iv_send_portrait_audio);
                    audioViewHolder.send_recorder_length = convertView.findViewById(R.id.send_recorder_length);
                    audioViewHolder.receive_recorder_length = convertView.findViewById(R.id.receive_recorder_length);
                    audioViewHolder.send_recorder_time = (TextView) convertView.findViewById(R.id.send_recorder_time);
                    audioViewHolder.receive_recorder_time = (TextView) convertView.findViewById(R.id.receive_recorder_time);
                    audioViewHolder.left = convertView.findViewById(R.id.ll_left_audio);
                    audioViewHolder.right = convertView.findViewById(R.id.ll_right_audio);
                    convertView.setTag(audioViewHolder);
                }else{
                    audioViewHolder = (AudioViewHolder) convertView.getTag();
                }
                String[] s  = message.getContent().split("\\?");
                if(message.getSender().getUsername().equals(senderUsername)){
                    audioViewHolder.left.setVisibility(View.GONE);
                    audioViewHolder.right.setVisibility(View.VISIBLE);
                    //String[] s  = message.getContent().split("\\?");
                    audioViewHolder.iv_send_portrait_audio.setImageResource(sendPortrait);
                    ViewGroup.LayoutParams lp = audioViewHolder.send_recorder_length.getLayoutParams();
                    lp.width = (int) (mMinWidth + (mMaxWidth / 60f) * (Float.parseFloat(s[1])));
                    audioViewHolder.send_recorder_time.setText( "'"+Math.round(Float.parseFloat(s[1])));
                    downAsynAudioRight(s[0],audioViewHolder.send_recorder_length,convertView);
                }else{
                    audioViewHolder.left.setVisibility(View.VISIBLE);
                    audioViewHolder.right.setVisibility(View.GONE);
                    audioViewHolder.iv_received_portrait_audio.setImageResource(receivedPortrait);
                    audioViewHolder.receive_recorder_length.getLayoutParams().width = (int) (mMinWidth + (mMaxWidth / 60f) * (Float.parseFloat(s[1])));
                    audioViewHolder.receive_recorder_time.setText(Math.round(Float.parseFloat(s[1])) + "'");
                    downAsynAudioLeft(s[0],audioViewHolder.receive_recorder_length,convertView);
                }
                break;
            case MsgType.IS_OTHER:
                TextViewHolder textViewHolder = null;
                if(convertView == null) {
                    convertView = activity.getLayoutInflater().inflate(R.layout.chat_text_item, null);
                    textViewHolder = new TextViewHolder();
                    textViewHolder.tv_send_msg = (TextView) convertView.findViewById(R.id.tv_send_msg);
                    textViewHolder.tv_received_msg = (TextView) convertView.findViewById(R.id.tv_received_msg);
                    textViewHolder.iv_received_portrait = (ImageView) convertView.findViewById(R.id.iv_received_portrait);
                    textViewHolder.iv_send_portrait = (ImageView) convertView.findViewById(R.id.iv_send_portrait);
                    textViewHolder.left = convertView.findViewById(R.id.ll_left_text);
                    textViewHolder.right = convertView.findViewById(R.id.ll_right_text);
                    convertView.setTag(textViewHolder);
                }else{
                    textViewHolder = (TextViewHolder) convertView.getTag();
                }
                if(message.getSender().getUsername().equals(senderUsername)){
                    textViewHolder.left.setVisibility(View.GONE);
                    textViewHolder.right.setVisibility(View.VISIBLE);
                    textViewHolder.iv_send_portrait.setImageResource(sendPortrait);
                    textViewHolder.tv_send_msg.setText(message.getContent());
                }else{
                    textViewHolder.left.setVisibility(View.VISIBLE);
                    textViewHolder.right.setVisibility(View.GONE);
                    textViewHolder.iv_received_portrait.setImageResource(receivedPortrait);
                    textViewHolder.tv_received_msg.setText(message.getContent());
                }
                break;
        }
        return convertView;
    }

    class ImgViewHolder{
        ImageView iv_send_portrait_img;
        ImageView iv_received_portrait_img;
        ProgressImageView iv_send_img;
        ProgressImageView iv_received_img;
        View left;
        View right;

    }
    class TextViewHolder{
        ImageView iv_send_portrait;
        ImageView iv_received_portrait;
        TextView tv_send_msg;
        TextView tv_received_msg;
        View left;
        View right;
    }
    class AudioViewHolder{
        View send_recorder_length;
        View receive_recorder_length;
        ImageView iv_send_portrait_audio;
        ImageView iv_received_portrait_audio;
        TextView send_recorder_time;
        TextView receive_recorder_time;
        View left;
        View right;
    }



    //进入查看图片界面
    public void viewPic(String path){
        Intent toView = new Intent(activity, ViewPicActivity.class);
        toView.putExtra("path",path);
        activity.startActivity(toView);
    }
    //加载图片
    private void downAsynFile(final String url, final ProgressImageView imageView) {
        imageView.setTag(url);
        final String filename = url.substring(url.lastIndexOf("/"));
        final File file = new File(activity.getCacheDir(),filename);
        final String path = file.getAbsolutePath();
        imageView.startProgress();
        imageView.setImageResource(R.drawable.pic_loading);
        //System.out.println("--------文件是否存在"+file.exists()+"-------");
        if(file.exists()){
            Bitmap bitmap = BitMapUtil.narrowImage(path,activity);
            imageView.setImageBitmap(bitmap);
            imageView.stopProgress();
            listView.setSelection(messages.size()-1);

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
                        byte[] buffer = new byte[4096];
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
                                imageView.stopProgress();
                                listView.setSelection(messages.size()-1);
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

    //加载 发送语音
    private void downAsynAudioRight(final String url, final View frame, final View view) {
        // frame.setTag(url);
        final String filename = url.substring(url.lastIndexOf("/"));
        final File file = new File(activity.getCacheDir(),filename);
        final String path = file.getAbsolutePath();
        //System.out.println("--------文件是否存在"+file.exists()+"-------");

        if(file.exists()){
            frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 声音播放动画
                    if (animViewRight != null) {
                        animViewRight.setBackgroundResource(R.drawable.adj);
                        animViewRight = null;
                    }
                    animViewRight = view.findViewById(R.id.send_recorder_anim);
                    animViewRight.setBackgroundResource(R.drawable.play_anim);
                    animationRight = (AnimationDrawable) animViewRight.getBackground();
                    animationRight.start();
                    if(animationLeft != null){
                        animationLeft.stop();
                        animViewLeft.setBackgroundResource(R.drawable.jda);
                    }
                    // 播放录音
                    MediaPlayerManager.playSound(path, new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            //播放完成后修改图片
                            animationRight.stop();
                            animViewRight.setBackgroundResource(R.drawable.adj);
                            //animView = null;
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
                        byte[] buffer = new byte[4096];
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
                                        if (animViewRight != null) {
                                            animViewRight.setBackgroundResource(R.drawable.adj);
                                            animViewRight = null;
                                        }
                                        animViewRight = view.findViewById(R.id.send_recorder_anim);
                                        animViewRight.setBackgroundResource(R.drawable.play_anim);
                                        animationRight = (AnimationDrawable) animViewRight.getBackground();
                                        animationRight.start();
                                        if(animationLeft!= null) {
                                            animationLeft.stop();
                                            animViewLeft.setBackgroundResource(R.drawable.jda);
                                        }
                                        // 播放录音
                                        MediaPlayerManager.playSound(path, new MediaPlayer.OnCompletionListener() {

                                            public void onCompletion(MediaPlayer mp) {
                                                //播放完成后修改图片
                                                animationRight.stop();
                                                animViewRight.setBackgroundResource(R.drawable.adj);
                                                //animView = null;
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

    //加载 接收语音
    private void downAsynAudioLeft(final String url, final View frame, final View view) {
        //frame.setTag(url);
        final String filename = url.substring(url.lastIndexOf("/"));
        final File file = new File(activity.getCacheDir(),filename);
        final String path = file.getAbsolutePath();
        // System.out.println("--------文件是否存在"+file.exists()+"-------");
        Log.d("MessageAdapter", path);
        if(file.exists()){
            Log.d("MessageAdapter", "onClick: 本地");
            frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MessageAdapter", "onClick: 语音播放1");
                    // 声音播放动画
                    if (animViewLeft != null) {
                        animViewLeft.setBackgroundResource(R.drawable.jda);
                        animViewLeft = null;
                    }
                    animViewLeft = view.findViewById(R.id.receive_recorder_anim);
                    animViewLeft.setBackgroundResource(R.drawable.play_anim2);
                    animationLeft = (AnimationDrawable) animViewLeft.getBackground();
                    animationLeft.start();
                    if(animationRight != null){
                        animationRight.stop();
                        animViewRight.setBackgroundResource(R.drawable.adj);
                    }
                    // 播放录音
                    MediaPlayerManager.playSound(path, new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            //播放完成后修改图片
                            animationLeft.stop();
                            animViewLeft.setBackgroundResource(R.drawable.jda);
                            //animView = null;
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
                        byte[] buffer = new byte[4096];
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
                                        Log.d("MessageAdapter", "onClick: 语音播放2"+file.exists());
                                        // 声音播放动画
                                        file.setReadable(true,false);
                                        if (animViewLeft != null) {
                                            animViewLeft.setBackgroundResource(R.drawable.jda);
                                            animViewLeft = null;
                                        }
                                        animViewLeft = view.findViewById(R.id.receive_recorder_anim);
                                        animViewLeft.setBackgroundResource(R.drawable.play_anim2);
                                        animationLeft = (AnimationDrawable) animViewLeft.getBackground();
                                        animationLeft.start();
                                        if(animationRight != null){
                                            animationRight.stop();
                                            animViewRight.setBackgroundResource(R.drawable.adj);
                                        }
                                        // 播放录音
                                        MediaPlayerManager.playSound(path, new MediaPlayer.OnCompletionListener() {

                                            public void onCompletion(MediaPlayer mp) {
                                                //播放完成后修改图片
                                                animationLeft.stop();
                                                animViewLeft.setBackgroundResource(R.drawable.jda);
                                                //animView = null;
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
