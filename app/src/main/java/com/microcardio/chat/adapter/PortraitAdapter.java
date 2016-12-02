package com.microcardio.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.microcardio.chat.R;

/**
 * Created by AMOBBS on 2016/11/21.
 */
public class PortraitAdapter extends BaseAdapter {
    int[] portrait ;
    Context context;
    public PortraitAdapter(Context context,int[] portrait){
        this.context = context;
        this.portrait = portrait;
    }
    @Override
    public int getCount() {
        return portrait.length;
    }

    @Override
    public Object getItem(int position) {
        return portrait[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null){
            view = View.inflate(context, R.layout.portrait_item,null);
            convertView = view;
        }else{
            view = convertView;
        }
        ImageView iv_item_portrait = (ImageView) view.findViewById(R.id.iv_item_portrait);
        iv_item_portrait.setImageResource(portrait[position]);
        return convertView;
    }
}
