package com.microcardio.chat.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.microcardio.chat.R;
import com.microcardio.chat.adapter.FileAdapter;
import com.microcardio.chat.po.FileInfo;
import com.microcardio.chat.service.SocketService;
import com.microcardio.chat.util.DateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileListActivity extends AppCompatActivity {
    ListView lv_fileList;
    List<FileInfo> fileInfoList;
    FileAdapter fileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        lv_fileList = (ListView) findViewById(R.id.lv_fileList);
        fileInfoList = new ArrayList<>();
        findFile();
        fileAdapter = new FileAdapter(fileInfoList,this);
        lv_fileList.setAdapter(fileAdapter);
        SocketService.count ++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SocketService.count --;
    }

    public void findFile(){
        File file1 = Environment.getExternalStorageDirectory();
        File file2 = getFilesDir();
        getFileInfo(file1);
        getFileInfo(file2);

    }

    public void getFileInfo(File file){
        if(file.isFile()){
            String fileName = file.getName();
            long fileSize = file.length();
            String lastModify = DateUtil.parseStr(new Date(file.lastModified()));
            FileInfo fileInfo = new FileInfo(fileName,fileSize,lastModify);
            fileInfoList.add(fileInfo);
        }else{
            File[] files = file.listFiles();
            for(File f:files){
                if(!f.getName().startsWith(".")) {
                    getFileInfo(f);
                }
            }
        }
    }

}
