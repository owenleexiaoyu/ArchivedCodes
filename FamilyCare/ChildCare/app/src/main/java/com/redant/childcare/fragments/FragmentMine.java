package com.redant.childcare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.redant.childcare.R;
import com.redant.childcare.model.MyUser;
import com.redant.childcare.ui.ChartActivity;
import com.redant.childcare.ui.InfoActivity;
import com.redant.childcare.ui.KnowladgeActivity;
import com.redant.childcare.ui.LoginActivity;
import com.redant.childcare.ui.RedAntActivity;
import com.redant.childcare.ui.SettingActivity;
import com.redant.childcare.model.UserInfo;
import com.redant.childcare.widgets.CircleImageView;
import com.squareup.picasso.Picasso;


import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

/**
 * Created by jpeng on 16-11-14.
 */
public class FragmentMine extends Fragment implements AdapterView.OnItemClickListener{
    private GridView gridView;
    private TextView tvNickName;
    private SimpleAdapter gridAaapter;
    private CircleImageView iconIv;
    private List<Map<String,Object>> dataList;
    private int [] imgId = {R.mipmap.chart,R.mipmap.book,R.mipmap.settings,R.mipmap.info,R.mipmap.about,R.mipmap.login};
    private String [] text = {"统计","小知识","设置","个人信息","关于我们","登录/注册"};

    private MyUser currentUser;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine,null);
        gridView = (GridView) view.findViewById(R.id.personal_grid);
        tvNickName = (TextView) view.findViewById(R.id.mine_nickname);
        iconIv = (CircleImageView) view.findViewById(R.id.personal_icon);
        //设置用户昵称
        setUserNickName();
        iconIv = (CircleImageView) view.findViewById(R.id.personal_icon);
        dataList = new ArrayList<>();
        gridAaapter = new SimpleAdapter(getActivity(),getData(),R.layout.mine_grid_item,
                new String[]{"img","text"},new int []{R.id.personal_grid_item_img,R.id.personal_grid_item_text});
        gridView.setAdapter(gridAaapter);
        gridView.setOnItemClickListener(this);
        return view;
    }

    private void setUserNickName() {
        //设置用户昵称
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        if(currentUser == null){
            tvNickName.setText("未登录");
            Toast.makeText(getActivity(), "获取用户昵称失败,请登录", Toast.LENGTH_SHORT).show();
        }else{
            tvNickName.setText(currentUser.getNickName());
            String imgUrl = currentUser.getIconUrl();
            //设置用户头像
            if(!TextUtils.isEmpty(imgUrl)){
                Picasso.with(getActivity()).load(imgUrl).into(iconIv);
            }
        }
    }

    private List<Map<String,Object>> getData() {
        for (int i = 0;i<imgId.length;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("img",imgId[i]);
            map.put("text",text[i]);
            dataList.add(map);
        }
        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        switch (i){
            case 0://统计
                intent.setClass(getActivity(), ChartActivity.class);
                startActivity(intent);
                break;
            case 1://小知识
                intent.setClass(getActivity(), KnowladgeActivity.class);
                startActivity(intent);
                break;
            case 2://设置
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case 3://个人信息
                if(currentUser != null){
                    intent.setClass(getActivity(), InfoActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "未登录，请登录后查看", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4://关于我们
                intent.setClass(getActivity(), RedAntActivity.class);
                startActivity(intent);
                break;
            case 5://登录注册
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //获得焦点时重新设置用户昵称
        setUserNickName();
    }
}
