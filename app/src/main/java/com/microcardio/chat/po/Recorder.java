package com.microcardio.chat.po;

/**
 * Description  录音实体类
 * Created by AMOBBS on 2016/11/11.
 */

public class Recorder {
    public float time;//时间长度
    String filePath;//文件路径

    public Recorder(){

    }

    public Recorder(String filePath, float time) {
        this.filePath = filePath;
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }



}
