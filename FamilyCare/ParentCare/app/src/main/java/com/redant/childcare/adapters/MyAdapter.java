package com.redant.childcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.redant.childcare.R;
import com.redant.childcare.entry.DaliyData;
import com.redant.childcare.entry.DaliyItem;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by 啸宇 on 2017-07-06.
 */

public class MyAdapter extends BaseAdapter {
    private List<DaliyData> mList;
    private Context mContext;

    public MyAdapter(Context mContext, List<DaliyData> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }
    public void setData(List<DaliyData> mList){
        this.mList = mList;
        notifyDataSetChanged();
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
        MyViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_daliy,parent,false);
            holder = new MyViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.item_title);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.item_desc);
            holder.tvTime = (TextView) convertView.findViewById(R.id.item_time);
            convertView.setTag(holder);
        }
        holder = (MyViewHolder) convertView.getTag();
        DaliyData item = mList.get(position);
        holder.tvTitle.setText(item.getChildName());
        holder.tvDesc.setText(item.getChildName()+"今天的运动情况已更新，详情请戳>>>");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.tvTime.setText(format.format(item.getDate()));
        return convertView;
    }

    class MyViewHolder{
        public TextView tvTitle;//标题
        public TextView tvDesc;//介绍
        public TextView tvTime;//时间
    }
}
