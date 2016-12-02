package com.microcardio.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microcardio.chat.R;
import com.microcardio.chat.activity.ChatActivity;
import com.microcardio.chat.po.User;

import java.util.List;

/**
 * Created by AMOBBS on 2016/11/15.
 */
public class UserListAdapter extends BaseAdapter {
    Context context;
    List<User> userList;


    public UserListAdapter(Context context, List<User> userList){
        this.userList = userList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = userList.get(position);
        View v;
        ViewHolder viewHolder;
        if(convertView == null){
            v = View.inflate(context, R.layout.user_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_portrait = (ImageView) v.findViewById(R.id.iv_portrait);
            viewHolder.tv_user_nickname = (TextView) v.findViewById(R.id.tv_user_nickname);
            viewHolder.tv_user_chat_content = (TextView) v.findViewById(R.id.tv_user_chat_content);
            viewHolder.tv_user_sendDate = (TextView) v.findViewById(R.id.tv_user_sendDate);
            v.setTag(viewHolder);
        }else{
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }
        ChatActivity.foreground = true;
        viewHolder.tv_user_nickname.setText(user.getNickname());
        viewHolder.tv_user_chat_content.setText(user.getNewInfo());
        viewHolder.iv_portrait.setImageResource(user.getPortrait());
        viewHolder.tv_user_sendDate.setText(user.getNewDate());
        return v;
    }

    class ViewHolder{
        ImageView iv_portrait;
        TextView tv_user_nickname;
        TextView tv_user_chat_content;
        TextView tv_user_sendDate;
    }
}
