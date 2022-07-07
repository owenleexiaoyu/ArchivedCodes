package com.redant.childcare.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.redant.childcare.R;


/**
 * Created by 啸宇 on 2016/10/27.
 */

public class HtmlReaderActivity extends AppCompatActivity {
    private WebView webView;
    private String filepath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmlreader);
        getSupportActionBar().setTitle(getIntent().getStringExtra(KnowladgeActivity.EXTRA_TITLE));
        webView = (WebView) findViewById(R.id.htmlreader_webview);
        filepath = getIntent().getStringExtra(KnowladgeActivity.EXTRA_FILEPATH);
        webView.loadUrl(filepath);
    }
}
