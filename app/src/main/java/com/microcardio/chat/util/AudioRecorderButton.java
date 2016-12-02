package com.microcardio.chat.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.microcardio.chat.R;

/**
 * Description  自定义Button
 * Created by AMOBBS on 2016/11/14.
 */

public class AudioRecorderButton extends Button{
    //按钮正常（默认）状态
    private static final int STATE_NORMAL = 1;
    //正在录音
    private static final int STATE_RECORDING = 2;
    //录音取消状态
    private static final int STATE_CANCEL = 3;
    //记录当前状态
    private int mCurrentState = STATE_NORMAL;
    //是否开始录音标识
    private Boolean isRecording = false;
    //判断在Button上滑动距离，以判断是否取消
    private static final int DISTANCE_Y_CACEL = 50;
    //对话框管理工具类
    private DialogManager mDialogManager;
    //录音管理工具类
    private AudioManager mAudioManager;
    //记录录音时间
    private float mTime;
    //是否触发longClick
    private Boolean mReady;
    //录音准备
    private static final int MSG_AUDIO_PREPARED = 0x110;
    //音量发生改变
    private static final int MSG_VOICE_CHANGE = 0x111;
    //取消提示对话框
    private static final int MSG_DIALOG_DISMISS = 0x112;



    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording){//判断正在录音
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);//每0.1秒发送消息
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    //显示对话框
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    //开启一个线程计算录音时间
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGE:
                    //更新声音
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DISMISS:
                    //取消对话框
                    mDialogManager.dismissDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);
        //录音文件存放地址
        String dir = context.getCacheDir().getAbsolutePath();
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener(){

            @Override
            public void wellPrepare() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }
        });

        //由于这个类是Button所以在O);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.audioPrepare();
                return true;
            }
        });
    }


    public AudioRecorderButton(Context context) {
        this(context,null);
    }


    /**
     * @author AMOBBS
     * @description 录音完成后的回调
     * @time on 2016/11/14 17:21
     */
    public interface AudioFinishRecordCallBack{
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecordCallBack finishRecordCallBack;

    public void setFinishRecordCallBack(AudioFinishRecordCallBack listener){
        finishRecordCallBack = listener;
    }


    /**
     * @author AMOBBS
     * @description 处理Button的onTouchEvent事件
     * @time on 2016/11/15 8:42
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取TouchEvent状态
        int action = event.getAction();
        //获得X轴坐标
        int x = (int) event.getX();
        //获得Y轴坐标
        int y = (int) event.getY();


        switch (action){
            case MotionEvent.ACTION_DOWN://手指按下
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isRecording){
                    //根据x，y的坐标判断是否需要取消
                    if (wantToCancel(x,y)){
                        changeState(STATE_CANCEL);
                    }else{
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP://手指放开
                if (!mReady){
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 0.6f){//如果时间少于0.6秒，则提示时间过短
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    //延迟显示对话框
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS,1000);
                }else if (mCurrentState == STATE_RECORDING){
                    //如果状态为正在录音，则结束录制
                    mDialogManager.dismissDialog();
                    mAudioManager.release();

                    if (finishRecordCallBack != null){
                        finishRecordCallBack.onFinish(mTime,mAudioManager.getCurrentFilePath());
                    }
                }else if (mCurrentState == STATE_CANCEL){//想要取消
                    mDialogManager.dismissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * @author AMOBBS
     * @description 恢复状态及标志位
     * @time on 2016/11/15 9:44
     */
    private void reset() {
        isRecording = false;
        mTime = 0;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancel(int x, int y) {
        //超过按钮的宽度
        if (x < 0 || x > getWidth()){
            return true;
        }
        //超过按钮的高度
        if (y < -DISTANCE_Y_CACEL || y > getHeight() + DISTANCE_Y_CACEL){
            return true;
        }
        return false;
    }


    /**
     * @author AMOBBS
     * @description 根据状态改变Button的显示
     * @time on 2016/11/15 8:56
     */
    private void changeState(int state) {
        if (mCurrentState != state){
            mCurrentState = state;
            switch (state){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText("按住说话");
                    break;

                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText("松开结束");
                    if (isRecording){
                        mDialogManager.recording();
                    }
                    break;

                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    mDialogManager.wantToCancel();
                    setText("手指上滑，取消发送");
                    break;
            }
        }
    }
}
