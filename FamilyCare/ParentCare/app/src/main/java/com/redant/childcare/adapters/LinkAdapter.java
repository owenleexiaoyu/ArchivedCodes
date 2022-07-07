package com.redant.childcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.redant.childcare.R;
import com.redant.childcare.entry.DaliyItem;
import com.redant.childcare.entry.MyUser;

import java.util.List;


/**
 * Created by 啸宇 on 2017-10-27.
 */

public class LinkAdapter extends BaseAdapter {
    private List<MyUser> mList;
    private Context mContext;
    public LinkAdapter(Context context, List<MyUser> list){
        this.mContext = context;
        this.mList = list;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_link,parent,false);
            holder = new ViewHolder();
            holder.tvUserName = (TextView) convertView.findViewById(R.id.item_link_username);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.item_link_nickname);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        MyUser item = mList.get(position);
        holder.tvUserName.setText(item.getUsername());
        holder.tvNickName.setText(item.getNickName());
        return convertView;
    }
    class ViewHolder{
        TextView tvUserName;
        TextView tvNickName;
    }
}
