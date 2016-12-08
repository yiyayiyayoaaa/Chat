package com.microcardio.chat.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.util.UUID;

/**
 * Created by AMOBBS on 2016/11/10.
 * @description 录音管理工具类
 */

public class AudioManager {
    //AudioRecord:主要是实现边录边播（AudioRecord+AudioTrack）以及对音频的实时处理
    //优点：可以语音实时处理，可以实现各种音频的封装
    private MediaRecorder mMediaRecorder;
    //录音文件
    private String mDir;
    //当前录音文件目录
    private String mCurrentFilePath;
    //单例模式
    private static AudioManager mInstance;
    //是否准备好
    private boolean isPrepare;

    //私有构造方法
    private AudioManager(String dir){
        mDir = dir;
    }

    //对外公布获取实例的方法
    public static AudioManager getInstance(String dir){
        if(mInstance == null){
            synchronized (AudioManager.class){
                if(mInstance == null){
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }
/**
 * @author AMOBBS
 * @description 录音准备完成回调接口
 * @time on 2016/11/11 10:55
 */
    public interface AudioStateListener{
        void wellPrepare();
    }

    public AudioStateListener mAudioStateListener;

    /**
     * @author AMOBBS
     * @description 供外部类调用的设置回调方法
     * @time on 2016/11/11 11:15
     */
    public void setOnAudioStateListener(AudioStateListener listener){
        mAudioStateListener = listener ;
    }

    public void audioPrepare(){
        try {
            isPrepare = false;
            File dir = new File(mDir);
        //        if(!dir.exists()){
        //            dir.mkdirs();//文件不存在，则创建文件
        //        }
            String fileName = generateFileName();
            File file = new File(dir,fileName);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            //设置输出文件路径
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式为AAC_ADTS
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //设置音频编码为ACC
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //准备录音
            mMediaRecorder.prepare();
            //开始必须在prepare后调用
            mMediaRecorder.start();
            //准备完成
            isPrepare = true;
            if(mAudioStateListener!=null){
                mAudioStateListener.wellPrepare();
            }
        } catch (Exception e) {
            mMediaRecorder.reset();
            e.printStackTrace();
        }
    }

    /**
     * @author AMOBBS
     * @description 随机生成录音文件名称
     * @time on 2016/11/11 16:04
     */
    private String generateFileName() {
        //随机生成不同的UUID
        return UUID.randomUUID().toString()+".3gp";
    }

    /**
     * @author AMOBBS
     * @description 获取音量值
     * @time on 2016/11/11 16:50
     */
    public int getVoiceLevel(int maxlever){
        if (isPrepare) {
            try {
                //getMaxAmplitude返回的数值最大是36767
                return maxlever * mMediaRecorder.getMaxAmplitude() / 32768 + 1;//返回结果1~7之间
            }catch (Exception e){

            }
        }
        return 1;
    }

    /**
     * @author AMOBBS
     * @description 释放资源
     * @time on 2016/11/11 16:52
     */
    public void release(){
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    /**
     * @author AMOBBS
     * @description 取消录音
     * @time on 2016/11/11 16:56
     */
    public void cancel(){
        release();
        if(mCurrentFilePath!=null){
            //取消录音后删除对应文件
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    /**
     * @author AMOBBS
     * @description 获取当前文件路径
     * @time on 2016/11/11 16:58
     */
    public String getCurrentFilePath(){
        return mCurrentFilePath;
    }


    /**
     * Description 播放音频工具类
     * Created by AMOBBS on 2016/11/14.
     */
    public static class MediaPlayerManager {
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
                mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
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
}