package com.microcardio.chat.po;

/**
 * Created by AMOBBS on 2016/11/23.
 */
public class FileInfo {
    private String fileName;  //文件名
    private int fileType;  //文件类型
    private long fileSize;  //文件大小
    private String lastModify;  //上一次修改时间

    public FileInfo(){

    }

    public FileInfo(String fileName, long fileSize, String lastModify) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.lastModify = lastModify;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }
}
