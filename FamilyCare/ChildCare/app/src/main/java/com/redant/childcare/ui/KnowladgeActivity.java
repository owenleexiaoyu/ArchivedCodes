package com.redant.childcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.redant.childcare.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 啸宇 on 2016/12/15.
 */

public class KnowladgeActivity extends BaseActivity {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_FILEPATH = "filepath";
    private String [] filepaths = {"file:///android_asset/basketball.html","file:///android_asset/football.html","file:///android_asset/volleyball.html",
            "file:///android_asset/pingpongball.html","file:///android_asset/badminton.html","file:///android_asset/biking.html","file:///android_asset/swimming.html","file:///android_asset/boxing.html"};
    private ListView knowledgelist;
    private String [] sportsNames = {"篮球","足球","排球","乒乓球","羽毛球","骑行","游泳","拳击"};
    private int [] sportsIconId = {R.mipmap.basketball, R.mipmap.football,R.mipmap.volleyball,
            R.mipmap.ping_pong,R.mipmap.badminton,R.mipmap.bicycle,R.mipmap.swimming,R.mipmap.boxing};
    private List<Map<String,Object>> dataList = null;
    private SimpleAdapter listAdapter = null;
    private Button btnOlympics;
    private Intent intent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
        knowledgelist = (ListView)findViewById(R.id.knowledge_list);
        intent = new Intent(this, HtmlReaderActivity.class);
        btnOlympics = (Button) findViewById(R.id.btn_Olympics);
        btnOlympics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(EXTRA_TITLE,"奥运会");
                intent.putExtra(EXTRA_FILEPATH,"file:///android_asset/Olympic.html");
                startActivity(intent);
            }
        });
        dataList = new ArrayList<Map<String, Object>>();
        listAdapter = new SimpleAdapter(this,getData(),R.layout.knowledgelist_item,
                new String[]{"icon","name","arrow"},new int []{R.id.knowledgeitem_img,R.id.knowledgeitem_text,R.id.knowledgeitem_arrow});
        knowledgelist.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(knowledgelist);
        knowledgelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra(EXTRA_TITLE,sportsNames[position]);
                intent.putExtra(EXTRA_FILEPATH,filepaths[position]);
                startActivity(intent);
            }
        });
    }
    private List<Map<String,Object>> getData() {
        for(int i = 0;i<sportsNames.length;i++){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("icon",sportsIconId[i]);
            map.put("name",sportsNames[i]);
            map.put("arrow",R.mipmap.arrow);
            dataList.add(map);
        }
        return dataList;
    }
    private void setListViewHeightBasedOnChildren(ListView listView) {

        SimpleAdapter listAdapter = (SimpleAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
