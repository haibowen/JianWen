package com.example.administrator.myzhihuproject.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.myzhihuproject.R;
import com.example.administrator.myzhihuproject.internet.MyHttpUtil;

public class Main2Activity extends AppCompatActivity {
    public static final String TITLE = "content_name";
    public static final String IMAGE = "image_id";
    public static final String CONTENT = "content";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //跳转传过来的数据
        Intent intent = getIntent();
        String title = intent.getStringExtra(TITLE);
        String image = intent.getStringExtra(IMAGE);
        String content = intent.getStringExtra(CONTENT);

        //控件注册
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        ImageView imageView = findViewById(R.id.image_top);
        WebView webView = findViewById(R.id.webview_content);

        setSupportActionBar(toolbar);

        //顶部返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        //数据的设置
        collapsingToolbarLayout.setTitle(title);
        Glide.with(this).load(image).into(imageView);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setDefaultTextEncodingName("utf-8");

        webView.loadUrl(content);
        /**
         webView.setWebViewClient(new WebViewClient(){
        @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String javascript="javascript:function hideOther(){"+
        "document.getElementsByTagName('head')[0].innerHTML;"+
        "document.getElementsByTagName('title')[0].remove();}";
        view.loadUrl(javascript);
        view.loadUrl("javascript:hideOther();");
        }
        });
         **/


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
