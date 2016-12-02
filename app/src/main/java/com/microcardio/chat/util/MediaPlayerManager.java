package com.microcardio.chat.util;

import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Description 播放音频工具类
 * Created by AMOBBS on 2016/11/14.
 */
public class MediaPlayerManager {
    //播放音频API类: MediaPlayer
    private static MediaPlayer mMediaPlayer;
    //是否暂停
    private static Boolean isPause;

    /**
     * @author AMOBBS
     * @description 播放声音  filePath：文件路径  onCompletionListener：播放完成监听
     * @time on 2016/11/14 13:49
     */
    public static void playSound(String filePath,MediaPlayer.OnCompletionListener onCompletionListener){
        if (mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            //设置一个Error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }else{
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author AMOBBS
     * @description 暂停播放
     * @time on 2016/11/14 14:22
     */
    public static void pause(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()){//正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * @author AMOBBS
     * @description 重新播放
     * @time on 2016/11/14 14:29
     */
    public static void resume(){
        if(mMediaPlayer != null && isPause){
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * @author AMOBBS
     * @description 释放操作
     * @time on 2016/11/14 14:30
     */
    public static void release(){
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
