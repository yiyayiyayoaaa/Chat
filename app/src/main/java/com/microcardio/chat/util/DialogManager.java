package com.microcardio.chat.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.microcardio.chat.R;

/**
 * Description 对话框管理工具类
 * Created by AMOBBS on 2016/11/11.
 */

public class DialogManager {
    //弹出对话框
    private Dialog mDialog;
    //录音图标
    private ImageView mIcon;
    //音量显示图标
    private ImageView mVoice;
    //对话框上提示文字
    private TextView mLable;
    //上下文对象
    private Context mContext;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * @author AMOBBS
     * @description 显示对话框
     * @time on 2016/11/14 9:40
     */
    public void showRecordingDialog(){
        //根据指定styles实例化Dialog
        mDialog = new Dialog(mContext, R.style.AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view  = inflater.inflate(R.layout.dialog_recorder,null);
        mDialog.setContentView(view);
        mIcon = (ImageView) view.findViewById(R.id.recorder_dialog_icon);
        mVoice = (ImageView) view.findViewById(R.id.recorder_dialog_voice);
        mLable = (TextView) view.findViewById(R.id.recorder_dialog_label);
        mDialog.show();
    }


    /**
     * @author AMOBBS
     * @description 正在录音状态的对话框
     * @time on 2016/11/14 10:52
     */
    public void recording(){
        if(mDialog!= null && mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recorder);
            mLable.setText("手指上划，取消发送");
        }
    }

    /**
     * @author AMOBBS
     * @description 取消录音状态对话框
     * @time on 2016/11/14 10:58
     */
    public void wantToCancel(){
        if (mDialog != null && mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.cancel);
            mLable.setText("松开手指，取消发送");
        }
    }

    /**
     * @author AMOBBS
     * @description 时间过短提示对话框
     * @time on 2016/11/14 11:32
     */
    public void tooShort(){
        if(mDialog != null && mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.voice_to_short);
            mLable.setText("录制时间过短，请重录");
        }
    }

    /**
     * @author AMOBBS
     * @description 取消（关闭）对话框
     * @time on 2016/11/14 13:00
     */
    public void dismissDialog(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * @author AMOBBS
     * @description 显示更新音量级别的对话框
     * @time on 2016/11/14 13:16
     */
    public void updateVoiceLevel(int level){
        if(mDialog != null && mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            //设置图片的id，放在drawable中的声音图片是以v+数字格式的
            int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }


}