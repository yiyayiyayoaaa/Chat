package com.microcardio.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microcardio.chat.R;
import com.microcardio.chat.po.FileInfo;

import java.util.List;

/**
 * Created by AMOBBS on 2016/11/23.
 */
public class FileAdapter extends BaseAdapter {
    List<FileInfo> fileInfoList;
    Context context;

    public FileAdapter(List<FileInfo> fileInfoList, Context context) {
        this.fileInfoList = fileInfoList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return fileInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileInfo fileInfo = fileInfoList.get(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = View.inflate(context, R.layout.file_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_file_portrait = (ImageView) view.findViewById(R.id.iv_file_portrait);
            viewHolder.tv_fileTitle = (TextView) view.findViewById(R.id.tv_fileTitle);
            viewHolder.tv_fileSize = (TextView) view.findViewById(R.id.tv_fileSize);
            viewHolder.tv_lastModify = (TextView) view.findViewById(R.id.tv_lastModify);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        //根据文件类型  设置文件图像
//        switch (fileInfo.getFileType()){
//            case 1:
//                viewHolder.iv_file_portrait.setImageResource(0);
//                break;
//            case 2:
//                break;
//            case 3:
//                break;
//        }
        viewHolder.tv_fileSize.setText(fileInfo.getFileSize()+"B");
        viewHolder.tv_fileTitle.setText(fileInfo.getFileName());
        viewHolder.tv_lastModify.setText(fileInfo.getLastModify());
        return view;
    }
    class ViewHolder{
        ImageView iv_file_portrait;
        TextView tv_fileTitle;
        TextView tv_fileSize;
        TextView tv_lastModify;
    }
}
